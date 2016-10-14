package ru.radom.kabinet.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ebelyaev on 13.10.2015.
 */
public class AddressUtils {

    private static String[] excludeSigns = {
            "-",
            "нет",
            "без улицы",
            "б/у",
            "есть"
    };


    // Федеральные округа
    private static String federalDistrict = "Федеральный округ";
    private static String[] federalDistricts = {
            "Центральный федеральный округ", // Москва
            "Южный федеральный округ", // Ростов-на-Дону
            "Северо-Западный федеральный округ", // Санкт-Петербург
            "Дальневосточный федеральный округ", // Хабаровск
            "Сибирский федеральный округ", // Новосибирск
            "Уральский федеральный округ", // Екатеринбург
            "Приволжский федеральный округ", // Нижний Новгород
            "Северо-Кавказский федеральный округ", // Пятигорск
            "Крымский федеральный округ", // Симферополь
    };

    // Республики
    private static String republic = "Республика";
    private static String[] republics = {
            "Адыгея",
            "Алтай",
            "Башкортостан",
            "Бурятия",
            "Дагестан",
            "Ингушетия",
            "Кабардино-Балкария",
            "Калмыкия",
            "Карачаево-Черкесия",
            "Карелия",
            "Коми",
            "Крым",
            "Марий Эл",
            "Мордовия",
            "Саха (Якутия)",
            "Северная Осетия — Алания",
            "Татарстан",
            "Тыва",
            "Удмуртия",
            "Хакасия",
            "Чечня",
            "Чувашия"
    };

    // Края
    private static String regionKray = "Край";
    private static String[] regionKrays = { // и края и области по англ region
            "Алтайский",
            "Забайкальский",
            "Камчатский",
            "Краснодарский",
            "Красноярский",
            "Пермский",
            "Приморский",
            "Ставропольский",
            "Хабаровский"
    };

    // Области
    private static String regionOblast = "Область";
    private static String[] regionOblasts = { // и края и области по англ region
            "Амурская",
            "Архангельская",
            "Астраханская",
            "Белгородская",
            "Брянская",
            "Владимирская",
            "Волгоградская",
            "Вологодская",
            "Воронежская",
            "Ивановская",
            "Иркутская",
            "Калининградская",
            "Калужская",
            "Кемеровская",
            "Кировская",
            "Костромская",
            "Курганская",
            "Курская",
            "Ленинградская",
            "Липецкая",
            "Магаданская",
            "Московская",
            "Мурманская",
            "Нижегородская",
            "Новгородская",
            "Новосибирская",
            "Омская",
            "Оренбургская",
            "Орловская",
            "Пензенская",
            "Псковская",
            "Ростовская",
            "Рязанская",
            "Самарская",
            "Саратовская",
            "Сахалинская",
            "Свердловская",
            "Смоленская",
            "Тамбовская",
            "Тверская",
            "Томская",
            "Тульская",
            "Тюменская",
            "Ульяновская",
            "Челябинская",
            "Ярославская"
    };

    // Города федерального значения
    private static String federalCity = "Город федерального значения";
    private static String[] federalCities = {
            "Москва",
            "Санкт-Петербург",
            "Севастополь"
    };

    // Автономные области
    private static String autonomousRegion = "Автономная область";
    private static String[] autonomousRegions = {
            "Еврейская"
    };

    // Автономные округа
    private static String autonomousDistrict = "Автономный округ";
    private static String[] autonomousDistricts = {
            "Ненецкий",
            "Ханты-Мансийский",
            "Чукотский",
            "Ямало-Ненецкий"
    };

