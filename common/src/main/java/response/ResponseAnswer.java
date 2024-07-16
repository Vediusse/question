package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import entities.question.AnswerDTO;
import entities.question.QuestionDTO;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAnswer extends Response{

    private AnswerDTO obj;

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
