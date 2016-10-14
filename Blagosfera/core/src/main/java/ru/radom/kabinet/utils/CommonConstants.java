package ru.radom.kabinet.utils;

/**
 *
 * Created by vgusev on 18.08.2015.
 */
public interface CommonConstants {

    // Наименование параметра - урл редиректа json объекта в ответе сервера
    String REDIRECT_URL_JSON_RESPONSE_PARAM_NAME = "redirectUrl";

    // Базовая ссылка на документ
    String BASE_DOCUMENT_LINK = "/document/service/documentPage?document_id=";

    // Базовая ссылка на пдф документа
    String BASE_DOCUMENT_PDF_LINK = "/document/service/exportDocumentToPdf?document_id=";

    // Тип ответа сервера - json
    String RESPONSE_JSON_MEDIA_TYPE = "application/json;charset=UTF-8";

    /**
     *
     */
    String RESPONSE_PDF_MEDIA_TYPE = "application/pdf";
}
