package ua.skillsup.demitt.phonebook.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public enum ContactType implements Serializable {
    MOBILE_PHONE( "моб.", 1, true, Option.CTYPE_MOBILE, OptionKey.KEY_1, "((\\+)?\\d{2})?\\d{3}\\d{7}" ),
    URBAN_PHONE( "гор.", 2, true, Option.CTYPE_URBAN, OptionKey.KEY_2, "\\d{6,7}" ),
    EMAIL( "почта", 3, false, Option.CTYPE_EMAIL, OptionKey.KEY_3, "[a-z\\d\\.-_]{1,20}@[a-z\\d\\.]{3,10}" ),
    ICQ( "ICQ", 4, false, Option.CTYPE_ICQ, OptionKey.KEY_4, "\\d{5,9}" ),
    SKYPE( "skype", 5, false, Option.CTYPE_SKYPE, OptionKey.KEY_5, "[a-z\\d-_]{1,30}" )
    ;

    //Поля describe и order применимы к детальному просмотру записи.
    //Списки options и optionKeys используется для выбора типа контакта (создание/редактирование записи).
    //Мапа keyTypeMap используется для поолучения типа контакта по введенному пользователем Option.
    private final String describe; //строковое описание типа контакта
    private final int order; //порядок отображения типов контактов
    private final boolean isSearchable; //флаг "можно искать по значению этого типа контакта"
    private final OptionKey optionKey; //вариант ответа при выборе этого типа контакта
    private final Option option; //отображаемое описание, соответствующее полю optionKey
    private final Pattern valueRegExpPattern; //pattern для проверки валидности значения для этого типа контакта
    private static List<ContactType> searchTypes = new ArrayList<>(); //список типов, в значениях которых можно искать
    private static List<Option> options = new ArrayList<>(); //список Option, соотв-щих типам (порядок соответствует полю order)
    private static List<OptionKey> optionKeys = new ArrayList<>(); //список ключей для options
    private static Map<OptionKey, ContactType> keyTypeMap = new HashMap<>();

    static {
        //Заполним searchTypes:
        for (ContactType type : ContactType.values()) {
            if (type.isSearchable()) {
                searchTypes.add(type);
            }
        }
        searchTypes = Collections.unmodifiableList(searchTypes);
        //Заполним options, optionKeys, keyTypeMap:
        List<ContactType> types = new ArrayList<>();
        types.addAll( Arrays.asList(ContactType.values()) );
        types.sort( (t1, t2) -> t1.getOrder() - t2.getOrder() ); //отсортировали по полю order
        for (ContactType type : types) {
            options.add(type.getOption());
            OptionKey key = type.getOptionKey();
            optionKeys.add(key);
            keyTypeMap.put(key, type);
        }
        options = Collections.unmodifiableList(options);
        optionKeys = Collections.unmodifiableList(optionKeys);
    }

    ContactType(String describe, int order, boolean isSearchable, Option option, OptionKey optionKey, String valueRegExpString) {
        this.describe = describe;
        this.order = order;
        this.isSearchable = isSearchable;
        this.option = option;
        this.optionKey = optionKey;
        this.valueRegExpPattern = Pattern.compile(valueRegExpString);
    }

    public String getDescribe() {
        return this.describe;
    }

    public Pattern getValueRegExpPattern() {
        return this.valueRegExpPattern;
    }

    public int getOrder() {
        return this.order;
    }

    public static List<ContactType> getSearchTypes() {
        return searchTypes;
    }

    public static List<OptionKey> getOptionKeys() {
        return optionKeys;
    }

    public static List<Option> getOptions() {
        return options;
    }

    public static ContactType getContactTypeByOptionKey(OptionKey optionKey) {
        ContactType type = keyTypeMap.get(optionKey);
        if (type == null) {
            throw new IllegalArgumentException(
                "Нет такого ContactType, который соответствует указанному OptionKey: " + optionKey
            );
        }
        return type;
    }

    public Option getOption() {
        return this.option;
    }

    private OptionKey getOptionKey() {
        return optionKey;
    }

    private boolean isSearchable() {
        return this.isSearchable;
    }




}
