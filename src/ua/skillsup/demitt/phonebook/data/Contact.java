package ua.skillsup.demitt.phonebook.data;

import java.io.Serializable;

/*
Контакт. Описывается парой "тип - строковое значение (номер)".
Комбинация полей, дающая уникальность: type, value.
*/

public class Contact implements Serializable , Comparable {

    private ContactType type;
    private String value;

    /*Конструктор контакта.
    При невалидном значении строки бросает IllegalContactValueException.
    */
    public Contact(ContactType type, String value) {
        this.type = type;
        this.value = value;
    }

    public ContactType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public int compareTo(Object obj) {
        Contact contactSecond = (Contact) obj;
        int compareResult =
            this.getType().getOrder() - contactSecond.getType().getOrder();
        if (compareResult == 0) {
            compareResult =
                this.getValue().compareTo( contactSecond.getValue() );
        }
        return compareResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (type != contact.type) return false;
        if (!value.equals(contact.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

}
