package com.viancis.user.service;

import entities.users.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import response.ResponseUser;
import response.ResponseUserConfidence;

public interface UserService {
    Mono<ResponseUser> auth(User user);
    Mono<ResponseUser> login(User user);
    Mono<ResponseUser> updateUser(Long id, User user);
    Mono<ResponseUser> getUserById(Long id);
    Flux<ResponseUser> getAllUsers();
    Mono<Void> deleteUser(Long id);

    Mono<ResponseUserConfidence> getCurrentUser(String token);

}
