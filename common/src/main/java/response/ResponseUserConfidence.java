package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import entities.users.User;
import entities.users.UserDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUserConfidence extends Response{

    private User obj;

    private List<User> objList;

    private String token;




    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    public void setObjList(List<User> objList) {
        this.objList = objList;
    }



    public User getUser() {
        return obj;
    }

    public void setUser(User user) {
        this.obj = user;
    }


}
