package response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import entities.question.PaginatedQuestion;
import entities.question.QuestionDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePaginatedQuestion extends Response{
    private List<QuestionDTO> questionList;
    private int currentPage;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public ResponsePaginatedQuestion() {
    }

    public List<QuestionDTO> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuestionDTO> questionList) {
        this.questionList = questionList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}


