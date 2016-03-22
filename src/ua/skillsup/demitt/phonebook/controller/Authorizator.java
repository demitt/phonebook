package ua.skillsup.demitt.phonebook.controller;

import ua.skillsup.demitt.phonebook.data.Field;
import ua.skillsup.demitt.phonebook.data.Message;
import ua.skillsup.demitt.phonebook.data.OptionKey;
import ua.skillsup.demitt.phonebook.data.UserAccount;
import ua.skillsup.demitt.phonebook.exception.IllegalOptionKeyException;
import ua.skillsup.demitt.phonebook.io.Dialog;
import ua.skillsup.demitt.phonebook.io.Storage;

import java.util.HashMap;
import java.util.List;

public class Authorizator {

    /*Начальное меню авторизации/входа.
    Выход:
        - -2: пользователь выходит;
        - -1: ошибка;
        - 0: пользователь не распознан/не зарегистрирован;
        - положительный int: авторизация прошла, передан uid.
    */
    public static int enter() {
        int result;
        while (true) {
            OptionKey answer =  Dialog.enter();
            switch (answer) {
                case KEY_1: //рег-ция
                    result = registration(); //варианты: -1, 0, int>0.
                    break;
                case KEY_2: //вход
                    result = auth(); //вар-ты: -1, 0, int>0.
                    break;
                case KEY_BACK: //выход
                    result = -2;
                    break;
                default:
                    throw new IllegalOptionKeyException();
            }
            if (result == 0) {
                continue;
            }
            break;
        }
        return result;
    }

    /*Авторизация пользователя.
    Выход:
        - -1: ошибка;
        - 0: пользователь не распознан;
        - положительный int: авторизация прошла, передан uid.
    */
    private static int auth() {
        List<UserAccount> accounts = Storage.getAccounts();
        if (accounts == null) {
            return -1;
        }
        String login = Dialog.getAccountData(Field.USER_LOGIN);
        String password = Dialog.getAccountData(Field.USER_PASSWORD);
        int uid = checkUserAccount(login, password, accounts);
        if (uid == 0) {
            Dialog.setMessage(Message.AUTH_NO);
        }
        return uid;
    }

    /*Регистрация пользователя.
    Выход:
        - -1: ошибка;
        - 0: регистрация не удалась;
        - положительный int: регистрация прошла, передан uid.
*/
    private static int registration() {
        List<UserAccount> accounts = Storage.getAccounts();
        if (accounts == null) {
            return -1;
        }

        //Получение логина и паролей, проверка:
        String login = Dialog.getAccountData(Field.USER_LOGIN);
        if (sameLoginExists(login, accounts)) {
            Dialog.setMessage(Message.LOGIN_EXISTS);
            return 0;
        }
        String passwordFirst = Dialog.getAccountData(Field.USER_PASSWORD);
        String passwordSecond = Dialog.getAccountData(Field.USER_PASSWORD_2);
        if (!Service.validateField(login, Field.USER_LOGIN)) {
            Dialog.setMessage(Message.BADFORMAT_LOGIN);
            return 0;
        }
        if (!passwordFirst.equals(passwordSecond)) {
            Dialog.setMessage(Message.BADFORMAT_PASSWORDS_NOT_EQUALS);
            return 0;
        }
        if (!Service.validateField(passwordFirst, Field.USER_PASSWORD)) {
            Dialog.setMessage(Message.BADFORMAT_PASSWORD);
            return 0;
        }

        //Получение нового uid:
        int uid = accounts.size() + 1;

        //Запись данных аккаунта в файл аккаунтов:
        boolean saveAccountResult = Storage.writeNewAccount(uid, login, passwordFirst);
        if (!saveAccountResult) {
            return 0;
        }

        //Создание файла данных:
        boolean createFileResult = Storage.dataSave(uid, new HashMap<>());
        if (!createFileResult) {
            return 0;
        }

        Dialog.setMessage(Message.REGISTER_YES);
        return uid;
    }

    /*Выйти или зайти под др. именем.
    Выход:
        - флаг выхода, true|false: выйти|зайтиПодДругимИменем
    */
    public static boolean exit() {
        OptionKey answer = Dialog.afterExit();
        switch(answer) {
            case KEY_1: //зайти под др. именем
                return false;
            case KEY_BACK: //выйти
                return true;
            default:
                throw new IllegalOptionKeyException();
        }
    }

    /*Поиск зарегистрированного пользователя по введенным логину и паролю.
    Если такой не найден, вернет 0. Иначе - вернет uid пользователя.
    */
    private static int checkUserAccount(String login, String password, List<UserAccount> accounts) {
        for (UserAccount account : accounts) {
            if (account.getLogin().equalsIgnoreCase(login) && account.getPassword().equals(password)) {
                return account.getId();
            }
        }
        return 0;
    }

    /*Поиск зарегистрированного пользователя по введенным логину.
    */
    private static boolean sameLoginExists(String login, List<UserAccount> accounts) {
        for (UserAccount account : accounts) {
            if (account.getLogin().equalsIgnoreCase(login)) {
                return true;
            }
        }
        return false;
    }


}
