package entities.users;

public class RoleDTO {
    private String name;
    private String local;
    private String description;

    public RoleDTO() {
    }

    public RoleDTO(Role role) {
        this.name = role.name();
        this.local = role.getLocal();
        this.description = role.getDescription();
    }

    // Геттеры и сеттеры для всех полей
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

