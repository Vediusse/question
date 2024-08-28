package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import entities.question.AnswerDTO;
import entities.question.QuestionDTO;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAnswer extends Response{

    @JsonProperty("answer")
    private AnswerDTO obj;
    @JsonProperty("objList")
    private List<AnswerDTO> objList;

    public ResponseAnswer() {
    }


    public List<AnswerDTO> getObjList() {
        return objList;
    }

    public void setObjList(List<AnswerDTO> objList) {
        this.objList = objList;
    }

    public void setObj(AnswerDTO obj) {
        this.obj = obj;
    }

    public AnswerDTO getObj() {
        return obj;
    }
}
