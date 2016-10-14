package ru.radom.kabinet.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Класс для работы с деньгами
 *
 * @author runcore
 */
public class HumansStringUtils {


    private static final String[][] SEX = {
            {
                    "", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"
            },
            {
                    "", "одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"
            },
    };

    private static final String[] STR_100 = {"", "сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"};

    private static final String[] STR_11 = {"", "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать", "двадцать"};

    private static final String[] STR_10 = {"", "десять", "двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто"};

    // Формы для копеек
    private static final String[][] CURRENCY_KOPECKS_FORMS = {
            {
                    "копейка", "копейки", "копеек", "1"
            }
    };

    // Формы для рублей
    private static final String[][] CURRENCY_RUBLES_FORMS = {
            {
                    "рубль", "рубля", "рублей", "0"
            },
            {
                    "тысяча", "тысячи", "тысяч", "1"
            },
            {
                    "миллион", "миллиона", "миллионов", "0"
            },
            {
                    "миллиард", "миллиарда", "миллиардов", "0"
            },
            {
                    "триллион", "триллиона", "триллионов", "0"
            },
            // можно добавлять дальше секстиллионы и т.д.
    };

    // Формы для центов
    private static final String[][] CURRENCY_USD_KOPECKS_FORMS = {
            {
                    "цент", "цента", "центов", "0"
            }
    };

    // Формы для долларов
    private static final String[][] CURRENCY_USD_FORMS = {
            {
                    "доллар", "доллара", "долларов", "0"
            },
            {
                    "тысяча", "тысячи", "тысяч", "1"
            },
            {
                    "миллион", "миллиона", "миллионов", "0"
            },
            {
                    "миллиард", "миллиарда", "миллиардов", "0"
            },
            {
                    "триллион", "триллиона", "триллионов", "0"
            },
            // можно добавлять дальше секстиллионы и т.д.
    };

    // Формы для центов
    private static final String[][] CURRENCY_EUR_KOPECKS_FORMS = {
            {
                    "цент", "цента", "центов", "0"
            }
    };

    // Формы для долларов
    private static final String[][] CURRENCY_EUR_FORMS = {
            {
                    "евро", "евро", "евро", "0"
            },
            {
                    "тысяча", "тысячи", "тысяч", "1"
            },
            {
                    "миллион", "миллиона", "миллионов", "0"
            },
            {
                    "миллиард", "миллиарда", "миллиардов", "0"
            },
            {
                    "триллион", "триллиона", "триллионов", "0"
            },
            // можно добавлять дальше секстиллионы и т.д.
    };

    // Формы для простых чисел
    private static final String[][] NUMBER_FORMS = {
            {
                    "", "", "", "0"
            },
            {
                    "тысяча", "тысячи", "тысяч", "1"
            },
            {
                    "миллион", "миллиона", "миллионов", "0"
            },
            {
                    "миллиард", "миллиарда", "миллиардов", "0"
            },
            {
                    "триллион", "триллиона", "триллионов", "0"
            },
            // можно добавлять дальше секстиллионы и т.д.
    };

    // Формы для дробной части чисел
    private static final String[][] FLOAT_PART_NUMBER_FORMS = {
            {
                    "", "", "", "1"
            },
            {
                    "тысяча", "тысячи", "тысяч", "1"
            },
            {
                    "миллион", "миллиона", "миллионов", "0"
            },
            {
                    "миллиард", "миллиарда", "миллиардов", "0"
            },
            {
                    "триллион", "триллиона", "триллионов", "0"
            },
            // можно добавлять дальше секстиллионы и т.д.
    };

    // Окончания дробных чисел
    private static final String[][] FLOAT_PART_NUMBER_SIFFIXES = {
            {
                "", "десятых", "сотых", "тысячных", "десятитысячных", "стотысячных", "миллионных", "десятимиллионных", "стомиллионных", "миллиардных"
            },
            {
                "", "десятая", "сотая", "тысячная", "десятитысячная", "стотысячная", "миллионная", "десятимиллионная", "стомиллионная", "миллиардная"
            }
    };

