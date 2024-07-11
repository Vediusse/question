package response;


import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseError extends Response{

    public ResponseError(String resultRequest, HttpStatus status) {
        this.setResultRequest(resultRequest);
        this.setStatus(status);
    }
}
