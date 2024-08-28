package entities.question;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import entities.comment.Comment;
import entities.users.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Answer cannot be empty")
    @Column(name = "answer", length = 10000)
    private String answer;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonBackReference(value = "question-answers")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-answers")
    private User user;

    @NotEmpty(message = "Rating cannot be empty")
    @JsonIgnore
    @Min(value = 0, message = "Rating must be more than 0")
    private Integer rating = 0;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "answer-comments")
    private Set<Comment> comments = new HashSet<>();

    private boolean bestAnswer;

    public Answer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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

    public boolean isBestAnswer() {
        return bestAnswer;
    }

    public void setBestAnswer(boolean bestAnswer) {
        this.bestAnswer = bestAnswer;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", answer='" + answer + '\'' +
                ", question=" + question +
                ", user=" + user +
                ", rating=" + rating +
                ", comments=" + comments +
                ", bestAnswer=" + bestAnswer +
                '}';
    }
}
