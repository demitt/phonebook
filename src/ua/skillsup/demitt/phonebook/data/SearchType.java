package ua.skillsup.demitt.phonebook.data;

/*
Типы поиска.
*/

public enum SearchType {
    PERSON_NAME, //фрагмент имени или фамилии
    CONTACT_VALUE, //фрагмент значения контакта (номера телефона, скайпа, ICQ etc)
    PERSON_AGE, //возрастной диапазон
    PERSON_NOTE, //заметки
    GO_BACK //возврат в предыдущее меню
}
