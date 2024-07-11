package entities.users;

public class UserDTO {

    private String username;
    private Role role;

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.role = user.getRole();
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


}
