package com.viancis.user.controller;

import annotation.CurrentUser;
import com.viancis.user.service.UserService;
import entities.users.User;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import response.ResponseError;
import response.ResponseUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Authenticate user or create model User in data base",
            description = "Register a user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(
                                    name = "authRequestExample",
                                    summary = "Example request for user authentication",
                                    value = "{\n  \"username\": \"test\",\n  \"password\": \"test\"\n}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully authenticated user and input user info",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseUser.class),
                            examples = @ExampleObject(
                                    name = "authResponse201Example",
                                    summary = "User successfully registered",
                                    value = "{\n  \"resultRequest\": \"Пользователь успешно зарегистрирован\",\n  \"status\": \"CREATED\",\n  \"user\": {\n    \"id\": 6,\n    \"username\": \"test\",\n    \"role\": {\n      \"name\": \"USER\",\n      \"local\": \"Пользователь\",\n      \"description\": \"Трудяга работяга\"\n    }\n  }\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "409", description = "User with this name already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "authResponse409Example",
                                    summary = "User already exists",
                                    value = "{\n  \"resultRequest\": \"Пользователь с таким именем уже существует\",\n  \"status\": \"CONFLICT\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "authResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @PostMapping("/auth")
    public Mono<ResponseUser> auth(@RequestBody User user) {
        return userService.auth(user);
    }

    @Operation(
            summary = "Log in a user and give a token",
            description = "Logs in a user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(
                                    name = "loginRequestExample",
                                    summary = "Example request for user login",
                                    value = "{\n  \"username\": \"test\",\n  \"password\": \"test\"\n}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Give a jwt token (Bearer + token)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseUser.class),
                            examples = @ExampleObject(
                                    name = "loginResponse200Example",
                                    summary = "JWT token provided",
                                    value = "{\n  \"resultRequest\": \"Успешный вход\",\n  \"status\": \"OK\",\n  \"token\": \"Bearer some-jwt-token\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "loginResponse401Example",
                                    summary = "Unauthorized",
                                    value = "{\n  \"resultRequest\": \"Неавторизованный доступ\",\n  \"status\": \"UNAUTHORIZED\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "loginResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @PostMapping("/login")
    public Mono<ResponseUser> login(@RequestBody User user) {
        return userService.login(user);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseUser.class),
                            examples = @ExampleObject(
                                    name = "getUserByIdResponse200Example",
                                    summary = "User found",
                                    value = "{\n  \"resultRequest\": \"Пользователь найден\",\n  \"status\": \"OK\",\n  \"user\": {\n    \"id\": 1,\n    \"username\": \"test\",\n    \"role\": {\n      \"name\": \"USER\",\n      \"local\": \"Пользователь\",\n      \"description\": \"Трудяга работяга\"\n    }\n  }\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "getUserByIdResponse404Example",
                                    summary = "User not found",
                                    value = "{\n  \"resultRequest\": \"Пользователь не найден\",\n  \"status\": \"NOT_FOUND\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "getUserByIdResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public Mono<ResponseUser> getUserById(@Parameter(description = "ID of the user to retrieve") @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Get all users", description = "Retrieves all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseUser.class),
                            examples = @ExampleObject(
                                    name = "getAllUsersResponse200Example",
                                    summary = "Users found",
                                    value = "[\n  {\n    \"resultRequest\": \"Пользователь найден\",\n    \"status\": \"OK\",\n    \"user\": {\n      \"id\": 1,\n      \"username\": \"test\",\n      \"role\": {\n        \"name\": \"USER\",\n        \"local\": \"Пользователь\",\n        \"description\": \"Трудяга работяга\"\n      }\n    }\n  }\n]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "getAllUsersResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @GetMapping
    public Mono<ResponseUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(
            summary = "Update user details",
            description = "Updates a user's details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(
                                    name = "updateUserRequestExample",
                                    summary = "Example request for updating user details",
                                    value = "{\n  \"username\": \"updatedUser\",\n  \"email\": \"updated@example.com\"\n}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseUser.class),
                            examples = @ExampleObject(
                                    name = "updateUserResponse200Example",
                                    summary = "User successfully updated",
                                    value = "{\n  \"resultRequest\": \"Пользователь успешно обновлен\",\n  \"status\": \"OK\",\n  \"user\": {\n    \"id\": 1,\n    \"username\": \"updatedUser\",\n    \"role\": {\n      \"name\": \"USER\",\n      \"local\": \"Пользователь\",\n      \"description\": \"Трудяга работяга\"\n    }\n  }\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "updateUserResponse404Example",
                                    summary = "User not found",
                                    value = "{\n  \"resultRequest\": \"Пользователь не найден\",\n  \"status\": \"NOT_FOUND\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "updateUserResponse403Example",
                                    summary = "Forbidden",
                                    value = "{\n  \"resultRequest\": \"Доступ запрещен\",\n  \"status\": \"FORBIDDEN\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "updateUserResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public Mono<ResponseUser> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @RequestBody User user,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        return userService.updateUser(id, user, currentUser);
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "deleteUserResponse404Example",
                                    summary = "User not found",
                                    value = "{\n  \"resultRequest\": \"Пользователь не найден\",\n  \"status\": \"NOT_FOUND\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "deleteUserResponse403Example",
                                    summary = "Forbidden",
                                    value = "{\n  \"resultRequest\": \"Доступ запрещен\",\n  \"status\": \"FORBIDDEN\"\n}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class),
                            examples = @ExampleObject(
                                    name = "deleteUserResponse500Example",
                                    summary = "API error",
                                    value = "{\n  \"resultRequest\": \"Ошибка API\",\n  \"status\": \"INTERNAL_SERVER_ERROR\"\n}"
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(
            @Parameter(description = "ID of the user to delete") @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser User currentUser) {
        return userService.deleteUser(id, currentUser);
    }

    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user's details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved current user",
                    content = @Content(schema = @Schema(implementation = ResponseUser.class))),
            @ApiResponse(responseCode = "500", description = "Some BIG java mistakes",
                    content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/me")
    public Mono<ResponseUser> getCurrentUser(@Parameter(hidden = true) @CurrentUser User currentUser) {
        return userService.getUserByCurrentUser(currentUser);
    }
}
