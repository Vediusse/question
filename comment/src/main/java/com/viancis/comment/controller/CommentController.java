package com.viancis.comment.controller;

import annotation.CurrentUser;
import com.viancis.comment.service.CommentService;
import entities.comment.Comment;
import entities.users.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import response.ResponseComment;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/question/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a comment for a question",
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = Comment.class),
                            examples = @ExampleObject(value = "{\"content\": \"Ты крутой, ваще восхищаюсь тобой\"}")
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comment successfully created",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseComment.class),
                                    examples = @ExampleObject(value = "{ \"resultRequest\": \"Комментарий успешно создан\", \"status\": \"CREATED\", \"obj\": { \"id\": 4, \"content\": \"Ты крутой, ваще восхищаюсь тобой\", \"createdAt\": \"2024-07-15T17:00:02.102+00:00\", \"user\": { \"id\": 2, \"username\": \"losharkkkasadf\", \"role\": { \"name\": \"USER\", \"local\": \"Пользователь\", \"description\": \"Трудяга работяга\" } }, \"questionId\": null, \"answerId\": 3 } }")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Question not found")
            }
    )
    public Mono<ResponseComment> createCommentForQuestion(@RequestBody Comment comment, @CurrentUser User currentUser, @PathVariable Long id) {
        return commentService.createCommentForQuestion(comment, currentUser, id);
    }

    @PostMapping("/answer/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a comment for an answer",
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = Comment.class),
                            examples = @ExampleObject(value = "{\"content\": \"Ты крутой, ваще восхищаюсь тобой\"}")
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comment successfully created",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseComment.class),
                                    examples = @ExampleObject(value = "{ \"resultRequest\": \"Комментарий успешно создан\", \"status\": \"CREATED\", \"obj\": { \"id\": 4, \"content\": \"Ты крутой, ваще восхищаюсь тобой\", \"createdAt\": \"2024-07-15T17:00:02.102+00:00\", \"user\": { \"id\": 2, \"username\": \"losharkkkasadf\", \"role\": { \"name\": \"USER\", \"local\": \"Пользователь\", \"description\": \"Трудяга работяга\" } }, \"questionId\": null, \"answerId\": 3 } }")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Answer not found")
            }
    )
    public Mono<ResponseComment> createCommentForAnswer(@RequestBody Comment comment, @CurrentUser User currentUser, @PathVariable Long id) {
        return commentService.createCommentForAnswer(comment, currentUser, id);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a comment by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseComment.class),
                                    examples = @ExampleObject(value = "{ \"resultRequest\": \"Комментарий успешно найден\", \"status\": \"OK\", \"obj\": { \"id\": 4, \"content\": \"Ты крутой, ваще восхищаюсь тобой\", \"createdAt\": \"2024-07-15T17:00:02.102+00:00\", \"user\": { \"id\": 2, \"username\": \"losharkkkasadf\", \"role\": { \"name\": \"USER\", \"local\": \"Пользователь\", \"description\": \"Трудяга работяга\" } }, \"questionId\": null, \"answerId\": 3 } }")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Comment not found")
            }
    )
    public Mono<ResponseComment> getCommentById(@PathVariable Long id) {
        return commentService.getCommentById(id);
    }

    @GetMapping
    @Operation(
            summary = "Get all comments",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All comments retrieved",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseComment.class),
                                    examples = @ExampleObject(value = "{ \"resultRequest\": \"Получены все комментарии\", \"status\": \"OK\", \"objList\": [ { \"id\": 4, \"content\": \"Ты крутой, ваще восхищаюсь тобой\", \"createdAt\": \"2024-07-15T17:00:02.102+00:00\", \"user\": { \"id\": 2, \"username\": \"losharkkkasadf\", \"role\": { \"name\": \"USER\", \"local\": \"Пользователь\", \"description\": \"Трудяга работяга\" } }, \"questionId\": null, \"answerId\": 3 } ] }")
                            )
                    )
            }
    )
    public Flux<ResponseComment> getAllComments() {
        return commentService.getAllComments();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a comment",
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = Comment.class),
                            examples = @ExampleObject(value = "{\"content\": \"Обновлённый комментарий\"}")
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment successfully updated",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseComment.class),
                                    examples = @ExampleObject(value = "{ \"resultRequest\": \"Комментарий успешно обновлён\", \"status\": \"OK\", \"obj\": { \"id\": 4, \"content\": \"Обновлённый комментарий\", \"createdAt\": \"2024-07-15T17:00:02.102+00:00\", \"user\": { \"id\": 2, \"username\": \"losharkkkasadf\", \"role\": { \"name\": \"USER\", \"local\": \"Пользователь\", \"description\": \"Трудяга работяга\" } }, \"questionId\": null, \"answerId\": 3 } }")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Comment not found"),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights to update the comment")
            }
    )
    public Mono<ResponseComment> updateComment(@PathVariable Long id, @RequestBody Comment comment, @CurrentUser User currentUser) {
        return commentService.updateComment(id, comment, currentUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a comment",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Comment successfully deleted"
                    ),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights to delete the comment")
            }
    )
    public Mono<Void> deleteComment(@PathVariable Long id, @CurrentUser User currentUser) {
        return commentService.deleteComment(id, currentUser);
    }
}
