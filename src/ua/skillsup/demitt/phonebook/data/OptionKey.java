package ua.skillsup.demitt.phonebook.data;

/*
Варианты ответов (вводимые пользователем символы) в диалогах, где необходимо выбрать один из предлагаемых.
*/

public enum OptionKey {
    KEY_BACK("0"),
    KEY_1("1"),
    KEY_2("2"),
    KEY_3("3"),
    KEY_4("4"),
    KEY_5("5"),
    KEY_SKIP("*")
    ;

    private String value;

    OptionKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
