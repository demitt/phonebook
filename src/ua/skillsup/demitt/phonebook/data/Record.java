package ua.skillsup.demitt.phonebook.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
Запись телефонной книги. Состоит из уникального номера, Person и списка контактов.
Комбинация полей, дающая уникальность: person.
Этот уникальный номер будет являться ключом в мапе контактов.
*/

public class Record implements Serializable, Comparable {
    private int id;
    private Person person;
    private List<Contact> contacts = new ArrayList<>();

    public Record(int id, Person person, List<Contact> contacts) {
        this.id = id;
        this.person = person;
        this.contacts = contacts;
    }

    /*Минимальный конструктор для создания уникальной записи.
    Используется при поиске такой же записи среди имеющихся.
    Не предназначен для создания записи, которая будет сохранена в телефонной книге.
    */
    public Record(Person person) {
        this(0, person, null);
    }

    public Person getPerson() {
        return this.person;
    }

    public int getId() {
        return this.id;
    }

    public List<Contact> getContacts() {
        return this.contacts;
    }

    @Override
    public int compareTo(Object obj) {
        Person first = this.getPerson();
        Person second = ((Record) obj).getPerson();
        int compareResult = first.getLastName().compareTo(second.getLastName());
        if (compareResult == 0) {
            compareResult = first.getFirstName().compareTo(second.getFirstName());
        }
        return compareResult;
        //return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (!person.equals(record.person)) return false;

        return true;
    }
    @Override
    public int hashCode() {
        return person.hashCode();
    }
}
