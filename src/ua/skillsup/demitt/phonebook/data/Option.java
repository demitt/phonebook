package ua.skillsup.demitt.phonebook.data;

/*
Варианты ответов в диалогах, где необходимо выбрать один из предлагаемых вариантов.
*/

public enum Option {
    SEARCH("поиск"),
    LIST("список"),
    CREATE("создать"),
    EDIT("отредактировать"),
    DELETE("удалить"),
    SKIP("пропустить"),
    //EXPORT_IMPORT("экспорт/импорт"),
    SEARCH_PERSON_NAMES("имя/фамилия"),
    SEARCH_CONTACTS("номерА"),
    SEARCH_PERSON_OTHER("другая информация"),
    SEARCH_PERSON_AGE("возраст"),
    SEARCH_PERSON_NOTE("заметки"),
    SEARCH_MAIN_ANSWER("ответ на главный вопрос..."),

    YES("да"),
    CANCEL("отмена"),
    GO_BACK("назад"),
    NEXT("дальше"),
    SAVE("сохранить"),
    SAVE_AND_EXIT("сохранить и выйти"),
    //MAIN_MENU("назад в главное меню"),
    REGISTER("регистрация"),
    ENTER("вход"),
    EXIT("выйти"),
    ENTER_WITH_OTHER_LOGIN("зайти под другим именем"),

    CTYPE_MOBILE("мобильный"),
    CTYPE_URBAN("городской"),
    CTYPE_ICQ("ICQ"),
    CTYPE_SKYPE("skype"),
    CTYPE_EMAIL("e-mail")
    ;

    private final String value;

    Option(String value) {
        this.value = value;
    }

    public String getVal() {
        return this.value;
    }
}
