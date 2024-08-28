package com.viancis.comment.service;

import entities.comment.Comment;
import entities.comment.CommentDTO;
import entities.question.Answer;
import entities.question.AnswerDTO;
import entities.question.Question;
import entities.question.QuestionDTO;
import entities.users.Role;
import entities.users.User;
import exception.CustomAuthenticationException;
import exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
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

import java.time.Duration;
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

    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    private static final String COMMENT_CACHE_KEY = "comment:";

    private static final String QUESTION_CACHE_KEY = "question:";


    private static final String ANSWER_CACHE_KEY = "answer:";


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

                        if (comment.getContent() == null || comment.getContent().isEmpty()) {
                            return Mono.error(new IllegalArgumentException("Поле 'content' не может быть пустым"));
                        }

                        return saveComment(comment)
                                .flatMap(responseComment -> {
                                    QuestionDTO questionDTO = new QuestionDTO(question);
                                    questionDTO.getComments().add(new CommentDTO(comment));


                                    return Mono.fromRunnable(() -> {
                                        redisTemplate.opsForValue()
                                                .set(QUESTION_CACHE_KEY + questionId, questionDTO, Duration.ofHours(1))
                                                .doOnSuccess(result -> System.out.println("Successfully saved questionDTO to Redis"))
                                                .doOnError(error -> System.out.println("Error saving questionDTO to Redis: " + error.getMessage()))
                                                .subscribe();
                                    }).thenReturn(responseComment);
                                });
                    } else {
                        return Mono.error(new NotFoundException("Вопрос не найден"));
                    }
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

                        if (comment.getContent() == null || comment.getContent().isEmpty()) {
                            return Mono.error(new IllegalArgumentException("Поле 'content' не может быть пустым"));
                        }

                        return saveComment(comment)
                                .flatMap(responseComment -> {
                                    AnswerDTO answerDTO = new AnswerDTO(answer);
                                    QuestionDTO questionDTO = new QuestionDTO(answer.getQuestion());

                                    answerDTO.getComments().add(new CommentDTO(comment));
                                    questionDTO.getComments().add(new CommentDTO(comment));
                                    return Mono.fromRunnable(() -> {
                                        redisTemplate.opsForValue()
                                                .set(ANSWER_CACHE_KEY + answerId, answerDTO, Duration.ofHours(1))
                                                .doOnSuccess(result -> System.out.println("Successfully saved to Redis"))
                                                .doOnError(error -> System.out.println("Error saving to Redis: " + error.getMessage()))
                                                .subscribe();
                                        redisTemplate.opsForValue()
                                                .set(QUESTION_CACHE_KEY + answer.getQuestion().getId(), questionDTO, Duration.ofHours(1)).subscribe();
                                    }).thenReturn(responseComment);
                                });
                    } else {
                        return Mono.error(new NotFoundException("Ответ не найден"));
                    }
                });
    }

    private Mono<ResponseComment> saveComment(Comment comment) {
        return Mono.fromCallable(() -> commentRepository.save(comment))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(savedComment -> redisTemplate.opsForValue()
                        .set(COMMENT_CACHE_KEY + savedComment.getId(), new CommentDTO(savedComment), Duration.ofHours(1))
                        .then(Mono.just(createResponseComment(new CommentDTO(savedComment), "Комментарий успешно создан", HttpStatus.CREATED))));
    }

    @Override
    public Mono<ResponseComment> getCommentById(Long id) {
        return redisTemplate.opsForValue()
                .get(COMMENT_CACHE_KEY + id)
                .flatMap(serializedComment -> {
                    try {
                        CommentDTO comment = (CommentDTO) serializedComment;
                        return Mono.just(createResponseComment(comment, "Комментарий успешно найден", HttpStatus.OK));
                    } catch (Exception e) {
                        return Mono.error(new Exception("Ошибка при получении комментария из кеша Redis"));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> Mono.fromCallable(() -> commentRepository.findById(id))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(optional -> optional.map(comment -> redisTemplate.opsForValue()
                                        .set(COMMENT_CACHE_KEY + comment.getId(), new CommentDTO(comment), Duration.ofHours(1))
                                        .thenReturn(createResponseComment(new CommentDTO(comment), "Комментарий успешно найден", HttpStatus.OK)))
                                .orElseGet(() -> Mono.error(new NotFoundException("Комментарий не найден"))))));
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
    @Transactional
    public Mono<ResponseComment> updateComment(Long id, Comment comment, User currentUser) {
        if (!comment.getUser().equals(currentUser)) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления комментария"));
        }

        return Mono.fromCallable(() -> commentRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(existingComment -> {
                            existingComment.setContent(comment.getContent());
                            existingComment.setCreatedAt(comment.getCreatedAt());
                            return existingComment;
                        })
                        .map(Mono::just)
                        .orElseGet((() -> Mono.error(new NotFoundException("Комментарий не найден"))))
                        .flatMap(commentToUpdate -> Mono.fromCallable(() -> commentRepository.save(commentToUpdate))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(savedComment -> redisTemplate.opsForValue()
                                        .set(COMMENT_CACHE_KEY + savedComment.getId(), new CommentDTO(savedComment), Duration.ofHours(1))
                                        .then(Mono.just(createResponseComment(new CommentDTO(savedComment), "Комментарий успешно обновлён", HttpStatus.OK))))
                        ));
    }

    @Override
    public Mono<Void> deleteComment(Long id, User currentUser) {
        if (currentUser.getRole().getLevel() < Role.ADMIN.getLevel()) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для удаления комментария"));
        }
        return Mono.fromRunnable(() -> commentRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then(redisTemplate.delete(COMMENT_CACHE_KEY + id).then());
    }

    private ResponseComment createResponseComment(CommentDTO comment, String message, HttpStatus status) {
        ResponseComment response = new ResponseComment();
        response.setResultRequest(message);
        response.setStatus(status);
        response.setObj(comment);
        return response;
    }
}
