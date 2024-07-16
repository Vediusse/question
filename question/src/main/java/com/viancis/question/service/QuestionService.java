package com.viancis.question.service;

import entities.question.Question;
import entities.users.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import response.ResponseQuestion;

public interface QuestionService {


    Mono<ResponseQuestion> createQuestion(Question question, User currentUser);

    Mono<ResponseQuestion> getQuestionById(Long id);

    Flux<ResponseQuestion> getAllQuestions();

    Mono<ResponseQuestion> updateQuestion(Long id, Question question, User currentUser);

    Mono<Void> deleteQuestion(Long id, User currentUser);

}