    /**
     * Возвращает описание региона(Область, Край и тд)
     *
     * @param region строка с регионом, например "Московская область" или "Московская"
     * @return "Московская область" или "Московская" вернёт "Область"
     */
    public static String getRegionDescription(String region) {
        region = prepareString(region);

        String result = "";

        for (String s : federalDistricts) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = "Город";
                break;
            }
        }
        for (String s : republics) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = republic;
                break;
            }
        }
        for (String s : regionKrays) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = regionKray;
                break;
            }
        }
        for (String s : regionOblasts) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = regionOblast;
                break;
            }
        }
        for (String s : federalCities) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = "Город";
                break;
            }
        }
        for (String s : autonomousRegions) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = autonomousRegion;
                break;
            }
        }
        for (String s : autonomousDistricts) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = autonomousDistrict;
                break;
            }
        }

        if (StringUtils.equalsIgnoreCase(region, republic)) {
            result = republic;
        }

        if (StringUtils.equalsIgnoreCase(region, regionKray)) {
            result = regionKray;
        }

        if (StringUtils.equalsIgnoreCase(region, regionOblast)) {
            result = regionOblast;
        }

        if (StringUtils.equalsIgnoreCase(region, "Город")) {
            result = "Город";
        }

        if (StringUtils.equalsIgnoreCase(region, autonomousRegion)) {
            result = autonomousRegion;
        }

        if (StringUtils.equalsIgnoreCase(region, autonomousDistrict)) {
            result = autonomousDistrict;
        }

        System.out.println("RegionDescription: [" + region + "] -> [" + result.trim() + "]");
        return result.trim();
    }


    public static String getRegionShortDescription(String description) {
        String shortDescription = description;
        if (shortDescription == null) {
            shortDescription = "";
        }

        if (StringUtils.equalsIgnoreCase(shortDescription, republic)) {
            shortDescription = "Респ.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, regionKray)) {
            shortDescription = "край";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, regionOblast)) {
            shortDescription = "обл.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, autonomousRegion)) {
            shortDescription = "Аобл.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, autonomousDistrict)) {
            shortDescription = "АО";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Город")) {
            shortDescription = "г.";
        }

        return shortDescription;
    }

    /**
     * Возвращает названеие региона(без Область, Край и тд)
     *
     * @param region строка с регионом, например "Московская область" или "Московская"
     * @return "Московская область" или "Московская" вернёт "Московская"
     */
    public static String getRegionName(String region) {
        region = prepareString(region);

        String result = "";
        for (String s : federalDistricts) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = s;
                break;
            }
        }
        for (String s : republics) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = s;
                break;
            }
        }
        for (String s : regionKrays) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = s;
                break;
            }
        }
        for (String s : regionOblasts) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = s;
                break;
            }
        }
        for (String s : federalCities) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = s;
                break;
            }
        }
        for (String s : autonomousRegions) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = s;
                break;
            }
        }
        for (String s : autonomousDistricts) {
            if (StringUtils.containsIgnoreCase(region, s)) {
                result = s;
                break;
            }
        }

        if (StringUtils.equalsIgnoreCase(region, "Центральный федеральный округ")) {
            result = "Москва";
        }
        if (StringUtils.equalsIgnoreCase(region, "Крымский федеральный округ")) {
            result = "Симферополь";
        }
        if (StringUtils.equalsIgnoreCase(region, "Северо-Западный федеральный округ")) {
            result = "Санкт-Петербург";
        }

        System.out.println("RegionName: [" + region + "] -> [" + result.trim() + "]");
        return result.trim();
    }

    private final static String[] rayonSigns = {
            "Район",
            "Р-он"
    };

    private final static String[] goSigns = {
            "Городской округ",
            "Го",
            "Гор. окр"
    };

    /**
     * Возвращает описание района.
     *
     * @param area
     * @return
     */
    public static String getAreaDescription(String area) {
        area = prepareString(area);

        String description = "Район";

        if (matchesSigns(area, rayonSigns)) {
            description = "Район";
        } else if (matchesSigns(area, goSigns)) {
            description = "Городской округ";
        } else if (matchesSigns(area, federalCities)) {
            description = "Город";
        } else if (matchesSigns(area, poselenieSigns)) {
            description = "Поселение";
        }

        if (StringUtils.isBlank(area)) {
            description = "";
        }

        System.out.println("AreaDescription: [" + area + "] -> [" + description + "]");
        return description;
    }

    public static String getAreaShortDescription(String description) {
        String shortDescription = description;

        if (StringUtils.equalsIgnoreCase(shortDescription, "Район")) {
            shortDescription = "р-н.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Городской округ")) {
            shortDescription = "ГО";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Город")) {
            shortDescription = "г.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Поселение")) {
            shortDescription = "п.";
        }

        return shortDescription;
    }

    /**
     * Возвращает название района.
     *
     * @param area строка с районом, например "Одинцовский район" или "Одинцовский"
     * @return "Одинцовский район" или "Одинцовский" вернёт "Одинцовский"
     */
    public static String getAreaName(String area) {
        area = prepareString(area);

        String result = area;

        if (matchesSigns(result, rayonSigns)) {
            result = removeSigns(result, rayonSigns);
        } else if (matchesSigns(result, goSigns)) {
            result = removeSigns(result, goSigns);
        } else if (matchesSigns(result, poselenieSigns)) {
            result = removeSigns(result, poselenieSigns);
        }

        System.out.println("AreaName: [" + area + "] -> [" + result.trim() + "]");
        return result.trim();
    }

    private static final String[] gorodSigns = {
            "Город",
            "Гор",
            "Г"
    };

    private static final String[] seloSigns = {
            "Село",
            "С"
    };

    private static final String[] derevnyaSigns = {
            "Деревня",
            "Д"
    };

    private static final String[] pgtSigns = {
            "Посёлок городского типа",
            "Поселок городского типа",
            "Пгт"
    };

    private static final String[] poselenieSigns = {
            "Поселение",
            "Посёлок",
            "Поселок",
            "Пос",
            "П"
    };

    private static String createPattern(String[] signs) {
        String pattern = "";
        pattern += "(?iu)(^|\\s)(";
        //pattern += StringUtils.join(signs, "\\.?|");

        for (int i = 0; i < signs.length; i++) {
            String sign = signs[i];
            pattern += sign + "\\.?";
            if (i < signs.length - 1) {
                pattern += "|";
            }
        }

        pattern += ")(\\s|$)";
        return pattern;
    }

    /**
     * Вырезает из строки промежуток и вырезает лишние пробелы(вначале, вконце и двойные)
     *
     * @param s     строка
     * @param start индекс начала промежутка
     * @param end   индекс конца промежутка
     * @return строка без символов сщ start по end и без лишних пробелов, которые могли образоваться
     */
    public static String cut(String s, int start, int end) {
        return (s.substring(0, start) + s.substring(end, s.length())).trim().replaceAll("\\s+", " ");
    }

    private static boolean matchesSigns(String str, String[] signs) {
        boolean result = false;

        String pattern = createPattern(signs);

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        while (m.find()) {
            result = true;
        }

        return result;
    }

    private static String removeSigns(String str, String[] signs) {
        String result = str;

        String pattern = createPattern(signs);

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        while (m.find()) {
            result = cut(str, m.start(2), m.end(2));
        }

        return result;
    }


    /**
     * Возвращает описание города.
     *
     * @param city строка с городом, например "Краснознаменск" или "город Краснознаменск"
     * @return "Краснознаменск" или "город Краснознаменск" вернёт "Город". По умолчанию возвращает "Город"
     */
    public static String getCityDescription(String city) {
        city = prepareString(city);

        String description = "Город";

        if (matchesSigns(city, gorodSigns)) {
            description = "Город";
        } else if (matchesSigns(city, seloSigns)) {
            description = "Село";
        } else if (matchesSigns(city, derevnyaSigns)) {
            description = "Деревня";
        } else if (matchesSigns(city, pgtSigns)) {
            description = "Посёлок городского типа";
        } else if (matchesSigns(city, poselenieSigns)) {
            description = "Поселение";
        }

        if (StringUtils.isBlank(city)) {
            description = "";
        }

        System.out.println("CityDescription: [" + city + "] -> [" + description + "]");
        return description;
    }



    public static String getCityShortDescription(String description) {
        String shortDescription = description;

        if (StringUtils.equalsIgnoreCase(shortDescription, "Город")) {
            shortDescription = "г.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Село")) {
            shortDescription = "с.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Деревня")) {
            shortDescription = "д.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Посёлок городского типа")) {
            shortDescription = "пгт.";
        }else if (StringUtils.equalsIgnoreCase(shortDescription, "Поселение")) {
            shortDescription = "п.";
        }

        return shortDescription;
    }

    /**
     * Возвращает название района.
     *
     * @param city строка с городом, например "Одинцовский район" или "Одинцовский"
     * @return "Краснознаменск" или "город Краснознаменск" вернёт "Краснознаменск"
     */
    public static String getCityName(String city) {
        city = prepareString(city);

        String name = city;

        if (matchesSigns(city, gorodSigns)) {
            name = removeSigns(city, gorodSigns);
        } else if (matchesSigns(city, seloSigns)) {
            name = removeSigns(city, seloSigns);
        } else if (matchesSigns(city, derevnyaSigns)) {
            name = removeSigns(city, derevnyaSigns);
        } else if (matchesSigns(city, pgtSigns)) {
            name = removeSigns(city, pgtSigns);
        } else if (matchesSigns(city, poselenieSigns)) {
            name = removeSigns(city, poselenieSigns);
        }

        System.out.println("CityName: [" + city + "] -> [" + name + "]");
        return name;
    }


    private static final String[] ulicaSigns = {
            "Улица",
            "Ул"
    };

    private static final String[] prospektSigns = {
            "Проспект",
            "Пр-кт",
            "Пр-т"
    };

    private static final String[] proezdSigns = {
            "Проезд",
            "Пр"
    };

    private static final String[] pereulokSigns = {
            "Переулок",
            "Пер"
    };

    private static final String[] naberejnayaSigns = {
            "Набережная",
            "Наб"
    };

    private static final String[] ploshadSigns = {
            "Площадь",
            "Пл"
    };

    private static final String[] bulvarSigns = {
            "Бульвар",
            "Б-р"
    };

    private static final String[] liniyaSigns = {
            "Линия",
            "Лин"
    };

    private static final String[] shosseSigns = {
            "Шоссе",
            "Ш"
    };

    private static final String[] spuskSigns = {
            "Спуск"
    };

    private static final String[] territoriyaSigns = {
            "Территория",
            "Тер"
    };

    private static final String[] sizeSmallSigns = {
            "Малый",
            "Малая",
            "М",
    };

    private static final String[] sizeBigSigns = {
            "Большой",
            "Большая",
            "Б"
    };

    /**
     * Возвращает описание улицы.
     *
     * @param street строка с улицой, например "улица Уличная" или "Уличная"
     * @return "улица Уличная" или "Уличная" вернёт "Улица". По умолчанию возвращает "Улица"
     */
    public static String getStreetDescription(String street) {
        street = prepareString(street);

        String description = "Улица";

        if (matchesSigns(street, ulicaSigns)) {
            description = "Улица";
        } else if (matchesSigns(street, prospektSigns)) {
            description = "Проспект";
        } else if (matchesSigns(street, proezdSigns)) {
            description = "Проезд";
        } else if (matchesSigns(street, pereulokSigns)) {
            description = "Переулок";
        } else if (matchesSigns(street, naberejnayaSigns)) {
            description = "Набережная";
        } else if (matchesSigns(street, ploshadSigns)) {
            description = "Площадь";
        } else if (matchesSigns(street, bulvarSigns)) {
            description = "Бульвар";
        } else if (matchesSigns(street, liniyaSigns)) {
            description = "Линия";
        } else if (matchesSigns(street, shosseSigns)) {
            description = "Шоссе";
        } else if (matchesSigns(street, spuskSigns)) {
            description = "Спуск";
        } else if (matchesSigns(street, territoriyaSigns)) {
            description = "Территория";
        }

        if (StringUtils.isBlank(street)) {
            description = "";
        }

        System.out.println("StreetDescription: [" + street + "] -> [" + description + "]");
        return description;
    }

    public static String getStreetShortDescription(String description) {
        String shortDescription = description;

        if (StringUtils.equalsIgnoreCase(shortDescription, "Улица")) {
            shortDescription = "ул.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Проспект")) {
            shortDescription = "пр-кт.";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Проезд")) {
            shortDescription = "проезд";
        } else if (StringUtils.equalsIgnoreCase(shortDescription, "Переулок")) {
            shortDescription = "пер.";
        }else if (StringUtils.equalsIgnoreCase(shortDescription, "Набережная")) {
            shortDescription = "наб.";
        }else if (StringUtils.equalsIgnoreCase(shortDescription, "Площадь")) {
            shortDescription = "пл.";
        }else if (StringUtils.equalsIgnoreCase(shortDescription, "Бульвар")) {
            shortDescription = "б-р.";
        }else if (StringUtils.equalsIgnoreCase(shortDescription, "Линия")) {
            shortDescription = "линия";
        }else if (StringUtils.equalsIgnoreCase(shortDescription, "Шоссе")) {
            shortDescription = "ш.";
        }else if (StringUtils.equalsIgnoreCase(shortDescription, "Спуск")) {
            shortDescription = "спуск";
        }else if (StringUtils.equalsIgnoreCase(shortDescription, "Территория")) {
            shortDescription = "тер.";
        }

        return shortDescription;
    }

    /**
     * Возвращает название улицы.
     *
     * @param street строка с улицой, например "улица Уличная" или "Уличная"
     * @return "улица Уличная" или "Уличная" вернёт "Уличная".
     */
    public static String getStreetName(String street) {
        street = prepareString(street);

        String name = street;

        if (matchesSigns(street, ulicaSigns)) {
            name = removeSigns(name, ulicaSigns);
        } else if (matchesSigns(street, prospektSigns)) {
            name = removeSigns(name, prospektSigns);
        } else if (matchesSigns(street, proezdSigns)) {
            name = removeSigns(name, proezdSigns);
        } else if (matchesSigns(street, pereulokSigns)) {
            name = removeSigns(name, pereulokSigns);
        } else if (matchesSigns(street, naberejnayaSigns)) {
            name = removeSigns(name, naberejnayaSigns);
        } else if (matchesSigns(street, ploshadSigns)) {
            name = removeSigns(name, ploshadSigns);
        } else if (matchesSigns(street, bulvarSigns)) {
            name = removeSigns(name, bulvarSigns);
        } else if (matchesSigns(street, liniyaSigns)) {
            name = removeSigns(name, liniyaSigns);
        } else if (matchesSigns(street, shosseSigns)) {
            name = removeSigns(name, shosseSigns);
        } else if (matchesSigns(street, spuskSigns)) {
            name = removeSigns(name, spuskSigns);
        } else if (matchesSigns(street, territoriyaSigns)) {
            name = removeSigns(name, territoriyaSigns);
        }

        if (matchesSigns(street, sizeSmallSigns)) {
            name = removeSigns(name, sizeSmallSigns);
            name += " М.";
        } else if (matchesSigns(street, sizeBigSigns)) {
            name = removeSigns(name, sizeBigSigns);
            name += " Б.";
        }

        System.out.println("StreetName: [" + street + "] -> [" + name + "]");
        return name;
    }

    private static final String[] domSigns = {
            "Дом",
            "д"
    };

    /**
     * Возвращает описание дома.
     *
     * @param house строка с домом, например "дом 1" или "1"
     * @return "дом 1" или "1" вернёт "Дом". По умолчанию возвращает "Дом"
     */
    public static String getHouseDescription(String house) {
        house = prepareString(house);

        String description = "Дом";

        if (matchesSigns(house, domSigns)) {
            description = "Дом";
        }

        if (StringUtils.isBlank(house)) {
            description = "";
        }

        System.out.println("HouseDescription: [" + house + "] -> [" + description + "]");
        return description;
    }

    public static String getHouseShortDescription(String description) {
        String shortDescription = description;

        if (StringUtils.equalsIgnoreCase(shortDescription, "Дом")) {
            shortDescription = "д.";
        }

        return shortDescription;
    }

    /**
     * Возвращает название улицы.
     *
     * @param house строка с домом, например "дом 1" или "1"
     * @return "дом 1" или "1" вернёт "1".
     */
    public static String getHouseName(String house) {
        house = prepareString(house,false);
        house =  house.replace("стр", "с");

        String name = house;

        if (matchesSigns(house, domSigns)) {
            name = removeSigns(name, domSigns);
        }

        System.out.println("HouseName: [" + house + "] -> [" + name + "]");
        return name;
    }

    private static final String[] kvartiraSigns = {
            "Квартира",
            "Кв"
    };

    /**
     * Возвращает описание квартиры.
     *
     * @param room строка с домом, например "квартира 1" или "1"
     * @return "квартира 1" или "1" вернёт "Квартира". По умолчанию возвращает "Квартира"
     */
    public static String getRoomDescription(String room) {
        room = prepareString(room,false);

        String description = "Квартира";

        if (matchesSigns(room, kvartiraSigns)) {
            description = "Квартира";
        }

        if (StringUtils.isBlank(room)) {
            description = "";
        }

        System.out.println("RoomDescription: [" + room + "] -> [" + description + "]");
        return description;
    }

    public static String getRoomShortDescription(String description) {
        String shortDescription = description;

        if (StringUtils.equalsIgnoreCase(shortDescription, "Квартира")) {
            shortDescription = "кв.";
        }

        return shortDescription;
    }

    /**
     * Возвращает название улицы.
     *
     * @param room строка с домом, например "квартира 1" или "1"
     * @return "квартира 1" или "1" вернёт "1".
     */
    public static String getRoomName(String room) {
        room = prepareString(room,false);

        String name = room;

        if (matchesSigns(room, kvartiraSigns)) {
            name = removeSigns(name, kvartiraSigns);
        }

        System.out.println("RoomName: [" + room + "] -> [" + name + "]");
        return name;
    }

    private static final String[] officeSigns = {
            "Офис"
    };

    /**
     * Возвращает описание офиса.
     *
     * @param office строка с офисом, например "офис 3" или "3"
     * @return "офис 3" или "3" вернёт "Офис". По умолчанию возвращает "Офис"
     */
    public static String getOfficeDescription(String office) {
        office = prepareString(office,false);

        String description = "Офис";

        if (matchesSigns(office, officeSigns)) {
            description = "Офис";
        }

        if (StringUtils.isBlank(office)) {
            description = "";
        }

        System.out.println("OfficeDescription: [" + office + "] -> [" + description + "]");
        return description;
    }

    public static String getOfficeShortDescription(String description) {
        String shortDescription = description;

        if (StringUtils.equalsIgnoreCase(shortDescription, "Офис")) {
            shortDescription = "офис";
        }

        return shortDescription;
    }

    /**
     * Возвращает название улицы.
     *
     * @param office строка с офисом, например "офис 3" или "3"
     * @return "офис 3" или "3" вернёт "3".
     */
    public static String getOfficeName(String office) {
        office = prepareString(office);

        String name = office;

        if (matchesSigns(office, officeSigns)) {
            name = removeSigns(name, officeSigns);
        }

        System.out.println("OfficeName: [" + office + "] -> [" + name + "]");
        return name;
    }

    public static String prepareString(String string, boolean capitalize) {
        // убираем лишние пробелы
        string = string.trim().replace(",", " ");
        string = string.trim().replaceAll("\\s+", " ");

        // Делаем только первые буквы в каждом слове большими
        string = string.toLowerCase();
        if(capitalize) {
            string = WordUtils.capitalize(string);
        }

        if (matchesSigns(string, excludeSigns)) {
            string = removeSigns(string, excludeSigns);
        }

        return string;
    }

    public static String prepareString(String string) {
        return prepareString(string,true);
    }
}
