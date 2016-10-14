package ru.radom.kabinet.services.pdf.impl;

import ru.radom.kabinet.services.pdf.PdfService;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Базовый класс генерации pdf файлов
 * Created by vgusev on 24.02.2016.
 */
public abstract class BaseHtmlToPdfServiceImpl implements PdfService {

    @Override
    public byte[] generatePdfByHtml(String html) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeStreamPdfByHtml(html, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void writeStreamPdfByHtml(String html, OutputStream outputStream) {
        writeStreamPdfByHtml(html, outputStream, null);
    }

    @Override
    public void writeStreamPdfByHtml(String html, OutputStream outputStream, String exportPdfArguments) {
        // Должен быть валидный xhml документ. Если сервер linux, то нужно добавить шрифты в каталог /usr/share/fonts/truetype
        String content =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                        "<html>" +
                        "<head>" +
                        "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />" +
                        "</head>" +
                        "<body style='padding: 0px; margin: 0px;'>" + html + "</body>" +
                        "</html>";
        writeStreamPdfByHtmlInternal(content, outputStream, exportPdfArguments);
    }

    /**
     * Реализация конкрентного вида генерации pdf файла
     * @param html html контент документа
     * @param outputStream стрим в который нужно записать результат
     */
    protected abstract void writeStreamPdfByHtmlInternal(String html, OutputStream outputStream, String exportPdfArguments);
}
