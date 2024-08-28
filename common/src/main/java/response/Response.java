package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Response {


    @JsonProperty("resultRequest")
    private String resultRequest;

    @JsonProperty("status")
    private HttpStatus status;

    @JsonProperty("status")
    private Object obj;


    @JsonProperty("objList")
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
