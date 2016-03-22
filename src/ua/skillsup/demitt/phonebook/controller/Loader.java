package ua.skillsup.demitt.phonebook.controller;

import ua.skillsup.demitt.phonebook.data.Message;
import ua.skillsup.demitt.phonebook.data.Record;
import ua.skillsup.demitt.phonebook.io.Dialog;
import ua.skillsup.demitt.phonebook.io.Storage;

import java.util.Map;

public class Loader {

    public static void startApplication() {

        while (true) {

            //Вход:
            int uid = Authorizator.enter();
            switch(uid) {
                case -2: //пользователь выходит
                    return;
                case -1: //ошибка
                    Dialog.setMessage(Message.ERROR_OCCURRED);
                    continue;
                case 0: //не распознан/не зарегистрирован
                    continue;
                default: //передан uid
            }

            //Чтение данных:
            Map<Integer, Record> records = Storage.dataRead(uid);
            if (records == null) {//ошибка
                Dialog.setMessage(Message.DATA_NOT_READ);
                return;
            }

            //Телефонная книга:
            PhoneBook phoneBook = new PhoneBook(uid, records);
            records = phoneBook.start();

            //Сохранение данных:
            Storage.dataSave(uid, records);

            //Варианты: выйти или зайти под др. именем:
            boolean exitFlag = Authorizator.exit();
            if (exitFlag) {
                break;
            }

        }

    }

}
