package ua.skillsup.demitt.phonebook.io;

import ua.skillsup.demitt.phonebook.data.Contact;
import ua.skillsup.demitt.phonebook.data.ContactType;
import ua.skillsup.demitt.phonebook.data.Field;
import ua.skillsup.demitt.phonebook.data.Message;
import ua.skillsup.demitt.phonebook.data.Option;
import ua.skillsup.demitt.phonebook.data.QuestionWithOptions;
import ua.skillsup.demitt.phonebook.data.QuestionWithText;
import ua.skillsup.demitt.phonebook.data.Person;
import ua.skillsup.demitt.phonebook.data.OptionKey;
import ua.skillsup.demitt.phonebook.data.Record;
import ua.skillsup.demitt.phonebook.exception.IllegalFieldException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
Ведение диалога с пользователем (в данном случае - через консоль).
*/

public class Dialog {

    //При ред-нии записи этот символ указывает, что новым значением строки будет пустая строка
    public static final String FOR_EMPTY_VALUE = "*";
    //DateTimeFormatter для вводимых пользователем дат:
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    //Визуальное описание фломата ввода дат пользователем; должно соответствовать полю DATE_FORMATTER:
    public static final String DATE_FORMAT_STRING = "ДД.ММ.ГГГГ";


    /*Главное меню с кол-вом записей и вариантами основных действий.
    */
    public static OptionKey mainMenu(int recordCount) {
        String header = recordCount == 0 ? "У Вас еще нет ни одной записи." : "Записей: " + recordCount + ".";
        QuestionWithOptions question = new QuestionWithOptions(
                header,
                Option.SAVE_AND_EXIT,
                Arrays.asList(OptionKey.KEY_1, OptionKey.KEY_2, OptionKey.KEY_3, OptionKey.KEY_4),
                Arrays.asList(Option.LIST, Option.SEARCH, Option.CREATE, Option.SAVE)
        );
        return askAQuestion(question);
    }

    /*Список записей.
    */
    public static void recordList(List<Record> records, boolean isSearch) {
        printMessage(createRecordList(records, isSearch));
    }

    /*Детальный просмотр записи.
    */
    public static OptionKey recordByNumber(Record record) {
        StringBuilder sb = new StringBuilder("Детальный просмотр записи");

        Person person = record.getPerson();
        String names = person.getNamesFormatted();
        int age = person.getAge();
        String note = person.getNote();
        String address = person.getAddress();
        String birthDate = person.getBirthDateFormatted();

        sb.
            append("\n").
            append(names);
        if (age != -1) {
            sb.append(", возраст ").append(age);
        }
        sb.append(createContactList(record))
        ;

        //Прочие данные:
        if ( !birthDate.isEmpty() ) {
            sb.append("\nдата рождения: ").append(birthDate);
        }
        if ( !note.isEmpty() ) {
            sb.append("\nзаметка: ").append(note);
        }
        if ( !address.isEmpty() ) {
            sb.append("\nадрес: ").append(address);
        }

        printMessageLn(sb);

        QuestionWithOptions question = new QuestionWithOptions(
            "",
            Option.GO_BACK,
            Arrays.asList(OptionKey.KEY_1, OptionKey.KEY_2),
            Arrays.asList(Option.EDIT, Option.DELETE)
        );

        return askAQuestion(question);
    }

    public static OptionKey search() {
        QuestionWithOptions question = new QuestionWithOptions(
            "Что будем искать?",
            Option.GO_BACK,
            Arrays.asList(OptionKey.KEY_1, OptionKey.KEY_2, OptionKey.KEY_3),
            Arrays.asList(
                Option.SEARCH_PERSON_NAMES,
                Option.SEARCH_CONTACTS,
                Option.SEARCH_PERSON_OTHER
                )
        );
        return askAQuestion(question);
    }

