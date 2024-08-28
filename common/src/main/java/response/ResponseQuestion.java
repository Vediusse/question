package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import entities.question.Question;
import entities.question.QuestionDTO;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseQuestion extends Response {


    private QuestionDTO obj;
    private List<QuestionDTO> objList;

    public ResponseQuestion() {
    }

    public QuestionDTO getQuestion() {
        return obj;
    }

    public void setQuestion(QuestionDTO question) {
        this.obj = question;
    }

    public List<QuestionDTO> getQuestionList() {
        return objList;
    }

    public void setQuestionList(List<QuestionDTO> questionList) {
        this.objList = questionList;
    }
}
