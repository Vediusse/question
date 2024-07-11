package com.viancis.user.controller;

import com.viancis.user.service.UserService;
import entities.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import response.ResponseUser;
import response.ResponseUserConfidence;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/auth")
    public Mono<ResponseUser> auth(@RequestBody User user) {
        return userService.auth(user);
    }

    @PostMapping("/login")
    public Mono<ResponseUser> login(@RequestBody User user) {
        return userService.login(user);
    }

    @GetMapping("/{id}")
    public Mono<ResponseUser> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public Flux<ResponseUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public Mono<ResponseUser> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/me")
    public Mono<ResponseUserConfidence> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return userService.getCurrentUser(token);
    }
}