    public static OptionKey getContactType(Contact contact) {
        Option goBackOption = Option.NEXT;
        List<OptionKey> optionKeys = new ArrayList<>(ContactType.getOptionKeys());
        List<Option> options = new ArrayList<>(ContactType.getOptions());
        if (contact != null) { //редактирование
            goBackOption = Option.DELETE;
            optionKeys.add(OptionKey.KEY_SKIP);
            options.add(Option.SKIP);
        }

        String message = "Укажите тип контакта" + additionalQuestionPart(contact, Field.CONTACT_TYPE);
        QuestionWithOptions question = new QuestionWithOptions(
            message,
            goBackOption,
            optionKeys,
            options
        );
        return askAQuestion(question);
    }

    public static OptionKey searchByPersonOtherInfo() {
        QuestionWithOptions question = new QuestionWithOptions(
            "Какую именно информацию будем искать?",
            Option.GO_BACK,
            Arrays.asList(OptionKey.KEY_1, OptionKey.KEY_2, OptionKey.KEY_3),
            Arrays.asList(Option.SEARCH_PERSON_AGE, Option.SEARCH_PERSON_NOTE, Option.SEARCH_MAIN_ANSWER)
        );
        return askAQuestion(question);
    }

    public static OptionKey searchBy42() {
        QuestionWithOptions question = new QuestionWithOptions(
            "Вы ищите Ответ на Главный Вопрос Жизни, Вселенной и Всего Такого.\nОтвет найден: 42.",
            Option.GO_BACK,
            Arrays.asList(),
            Arrays.asList()
        );

        return askAQuestion(question);
    }

    public static String searchByPersonName() {
        QuestionWithText question = new QuestionWithText("Введите имя или фамилию (целиком или часть)");
        return askAQuestion(question);
    }

    public static String searchByContactValue() {
        QuestionWithText question = new QuestionWithText("Введите номер контакта (целиком или часть)");
        return askAQuestion(question);
    }

    public static String searchByPersonAge(boolean isLowerBound) {
        String boundDescribe = isLowerBound ?
                "первую границу диапазона" : "вторую границу диапазона (оставьте пустой, если она равна первой)" ;
        QuestionWithText question = new QuestionWithText("Введите " + boundDescribe);
        return askAQuestion(question);
    }

    public static String searchByPersonNote() {
        QuestionWithText question = new QuestionWithText("Введите заметку (целиком или часть)");
        return askAQuestion(question);
    }

    public static String getRecordByNumber() {
        QuestionWithText question = new QuestionWithText(
            "Введите № для просмотра/редактирования/удаления или " +
            OptionKey.KEY_BACK.getValue() +
            " - для возврата назад."
        );
        return askAQuestion(question);
    }

    public static String getName(Field field, Record record) {
        String name;
        switch(field) {
            case LAST_NAME:
                name = "фамилию";
                break;
            case FIRST_NAME:
                name = "имя";
                break;
            default:
                throw new IllegalFieldException("Недопустимое значение Field: " + field);
        }
        String message = "Введите " + name + additionalQuestionPart(record, field);
        QuestionWithText question = new QuestionWithText(message);
        return askAQuestion(question);
    }

    /*Получение доп. строковой информации (она м.б. пустой строкой).
    */
    public static String getAdditionData(Record record, Field field) {
        String describe;
        switch(field) {
            case ADDRESS:
                describe = "адрес";
                break;
            case NOTE:
                describe = "заметку";
                break;
            default:
                throw new IllegalFieldException("Недопустимое значение Field: " + field);
        }
        String message = "Введите " + describe + additionalQuestionPart(record, field);
        QuestionWithText question = new QuestionWithText(message);
        return askAQuestion(question);
    }

    /*Получение дат.
    В данный момент это только дата рождения.
    */
    public static String getDate(Record record, Field field) {
        String describe;
        switch(field) {
            case BIRTH_DATE:
                describe = "дату рождения";
                break;
            default:
                throw new IllegalFieldException("Недопустимое значение Field: " + field);
        }
        String message =
            "Введите " + describe + " в формате " + DATE_FORMAT_STRING +
            additionalQuestionPart(record, field);
        QuestionWithText question = new QuestionWithText(message);
        return askAQuestion(question);
    }

