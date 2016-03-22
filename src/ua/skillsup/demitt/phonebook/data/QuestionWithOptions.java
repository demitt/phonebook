package ua.skillsup.demitt.phonebook.data;

import java.util.List;

public class QuestionWithOptions {
    private final String question; //текст вопроса
    private final Option goBackOption; //вариант, соотвествующий последнему обязательному ответу "назад" (или схожему с ним)
    private final List<OptionKey> optionKeys; //список возможных ключей, к-рые может ввести пользователь в ответ
    private final List<Option> options; //список вариантов, соотвествующий порядку вариантов в optionKeys

    public QuestionWithOptions(String question, Option goBackOption, List<OptionKey> optionKeys, List<Option> options) {
        this.question = question;
        this.goBackOption = goBackOption;
        this.optionKeys = optionKeys;
        this.options = options;
    }

    public String getQuestion() {
        return this.question;
    }

    public List<OptionKey> getOptionKeys() {
        return this.optionKeys;
    }

    public List<Option> getOptions() {
        return this.options;
    }

    public Option getGoBackOption() {
        return this.goBackOption;
    }

}
