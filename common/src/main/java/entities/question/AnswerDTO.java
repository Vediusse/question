package entities.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import entities.comment.CommentDTO;
import entities.users.UserDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class AnswerDTO {
    @JsonProperty("id")
    private Long id;


    @JsonProperty("answer")
    private String answer;


    @JsonProperty("rating")
    private Integer rating;


    @JsonProperty("answerer")
    private UserDTO answerer;


    @JsonProperty("comments")
    private Set<CommentDTO> comments;

    public AnswerDTO(Answer answer) {
        this.id = answer.getId();
        this.answer = answer.getAnswer();
        this.answerer = new UserDTO(answer.getUser());
        this.rating = answer.getRating();
        this.comments = answer.getComments().stream()
                .map(CommentDTO::new)
                .collect(Collectors.toSet());
    }

    public AnswerDTO() {
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

    public UserDTO getAnswerer() {
        return answerer;
    }

    public void setAnswerer(UserDTO answerer) {
        this.answerer = answerer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(Set<CommentDTO> comments) {
        this.comments = comments;
    }


    public void setComment(CommentDTO comment){
        this.comments.add(comment);
    }

    @Override
    public String toString() {
        return "AnswerDTO{" +
                "id=" + id +
                ", answer='" + answer + '\'' +
                ", rating=" + rating +
                ", answerer=" + answerer +
                ", comments=" + comments +
                '}';
    }
}