    public static String getContactValue(Contact contact) {
        String message = "Введите значение" + additionalQuestionPart(contact, Field.CONTACT_VALUE);
        QuestionWithText question = new QuestionWithText(message);
        return askAQuestion(question);
    }

    /*Доп. часть вопроса при получении строки от пользователя.
    Используется при редактировании записи.
    */
    private static String additionalQuestionPart(Record record, Field field) {
        String message = "";
        if (record != null) {
            String oldValue;
            Person person = record.getPerson();
            switch (field) {
                case FIRST_NAME:
                    oldValue = person.getFirstName();
                    break;
                case LAST_NAME:
                    oldValue = person.getLastName();
                    break;
                case BIRTH_DATE:
                    oldValue = person.getBirthDateFormatted();
                    break;
                case NOTE:
                    oldValue = person.getNote();
                    break;
                case ADDRESS:
                    oldValue = person.getAddress();
                    break;
                default:
                    throw new IllegalFieldException("Недопустимое значение Field: " + field);
            }
            if ("".equals(oldValue)) {
                oldValue = "отсутствует";
            }
            message = ". Прежнее значение: " + oldValue + "\n" +
                "Чтобы не менять, нажмите Enter. " +
                "Чтобы удалить значение, введите " + FOR_EMPTY_VALUE;
        }
        return message;
    }

    /*Доп. часть вопроса при получении данных контакта от пользователя.
    Используется при редактировании записи.
    */
    private static String additionalQuestionPart(Contact contact, Field field) {
        String message = "";
        String addMessage = "";
        if (contact != null) {
            switch (field) {
                case CONTACT_TYPE:
                    break;
                case CONTACT_VALUE:
                    addMessage = "\nЧтобы не менять, нажмите Enter.";
                    break;
                default:
                    throw new IllegalFieldException("Недопустимое значение Field: " + field);
            }
            String oldValue = contact.getType().getOption().getVal() + " " +  contact.getValue();
            message = ". Прежние тип и значение контакта: " + oldValue + "." + addMessage;
        }
        return message;
    }

    public static OptionKey enter() {
        QuestionWithOptions question = new QuestionWithOptions(
                "Войдите или зарегистрируйтесь.",
                Option.EXIT,
                Arrays.asList(OptionKey.KEY_1, OptionKey.KEY_2),
                Arrays.asList(Option.REGISTER, Option.ENTER)
        );
        return askAQuestion(question);
    }

    public static String getAccountData(Field field) {
        String accountData;
        switch (field) {
            case USER_LOGIN:
                accountData = "логин";
                break;
            case USER_PASSWORD:
                accountData = "пароль";
                break;
            case USER_PASSWORD_2:
                accountData = "пароль еще раз";
                break;
            default:
                throw new IllegalFieldException("Недопустимое значение Field: " + field);
        }
        String message = "Введите " + accountData + " (латиница, цифры - от 2 до 10 символов)";
        QuestionWithText question = new QuestionWithText(message);
        return askAQuestion(question);
    }

    public static OptionKey afterExit() {
        QuestionWithOptions curAnsOpt = new QuestionWithOptions(
            "Данные сохранены. Чего изволите?",
            Option.EXIT,
            Arrays.asList(OptionKey.KEY_1),
            Arrays.asList(Option.ENTER_WITH_OTHER_LOGIN)
        );
        return askAQuestion(curAnsOpt);
    }

    public static void setMessage(Message message, String addString) {
        String allMessage = message.getValue() + ( addString == null ? "" : (" " + addString)  ) ;
        printMessage(allMessage);
    }

    public static void setMessage(Message message) {
        setMessage(message, null);
    }

    public static OptionKey removeRecordWarning(Record record) {
        String names = record.getPerson().getNamesFormatted();
        QuestionWithOptions question = new QuestionWithOptions(
            names + ": эта запись будет удалена. Вы уверены?",
            Option.CANCEL,
            Arrays.asList(OptionKey.KEY_1),
            Arrays.asList(Option.YES)
        );
        return askAQuestion(question);
    }

