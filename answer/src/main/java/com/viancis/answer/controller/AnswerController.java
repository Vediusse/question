package com.viancis.answer.controller;

import annotation.CurrentUser;
import com.viancis.answer.service.AnswerService;
import entities.question.Answer;
import entities.users.User;
import exception.CustomAuthenticationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import response.ResponseAnswer;

@RestController
@RequestMapping("/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Operation(summary = "Create a new answer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Answer created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseAnswer.class),
                            examples = @ExampleObject(value = "{\"resultRequest\": \"Ответ успешно создан\", \"status\": \"CREATED\", \"obj\": {\"id\": 9, \"answer\": \"Ты молодец\", \"rating\": 0, \"answerer\": {\"id\": 2, \"username\": \"user\", \"role\": {\"name\": \"USER\", \"local\": \"Пользователь\", \"description\": \"Трудяга работяга\"}}}}"))
            ),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions to create an answer",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomAuthenticationException.class),
                            examples = @ExampleObject(value = "{\"message\": \"Недостаточно прав для создания ответа\"}"))
            )
    })
    @PostMapping("/question/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseAnswer> createAnswer(@RequestBody Answer answer, @CurrentUser User currentUser, @PathVariable Long id) {
        return answerService.createAnswer(answer, currentUser, id);
    }

    @Operation(summary = "Get an answer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Answer retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseAnswer.class),
                            examples = @ExampleObject(value = "{\"resultRequest\": \"Ответ успешно найден\", \"status\": \"OK\", \"obj\": {\"id\": 9, \"answer\": \"Ты молодец\", \"rating\": 0, \"answerer\": {\"id\": 2, \"username\": \"user\", \"role\": {\"name\": \"USER\", \"local\": \"Пользователь\", \"description\": \"Трудяга работяга\"}}}}"))
            ),
            @ApiResponse(responseCode = "404", description = "Answer not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseAnswer.class),
                            examples = @ExampleObject(value = "{\"resultRequest\": \"Ответ не найден\", \"status\": \"NOT_FOUND\"}"))
            )
    })
    @GetMapping("/{id}")
    public Mono<ResponseAnswer> getAnswerById(@PathVariable Long id) {
        return answerService.getAnswerById(id);
    }

    @Operation(summary = "Get all answers")
    @ApiResponse(responseCode = "200", description = "All answers retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseAnswer.class),
                    examples = @ExampleObject(value = "{\"resultRequest\": \"Получены все вопросы\", \"status\": \"OK\", \"objList\": [{\"id\": 9, \"answer\": \"Ты молодец\", \"rating\": 0, \"answerer\": {\"id\": 2, \"username\": \"user\", \"role\": {\"name\": \"USER\", \"local\": \"Пользователь\", \"description\": \"Трудяга работяга\"}}}]}"))
    )
    @GetMapping
    public Flux<ResponseAnswer> getAllAnswers() {
        return answerService.getAllAnswers();
    }

    @Operation(summary = "Update an answer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Answer updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseAnswer.class),
                            examples = @ExampleObject(value = "{\"resultRequest\": \"Ответ успешно обновлён\", \"status\": \"OK\", \"obj\": {\"id\": 9, \"answer\": \"Ты молодец\", \"rating\": 0, \"answerer\": {\"id\": 2, \"username\": \"user\", \"role\": {\"name\": \"USER\", \"local\": \"Пользователь\", \"description\": \"Трудяга работяга\"}}}}"))
            ),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions to update the answer",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomAuthenticationException.class),
                            examples = @ExampleObject(value = "{\"message\": \"Недостаточно прав для обновления ответа\"}"))
            )
    })
    @PutMapping("/{id}")
    public Mono<ResponseAnswer> updateAnswer(@PathVariable Long id, @RequestBody Answer answer, @CurrentUser User currentUser) {
        return answerService.updateAnswer(id, answer, currentUser);
    }

    @Operation(summary = "Delete an answer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Answer deleted successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions to delete the answer",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomAuthenticationException.class),
                            examples = @ExampleObject(value = "{\"message\": \"Недостаточно прав для удаления ответа\"}"))
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAnswer(@PathVariable Long id, @CurrentUser User currentUser) {
        return answerService.deleteAnswer(id, currentUser);
    }
}
