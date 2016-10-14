package ru.radom.kabinet.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Индексы падежей в библиотеке Padeg
 * Created by vgusev on 11.09.2015.
 */
public interface PadegConstants {

    // Именительный
    int PADEG_I = 1;
    // Родительный
    int PADEG_R = 2;
    // Дательный
    int PADEG_D = 3;
    // Винительный
    int PADEG_V = 4;
    // Творительный
    int PADEG_T = 5;
    // Предложный
    int PADEG_P = 6;

    /**
     * Мапа с ИД падежей.
     */
    Map<String, Integer> PADEGES_MAP = new HashMap<String, Integer>() {{
        put("CASE_I", PADEG_I);
        put("CASE_R", PADEG_R);
        put("CASE_D", PADEG_D);
        put("CASE_V", PADEG_V);
        put("CASE_T", PADEG_T);
        put("CASE_P", PADEG_P);
    }};
}
