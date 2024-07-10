package com.viancis.question.Service;


import Entities.Question.Answer;
import Entities.Question.Question;
import com.viancis.question.Repository.AnswerRepository;
import com.viancis.question.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import Response.ResponseQuestion;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public Mono<ResponseQuestion> createQuestion(Question question) {
        return Mono.fromCallable(() -> {
            if (question.getAnswer() != null && question.getAnswer().getId() == null) {
                Answer savedAnswer = answerRepository.save(question.getAnswer());
                question.setAnswer(savedAnswer);
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
                    existingQuestion.setAnswer(question.getAnswer());
                    Question updatedQuestion = questionRepository.save(existingQuestion);
                    ResponseQuestion response = new ResponseQuestion();
                    response.setResultRequest("Запрос успешно обновлён");
                    response.setStatus(HttpStatus.OK);
                    response.setQuestion(updatedQuestion);
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

