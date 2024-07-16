package com.viancis.user.service;

import entities.users.Role;
import entities.users.User;
import entities.users.UserDTO;
import exception.CustomAuthenticationException;
import filter.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import repository.user.UserRepository;
import response.ResponseUser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Mono<ResponseUser> auth(User user) {
        return Mono.fromCallable(() -> userRepository.findByUsername(user.getUsername()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(existingUser -> {
                    if (existingUser.isPresent()) {
                        ResponseUser response = new ResponseUser();
                        response.setResultRequest("Пользователь с таким именем уже существует");
                        response.setStatus(HttpStatus.CONFLICT);
                        return Mono.just(response);
                    } else {
                        return Mono.fromCallable(() -> {
                            user.setPassword(passwordEncoder.encode(user.getPassword()));
                            user.setRole(Role.USER);
                            User savedUser = userRepository.save(user);
                            ResponseUser response = new ResponseUser();
                            response.setResultRequest("Пользователь успешно зарегистрирован");
                            response.setStatus(HttpStatus.CREATED);
                            response.setUser(new UserDTO(savedUser));
                            return response;
                        }).subscribeOn(Schedulers.boundedElastic());
                    }
                })
                .onErrorResume(e -> {
                    ResponseUser errorResponse = new ResponseUser();
                    errorResponse.setResultRequest("Произошла ошибка: " + e.getMessage());
                    errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    return Mono.just(errorResponse);
                });
    }

    @Override
    public Mono<ResponseUser> login(User user) {
        return Mono.fromCallable(() -> {
                    try {
                        return userDetailsService.loadUserByUsername(user.getUsername());
                    } catch (UsernameNotFoundException e) {
                        throw new CustomAuthenticationException("Неверное имя пользователя или пароль");
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(userDetails -> {
                    if (passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
                        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
                        try {
                            Authentication authResult = authenticationManager.authenticate(authentication);
                            if (authResult.isAuthenticated()) {
                                String token = jwtTokenProvider.generateToken(userDetails);
                                ResponseUser response = new ResponseUser();
                                response.setResultRequest("Аутентификация успешна");
                                response.setStatus(HttpStatus.OK);
                                response.setToken(token);
                                return Mono.just(response);
                            } else {
                                return Mono.error(new CustomAuthenticationException("Аутентификация зафейлена"));
                            }
                        } catch (AuthenticationException e) {
                            return Mono.error(new CustomAuthenticationException("Аутентификация зафейлена"));
                        }
                    } else {
                        return Mono.error(new CustomAuthenticationException("Аутентификация зафейлена"));
                    }
                });
    }

    @Override
    public Mono<ResponseUser> getUserById(Long id) {
        return Mono.fromCallable(() -> userRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(user -> {
                    ResponseUser response = new ResponseUser();
                    response.setResultRequest("Пользователь успешно найден");
                    response.setStatus(HttpStatus.OK);
                    response.setUser(new UserDTO(user));
                    return Mono.just(response);
                }).orElseGet(() -> {
                    ResponseUser response = new ResponseUser();
                    response.setResultRequest("Пользователь не найден");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    return Mono.just(response);
                }));
    }

    @Override
    public Mono<ResponseUser> getAllUsers() {
        return Mono.fromCallable(() -> userRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(users -> {
                    List<UserDTO> userDTOs = users.stream()
                            .map(UserDTO::new)
                            .collect(Collectors.toList());
                    ResponseUser response = new ResponseUser();
                    response.setResultRequest("Получены все пользователи");
                    response.setStatus(HttpStatus.OK);

                    response.setObjList(userDTOs);
                    return Mono.just(response);
                })
                .onErrorResume(e -> {
                    ResponseUser errorResponse = new ResponseUser();
                    errorResponse.setResultRequest("Произошла ошибка: " + e.getMessage());
                    errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    return Mono.just(errorResponse);
                });
    }

    @Override
    public Mono<ResponseUser> updateUser(Long id, User user, User currentUser) {
        if ((currentUser.getRole().getLevel() < Role.ADMIN.getLevel()) || (Objects.equals(currentUser.getId(), id)) ) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для обновления пользователя"));
        }

        return Mono.fromCallable(() -> userRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    existingUser.setRole(user.getRole());

                    return Mono.fromCallable(() -> userRepository.save(existingUser))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(savedUser -> {
                                ResponseUser response = new ResponseUser();
                                response.setResultRequest("Пользователь успешно обновлён");
                                response.setStatus(HttpStatus.OK);
                                response.setUser(new UserDTO(savedUser));
                                return response;
                            });
                }).orElseGet(() -> {
                    ResponseUser response = new ResponseUser();
                    response.setResultRequest("Пользователь не найден");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    return Mono.just(response);
                }));
    }

    @Override
    public Mono<Void> deleteUser(Long id, User currentUser) {
        if ((currentUser.getRole().getLevel() != Role.ADMIN.getLevel()) || !(Objects.equals(currentUser.getId(), id))) {
            return Mono.error(new CustomAuthenticationException("Недостаточно прав для удаления пользователя"));
        }

        return Mono.fromRunnable(() -> userRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<ResponseUser> getUserByCurrentUser(User currentUser) {
        return Mono.defer(() -> {
            ResponseUser response = new ResponseUser();
            if (currentUser == null) {
                response.setResultRequest("Пользователь не найден");
                response.setStatus(HttpStatus.NOT_FOUND);
            } else {
                response.setResultRequest("Пользователь успешно найден");
                response.setStatus(HttpStatus.OK);
                response.setUser(new UserDTO(currentUser));
            }
            return Mono.just(response);
        });
    }
}
