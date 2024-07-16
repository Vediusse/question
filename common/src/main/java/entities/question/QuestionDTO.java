package entities.question;

import entities.comment.CommentDTO;
import entities.users.UserDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class QuestionDTO {

    private final Long id;
    private String question;
    private UserDTO user;
    private Set<AnswerDTO> answers;

    private Set<CommentDTO> comments;

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.question = question.getQuestion();
        this.user = new UserDTO(question.getUser());
        this.answers = question.getAnswers().stream()
                .map(AnswerDTO::new)
                .collect(Collectors.toSet());
        this.comments = question.getComments().stream()
                .map(CommentDTO::new)
                .collect(Collectors.toSet());
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<AnswerDTO> answers) {
        this.answers = answers;
    }

    public Long getId() {
        return id;
    }

    public Set<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(Set<CommentDTO> comments) {
        this.comments = comments;
    }
}