    // Формы для годов
    private static final String[][] DATE_YEAR_FORMS = {
            {
                    "года", "года", "года", "0"
            },
            {
                    "тысяча", "тысячи", "тысяч", "1"
            },
            {
                    "миллион", "миллиона", "миллионов", "0"
            },
            {
                    "миллиард", "миллиарда", "миллиардов", "0"
            },
            {
                    "триллион", "триллиона", "триллионов", "0"
            },
            // можно добавлять дальше секстиллионы и т.д.
    };

    // Формы для дней
    private static final String[][] DATE_DAY_FORMS = {
            {
                    "", "", "", "0"
            }
    };

    // Список месяцев
    private static final String[] MONTHES = {
            "января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"
    };

    private static final String[][][] DEFAULT_LAST_SEGMENT_FORMS = null;

    private static final String[][][] DATE_LAST_SEGMENT_FORMS = {
            {
                {
                        "нулевого", "первого", "второго", "третьего", "четвёртого", "пятого", "шестого", "седьмого", "восьмого", "девятого", "десятого",
                        "одиннадцатого", "двенадцатого", "тринадцатого", "четырнадцатого", "пятнадцатого", "шестнадцатого", "семнадцатого", "восемнадцатого", "девятнадцатого"
                },
                {
                        "", "десятого", "двадцатого", "тридцатого", "сорокового", "пятидесятого", "шестидесятого", "семидесятого", "восьмидесятого", "девяностого"
                },
                {
                        "", "сотого", "двухсотого", "трёхсотого", "четырёхсотого", "пятисотого", "шестисотого", "семисотого", "восьмисотого", "девятисотого"
                }
            },
            {
                    {
                            "", "тысячного", "двухтысячного", "трёхтысячного", "четырёхтысячного", "пятитысячного", "шеститысячного", "семитысячного", "восьмитысячного", "девятитысячного"
                    }
            }
            // Думаю для дат хватит 10тысяч :)
    };

    private static final Map<String, Object> CURRENCY_TYPES_FORMS = new HashMap<>();

    private static final String CURRENCY_SUFFIX = "_decimap";

    private static final String CURRENCY_KOPECKS_SUFFIX = "_precition";

    static  {
        CURRENCY_TYPES_FORMS.put("RUR" + CURRENCY_SUFFIX, CURRENCY_RUBLES_FORMS);
        CURRENCY_TYPES_FORMS.put("RUR" + CURRENCY_KOPECKS_SUFFIX, CURRENCY_KOPECKS_FORMS);

        CURRENCY_TYPES_FORMS.put("USD" + CURRENCY_SUFFIX, CURRENCY_USD_FORMS);
        CURRENCY_TYPES_FORMS.put("USD" + CURRENCY_KOPECKS_SUFFIX, CURRENCY_USD_KOPECKS_FORMS);

        CURRENCY_TYPES_FORMS.put("EUR" + CURRENCY_SUFFIX, CURRENCY_EUR_FORMS);
        CURRENCY_TYPES_FORMS.put("EUR" + CURRENCY_KOPECKS_SUFFIX, CURRENCY_EUR_KOPECKS_FORMS);
    }

