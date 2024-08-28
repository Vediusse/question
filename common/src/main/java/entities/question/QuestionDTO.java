package entities.question;

import entities.comment.CommentDTO;
import entities.users.UserDTO;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.stream.Collectors;

public class QuestionDTO {

    @JsonProperty("public")
    private boolean isPublic;
    @JsonProperty("subject")
    private Subject subject;
    @JsonProperty("id")
    private Long id;

    @JsonProperty("labNumber")
    private Integer labNumber;

    @JsonProperty("description")
    private String description;
    @JsonProperty("question")
    private String question;
    @JsonProperty("user")
    private UserDTO user;
    @JsonProperty("answers")
    private Set<AnswerDTO> answers;
    @JsonProperty("comments")
    private Set<CommentDTO> comments;

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.question = question.getQuestion();
        this.user = new UserDTO(question.getUser());
        this.subject = question.getSubject();
        this.isPublic = question.isPublic();
        this.labNumber = question.getLabNumber();
        this.description = question.getDescription();
        this.answers = question.getAnswers().stream()
                .map(AnswerDTO::new)
                .collect(Collectors.toSet());
        this.comments = question.getComments().stream()
                .map(CommentDTO::new)
                .collect(Collectors.toSet());

    }

    @JsonCreator
    public QuestionDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("question") String question,
            @JsonProperty("user") UserDTO user,
            @JsonProperty("subject") Subject subject,
            @JsonProperty("answers") Set<AnswerDTO> answers,
            @JsonProperty("public") boolean isPublic,
            @JsonProperty("description") String description,
            @JsonProperty("labNumber") int lab_number,
            @JsonProperty("comments") Set<CommentDTO> comments) {
        this.id = id;
        this.question = question;
        this.subject = subject;
        this.user = user;
        this.isPublic = isPublic;
        this.description = description;
        this.labNumber = lab_number;
        this.answers = answers;
        this.comments = comments;
    }

    public QuestionDTO() {
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

    public void setComment(CommentDTO comment){
        this.comments.add(comment);
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
