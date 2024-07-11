package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import entities.users.User;
import org.springframework.http.HttpStatus;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Response {

    private String resultRequest;
    private HttpStatus status;

    private Object obj;

    private List<Object> objList;



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





}
