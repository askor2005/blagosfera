package ru.radom.kabinet.services.pdf.impl;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.radom.kabinet.services.pdf.PdfService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация генератора pdf файлов на основе консольной утилиты WkHtmlToPdf
 * Created by vgusev on 13.01.2016.
 */
@Service(value = "wkHtmlToPdfServiceImpl")
public class WkHtmlToPdfServiceImpl extends BaseHtmlToPdfServiceImpl implements PdfService {

    final static private String DEFAULT_COMMAND_LINE = "C:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltopdf.exe";

    final static private String COMMAND_LINE_SETTINGS_ATTR_NAME = "wkhtmltopdf.path";

    final static private String[] DEFAULT_ARGUMENTS = new String[]{
            "--disable-smart-shrinking", "",
            "--image-quality", "100",
            "--margin-bottom", "0",
            "--margin-left", "0",
            "--margin-right", "0",
            "--margin-top", "0",
            "--page-size", "A4",
            //"--page-height", "29.7cm",
            //"--page-width", "21cm",
            "--dpi", "96",
            //"--zoom", "1.34",
            "--zoom", "1",
            "${htmlFileUri}",
            "${pdfFilePath}"
    };

    @Autowired
    private SettingsManager settingsManager;

    @Override
    public void writeStreamPdfByHtmlInternal(String html, OutputStream outputStream, String exportPdfArguments) {
        File htmlFile = null;
        File pdfFile = null;
        try {
            htmlFile = File.createTempFile("generated_html", ".html");
            pdfFile = File.createTempFile("generated_pdf", ".pdf");
            FileUtils.write(htmlFile, html);

            String htmlFileUri = htmlFile.toURI().toString();
            htmlFileUri = htmlFileUri.replaceAll("file:", "file://");

            String pdfFilePath = pdfFile.getAbsolutePath();

            Map<String, Object> argumentsValues = new HashMap<>();
            argumentsValues.put("htmlFileUri", htmlFileUri);
            argumentsValues.put("pdfFilePath", pdfFilePath);

            String commandLine = settingsManager.getSystemSetting(COMMAND_LINE_SETTINGS_ATTR_NAME, DEFAULT_COMMAND_LINE);
            executeCommandLine(commandLine, DEFAULT_ARGUMENTS, argumentsValues);

            InputStream pdfFileInputStream = new FileInputStream(pdfFile);

            IOUtils.copyLarge(pdfFileInputStream, outputStream);

            pdfFile = new File(pdfFilePath);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            FileUtils.deleteQuietly(htmlFile);
            FileUtils.deleteQuietly(pdfFile);
        }
    }

    /**
     * Выполнить команду по созданию пдф файла
     * @param commandString путь до утилиы
     * @param arguments параметры
     * @param argumentsValues значения параметризированных параметров
     * @throws Exception
     */
    private void executeCommandLine(String commandString, String[] arguments, Map<String, Object> argumentsValues) throws Exception {
        CommandLine cmdLine = new CommandLine(commandString);
        for (String argument : arguments) {
            cmdLine.addArgument(argument);
        }
        cmdLine.setSubstitutionMap(argumentsValues);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        int exitValue = executor.execute(cmdLine);
        if (exitValue == 1) {
            throw new RuntimeException("Произошла ошибка во время вызова утилиты wkhtmltopdf");
        }
    }
}
