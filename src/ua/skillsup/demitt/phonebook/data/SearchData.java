package ua.skillsup.demitt.phonebook.data;

/*
Данные поиска.
*/

public class SearchData {
    private final SearchType type; //тип поиска
    private final String string; //искомая строка (для поиска по строке)
    private final int ageFrom; //нижняя граница возраста, включительно (для поиска по возрасту)
    private final  int ageTo; //верхняя граница возраста, включительно (для поиска по возрасту)

    //Конструктор для случая "искать передумали, просто хотим вернуться в предыдущее меню":
    public SearchData() {
        this(SearchType.GO_BACK, "");
    }

    public SearchData(SearchType type, String string) {
        this.type = type;
        this.string = string.toLowerCase();
        this.ageFrom = 0;
        this.ageTo = 0;
    }

    public SearchData(SearchType type, int ageFrom, int ageTo) {
        this.type = type;
        this.ageFrom = ageFrom;
        this.ageTo = ageTo;
        this.string = null;
    }

    public String getString() {
        return this.string;
    }

    public int getAgeFrom() {
        return this.ageFrom;
    }

    public int getAgeTo() {
        return this.ageTo;
    }

    public SearchType getType() {
        return this.type;
    }
}
