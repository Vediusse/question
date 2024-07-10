package com.viancis.question.Repository;

import Entities.Question.Answer;
import Entities.Question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