    public static OptionKey editRecordInsteadOfCreating() {
        QuestionWithOptions question = new QuestionWithOptions(
            "Такая запись уже существует. Желаете ее отредактировать?",
            Option.CANCEL,
            Arrays.asList(OptionKey.KEY_1),
            Arrays.asList(Option.YES)
        );
        return askAQuestion(question);
    }


    //Вывод сообщений ***

    private static void printMessage(String message) {
        System.out.println(message);
    }

    private static void printMessageLn(StringBuilder messageSB) {
        printMessage(messageSB.toString());
    }


    //Диалоги "вопрос-ответ" ***

    private static String askAQuestion(QuestionWithText question) {
        String questionString = question.getQuestion();
        StringBuilder out = new StringBuilder(questionString).
            append("\n").append(createHorizontalLine());
        printMessageLn(out);
        return getString();
    }

    private static OptionKey askAQuestion(QuestionWithOptions question) {
        String questionString = question.getQuestion();
        Option goBackOption = question.getGoBackOption();
        List<OptionKey> optionKeys = new ArrayList<>(question.getOptionKeys());
        List<Option> options = new ArrayList<>(question.getOptions());

        int keysCount = optionKeys.size();

        if (keysCount != options.size()) {
            throw new IllegalArgumentException("Количество ключей ответов не совпадает с кол-вом значений. Вопрос: " + questionString);
        }

        //Добавим в конец списка вариант ответа, соответствующий goBackOption
        //(обычно это "назад" или нечто подобное), ему всегда будет соответствовать OptionKey.KEY_BACK
        optionKeys.add(OptionKey.KEY_BACK);
        options.add(goBackOption);
        keysCount++;

        //Сформируем список из валидных вариантов ответов:
        List<String> stringKeys = new ArrayList<>();
        for (OptionKey curKey : optionKeys) {
            stringKeys.add(curKey.getValue());
        }

        //Если вопрос не пуст, напечатаем его:
        if (!questionString.isEmpty()) {
            printMessage(questionString);
        }

        //Сформируем список вариантов ответа:
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < keysCount; i++) {
            out.
                append(optionKeys.get(i).getValue()).
                append(") ").
                append(options.get(i).getVal()).
                append("   ");
        }
        out.append("\n").append(createHorizontalLine());

        while (true) {
            printMessageLn(out);
            String key = getString();

            int indexOfKey = stringKeys.indexOf(key);
            if (indexOfKey != -1) {
                return optionKeys.get(indexOfKey);
            }
        }

    }


    //Формирование списков ***

    /*Формирование списка записей (полный или по результатам поиска).
    */
    private static String createRecordList(List<Record> records, boolean isSearch) {
        int size = records.size();
        String header = isSearch ? "Результаты поиска" : "Записи";
        StringBuilder sb = new StringBuilder(header).append(" (").append(size).append(" шт.)");
        String names, ageString;
        int age, contactsCount;
        Record record;
        Person person;
        for (int i = 0; i < size; i++) {
            record = records.get(i);
            person = record.getPerson();
            names = person.getNamesFormatted();
            contactsCount = record.getContacts().size();
            age = person.getAge();
            ageString = (age == -1) ? "неизвестен" : String.valueOf(age);
            sb.
                append("\n").
                append(i + 1).append(". ").
                append(names).
                append(" (возраст ").append(ageString).append(", контактов ").append(contactsCount).append(")")
            ;
        }
        return sb.toString();
    }

    /*Формирование списка контактов записи.
    */
    private static String createContactList(Record record) {
        StringBuilder sb = new StringBuilder();

        for (Contact contact : record.getContacts()) {
            sb.append("\n").append(contact.getType().getDescribe()).append(": ").append(contact.getValue());
        }

        return sb.toString();
    }

    private static String getString() {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine().trim();
    }

    private static String createHorizontalLine() {
        return "----------------------------------------------------------------------";
    }

}
