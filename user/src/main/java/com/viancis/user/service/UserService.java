package com.viancis.user.service;

import entities.users.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import response.ResponseUser;
import response.ResponseUserConfidence;

public interface UserService {
    Mono<ResponseUser> auth(User user);

    Mono<ResponseUser> login(User user);

    Mono<ResponseUser> updateUser(Long id, User user, User currentUser);

    Mono<ResponseUser> getUserById(Long id);

    Mono<ResponseUser> getAllUsers();

    Mono<Void> deleteUser(Long id, User currentUser);

    Mono<ResponseUser> getUserByCurrentUser(User currentUser);

}
