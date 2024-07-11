package com.viancis.question.service;


import com.viancis.question.repository.AnswerRepository;
import com.viancis.question.repository.QuestionRepository;
import entities.question.Answer;
import entities.question.Question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import response.ResponseQuestion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public Mono<ResponseQuestion> createQuestion(Question question) {
        return Mono.fromCallable(() -> {
            Set<Answer> answers = question.getAnswers();
            if (answers != null) {
                for (Answer answer : answers) {
                    answer.setQuestion(question);
                }
            }
            Question savedQuestion = questionRepository.save(question);
            ResponseQuestion response = new ResponseQuestion();
            response.setResultRequest("Вопрос успешно создан");
            response.setStatus(HttpStatus.CREATED);
            response.setQuestion(savedQuestion);
            return response;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ResponseQuestion> updateQuestion(Long id, Question question) {
        return Mono.fromCallable(() -> questionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(existingQuestion -> {
                    existingQuestion.setQuestion(question.getQuestion());

                    Set<Answer> newAnswers = question.getAnswers();
                    if (newAnswers != null) {
                        for (Answer answer : newAnswers) {
                            answer.setQuestion(existingQuestion);
                        }
                    } else {
                        existingQuestion.setAnswers(new HashSet<>());
                    }

                    Question savedQuestion = questionRepository.save(existingQuestion);
                    ResponseQuestion response = new ResponseQuestion();
                    response.setResultRequest("Вопрос успешно обновлён");
                    response.setStatus(HttpStatus.OK);
                    response.setQuestion(savedQuestion);
                    return Mono.just(response);
                }).orElseGet(() -> {
                    ResponseQuestion response = new ResponseQuestion();
                    response.setResultRequest("Вопрос не найден");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    return Mono.just(response);
                }));
    }

    @Override
    public Mono<ResponseQuestion> getQuestionById(Long id) {
        return Mono.fromCallable(() -> questionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(question -> {
                    ResponseQuestion response = new ResponseQuestion();
                    response.setResultRequest("Вопрос успешно найден");
                    response.setStatus(HttpStatus.OK);
                    response.setQuestion(question);
                    return Mono.just(response);
                }).orElseGet(() -> {
                    ResponseQuestion response = new ResponseQuestion();
                    response.setResultRequest("Вопрос не найден");
                    response.setStatus(HttpStatus.NOT_FOUND);
                    return Mono.just(response);
                }));
    }

    @Override
    public Flux<ResponseQuestion> getAllQuestions() {
        return Flux.defer(() -> Mono.fromCallable(() -> {
            List<Question> questions = questionRepository.findAll();
            ResponseQuestion response = new ResponseQuestion();
            response.setResultRequest("Получены все вопросы");
            response.setStatus(HttpStatus.OK);
            response.setQuestionList(questions);
            return response;
        }).subscribeOn(Schedulers.boundedElastic()));
    }

    @Override
    public Mono<Void> deleteQuestion(Long id) {
        return Mono.fromRunnable(() -> questionRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}


