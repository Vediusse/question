package com.viancis.answer.service;

import entities.question.Answer;
import entities.question.Question;
import entities.users.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import response.ResponseAnswer;
import response.ResponseQuestion;

public interface AnswerService {


    Mono<ResponseAnswer> createAnswer(Answer answer, User currentUser, Long questionId);

    Mono<ResponseAnswer> getAnswerById(Long id);

    Flux<ResponseAnswer> getAllAnswers();

    Mono<ResponseAnswer> updateAnswer(Long id, Answer answer, User currentUser);

    Mono<Void> deleteAnswer(Long id, User currentUser);

}
