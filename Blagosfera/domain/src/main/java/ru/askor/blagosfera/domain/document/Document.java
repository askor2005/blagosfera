package ru.askor.blagosfera.domain.document;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by vgusev on 17.03.2016.
 */
@Data
public class Document {

    private Long id;
    private String code;
    private Date createDate;
    private String name;

    /**
     * Короткое имя документа.
     */
    private String shortName;

    /**
     * Контент документа.
     */
    private String content;

    /**
     * ИД класса документов.
     */
    private Long documentClassId;

    /**
     * Ссылка на список участников документа.
     */
    private List<DocumentParticipant> participants;

    /**
     * ИД создателя документа.
     */
    private DocumentCreator creator;

    /**
     * Список параметров документа.
     */
    private List<DocumentParameter> parameters;

    /**
     * Уникальный код документа для ссылок.
     */
    private String hashCode;

    /**
     * Активность документа
     */
    private boolean isActive;

    /**
     * Дата истечения документа
     */
    private Date expiredDate;

    /**
     * Хеш код документа для его подписания участниками, которые его подписывают
     */
    private String hashCodeForSignature;

    /**
     *
     */
    private String link;

    /**
     *
     */
    private boolean canUnsignDocument;

    /**
     * Нужно ли подписывать документ с использованием ЭЦП
     */
    private boolean needSignByEDS;

    /**
     * Параметры выгрузки документа в pdf
     */
    private String pdfExportArguments;

    private DocumentFolder documentFolder;


}
