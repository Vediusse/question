package response;

import entities.Question.Question;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ResponseQuestion {

    private String resultRequest;
    private HttpStatus status;

    private Question question;

    private List<Question> questionList;

    public ResponseQuestion() {
    }

    public String getResultRequest() {
        return resultRequest;
    }

    public void setResultRequest(String resultRequest) {
        this.resultRequest = resultRequest;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
