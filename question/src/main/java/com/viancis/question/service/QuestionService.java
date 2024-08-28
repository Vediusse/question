package com.viancis.question.service;

import entities.question.Question;
import entities.question.Subject;
import entities.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import response.ResponsePaginatedQuestion;
import response.ResponseQuestion;

public interface QuestionService {


    Mono<ResponseQuestion> createQuestion(Question question, User currentUser);

    Mono<ResponseQuestion> getQuestionById(Long id);

    Flux<ResponseQuestion> getAllQuestions();

    Mono<ResponseQuestion> updateQuestion(Long id, Question question, User currentUser);

    Mono<Void> deleteQuestion(Long id, User currentUser);

    Mono<ResponseQuestion> recommendQuestion(Long id, boolean isPublic, User currentUser);

    Mono<ResponsePaginatedQuestion> getPaginatedQuestions(int page, int size, Subject subject, Integer labNumber, boolean isPublic, boolean isMe, User user);

}
