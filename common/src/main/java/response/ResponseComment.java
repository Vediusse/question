package response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import entities.comment.CommentDTO;
import org.springframework.http.HttpStatus;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseComment extends Response{

    @JsonProperty("resultRequest")
    private String resultRequest;

    @JsonProperty("status")
    private HttpStatus status;

    @JsonProperty("obj")
    private CommentDTO obj;

    @JsonProperty("objList")
    private List<CommentDTO> objList;

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

    public CommentDTO getObj() {
        return obj;
    }

    public void setObj(CommentDTO obj) {
        this.obj = obj;
    }

    public List<CommentDTO> getObjList() {
        return objList;
    }

    public void setObjList(List<CommentDTO> objList) {
        this.objList = objList;
    }
}

