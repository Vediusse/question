package com.viancis.comment.service;

import entities.comment.Comment;
import entities.users.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import response.ResponseComment;

public interface CommentService {
    Mono<ResponseComment> createCommentForQuestion(Comment comment, User currentUser, Long questionId);
    Mono<ResponseComment> createCommentForAnswer(Comment comment, User currentUser, Long answerId);
    Mono<ResponseComment> getCommentById(Long id);
    Flux<ResponseComment> getAllComments();
    Mono<ResponseComment> updateComment(Long id, Comment comment, User currentUser);
    Mono<Void> deleteComment(Long id, User currentUser);
}
