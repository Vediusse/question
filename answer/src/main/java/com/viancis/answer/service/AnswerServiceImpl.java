package com.viancis.answer.service;

import entities.question.Answer;
import entities.question.AnswerDTO;
import entities.question.Question;
import entities.question.QuestionDTO;
import entities.users.Role;
import entities.users.User;
import exception.CustomAuthenticationException;
import exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import repository.question.AnswerRepository;
import repository.question.QuestionRepository;
import response.ResponseAnswer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ANSWER_CACHE_KEY = "answer:";


    private static final String QUESTION_CACHE_KEY = "question:";

    @Override
    @Transactional
    public Mono<ResponseAnswer> createAnswer(Answer answer, User currentUser, Long questionId) {
        if (currentUser == null) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для создания ответа"));
        }



        return Mono.fromCallable(() -> questionRepository.findById(questionId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalQuestion -> {
                    if (optionalQuestion.isPresent()) {
                        Question question = optionalQuestion.get();
                        answer.setUser(currentUser);
                        answer.setQuestion(question);
                        return Mono.fromCallable(() -> answerRepository.save(answer))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(savedAnswer -> {

                                    QuestionDTO questionDTO = new QuestionDTO(question);
                                    questionDTO.getAnswers().add(new AnswerDTO(savedAnswer));

                                    Mono<Boolean> saveAnswerToRedis = redisTemplate.opsForValue()
                                            .set(ANSWER_CACHE_KEY + savedAnswer.getId(), savedAnswer, Duration.ofHours(1))
                                            .subscribeOn(Schedulers.boundedElastic());

                                    Mono<Boolean> saveQuestionToRedis = redisTemplate.opsForValue()
                                            .set(QUESTION_CACHE_KEY + questionId, questionDTO, Duration.ofHours(1))
                                            .doOnSuccess(result -> System.out.println("Successfully saved questionDTO to Redis"))
                                            .doOnError(error -> System.out.println("Error saving questionDTO to Redis: " + error.getMessage()))
                                            .subscribeOn(Schedulers.boundedElastic());
                                    return saveAnswerToRedis
                                            .then(saveQuestionToRedis)
                                            .thenReturn(createResponseAnswer(new AnswerDTO(answer), "Ответ успешно создан", HttpStatus.CREATED));
                                });
                    } else {
                        return Mono.error(new NotFoundException("Вопрос не найден"));
                    }
                });
    }

    @Override
    public Mono<ResponseAnswer> getAnswerById(Long id) {
        return redisTemplate.opsForValue()
                .get(ANSWER_CACHE_KEY + id)
                .flatMap(serializedAnswer -> {
                    try {
                        AnswerDTO answer = objectMapper.convertValue(serializedAnswer, AnswerDTO.class);
                        return Mono.just(createResponseAnswer(answer, "Ответ успешно найден", HttpStatus.OK));
                    } catch (Exception e) {
                        return Mono.error(new Exception("Ошибка при получении ответа из кеша Redis"));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> Mono.fromCallable(() -> answerRepository.findById(id))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(optional -> optional.map(answer -> redisTemplate.opsForValue()
                                        .set(ANSWER_CACHE_KEY + answer.getId(), new AnswerDTO(answer), Duration.ofHours(1))
                                        .thenReturn(createResponseAnswer( new AnswerDTO(answer), "Ответ успешно найден", HttpStatus.OK)))
                                .orElseGet(() -> Mono.error(new NotFoundException("Ответ не найден"))))));
    }

    @Override
    public Flux<ResponseAnswer> getAllAnswers() {
        return Flux.defer(() -> Mono.fromCallable(() -> {
            List<Answer> answers = answerRepository.findAll();
            List<AnswerDTO> answerDTOS = answers.stream()
                    .map(AnswerDTO::new)
                    .collect(Collectors.toList());
            ResponseAnswer response = new ResponseAnswer();
            response.setResultRequest("Получены все ответы");
            response.setStatus(HttpStatus.OK);
            response.setObjList(answerDTOS);
            return response;
        }).subscribeOn(Schedulers.boundedElastic()));
    }

    @Override
    public Mono<ResponseAnswer> updateAnswer(Long id, Answer answer, User currentUser) {
        if ((currentUser.getRole().getLevel() < Role.ADMIN.getLevel()) || !answer.getUser().equals(currentUser)) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления ответа"));
        }

        return Mono.fromCallable(() -> answerRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(existingAnswer -> {
                    existingAnswer.setAnswer(answer.getAnswer());
                    existingAnswer.setRating(answer.getRating());
                    existingAnswer.setBestAnswer(answer.isBestAnswer());
                    Answer savedAnswer = answerRepository.save(existingAnswer);
                    return redisTemplate.opsForValue()
                            .set(ANSWER_CACHE_KEY + savedAnswer.getId(), new AnswerDTO(answer), Duration.ofHours(1))
                            .thenReturn(createResponseAnswer(new AnswerDTO(answer), "Ответ успешно обновлён", HttpStatus.OK));
                }).orElseGet(() -> Mono.error(new NotFoundException("Ответ не найден"))));
    }

    @Override
    public Mono<Void> deleteAnswer(Long id, User currentUser) {
        if (currentUser.getRole().getLevel() < Role.ADMIN.getLevel()) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для удаления ответа"));
        }
        return Mono.fromRunnable(() -> answerRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then(redisTemplate.delete(ANSWER_CACHE_KEY + id).then());
    }

    private ResponseAnswer createResponseAnswer(AnswerDTO answer, String message, HttpStatus status) {
        ResponseAnswer response = new ResponseAnswer();
        response.setResultRequest(message);
        response.setStatus(status);
        response.setObj(answer);
        return response;
    }
}
