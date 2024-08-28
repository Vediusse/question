package com.viancis.question.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.question.Answer;
import entities.question.Question;
import entities.question.QuestionDTO;
import entities.question.Subject;
import entities.users.Role;
import entities.users.User;
import exception.CustomAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import repository.question.AnswerRepository;
import repository.question.QuestionRepository;
import response.ResponsePaginatedQuestion;
import response.ResponseQuestion;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.Duration;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String QUESTION_CACHE_KEY = "question:";

    @Override
    @Transactional
    public Mono<ResponseQuestion> createQuestion(Question question, User currentUser) {
        if (currentUser == null) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления пользователя"));
        }
        return Mono.fromCallable(() -> {
                    Set<Answer> answers = question.getAnswers();
                    Set<Answer> newAnswers = new HashSet<>();
                    if (answers != null) {
                        for (Answer answer : answers) {
                            Answer newAnswer = new Answer();
                            newAnswer.setUser(currentUser);
                            newAnswer.setAnswer(answer.getAnswer());
                            newAnswer.setQuestion(question);
                            newAnswers.add(newAnswer);
                        }
                    }
                    question.setAnswers(newAnswers);
                    question.setUser(currentUser);
                    return question;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(questionToSave -> Mono.fromCallable(() -> questionRepository.save(questionToSave))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(savedQuestion -> {
                            return redisTemplate.opsForValue()
                                    .set(QUESTION_CACHE_KEY + savedQuestion.getId(), new QuestionDTO(question), Duration.ofHours(1))
                                    .then(Mono.defer(() -> {
                                        ResponseQuestion response = new ResponseQuestion();
                                        response.setResultRequest("Вопрос успешно создан");
                                        response.setStatus(HttpStatus.CREATED);
                                        response.setQuestion(new QuestionDTO(savedQuestion));
                                        return Mono.just(response);
                                    }));
                        })
                );
    }

    @Override
    public Mono<ResponseQuestion> updateQuestion(Long id, Question question, User currentUser) {
        if ((currentUser.getRole().getLevel() < Role.ADMIN.getLevel())) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления пользователя"));
        }
        return Mono.fromCallable(() -> questionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalQuestion -> optionalQuestion
                        .map(existingQuestion -> {
                            existingQuestion.setQuestion(question.getQuestion());

                            Set<Answer> newAnswers = question.getAnswers();
                            if (newAnswers != null) {
                                for (Answer answer : newAnswers) {
                                    answer.setQuestion(existingQuestion);
                                }
                            } else {
                                existingQuestion.setAnswers(new HashSet<>());
                            }
                            return existingQuestion;
                        })
                        .map(Mono::just)
                        .orElseGet(() -> Mono.error(new CustomAuthenticationException("Вопрос не найден")))
                )
                .flatMap(questionToUpdate -> Mono.fromCallable(() -> questionRepository.save(questionToUpdate))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(savedQuestion -> {
                            return redisTemplate.opsForValue()
                                    .set(QUESTION_CACHE_KEY + savedQuestion.getId(), new QuestionDTO(question), Duration.ofHours(1))
                                    .then(Mono.defer(() -> {
                                        ResponseQuestion response = new ResponseQuestion();
                                        response.setResultRequest("Вопрос успешно обновлён");
                                        response.setStatus(HttpStatus.OK);
                                        response.setQuestion(new QuestionDTO(savedQuestion));
                                        return Mono.just(response);
                                    }));
                        })
                );
    }

    @Override
    public Mono<ResponseQuestion> getQuestionById(Long id) {
        return redisTemplate.opsForValue()
                .get(QUESTION_CACHE_KEY + id)
                .flatMap(cachedQuestion -> {
                    try {
                        QuestionDTO question = objectMapper.convertValue(cachedQuestion, QuestionDTO.class);
                        ResponseQuestion response = new ResponseQuestion();
                        response.setStatus(HttpStatus.OK);
                        response.setResultRequest("Вопрос был найден");
                        response.setQuestion(question);
                        return Mono.just(response);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Ошибка преобразования данных", e));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> Mono.fromCallable(() -> questionRepository.findById(id))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(optional -> optional.map(question -> {
                            return redisTemplate.opsForValue()
                                    .set(QUESTION_CACHE_KEY + question.getId(), new QuestionDTO(question), Duration.ofHours(1))
                                    .then(Mono.defer(() -> {
                                        ResponseQuestion response = new ResponseQuestion();
                                        response.setResultRequest("Вопрос успешно найден");
                                        response.setStatus(HttpStatus.OK);
                                        response.setQuestion(new QuestionDTO(question));
                                        return Mono.just(response);
                                    }));
                        }).orElseGet(() -> {
                            ResponseQuestion response = new ResponseQuestion();
                            response.setResultRequest("Вопрос не найден");
                            response.setStatus(HttpStatus.NOT_FOUND);
                            return Mono.just(response);
                        }))
                ));
    }

    @Override
    public Flux<ResponseQuestion> getAllQuestions() {
        return Flux.defer(() -> Mono.fromCallable(() -> {
            List<Question> questions = questionRepository.findAll();
            List<QuestionDTO> questionDTOS = questions.stream()
                    .map(QuestionDTO::new)
                    .collect(Collectors.toList());
            ResponseQuestion response = new ResponseQuestion();
            response.setQuestionList(questionDTOS);
            response.setResultRequest("Получены все вопросы");
            response.setStatus(HttpStatus.OK);
            return response;
        }).subscribeOn(Schedulers.boundedElastic()));
    }

    @Override
    public Mono<Void> deleteQuestion(Long id, User currentUser) {
        if ((currentUser.getRole().getLevel() < Role.ADMIN.getLevel())) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для удаления вопроса"));
        }
        return Mono.fromRunnable(() -> questionRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then(redisTemplate.delete(QUESTION_CACHE_KEY + id).then());
    }

    @Override
    public Mono<ResponsePaginatedQuestion> getPaginatedQuestions(int page, int size, Subject subject, Integer labNumber, boolean isPublic, boolean isMe, User user) {
        Pageable pageable = PageRequest.of(page, size);

        return Mono.fromCallable(() -> {
            Page<Question> questionPage;


            if (isMe) {
                if (subject != null && labNumber != null) {
                    questionPage = questionRepository.findBySubjectAndLabNumberAndUserAndIsPublic(subject, labNumber, user, isPublic, pageable);
                } else if (subject != null) {
                    questionPage = questionRepository.findBySubjectAndUserAndIsPublic(subject, user, isPublic, pageable);
                } else if (labNumber != null) {
                    questionPage = questionRepository.findByLabNumberAndUserAndIsPublic(labNumber, user, isPublic, pageable);
                } else {
                    questionPage = questionRepository.findByUserAndIsPublic(user, isPublic, pageable);
                }
            } else {
                if (subject != null && labNumber != null) {
                    questionPage = questionRepository.findBySubjectAndLabNumberAndIsPublic(subject, labNumber, isPublic, pageable);
                } else if (subject != null) {
                    questionPage = questionRepository.findBySubjectAndIsPublic(subject, isPublic, pageable);
                } else if (labNumber != null) {
                    questionPage = questionRepository.findByLabNumberAndIsPublic(labNumber, isPublic, pageable);
                } else {
                    questionPage = questionRepository.findByIsPublic(isPublic, pageable);
                }
            }

            return createResponsePaginatedQuestion(page, questionPage);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Page<Question> filterByIsPublic(Page<Question> page, boolean isPublic) {
        List<Question> filteredQuestions = page.getContent().stream()
                .filter(question -> question.isPublic() == isPublic)
                .collect(Collectors.toList());
        return new PageImpl<>(filteredQuestions, page.getPageable(), page.getTotalElements());
    }

    private ResponsePaginatedQuestion createResponsePaginatedQuestion(int page, Page<Question> questionPage) {
        List<QuestionDTO> questionDTOS = questionPage.getContent().stream()
                .map(QuestionDTO::new)
                .collect(Collectors.toList());

        ResponsePaginatedQuestion response = new ResponsePaginatedQuestion();
        response.setQuestionList(questionDTOS);
        response.setResultRequest("Получены вопросы для страницы " + page);
        response.setStatus(HttpStatus.OK);
        response.setCurrentPage(page);
        response.setTotalPages(questionPage.getTotalPages());
        response.setHasNext(questionPage.hasNext());
        response.setHasPrevious(questionPage.hasPrevious());

        return response;
    }

    @Override
    public Mono<ResponseQuestion> recommendQuestion(Long id, boolean isPublic, User currentUser) {
        if (currentUser.getRole().getLevel() < Role.ADMIN.getLevel()) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для изменения статуса вопроса"));
        }

        return Mono.fromCallable(() -> questionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalQuestion -> optionalQuestion
                        .map(Mono::just)
                        .orElseGet(() -> Mono.error(new CustomAuthenticationException("Вопрос не найден"))))
                .flatMap(existingQuestion -> {
                    if (isPublic) {
                        existingQuestion.setPublic(true);
                        return Mono.just(existingQuestion);
                    } else {
                        return deleteQuestion(existingQuestion.getId(), currentUser)
                                .then(Mono.empty());
                    }
                })
                .flatMap(questionToUpdate -> {
                    return Mono.fromCallable(() -> questionRepository.save(questionToUpdate))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(savedQuestion -> {
                                ResponseQuestion response = new ResponseQuestion();
                                response.setResultRequest("Вопрос успешно обновлён");
                                response.setStatus(HttpStatus.OK);
                                response.setQuestion(new QuestionDTO(savedQuestion));
                                return Mono.just(response);
                            });
                });
    }






}
