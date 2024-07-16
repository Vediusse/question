package com.viancis.question.service;



import entities.question.Answer;
import entities.question.Question;
import entities.question.QuestionDTO;
import entities.users.Role;
import entities.users.User;
import exception.CustomAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import repository.question.AnswerRepository;
import repository.question.QuestionRepository;
import response.ResponseQuestion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

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
            Question savedQuestion = questionRepository.save(question);
            ResponseQuestion response = new ResponseQuestion();
            response.setResultRequest("Вопрос успешно создан");
            response.setStatus(HttpStatus.CREATED);
            response.setQuestion(new QuestionDTO(savedQuestion));
            return response;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ResponseQuestion> updateQuestion(Long id, Question question, User currentUser) {
        if ((currentUser.getRole().getLevel() < Role.ADMIN.getLevel())) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления пользователя"));
        }
        return Mono.fromCallable(() -> questionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(existingQuestion -> {
                    existingQuestion.setQuestion(question.getQuestion());

                    Set<Answer> newAnswers = question.getAnswers();
                    if (newAnswers != null) {
                        for (Answer answer : newAnswers) {
                            answer.setQuestion(existingQuestion);
                        }
                    } else {
                        existingQuestion.setAnswers(new HashSet<>());
                    }

                    Question savedQuestion = questionRepository.save(existingQuestion);
                    ResponseQuestion response = new ResponseQuestion();
                    response.setResultRequest("Вопрос успешно обновлён");
                    response.setStatus(HttpStatus.OK);
                    response.setQuestion(new QuestionDTO(savedQuestion));
                    return Mono.just(response);
                }).orElseGet(() -> {
                    ResponseQuestion response = new ResponseQuestion();
                    response.setResultRequest("Вопрос не найден");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    return Mono.just(response);
                }));
    }

    @Override
    public Mono<ResponseQuestion> getQuestionById(Long id) {
        return Mono.fromCallable(() -> questionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(question -> {
                    ResponseQuestion response = new ResponseQuestion();
                    response.setResultRequest("Вопрос успешно найден");
                    response.setStatus(HttpStatus.OK);
                    response.setQuestion(new QuestionDTO(question));
                    return Mono.just(response);
                }).orElseGet(() -> {
                    ResponseQuestion response = new ResponseQuestion();
                    response.setResultRequest("Вопрос не найден");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    return Mono.just(response);
                }));
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
                .then();
    }
}


