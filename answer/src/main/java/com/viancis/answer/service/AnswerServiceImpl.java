package com.viancis.answer.service;


import entities.question.Answer;
import entities.question.AnswerDTO;
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
import response.ResponseAnswer;
import response.ResponseQuestion;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

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
                                .map(savedAnswer -> {
                                    ResponseAnswer response = new ResponseAnswer();
                                    response.setResultRequest("Ответ успешно создан");
                                    response.setStatus(HttpStatus.CREATED);
                                    response.setObj(new AnswerDTO(savedAnswer));
                                    return response;
                                });
                    } else {
                        return Mono.error(new CustomAuthenticationException("Вопрос не найден"));
                    }
                });
    }

    @Override
    public Mono<ResponseAnswer> getAnswerById(Long id) {
        return Mono.fromCallable(() -> answerRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(answer -> {
                    ResponseAnswer response = new ResponseAnswer();
                    response.setResultRequest("Ответ успешно найден");
                    response.setStatus(HttpStatus.OK);
                    response.setObj(new AnswerDTO(answer));
                    return Mono.just(response);
                }).orElseGet(() -> {
                    ResponseAnswer response = new ResponseAnswer();
                    response.setResultRequest("Ответ не найден");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    return Mono.just(response);
                }));
    }

    @Override
    public Flux<ResponseAnswer> getAllAnswers() {
        return Flux.defer(() -> Mono.fromCallable(() -> {
            List<Answer> questions = answerRepository.findAll();
            List<AnswerDTO> answerDTOS = questions.stream()
                    .map(AnswerDTO::new)
                    .collect(Collectors.toList());
            ResponseAnswer response = new ResponseAnswer();
            response.setResultRequest("Получены все вопросы");
            response.setStatus(HttpStatus.OK);
            response.setObjList(answerDTOS);
            return response;
        }).subscribeOn(Schedulers.boundedElastic()));
    }

    @Override
    public Mono<ResponseAnswer> updateAnswer(Long id, Answer answer, User currentUser) {
        if ((currentUser.getRole().getLevel() < Role.ADMIN.getLevel())) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления ответа"));
        }
        if (!answer.getUser().equals(currentUser)) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления ответа"));
        }

        return Mono.fromCallable(() -> questionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(existingQuestion -> {
                    answer.setAnswer(answer.getAnswer());
                    answer.setRating(answer.getRating());
                    answer.setBestAnswer(answer.isBestAnswer());
                    Answer savedAnswer = answerRepository.save(answer);
                    ResponseAnswer response = new ResponseAnswer();
                    response.setResultRequest("Ответ успешно обновлён");
                    response.setStatus(HttpStatus.OK);
                    response.setObj(new AnswerDTO(savedAnswer));
                    return Mono.just(response);
                }).orElseGet(() -> {
                    ResponseAnswer response = new ResponseAnswer();
                    response.setResultRequest("Вопрос не найден");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    return Mono.just(response);
                }));
    }

    @Override
    public Mono<Void> deleteAnswer(Long id, User currentUser) {
        if ((currentUser.getRole().getLevel() < Role.ADMIN.getLevel())) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления ответа"));
        }
        return Mono.fromRunnable(() -> answerRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}

