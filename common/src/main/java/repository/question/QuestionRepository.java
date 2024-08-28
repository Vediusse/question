package repository.question;

import entities.question.Question;
import entities.question.Subject;
import entities.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findBySubjectAndLabNumberAndUserAndIsPublic(Subject subject, Integer labNumber, User user, boolean isPublic, Pageable pageable);
    Page<Question> findBySubjectAndUserAndIsPublic(Subject subject, User user, boolean isPublic, Pageable pageable);
    Page<Question> findByLabNumberAndUserAndIsPublic(Integer labNumber, User user, boolean isPublic, Pageable pageable);
    Page<Question> findByUserAndIsPublic(User user, boolean isPublic, Pageable pageable);

    // Методы для общедоступных вопросов
    Page<Question> findBySubjectAndLabNumberAndIsPublic(Subject subject, Integer labNumber, boolean isPublic, Pageable pageable);
    Page<Question> findBySubjectAndIsPublic(Subject subject, boolean isPublic, Pageable pageable);
    Page<Question> findByLabNumberAndIsPublic(Integer labNumber, boolean isPublic, Pageable pageable);
    Page<Question> findByIsPublic(boolean isPublic, Pageable pageable);
}
