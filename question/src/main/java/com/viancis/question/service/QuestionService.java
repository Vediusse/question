package com.viancis.question.service;

import entities.question.Question;
import response.ResponseQuestion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QuestionService {



    Mono<ResponseQuestion> createQuestion(Question question);
    Mono<ResponseQuestion> getQuestionById(Long id);
    Flux<ResponseQuestion> getAllQuestions();
    Mono<ResponseQuestion> updateQuestion(Long id, Question question);
    Mono<Void> deleteQuestion(Long id);

}
