package ua.skillsup.demitt.phonebook.data;

import java.util.regex.Pattern;

public enum Field {
    FIRST_NAME(".{0,15}"),
    LAST_NAME(".{0,15}"),
    ADDRESS(".{0,100}"),
    NOTE(".{0,200}"),
    BIRTH_DATE(""), //проверка даты происходит при парсинге строки в дату

    CONTACT_TYPE(""), //валидность типа обеспечивается выбором варианта из предложенных
    CONTACT_VALUE(""), //RegExp для значения заданы в ContactType

    USER_LOGIN("[a-zA-Z\\d]{2,10}"),
    USER_PASSWORD("[a-zA-Z\\d]{2,10}"),
    USER_PASSWORD_2("") //то же, что и USER_PASSWORD
    ;

    private final Pattern valueRegExpPattern; //pattern для проверки валидности значения

    Field(String valueRegExpString) {
        this.valueRegExpPattern = Pattern.compile(valueRegExpString);
    }

    public Pattern getValueRegExpPattern() {
        if (this.valueRegExpPattern.toString().equals("")) {
            throw new IllegalStateException("Данное поле проверяется не через Field.valueRegExpPattern");
        }
        return this.valueRegExpPattern;
    }
}
