package entities.question;

public enum Subject {

    INFORMATIK(1, "Информатика", 5),
    PROGA(1, "Программирование", 8),
    OPD(1, "ОПД", 7),
    DB(1, "Базы Данных", 4),
    WEB(2, "Веб-Программирование", 4),
    LP(2, "Языки Программирования", 5);

    private final String local;
    private final int year;
    private final int labCount; // количество лабораторных работ

    Subject(int year, String local, int labCount) {
        this.year = year;
        this.local = local;
        this.labCount = labCount;
    }

    public String getLocal() {
        return local;
    }

    public int getYear() {
        return year;
    }

    public int getLabCount() {
        return labCount;
    }
}
