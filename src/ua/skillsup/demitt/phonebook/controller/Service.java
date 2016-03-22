package ua.skillsup.demitt.phonebook.controller;

import ua.skillsup.demitt.phonebook.data.ContactType;
import ua.skillsup.demitt.phonebook.data.Field;

import java.time.LocalDate;
import java.util.regex.Matcher;

public class Service {

    public static boolean validateContactValue(String value, ContactType type) {
        Matcher valueMatcher = type.getValueRegExpPattern().matcher(value);
        return valueMatcher.matches();
    }

    public static boolean validateField(String value, Field field) {
        return field.getValueRegExpPattern().matcher(value).matches();
    }

    public static String firstToUpperCase(String string) {
        if ("".equals(string)) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String stringFromLocalDate(LocalDate date) {
        String day, mount, year;
        String filler = "0";
        day = String.valueOf(date.getDayOfMonth());
        mount = String.valueOf(date.getMonthValue());
        year = String.valueOf(date.getYear());
        if (day.length() == 1) {
            day = filler + day;
        }
        if (mount.length() == 1) {
            mount = filler + mount;
        }
        return day + "." + mount + "." + year;
        //^ по хорошему надо получать требуемый формат строки из Dialog.DATE_FORMATTER
    }

    /*Отладочная ф-ция, которая помещается в места еще не реализованного функционала.
    Бросает исключение.
    */
    /*public static void throwExc() {
        throw new IllegalArgumentException("Данный функционал еще не реализован.");
    }*/

}
