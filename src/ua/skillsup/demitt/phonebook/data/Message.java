package ua.skillsup.demitt.phonebook.data;

public enum Message {
    DATA_SAVED("Данные сохранены."),
    DATA_NOT_SAVED("Не удалось сохранить данные. Печалька:("),
    RECORD_CREATE("Создание новой записи"),
    RECORD_EDIT("Редактирование записи:"),
    RECORD_EXISTS("Такая запись уже существует."),
    NO_CONTACTS("Контактов нет."),
    EXISTS_CONTACTS_EDIT("Отредактируем существующие контакты."),
    ADD_NEW_CONTACTS("Добавим новые контакты."),
    BADFORMAT_FIRSTNAME("Имя не по формату."),
    BADFORMAT_LASTNAME("Фамилия не по формату."),
    BADFORMAT_NAMES_BOTH_EMPTY("Имя и фамилия не могут быть пустыми одновременно."),
    BADFORMAT_CONTACTVALUE("Значение не по формату."),
    BADFORMAT_LOGIN("Логин не по формату."),
    BADFORMAT_PASSWORD("Пароль не по формату."),
    BADFORMAT_PASSWORDS_NOT_EQUALS("Пароли не совпадают."),
    CONTACT_ALREADY_ADDED("Такой контакт уже добавлен в список."),
    CONTACT_EXISTS("Такой контакт уже есть в списке. Игнорирую правку контакта."),
    CONTACT_DELETED("Контакт удален."),
    RECORD_CREATED("Запись создана."),
    RECORD_EDITED("Запись отредактирована."),
    RECORD_DELETED("Запись удалена."),
    RECORD_DELETE_CANCELED("Удаление записи отменено."),
    ERROR_OCCURRED("Произошла ошибка. Паникуйте."),
    DATA_NOT_READ("Не удалось прочесть данные. Печалька:("),
    AUTH_NO("Авторизация не удалась."),
    REGISTER_YES("Регистрация прошла успешно."),
    LOGIN_EXISTS("Такой пользователь уже сущестует.")
    ;

    private final String value;

    Message(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
