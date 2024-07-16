package com.viancis.comment.service;

import entities.comment.Comment;
import entities.comment.CommentDTO;
import entities.question.Answer;
import entities.question.Question;
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
import repository.comment.CommentRepository;
import repository.question.AnswerRepository;
import repository.question.QuestionRepository;
import response.ResponseComment;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    @Transactional
    public Mono<ResponseComment> createCommentForQuestion(Comment comment, User currentUser, Long questionId) {
        comment.setUser(currentUser);
        return Mono.fromCallable(() -> questionRepository.findById(questionId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalQuestion -> {
                    if (optionalQuestion.isPresent()) {
                        Question question = optionalQuestion.get();
                        comment.setQuestion(question);
                        return getMono(comment);
                    } else {
                        return Mono.error(new NotFoundException("Вопрос не найден"));
                    }
                });
    }

    private Mono<? extends ResponseComment> getMono(Comment comment) {
        return Mono.fromCallable(() -> commentRepository.save(comment))
                .subscribeOn(Schedulers.boundedElastic())
                .map(savedComment -> {
                    ResponseComment response = new ResponseComment();
                    response.setResultRequest("Комментарий успешно создан");
                    response.setStatus(HttpStatus.CREATED);
                    response.setObj(new CommentDTO(savedComment));
                    return response;
                });
    }

    @Override
    @Transactional
    public Mono<ResponseComment> createCommentForAnswer(Comment comment, User currentUser, Long answerId) {
        comment.setUser(currentUser);
        return Mono.fromCallable(() -> answerRepository.findById(answerId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalAnswer -> {
                    if (optionalAnswer.isPresent()) {
                        Answer answer = optionalAnswer.get();
                        comment.setAnswer(answer);
                        return getMono(comment);
                    } else {
                        return Mono.error(new NotFoundException("Ответ не найден"));
                    }
                });
    }

    @Override
    public Mono<ResponseComment> getCommentById(Long id) {
        return Mono.fromCallable(() -> commentRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(comment -> {
                    ResponseComment response = new ResponseComment();
                    response.setResultRequest("Комментарий успешно найден");
                    response.setStatus(HttpStatus.OK);
                    response.setObj(new CommentDTO(comment));
                    return Mono.just(response);
                }).orElseGet(() -> Mono.error(new NotFoundException("Комментарий не найден"))));
    }

    @Override
    public Flux<ResponseComment> getAllComments() {
        return Flux.defer(() -> Mono.fromCallable(() -> {
            List<Comment> comments = commentRepository.findAll();
            List<CommentDTO> commentDTOS = comments.stream()
                    .map(CommentDTO::new)
                    .collect(Collectors.toList());
            ResponseComment response = new ResponseComment();
            response.setResultRequest("Получены все комментарии");
            response.setStatus(HttpStatus.OK);
            response.setObjList(commentDTOS);
            return response;
        }).subscribeOn(Schedulers.boundedElastic()));
    }

    @Override
    public Mono<ResponseComment> updateComment(Long id, Comment comment, User currentUser) {
        if (!comment.getUser().equals(currentUser)) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления комментария"));
        }

        return Mono.fromCallable(() -> commentRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(existingComment -> {
                    existingComment.setContent(comment.getContent());
                    existingComment.setCreatedAt(comment.getCreatedAt());
                    Comment savedComment = commentRepository.save(existingComment);
                    ResponseComment response = new ResponseComment();
                    response.setResultRequest("Комментарий успешно обновлён");
                    response.setStatus(HttpStatus.OK);
                    response.setObj(new CommentDTO(savedComment));
                    return Mono.just(response);
                }).orElseGet((() -> Mono.error(new NotFoundException("Комментарий не найден")))));
    }

    @Override
    public Mono<Void> deleteComment(Long id, User currentUser) {
        if ((currentUser.getRole().getLevel() < Role.ADMIN.getLevel())) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления (так сказать) ответа"));
        }
        return Mono.fromRunnable(() -> commentRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}