    /**
     * Пропись денег из строки
     * @param value
     * @param currencyType - наименование типа валюты
     * @return
     */
    public static String money2string(String value, String currencyType){
        String[] moneyParts = value.split("\\.");
        long rubles = 0;
        long kopecks = 0;
        if (moneyParts.length == 2) { // Рубли и копейки
            rubles = Long.valueOf(moneyParts[0]);
            if (moneyParts[1].length() == 1 && !moneyParts[1].equals("0")) {
                moneyParts[1] = moneyParts[1] + "0";
            }
            kopecks = Long.valueOf(moneyParts[1]);
        } else if (moneyParts.length == 1) { // Только рубли
            rubles = Long.valueOf(moneyParts[0]);
        }
        currencyType = currencyType == null ? "RUR" : currencyType;
        String[][] currencyDeciamalForms = (String[][])CURRENCY_TYPES_FORMS.get(currencyType + CURRENCY_SUFFIX);
        String[][] currencyKopecksForms = (String[][])CURRENCY_TYPES_FORMS.get(currencyType + CURRENCY_KOPECKS_SUFFIX);
        if (currencyDeciamalForms == null || currencyKopecksForms == null) {
            throw new RuntimeException("Выбрана валюта, формы которой не реализованы!");
        }

        String rublesString = getStringFromNumber(new BigDecimal(rubles), currencyDeciamalForms, DEFAULT_LAST_SEGMENT_FORMS);
        String kopecksString = getStringFromNumber(new BigDecimal(kopecks), currencyKopecksForms, DEFAULT_LAST_SEGMENT_FORMS);
        String result = rublesString + " " + kopecksString;
        /*try {
            System.err.println(new String(result.getBytes("UTF-8"), "CP1251"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return result;
    }

    /**
     * Простое числовое предстваление денег с названием валюты
     * @param value
     * @return
     */
    public static String money2numbersWithCurrencyNames(String value, String currencyType) {
        String[] moneyParts = value.split("\\.");
        long rubles = 0;
        long kopecks = 0;
        if (moneyParts.length == 2) { // Рубли и копейки
            rubles = Long.valueOf(moneyParts[0]);
            if (moneyParts[1].length() == 1 && !moneyParts[1].equals("0")) {
                moneyParts[1] = moneyParts[1] + "0";
            }
            kopecks = Long.valueOf(moneyParts[1]);
        } else if (moneyParts.length == 1) { // Только рубли
            rubles = Long.valueOf(moneyParts[0]);
        }
        String rublesStr = morph(rubles, CURRENCY_RUBLES_FORMS[0][0], CURRENCY_RUBLES_FORMS[0][1], CURRENCY_RUBLES_FORMS[0][2]);
        String kopecksStr = morph(kopecks, CURRENCY_KOPECKS_FORMS[0][0], CURRENCY_KOPECKS_FORMS[0][1], CURRENCY_KOPECKS_FORMS[0][2]);

        String result = rubles + " " + rublesStr + " " + kopecks + " " + kopecksStr;

        return result;
    }

    public static String money2numbers(String value, String currencyType) {
        double dbValue = 0;
        try {
            dbValue = Double.valueOf(value).doubleValue();
        } catch (Exception e) {
            //
        }
        String pattern = "###,###.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String result = decimalFormat.format(dbValue);
        String[] parts = result.split(",");
        if (parts.length == 2 && parts[1].length() == 1) {
            result = parts[0] + "," + parts[1] + "0";
        }
        return result;
    }

    /**
     * Пропись числа из Double
     * @param value
     * @return
     */
    public static String number2string(double value) {
        return number2string(Double.toString(value));
    }

    /**
     * Пропись числа из Long
     * @param value
     * @return
     */
    public static String number2string(long value) {
        return number2string(String.valueOf(value));
    }

    /**
     * Пропись числа из строки
     * @param value
     * @return
     */
    public static String number2string(String value){
        String[] numberParts = value.split("\\.");
        long intPartNumber = 0;
        long floatPartNumber = 0;
        String floatPartStr = "";
        if (numberParts.length == 2) { // Целые и дробные
            intPartNumber = Long.valueOf(numberParts[0]);
            floatPartStr = numberParts[1];
            floatPartNumber = Long.valueOf(floatPartStr);
        } else if (numberParts.length == 1) { // Только целые
            intPartNumber = Long.valueOf(numberParts[0]);
        }
        String intPartNumberString = getStringFromNumber(new BigDecimal(intPartNumber), NUMBER_FORMS, DEFAULT_LAST_SEGMENT_FORMS);
        String floatPartNumberString = getStringFromNumber(new BigDecimal(floatPartNumber), FLOAT_PART_NUMBER_FORMS, DEFAULT_LAST_SEGMENT_FORMS);

        // Тип формы дробной части
        int floatPartType = 0;
        long ostatok = floatPartNumber % 100;
        if (ostatok == 11) {
            floatPartType = 0;
        } else {
            ostatok = floatPartNumber % 10;
            if (ostatok == 1) {
                floatPartType = 1;
            }
        }

        String suffix = "";
        if (FLOAT_PART_NUMBER_SIFFIXES[floatPartType].length > floatPartStr.length()) {
            if (!floatPartNumberString.replaceAll(" ", "").equals("")) {
                floatPartNumberString = "и " + floatPartNumberString;
                suffix = " " + FLOAT_PART_NUMBER_SIFFIXES[floatPartType][floatPartStr.length()];
            }
        }

        String result = intPartNumberString + " " + floatPartNumberString + suffix;
        /*try {
            System.err.println(new String(result.getBytes("UTF-8"), "CP1251"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return result;
    }

    /**
     * Склоняем словоформу
     *
     * @param n  Long количество объектов
     * @param f1 String вариант словоформы для 1
     * @param f2 String вариант словоформы от 1 до 5
     * @param f5 String вариант словоформы для остальных
     * @return String правильный вариант словоформы для указанного количества объектов
     */
    public static String morph(long n, String f1, String f2, String f5) {
        n = Math.abs(n) % 100;
        long n1 = n % 10;
        if (n > 10 && n < 20) return f5;
        if (n1 > 1 && n1 < 5) return f2;
        if (n1 == 1) return f1;
        return f5;
    }

    /**
     *
     * @param number
     * @param forms - формы чисел (рубли, тонны, и т.д.)
     * @param lastSegmentForms - формы отображения последнего не нулевого сегмента числа (например: девяносто -> девяностого)
     * @return
     */
    private static String getStringFromNumber(BigDecimal number, String[][] forms, String[][][] lastSegmentForms) {
        long longNumber = number.longValue();
        ArrayList<Long> segments = new ArrayList<>();
        long longNumberTmp = longNumber;
        while (longNumberTmp > 999) {
            long seg = longNumberTmp / 1000;
            segments.add(longNumberTmp - (seg * 1000));
            longNumberTmp = seg;
        }
        segments.add(longNumberTmp);

        StringBuilder result = new StringBuilder();

        if (longNumber == 0) {
            segments.clear();
            if (lastSegmentForms != null && lastSegmentForms.length > 0 && lastSegmentForms[0].length > 0 && lastSegmentForms[0][0].length > 0) {
                result.append(lastSegmentForms[0][0][0]).append(" ")
                    .append(morph(0, forms[0][0], forms[0][1], forms[0][2])).append(" ");
            } else {
                String zeroMorph = morph(0, forms[0][0], forms[0][1], forms[0][2]);
                if (!zeroMorph.equals("")) {
                    result.append("ноль ").append(zeroMorph).append(" ");
                }
            }
        }

        int segmentLength = segments.size();
        for (int i = segmentLength - 1; i > -1; i--) {// перебираем сегменты
            int sexi = Integer.valueOf(forms[i][3].toString()).intValue();// определяем род
            int segment = segments.get(i).intValue();// текущий сегмент

            String strSegment = String.valueOf(segment);
            if (strSegment.length() == 1) {
                strSegment = "00" + strSegment;
            } else if (strSegment.length() == 2) {
                strSegment = "0" + strSegment;
            }

            int sotni = Integer.valueOf(strSegment.substring(0, 1)).intValue(); //первая цифра
            int desyatki = Integer.valueOf(strSegment.substring(1, 2)).intValue(); //вторая
            int edinici = Integer.valueOf(strSegment.substring(2, 3)).intValue(); //третья

            String str10Result = "";
            if (desyatki == 1 && edinici > 0) {
                str10Result = STR_11[edinici + 1];
            } else {
                str10Result = STR_10[desyatki] + " " + SEX[sexi][edinici];
            }

            // Если это последний не нулевой сегемент
            if ((i == segmentLength - 1) || ((i < segmentLength - 1) && !"000".equals(strSegment))){
                if (lastSegmentForms != null && lastSegmentForms.length > i) {
                    if (edinici > 0) {
                        int num11 = edinici;
                        if (desyatki == 0 || desyatki == 1) {
                            num11 = desyatki * 10 + edinici;
                            result.append(STR_100[sotni]).append(" ")
                                    .append(lastSegmentForms[i][0][num11]).append(" ")
                                    .append(morph(segment, forms[i][0], forms[i][1], forms[i][2])).append(" ");
                        } else {
                            result.append(STR_100[sotni]).append(" ")
                                    .append(STR_10[desyatki]).append(" ")
                                    .append(lastSegmentForms[i][0][num11]).append(" ")
                                    .append(morph(segment, forms[i][0], forms[i][1], forms[i][2])).append(" ");
                        }
                    } else if (desyatki != 0 && lastSegmentForms[i].length > 1) {
                        result.append(STR_100[sotni]).append(" ")
                                .append(lastSegmentForms[i][1][desyatki]).append(" ")
                                .append(morph(segment, forms[i][0], forms[i][1], forms[i][2])).append(" ");
                    } else if (sotni != 0 && lastSegmentForms[i].length > 2) {
                        result.append(lastSegmentForms[i][2][sotni]).append(" ")
                                .append(morph(segment, forms[i][0], forms[i][1], forms[i][2])).append(" ");
                    } else {
                        result.append(STR_100[sotni]).append(" ")
                                .append(str10Result).append(" ")
                                .append(morph(segment, forms[i][0], forms[i][1], forms[i][2])).append(" ");
                    }
                } else {
                    result.append(STR_100[sotni]).append(" ")
                            .append(str10Result).append(" ")
                            .append(morph(segment, forms[i][0], forms[i][1], forms[i][2])).append(" ");
                }
            }/* else {
                result.append(STR_100[sotni]).append(" ")
                        .append(str10Result).append(" ")
                        .append(morph(segment, forms[i][0], forms[i][1], forms[i][2])).append(" ");
            }*/
        }
        return result.toString().replaceAll("[\\s]{2,}", " ").trim();
    }

    /**
     * Получить пропись из строки даты
     * @param dateString
     * @return
     */
    public static String date2string(String dateString) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = dateFormatter.parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return date2string(date);
    }

    /**
     * Получить пропись из даты
     * @param date
     * @return
     */
    public static String date2string(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        BigDecimal yearBD = new BigDecimal(year);
        String yearStr = getStringFromNumber(yearBD, DATE_YEAR_FORMS, DATE_LAST_SEGMENT_FORMS);

        String monthStr = MONTHES[month];

        BigDecimal dayBD = new BigDecimal(day);
        String dayStr = getStringFromNumber(dayBD, DATE_DAY_FORMS, DATE_LAST_SEGMENT_FORMS);

        String result = dayStr + " " + monthStr + " " + yearStr;

        return result;
	}

    private static final String[] FILE_SIZE_SUFIXES = new String[]{"б", "Кб", "Мб", "Гб", "Тб"};

    /**
     * Получить размер файла
     * @param size
     * @return
     */
    public static String getFileSize(Long size) {
        double i = Math.floor( Math.log(size.doubleValue()) / Math.log(1024d) );
        double dSize = size / Math.pow(1024, i);
        int index = (int)i;

        return String.format("%.2f", dSize) + FILE_SIZE_SUFIXES[index];
    }

}