package ua.skillsup.demitt.phonebook.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/*
Личность. Описывает человека.
Комбинация полей, дающая уникальность: firstName, lastName.
*/

public class Person implements Serializable {

    private String firstName;
    private String lastName;
    private LocalDate birthDate; //дата рождения; если не указана, будет null
    private String address; //адрес: если не указан, будет пустой строкой
    private String note; //заметка; если не указана, будет пустой строкой

    /*Минимальный конструктор для передачи фамилии и имени из метода.
    */
    public Person(String firstName, String lastName) {
        this(firstName, lastName, null, "", "");
    }

    public Person(String firstName, String lastName, LocalDate birthDate, String address, String note) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.note = note;
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public String getBirthDateFormatted() {
        if (this.birthDate == null) {
            return "";
        }
        DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return this.birthDate.format(dtf);
    }

    public String getNamesFormatted() {
        String separator = ("".equals(this.firstName) || "".equals(this.lastName)) ? "" : " ";
        return this.lastName + separator + this.firstName;
    }

    /*Расчет кол-ва полных лет.
    Если дата рождения не была указана, вернет -1.
    */
    public int getAge() {
        return this.birthDate == null ? -1 : Period.between(this.birthDate, LocalDate.now()).getYears();
    }

    public String getAddress() {
        return this.address;
    }

    public String getNote() {
        return this.note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (!firstName.equalsIgnoreCase(person.firstName)) return false;
        if (!lastName.equalsIgnoreCase(person.lastName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }

}
