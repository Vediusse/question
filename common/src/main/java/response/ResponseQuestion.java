package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import entities.question.Question;
import org.springframework.http.HttpStatus;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseQuestion extends Response{
    private Question obj;

    private List<Question> objList;

    public ResponseQuestion() {
    }

    public Question getQuestion() {
        return obj;
    }

    public void setQuestion(Question question) {
        this.obj = question;
    }

    public List<Question> getQuestionList() {
        return objList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.objList = questionList;
    }
}
