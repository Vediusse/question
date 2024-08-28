package entities.question;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import entities.comment.Comment;
import entities.users.User;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Question cannot be empty")
    @Column(name = "question", length = 255, nullable = false)
    private String question;
    @Column(name = "description", length = 10000)
    private String description;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "question-answers")
    private Set<Answer> answers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "subject", nullable = false)
    private Subject subject;

    @Column(name = "public", nullable = false)
    private boolean isPublic = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-questions")
    private User user;


    @NotNull(message = "Lab number cannot be null")
    @Min(1)  // минимальное значение 1
    @Max(value = Integer.MAX_VALUE, message = "Lab number exceeds the maximum allowed")
    @Column(name = "labNumber", nullable = false)
    private Integer labNumber;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Comment> comments = new HashSet<>();

    public Question() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
        answer.setQuestion(this);
    }

    public void setBestAnswer(Answer bestAnswer) {
        for (Answer answer : answers) {
            if (answer.equals(bestAnswer)) {
                answer.setBestAnswer(true);
            } else {
                answer.setBestAnswer(false);
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }


    public Integer getLabNumber() {
        return labNumber;
    }

    public void setLabNumber(Integer labNumber) {
        validateLabNumber();
        this.labNumber = labNumber;
    }

    private void validateLabNumber() {
        if (subject != null && labNumber != null && labNumber > subject.getLabCount()) {
            throw new IllegalArgumentException("Lab number cannot exceed the total number of labs for the subject");
        }
    }
}