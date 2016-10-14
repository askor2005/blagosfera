package ru.radom.kabinet.services.taxcode;

import lombok.Data;

/**
 * Обёртка ответа от сервиса получения кода налогового органа
 * Created by vgusev on 16.02.2016.
 */
@Data
public class TaxCodeResponseDto {

    /**
     * Код КЛАДР строения
     * Пример: "5900001200500090001"
     */
    private String code;

    /**
     * Код налоговой
     * Пример: "5920"
     */
    private String ifns;

    /**
     * Код ОКАТО
     * Пример: "57435804000"
     */
    private String okatom;

    /**
     * Строка поиска
     * Пример: "617759,59,,ЧАЙКОВСКИЙ Г,БОЛЬШОЙ БУКОР С,ЮБИЛЕЙНАЯ УЛ,,,"
     */
    private String text;

    /**
     * Индес
     * Пример: "617759"
     */
    private String zip;
}
