package ua.skillsup.demitt.phonebook.exception;

public class IllegalOptionKeyException extends RuntimeException {
    public IllegalOptionKeyException() {
        super("Неразрешенное значение OptionKey в текущем switch.");
    }
}
