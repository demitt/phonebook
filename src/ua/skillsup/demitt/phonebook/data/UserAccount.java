package ua.skillsup.demitt.phonebook.data;

public class UserAccount {
    private final int id;
    private final String login;
    private final String password;

    public UserAccount(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

}
