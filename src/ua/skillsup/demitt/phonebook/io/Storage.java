package ua.skillsup.demitt.phonebook.io;

import ua.skillsup.demitt.phonebook.data.Record;
import ua.skillsup.demitt.phonebook.data.UserAccount;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/*
Обмен данными с хранилищем (в данном случае - с файлами).
*/

public class Storage {

    private static final String ACCOUNTFILE_DELIMETER = ";"; //разделитель полей в ACCOUNTS_FILE
    private static final String DATABASE_DIR = "DB"; //папка "базы данных"
    private static final String PHBS_DIR = "phonebooks"; //папка с файлами данных; находится внутри DATABASE_DIR
    private static final String ACCOUNTS_FILE = "accounts.dat"; //файл аккаунтов пользователей; находится в DATABASE_DIR
    private static final String PHB_FILEEXT = "ser"; //расширение файлов данных; файлы находятся в PHBS_DIR


    /*Получение списка аккаунтов пользователей.
    При ошибке вернет null.
    */
    public static List<UserAccount> getAccounts() {
        String path = getAccountFileName();
        List<String> strings = readAccountsFile();
        if (strings == null) {
            return null;
        }
        List<UserAccount> accounts = new ArrayList<>();
        String[] elements;
        String login, password;
        int uid;
        for (String currentString : strings) {
            /*if (currentString.trim().equals("")) {
                continue;
            }*/
            elements = currentString.split(ACCOUNTFILE_DELIMETER);
            /*if (elements.length !=3) { //д.б. 3 эл-та
                return null;
            }*/
            try {
                uid = Integer.parseInt(elements[0]);
            }
            catch (NumberFormatException e) { //uid д.б. int-ом
                //e.printStackTrace();
                return null;
            }
            /*if (uid<=0) { //uid д.б. >0
                return null;
            }*/
            login = elements[1];
            password = elements[2];
            accounts.add( new UserAccount(uid, login, password) );
        }
        return accounts;
    }

    /*Чтение файла с данными аккаунтов.
    При ошибке вернет null.
    */
    private static List<String> readAccountsFile() {
        List<String> strings = new ArrayList<>();
        String path = getAccountFileName();
        try ( Scanner sc = new Scanner(new FileReader(path)) ) {
            while (true) {
                if (sc.hasNextLine()) {
                    strings.add(sc.nextLine());
                } else {
                    break;
                }
            }
        }
        catch (IOException e) {
            strings = null;
            //e.printStackTrace();
        }
        return strings;
    }

    /*Запись текстового файла.
    */
    private static boolean writeTextFile(String path, List<String> strings) {
        String[] stringsArr = strings.toArray(new String[strings.size()]);
        String dataString = String.join("\n", stringsArr);
        try ( BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(dataString);
        }
        catch (IOException e) {
            //e.printStackTrace();
            return false;

        }
        return true;
    }

    public static boolean writeNewAccount(int uid, String login, String password) {
        String path = getAccountFileName();
        List<String> strings = readAccountsFile();
        if (strings == null) {
            return false;
        }
        String accountLine =
            uid + ACCOUNTFILE_DELIMETER +
            login + ACCOUNTFILE_DELIMETER +
            password + "\n";
        strings.add(accountLine);
        return writeTextFile(path, strings);
    }

    /*Чтение данных.
    При ошибке вернет null.
    */
    public static Map<Integer, Record> dataRead(int uid) {

        String path = getDataFileName(uid);
         Map<Integer, Record> records;

        try ( ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            records = (Map<Integer, Record>) ois.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            return null;
        }

        return records;
    }

    /*Формирование пути к файлу данных для конкретного пользователя.
    */
    private static String getDataFileName(int uid) {
        return
            DATABASE_DIR + File.separator +
            PHBS_DIR + File.separator +
            + uid + "." + PHB_FILEEXT
        ;
    }

    /*Формирование пути к файлу аккаунтов.
    */
    private static String getAccountFileName() {
        return DATABASE_DIR + File.separator + ACCOUNTS_FILE;
    }

    /*Сохранение данных.
    */
    public static boolean dataSave(int uid, Map<Integer, Record> records) {
        String path = getDataFileName(uid);

        try ( ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path)) ) {
            oos.writeObject(records);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
