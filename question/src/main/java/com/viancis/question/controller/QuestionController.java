package com.viancis.question.controller;

import annotation.CurrentUser;
import com.viancis.question.service.QuestionService;
import entities.question.Question;
import entities.users.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import response.ResponseError;
import response.ResponseQuestion;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Operation(
            summary = "Create a new question",
            description = "Creates a new question and associates it with the current user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Question.class),
                            examples = @ExampleObject(
                                    name = "createQuestionRequestExample",
                                    summary = "Example request for creating a new question",
                                    value = "{\n  \"question\": \"Ищу жену\",\n  \"answers\": [\n    {\n      \"answer\": \"Нет, лучше изучай spring\",\n      \"bestAnswer\": false\n    }\n  ]\n}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created question",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseQuestion.class),
                            examples = @ExampleObject(
                                    name = "createQuestionResponse201Example",
                                    summary = "Question successfully created",
                                    value = "{\n  \"resultRequest\": \"Вопрос успешно создан\",\n  \"status\": \"CREATED\",\n  \"question\": {\n    \"id\": 1,\n    \"question\": \"Ищу жену\",\n    \"answers\": [\n      {\n        \"answer\": \"Нет, лучше изучай spring\",\n        \"bestAnswer\": false\n      }\n    ],\n    \"user\": {\n      \"id\": 2,\n      \"username\": \"testUser\"\n    }\n  }\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "createQuestionResponse401Example",
                                    summary = "Unauthorized access",
                                    value = "{\n  \"resultRequest\": \"Неавторизованный доступ\",\n  \"status\": \"UNAUTHORIZED\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "createQuestionResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseQuestion> createQuestion(@RequestBody Question question, @CurrentUser User currentUser) {
        return questionService.createQuestion(question, currentUser);
    }

    @Operation(
            summary = "Get a question by ID",
            description = "Retrieves a question by its ID.",
            parameters = @Parameter(name = "id", description = "ID of the question to retrieve", required = true)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved question",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseQuestion.class),
                            examples = @ExampleObject(
                                    name = "getQuestionByIdResponse200Example",
                                    summary = "Question found",
                                    value = "{\n  \"resultRequest\": \"Вопрос успешно найден\",\n  \"status\": \"OK\",\n  \"question\": {\n    \"id\": 1,\n    \"question\": \"Ищу жену\",\n    \"answers\": [\n      {\n        \"answer\": \"Нет, лучше изучай spring\",\n        \"bestAnswer\": false\n      }\n    ],\n    \"user\": {\n      \"id\": 2,\n      \"username\": \"testUser\"\n    }\n  }\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Question not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "getQuestionByIdResponse404Example",
                                    summary = "Question not found",
                                    value = "{\n  \"resultRequest\": \"Вопрос не найден\",\n  \"status\": \"NOT_FOUND\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "getQuestionByIdResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public Mono<ResponseQuestion> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    @Operation(
            summary = "Get all questions",
            description = "Retrieves all questions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved questions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseQuestion.class),
                            examples = @ExampleObject(
                                    name = "getAllQuestionsResponse200Example",
                                    summary = "Questions found",
                                    value = "[\n  {\n    \"resultRequest\": \"Получены все вопросы\",\n    \"status\": \"OK\",\n    \"question\": {\n      \"id\": 1,\n      \"question\": \"Ищу жену\",\n      \"answers\": [\n        {\n          \"answer\": \"Нет, лучше изучай spring\",\n          \"bestAnswer\": false\n        }\n      ],\n      \"user\": {\n        \"id\": 2,\n        \"username\": \"testUser\"\n      }\n    }\n  }\n]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "getAllQuestionsResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @GetMapping
    public Flux<ResponseQuestion> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @Operation(
            summary = "Update an existing question",
            description = "Updates an existing question by its ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Question.class),
                            examples = @ExampleObject(
                                    name = "updateQuestionRequestExample",
                                    summary = "Example request for updating a question",
                                    value = "{\n  \"question\": \"Updated question text\",\n  \"answers\": [\n    {\n      \"answer\": \"Updated answer\",\n      \"bestAnswer\": true\n    }\n  ]\n}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated question",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseQuestion.class),
                            examples = @ExampleObject(
                                    name = "updateQuestionResponse200Example",
                                    summary = "Question successfully updated",
                                    value = "{\n  \"resultRequest\": \"Вопрос успешно обновлён\",\n  \"status\": \"OK\",\n  \"question\": {\n    \"id\": 1,\n    \"question\": \"Updated question text\",\n    \"answers\": [\n      {\n        \"answer\": \"Updated answer\",\n        \"bestAnswer\": true\n      }\n    ],\n    \"user\": {\n      \"id\": 2,\n      \"username\": \"testUser\"\n    }\n  }\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Question not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "updateQuestionResponse404Example",
                                    summary = "Question not found",
                                    value = "{\n  \"resultRequest\": \"Вопрос не найден\",\n  \"status\": \"NOT_FOUND\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "updateQuestionResponse403Example",
                                    summary = "Forbidden",
                                    value = "{\n  \"resultRequest\": \"Доступ запрещён\",\n  \"status\": \"FORBIDDEN\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "updateQuestionResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public Mono<ResponseQuestion> updateQuestion(@PathVariable Long id, @RequestBody Question question, @CurrentUser User currentUser) {
        return questionService.updateQuestion(id, question, currentUser);
    }

    @Operation(
            summary = "Delete a question",
            description = "Deletes a question by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted question"),
            @ApiResponse(responseCode = "404", description = "Question not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "deleteQuestionResponse404Example",
                                    summary = "Question not found",
                                    value = "{\n  \"resultRequest\": \"Вопрос не найден\",\n  \"status\": \"NOT_FOUND\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "deleteQuestionResponse403Example",
                                    summary = "Forbidden",
                                    value = "{\n  \"resultRequest\": \"Доступ запрещён\",\n  \"status\": \"FORBIDDEN\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "deleteQuestionResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteQuestion(@PathVariable Long id, @CurrentUser User currentUser) {
        return questionService.deleteQuestion(id, currentUser);
    }
}
