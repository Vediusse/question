package entities.users;

public enum Role {
    USER(1, "Пользователь", "Трудяга работяга"),
    MODERATOR(2, "Модератище", "Модерирует вопросы и ответы"),
    ADMIN(3, "Железный человек", "Назначают модераторов");

    private final int level;



    private final String local;



    private final String description;

    Role(int level, String local, String description) {
        this.level = level;
        this.local = local;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getLocal() {
        return local;
    }

    public String getDescription() {
        return description;
    }
}