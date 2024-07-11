package com.viancis.user.service;


import entities.users.UserDTO;
import exception.CustomAuthenticationException;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import repository.UserRepository;
import entities.users.Role;
import entities.users.User;
import filter.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import response.ResponseUser;
import response.ResponseUserConfidence;

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
                                return Mono.error(new CustomAuthenticationException("Аунтетификация зафейлена - инетересно а как это так случилось )"));
                            }
                        } catch (AuthenticationException e) {
                            return Mono.error(new CustomAuthenticationException("Аунтетификация зафейлена - инетересно а как это так случилось )"));
                        }
                    } else {
                        return Mono.error(new CustomAuthenticationException("Аунтетификация зафейлена - инетересно а как это так случилось )"));
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
    public Flux<ResponseUser> getAllUsers() {
        return Flux.defer(() -> Mono.fromCallable(() -> userRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(users -> Flux.fromIterable(users)
                        .map(user -> {
                            ResponseUser response = new ResponseUser();
                            response.setResultRequest("Получены все пользователи");
                            response.setStatus(HttpStatus.OK);
                            response.setUser(new UserDTO(user));
                            return response;
                        })
                ));
    }

    @Override
    public Mono<ResponseUser> updateUser(Long id, User user) {
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
    public Mono<Void> deleteUser(Long id) {
        return Mono.fromRunnable(() -> userRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }


    public Mono<ResponseUserConfidence> getUserByUsername(String username) {
        return Mono.fromCallable(() -> userRepository.findByUsername(username))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(user -> {
                    ResponseUserConfidence response = new ResponseUserConfidence();
                    response.setResultRequest("Пользователь успешно найден");
                    response.setStatus(HttpStatus.OK);
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    response.setUser(user);
                    return Mono.just(response);
                }).orElseGet(() -> {
                    ResponseUserConfidence response = new ResponseUserConfidence();
                    response.setResultRequest("Пользователь не найден");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    return Mono.just(response);
                }));
    }

    @Override
    public Mono<ResponseUserConfidence> getCurrentUser(String token) {
        if (token == null || token.isEmpty()) {
            return Mono.error(new CustomAuthenticationException("Токен не найден"));
        }
        try {
            String username = jwtTokenProvider.extractUsername(token);
            return getUserByUsername(username);
        } catch (SignatureException e) {
            return Mono.error(new CustomAuthenticationException("Ошибка при дешифровке токена: " + e.getMessage()));
        } catch (CustomAuthenticationException e) {
            return Mono.error(e);
        } catch (Exception e) {
            return Mono.error(new CustomAuthenticationException("Произошла ошибка при обработке токена: " + e.getMessage()));
        }
    }
}
