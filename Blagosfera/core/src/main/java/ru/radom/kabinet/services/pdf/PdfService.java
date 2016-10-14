package ru.radom.kabinet.services.pdf;

import org.springframework.stereotype.Service;

import java.io.OutputStream;

/**
 * Сервис для работы с pdf файлами
 * Created by vgusev on 13.01.2016.
 */
@Service
public interface PdfService {

    /**
     * Сгенерировать pdf файл по html строке
     * @param html содердимое html документа
     * @return массив байтов pdf документа
     */
    byte[] generatePdfByHtml(String html);

    /**
     * Записать данные стрима файла pdf в outputStream (в реализации лучше использовать буферную запись)
     * @param html содердимое html документа
     * @param outputStream стрим, в который пишем данные
     */
    void writeStreamPdfByHtml(String html, OutputStream outputStream);

    /**
     *
     * @param html
     * @param outputStream
     * @param exportPdfArguments
     */
    void writeStreamPdfByHtml(String html, OutputStream outputStream, String exportPdfArguments);
}
