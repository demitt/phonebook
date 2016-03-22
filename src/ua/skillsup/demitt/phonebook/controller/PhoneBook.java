package ua.skillsup.demitt.phonebook.controller;

import ua.skillsup.demitt.phonebook.data.Contact;
import ua.skillsup.demitt.phonebook.data.ContactType;
import ua.skillsup.demitt.phonebook.data.Field;
import ua.skillsup.demitt.phonebook.data.Message;
import ua.skillsup.demitt.phonebook.data.OptionKey;
import ua.skillsup.demitt.phonebook.data.Person;
import ua.skillsup.demitt.phonebook.data.Record;
import ua.skillsup.demitt.phonebook.data.SearchData;
import ua.skillsup.demitt.phonebook.data.SearchType;
import ua.skillsup.demitt.phonebook.exception.IllegalOptionKeyException;
import ua.skillsup.demitt.phonebook.exception.UnknownSearchTypeException;
import ua.skillsup.demitt.phonebook.io.Dialog;
import ua.skillsup.demitt.phonebook.io.Storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class PhoneBook {

    private final int uid;
    private int lastRecordId = 0;
    private Map<Integer, Record> records = new HashMap<>(); //основное хранилище записей
    private List<Record> recordSortedList = new ArrayList<>(); //вспомогательный список, хранящий отсортированные записи

    public PhoneBook(int uid, Map<Integer, Record> records) {
        this.uid = uid;
        this.records = records;
        if (this.records.size() != 0) {
            this.lastRecordId = Collections.max(this.records.keySet());
        }
        createRecordSortedList();
    }

    /*Отладочная ф-ция.
    */
    /*public Map<Integer, Record> tmpGetRecords() {
        return this.records;
    }*/

    /*Получение отсортированного списка записей.
    */
    private void createRecordSortedList() {
        this.recordSortedList = new ArrayList<>( this.records.values() );
        Collections.sort(this.recordSortedList);
    }


    public Map<Integer, Record> start() {
        outerWhile:
        while (true) {
            OptionKey answer = Dialog.mainMenu(this.records.size());
            switch (answer) {
                case KEY_1: //список
                    recordList();
                    break;
                case KEY_2: //поиск
                    search();
                    break;
                case KEY_3: //создание записи
                    createRecord();
                    break;
                /*case KEY_4: //экспорт/экспорт записей
                    Service.throwExc();
                    //
                    break;*/
                case KEY_4: //сохранение
                    boolean saved = saveData();
                    if (saved) {
                        Dialog.setMessage(Message.DATA_SAVED);
                    } else {
                        Dialog.setMessage(Message.DATA_NOT_SAVED);
                    }
                    break;
                case KEY_BACK: //выход
                    break outerWhile;
                default:
                    throw new IllegalOptionKeyException();
            }
        }

        return this.records;
    }


    //Список ***


    private void recordList(List<Record> records, boolean isSearch) {
        Dialog.recordList(records, isSearch); //вывод списка записей

        if (records.size() == 0) {
            return;
        }

        int recordNumber;
        while (true) {
            String stringRecordNumber = Dialog.getRecordByNumber();
            if (stringRecordNumber.equals(OptionKey.KEY_BACK.getValue())) { //был введен вариан "назад"
                return;
            }
            try {
                recordNumber = Integer.parseInt(stringRecordNumber) -1;
            }
            catch (NumberFormatException e) {
                //e.printStackTrace();
                continue;
            }
            if (recordNumber >= 0 && recordNumber < records.size()) { //номер годится
                break;
            }
        }

        /*boolean needGoBack =*/
        recordByNumber( records.get(recordNumber) );
        /*if (needGoBack) {
            return;
        }*/

    }

    private void recordList() {
        recordList(this.recordSortedList, false);
    }


    //Поиск ***


    private void search() {
        SearchData searchData;

        while (true) {
            OptionKey answer = Dialog.search();
            switch (answer) {
                case KEY_1: //имя/фамилия
                    searchData = searchByPersonName();
                    break;
                case KEY_2: //контакты
                    searchData = searchByContactValue();
                    break;
                case KEY_3: //прочее
                    searchData = searchByPersonOtherInfo();
                    break;
                case KEY_BACK: //назад
                    return;
                default:
                    throw new IllegalOptionKeyException();
            }
            if (!needGoBack(searchData)) { //пользователь НЕ выбрал возврат в предыдущее меню
                break;
            }
        }

        List<Record> records = search(searchData);
        recordList(records, true);
    }

    /*Проверяем, не отказался ли пользователь от поиска и не хочет ли он вернуться в предыдущее меню.
    */
    private boolean needGoBack(SearchData searchData) {
        return searchData.getType() == SearchType.GO_BACK;
    }

    private List<Record> search(SearchData searchData) {
        SearchType type = searchData.getType();
        String searchString = searchData.getString();
        int ageFrom = searchData.getAgeFrom();
        int ageTo = searchData.getAgeTo();

        List<Record> searchedRecords = new ArrayList<>();
        Person person;
        boolean addFlag;
        for (Record record : this.recordSortedList) {
            addFlag = false;

            switch (type) {
                case PERSON_NAME:
                    person = record.getPerson();
                    if ( isRightName(person, searchString) ) {
                        addFlag = true;
                    }
                    break;
                case CONTACT_VALUE:
                    if ( isRightContactList(record.getContacts(), searchString) ) {
                        addFlag = true;
                    }
                    break;
                case PERSON_AGE:
                    person = record.getPerson();
                    if ( isRightAge(person, ageFrom, ageTo) ) {
                        addFlag = true;
                    }
                    break;
                case PERSON_NOTE:
                    person = record.getPerson();
                    if ( isRightNote(person, searchString) ) {
                        addFlag = true;
                    }
                    break;
                default:
                    throw new UnknownSearchTypeException("Неизвестный тип поиска: " + type);
            }

            if (addFlag) {
                searchedRecords.add(record);
            }

        }

        return searchedRecords;
    }


    //Соответствие записи поисковому параметру ***

    private boolean isRightName(Person person, String searchString) {
        String firstName = person.getFirstName();
        String lastName = person.getLastName();
        return
            lastName != null && lastName.toLowerCase().contains(searchString) ||
            firstName != null && firstName.toLowerCase().contains(searchString)
            ;
    }

    private boolean isRightAge(Person person, int ageFrom, int ageTo) {
        int age = person.getAge();
        return age >= ageFrom && age <= ageTo;
    }

    private boolean isRightContactList(List<Contact> contacts, String searchString) {
        for (Contact contact : contacts) {
            if (
                ContactType.getSearchTypes().contains(contact.getType())
                //^ тип находится в списке тех, значения которых можно искать
                &&
                contact.getValue().contains(searchString)
            ) {
                return true;
            }
        }
        return false;
    }

    private boolean isRightNote(Person person, String searchString) {
        return person.getNote().toLowerCase().contains(searchString);
    }


    //Формирование объекта данных для выполнения поиска ***

    private SearchData searchByPersonOtherInfo() {
        OptionKey answer = Dialog.searchByPersonOtherInfo();
        SearchData searchData = new SearchData();
        switch (answer) {
            case KEY_1: //возраст
                searchData = searchByPersonAge();
                break;
            case KEY_2: //заметка
                searchData = searchByPersonNote();
                break;
            case KEY_3: //42
                searchData = searchBy42();
                break;
            case KEY_BACK: //назад
                break;
            default:
                throw new IllegalOptionKeyException();
        }

        return searchData;
    }

    private SearchData searchByPersonName() {
        String searchString;
        while (true) {
            searchString = Dialog.searchByPersonName();
            if (!searchString.isEmpty()) {
                break;
            }
        }
        return new SearchData(SearchType.PERSON_NAME, searchString);
    }

    private SearchData searchByContactValue() {
        String searchString;
        while (true) {
            searchString = Dialog.searchByContactValue();
            if (!searchString.isEmpty()) {
                break;
            }
        }
        return new SearchData(SearchType.CONTACT_VALUE, searchString);
    }

    private SearchData searchByPersonNote() {
        String searchString;
        while (true) {
            searchString = Dialog.searchByPersonNote();
            if (!searchString.isEmpty()) {
                break;
            }
        }
        return new SearchData(SearchType.PERSON_NOTE, searchString);
    }

    private SearchData searchByPersonAge() {
        int ageFrom = getAgeBound(true);
        int ageTo = getAgeBound(false);
        if ( ageTo == -1) { //вторая граница равна первой
            ageTo = ageFrom;
        } else if ( ageFrom > ageTo ) { //вторая граница больше первой
            //Просто поменяем их местами, а пользователю ничего не скажем:
            int tempAgeFrom = ageFrom;
            ageFrom = ageTo;
            ageTo = tempAgeFrom;
        }
        return new SearchData(SearchType.PERSON_AGE, ageFrom, ageTo);
    }

    private SearchData searchBy42() {
        Dialog.searchBy42(); //возвращаемый вариант нас не интересует:)
        return new SearchData(SearchType.GO_BACK, "");
    }

    /*Получение границы возраста для поиска по возрасту.
    Если верхняя (вторая) граница была оставлена пустой строкой, вернет -1.
    */
    private int getAgeBound(boolean isLowerBound) {
        int ageBound;
        while (true) {
            String ageBoundString = Dialog.searchByPersonAge(isLowerBound);
            try {
                if ("".equals(ageBoundString) && !isLowerBound) { //вторая граница оставлена пустой строкой
                    ageBound = -1;
                } else {
                    ageBound = Integer.parseInt(ageBoundString);
                    if (ageBound <= 0) {
                        continue;
                    }
                }
            }
            catch (NumberFormatException e) {
                continue;
            }
            break;
        }
        return ageBound;
    }


    //Отображение записи по ее порядковому номеру в текущем списке ***

    /*Детальное отображение записи.
    */
    private void recordByNumber(Record record) {
        OptionKey answer = Dialog.recordByNumber(record);
        switch (answer) {
            case KEY_1: //редактирование
                editRecord(record);
                //return true;
                break;
            case KEY_2: //удаление
                /*return*/ removeRecordWarning(record);
                break;
            case KEY_BACK: //назад
                break;
            default:
                throw new IllegalOptionKeyException();
        }

        //return; false;
    }


    //Создание/редактирование записи ***

    //Отладочный метод,
    /*public void tempCreateRecord(Record newRecord) {
        int recordId = this.getRecordId();
        this.records.put(recordId, newRecord);
        createRecordSortedList();
    }*/

    private void createRecord() {
        Dialog.setMessage(Message.RECORD_CREATE);

        //Получим имя и фамилию, запакованные в Person:
        Person person = getNames(null);

        //Поиск такой же записи:
        if ( sameRecordExists(new Record(person)) ) { //существует
            OptionKey answer = Dialog.editRecordInsteadOfCreating();
            switch (answer) {
                case KEY_1: //да
                    Record record = getRecordByPerson(person);
                    editRecord(record);
                    break;
                case KEY_BACK: //отмена
                    return;
            }
            return;
        }

        //Получим дату рождения:
        LocalDate birthDate = getBirthDate(null);

        //Получим прочие данные:
        String address = getAddress(null);
        String note = getNote(null);

        person = new Person(person.getFirstName(), person.getLastName(), birthDate, address, note);

        //Получим контакты:
        List<Contact> contactList = getContactList();
        Collections.sort(contactList);

        int id = getRecordId();
        Record newRecord = new Record(id, person, contactList);
        createRecord(newRecord);
        Dialog.setMessage(Message.RECORD_CREATED);
    }

    private void editRecord(Record record) {
        Dialog.setMessage(Message.RECORD_EDIT, record.getPerson().getNamesFormatted());

        //Получим имя и фамилию, запакованные в Person:
        Person person = getNames(record);

        removeRecord(record, false); //чтобы не нашли самого себя через sameRecordExists()

        //Поиск такой же записи:
        if ( sameRecordExists(new Record(person)) ) {
            Dialog.setMessage(Message.RECORD_EXISTS);
            return;
        }

        //Получим дату рождения:
        LocalDate birthDate = getBirthDate(record);

        //Получим прочие данные:
        String address = getAddress(record);
        String note = getNote(record);

        person = new Person(person.getFirstName(), person.getLastName(), birthDate, address, note);

        //Получим отредактированные контакты (если контакты вообще были):
        List<Contact> contactList = new ArrayList<>();
        if (record.getContacts().size() == 0) {
            Dialog.setMessage(Message.NO_CONTACTS);
        } else {
            Dialog.setMessage(Message.EXISTS_CONTACTS_EDIT);
            contactList = getContactList(record.getContacts());
        }
        //Возможность добавить новые контакты:
        Dialog.setMessage(Message.ADD_NEW_CONTACTS);
        List<Contact> addContactList = getContactList();
        if (addContactList.size() !=0) {
            contactList.addAll(addContactList);
        }
        Collections.sort(contactList);

        int id = record.getId();
        Record newRecord = new Record(id, person, contactList);
        createRecord(newRecord);
        Dialog.setMessage(Message.RECORD_EDITED);
    }

    /*Получение имени и фамилии.
    Вернет в виде Person, из к-рой можно выковырять имя и фамилию.
    */
    private Person getNames(Record record) {
        boolean isEdit = record != null;
        String firstName, lastName;
        outerWhile:
        while (true) {

            while (true) {
                lastName = Dialog.getName(Field.LAST_NAME, record);
                if (isEdit) {
                    lastName = newValueAfterEdit(record.getPerson().getLastName(), lastName);
                }
                if ( !Service.validateField(lastName, Field.LAST_NAME)) {
                    Dialog.setMessage(Message.BADFORMAT_FIRSTNAME);
                } else { //всё хорошо
                    lastName = Service.firstToUpperCase(lastName);
                    break;
                }
            }

            while (true) {
                firstName = Dialog.getName(Field.FIRST_NAME, record);
                if (isEdit) {
                    firstName = newValueAfterEdit(record.getPerson().getFirstName(), firstName);
                }
                if ( firstName.isEmpty() && lastName.isEmpty() ) { //имя и фамилия не указаны
                    Dialog.setMessage(Message.BADFORMAT_NAMES_BOTH_EMPTY);
                    break;
                } else if ( !Service.validateField(firstName, Field.FIRST_NAME) ) {
                    Dialog.setMessage(Message.BADFORMAT_LASTNAME);
                } else { //всё хорошо
                    firstName = Service.firstToUpperCase(firstName);
                    break outerWhile;
                }
            }

        }

        return new Person(firstName, lastName);
    }

    /*Поиск такой же записи среди имеющихся.
    */
    private boolean sameRecordExists(Record record) {
        return this.records.containsValue(record);
    }

    /*Поиск записи, которая гарантированно имеется в списке.
    Вернет запись.
    Если записи внезапно не оказалось, бросает исключение.
    */
    private Record getRecordByPerson(Person person) {
        Record record = new Record(person);
        for (Map.Entry<Integer, Record> entry : this.records.entrySet()) {
            if (entry.getValue().equals(record)) {
                return entry.getValue();
            }
        }
        throw new IllegalStateException("Не найдена запись: " + record.getPerson().getNamesFormatted() );
    }

    /*Получение даты рождения.
    Если не указана, вернет null.
    */
    private LocalDate getBirthDate(Record record) {
        boolean isEdit = record != null;
        LocalDate birthDate;
        while (true) {
            String birthDateString = Dialog.getDate(record, Field.BIRTH_DATE);
            if (isEdit) {
                birthDateString = newValueAfterEdit(record.getPerson().getBirthDate(), birthDateString);
            }
            try {
                if ("".equals(birthDateString)) { //дата оставлена пустой строкой
                    birthDate = null;
                } else {
                    birthDate = LocalDate.parse(birthDateString, Dialog.DATE_FORMATTER);
                }
            }
            catch (DateTimeParseException e) {
                continue;
            }
            break;
        }
        return birthDate;
    }

    private String getAddress(Record record) {
        String address = Dialog.getAdditionData(record, Field.ADDRESS);
        if (record != null) {
            address = newValueAfterEdit(record.getPerson().getAddress(), address);
        }
        return address;
    }

    private String getNote(Record record) {
        String note = Dialog.getAdditionData(record, Field.NOTE);
        if (record != null) {
            note = newValueAfterEdit(record.getPerson().getNote(), note);
        }
        return note;
    }

    /*Получение списка новых контактов.
    Список может быть пустым.
    */
    private List<Contact> getContactList() {
        List<Contact> contacts = new ArrayList<>();
        while (true) {
            //Получим тип контакта:
            OptionKey answer = Dialog.getContactType(null);
            if (answer == OptionKey.KEY_BACK) {
                break;
            }
            ContactType contactType = ContactType.getContactTypeByOptionKey(answer);

            //Получим значение контакта:
            String contactValue = Dialog.getContactValue(null);
            if (!Service.validateContactValue(contactValue, contactType)) {
                Dialog.setMessage(Message.BADFORMAT_CONTACTVALUE);
                continue;
            }

            Contact contact = new Contact(contactType, contactValue);
            if ( !contacts.contains(contact) ) {
                contacts.add(contact);
            } else {
                Dialog.setMessage(Message.CONTACT_ALREADY_ADDED);
            }
        }

        return contacts;
    }

    /*Получение списка отредактированных контактов.
    Список может оказаться пустым (если все контакты были удалены).
    */
    private List<Contact> getContactList(List<Contact> contacts) {
        List<Contact> oldContacts = new ArrayList<>(contacts);

        List<Contact> newContacts = new ArrayList<>();
        for (Contact currentContact : contacts) { //перебираем по неизменному списку-аргументу

            oldContacts.remove(currentContact); //чтобы не найти самого себя

            //Получим тип контакта:
            OptionKey answer = Dialog.getContactType(currentContact);
            if (answer == OptionKey.KEY_SKIP ) { //запрошен пропуск контакта
                newContacts.add(currentContact);
                continue;
            } else if (answer == OptionKey.KEY_BACK) { //запрошено удаление контакта
                Dialog.setMessage(Message.CONTACT_DELETED);
                continue;
            }
            ContactType contactType = ContactType.getContactTypeByOptionKey(answer);

            //Получим значение контакта:
            String contactValue = Dialog.getContactValue(currentContact);
            contactValue = newValueAfterEdit(currentContact.getValue(), contactValue);
            if (!Service.validateContactValue(contactValue, contactType)) { //не по формату
                Dialog.setMessage(Message.BADFORMAT_CONTACTVALUE); //сообщаем
                newContacts.add(currentContact); //оставляем прежнее значение
                continue;
            }

            Contact newContact = new Contact(contactType, contactValue);
            if (!oldContacts.contains(newContact)) { //не находим такой же
                newContacts.add(newContact); //добавляем новый
            } else {
                Dialog.setMessage(Message.CONTACT_EXISTS);
                newContacts.add(currentContact); //оставляем старый (т.е. игнорируем его изменение)
            }
        }

        return newContacts;
    }


    private void createRecord(Record record) {
        int recordId = record.getId();
        this.records.put(recordId, record);
        createRecordSortedList();
    }

    /*Получение первого свободного id для создания новой записи.
    Выход: id.
    */
    private int getRecordId() {
        return ++this.lastRecordId;
    }

    private String newValueAfterEdit(String oldValue, String newValue) {
        String value = newValue;
        if (Dialog.FOR_EMPTY_VALUE.equals(newValue)) {
            value = "";
        } else if ("".equals(newValue)) {
            value = oldValue;
        }
        return value;
    }

    private String newValueAfterEdit(LocalDate oldValue, String newValue) {
        String value = newValue;
        if (Dialog.FOR_EMPTY_VALUE.equals(newValue)) {
            value = "";
        } else if ("".equals(newValue)) {
            value = (oldValue == null) ? "" : Service.stringFromLocalDate(oldValue);
        }
        return value;
    }


    //Удаление записи ***

    private void removeRecordWarning(Record record) {
        OptionKey answer = Dialog.removeRecordWarning(record);
        switch (answer) {
            case KEY_1: //да
                removeRecord(record, true);
                Dialog.setMessage(Message.RECORD_DELETED);
                break; //return true;
            case KEY_BACK: //отмена
                Dialog.setMessage(Message.RECORD_DELETE_CANCELED);
                break; //return false;
            default:
                throw new IllegalOptionKeyException();
        }
    }

    private void removeRecord(Record record, boolean needSortFlag) {
        Record deleted = this.records.remove(record.getId());
        if (deleted == null) {
            throw new IllegalStateException(
                "Запись не удалена! " + record.getPerson().getBirthDateFormatted() + ", id = " + record.getId()
            );
        }
        if (needSortFlag) {
            createRecordSortedList();
        }
    }


    //Сохранение данных ***

    private boolean saveData() {
        return Storage.dataSave(this.uid, this.records);
    }



}
