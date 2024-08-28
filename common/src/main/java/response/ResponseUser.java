package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import entities.users.UserDTO;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser extends Response {


    private UserDTO obj;

    private List<UserDTO> objList;


    @JsonProperty("token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return obj;
    }

    public void setUser(UserDTO user) {
        this.obj = user;
    }

    public List<UserDTO> getObjList() {
        return objList;
    }

    public void setObjList(List<UserDTO> objList) {
        this.objList = objList;
    }
}

