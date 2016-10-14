package ru.radom.kabinet.services.pdf.impl;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
 * Реализация генератора pdf файлов на основе консольной утилиты ebook-convert (calibre)
 * Created by vgusev on 05.02.2016.
 */
@Service(value = "calibreHtmlToPdfServiceImpl")
public class CalibreHtmlToPdfServiceImpl extends BaseHtmlToPdfServiceImpl implements PdfService {

    final static private String DEFAULT_COMMAND_LINE = "C:\\Program Files (x86)\\Calibre2\\ebook-convert.exe";

    final static private String COMMAND_LINE_SETTINGS_ATTR_NAME = "calibre.path";

    final static private String[] DEFAULT_ARGUMENTS = {
            "${htmlFilePath}",
            "${pdfFilePath}",
            "--disable-font-rescaling", "",
            "--margin-bottom", "0",
            "--margin-left", "0",
            "--margin-right", "0",
            "--margin-top", "0",
            "--paper-size", "a4",
            //"--custom-size", "202x285.2",
            "--custom-size", "210x297",
            "--unit", "millimeter"
    };

    /**
     * Параметры для тулзы из настроек
     */
    final static private String CALIBRE_ARGUMENTS_ATTR_NAME = "calibre.arguments";

    @Autowired
    private SettingsManager settingsManager;

    @Override
    public void writeStreamPdfByHtmlInternal(String html, OutputStream outputStream, String exportPdfArguments) {
        if (StringUtils.isBlank(exportPdfArguments)) {
            exportPdfArguments = settingsManager.getSystemSetting(CALIBRE_ARGUMENTS_ATTR_NAME, null);
        }
        String[] arguments;
        if (exportPdfArguments != null) {
            arguments = exportPdfArguments.split(" ");
        } else {
            arguments = DEFAULT_ARGUMENTS;
        }
        /*List<String> args = new ArrayList<>(Arrays.asList(arguments));
        args.add("--pdf-mono-family");
        args.add("Liberation Mono");
        arguments = args.toArray(new String[]{});*/

        File htmlFile = null;
        File pdfFile = null;
        try {
            htmlFile = File.createTempFile("generated_html", ".html");
            pdfFile = File.createTempFile("generated_pdf", ".pdf");
            // TODO Магия со шрифтами
            html = html.replaceAll("Courier New,","").replaceAll("Courier,", "");
            html = html.replaceAll("Courier New","monospace").replaceAll("Courier","monospace");

            FileUtils.write(htmlFile, html);

            String htmlFilePath = htmlFile.getAbsolutePath();
            String pdfFilePath = pdfFile.getAbsolutePath();

            Map<String, Object> argumentsValues = new HashMap<>();
            argumentsValues.put("htmlFilePath", htmlFilePath);
            argumentsValues.put("pdfFilePath", pdfFilePath);

            String commandLine = settingsManager.getSystemSetting(COMMAND_LINE_SETTINGS_ATTR_NAME, DEFAULT_COMMAND_LINE);
            executeCommandLine(commandLine, arguments, argumentsValues);

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
            throw new RuntimeException("Произошла ошибка во время вызова утилиты ebook-convert");
        }
    }
}
