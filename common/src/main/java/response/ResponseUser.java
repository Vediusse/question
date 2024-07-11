package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import entities.question.Question;
import entities.users.User;
import entities.users.UserDTO;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser extends Response{

    private UserDTO obj;

    private List<UserDTO> objList;

    private String token;




    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    public void setObjList(List<UserDTO> objList) {
        this.objList = objList;
    }



    public UserDTO getUser() {
        return obj;
    }

    public void setUser(UserDTO user) {
        this.obj = user;
    }


}
