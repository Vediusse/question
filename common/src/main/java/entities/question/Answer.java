package entities.question;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Answer cannot be empty")
    private String answer;


    @NotEmpty(message = "Rating  cannot be empty")
    @JsonIgnore
    @Min(value = 0, message = "Rating must be more than 0")
    private Integer rating = 0;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonIgnore
    private Question question;

    @JsonIgnore
    private boolean isBestAnswer;

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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setBestAnswer(boolean bestAnswer) {
        isBestAnswer = bestAnswer;
    }

    public boolean isBestAnswer() {
        return isBestAnswer;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
