package entities.question;

import entities.comment.CommentDTO;
import entities.users.UserDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class AnswerDTO {

    private Long id;

    private String answer;

    private Integer rating;

    private UserDTO answerer;

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
}
