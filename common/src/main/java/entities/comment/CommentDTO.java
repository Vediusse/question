package entities.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import entities.users.UserDTO;

import java.util.Date;

public class CommentDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("createdAt")
    private Date createdAt;

    @JsonProperty("user")
    private UserDTO user;

    @JsonProperty("questionId")
    private Long questionId;

    @JsonProperty("answerId")
    private Long answerId;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.user = new UserDTO(comment.getUser());
        if (comment.getQuestion() != null) {
            this.questionId = comment.getQuestion().getId();
        }
        if (comment.getAnswer() != null) {
            this.answerId = comment.getAnswer().getId();
        }
    }

    public CommentDTO() {
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", user=" + user +
                ", questionId=" + questionId +
                ", answerId=" + answerId +
                '}';
    }
}

