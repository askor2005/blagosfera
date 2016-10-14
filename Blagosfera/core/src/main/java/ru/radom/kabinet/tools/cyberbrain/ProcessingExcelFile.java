package ru.radom.kabinet.tools.cyberbrain;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Вспомогательный класс для обработки Excel документа
 */
public class ProcessingExcelFile {
    private Map<String, Object> dataMap;
    private ByteArrayOutputStream stream;

    public ProcessingExcelFile(ByteArrayOutputStream stream) {
        this.stream = stream;
    }

    private Map<String, Object> processingStream(String object, ByteArrayOutputStream outputBuffer) throws IOException {
        InputStream is = new ByteArrayInputStream(outputBuffer.toByteArray());

        Workbook workbook = new SXSSFWorkbook(100);

        try {
            workbook = WorkbookFactory.create(is);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();

        List<List<String>> lists = new ArrayList<>();
        List<String> rowsList;

        String txtTemp;
        List<String> errors = new ArrayList<>();

        if (object.equals(sheet.getRow(0).getCell(0).getStringCellValue())) {
            while (it.hasNext()) {
                Row row = it.next();
                rowsList = new ArrayList<>();

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);

                    txtTemp = "";
                    if (cell != null) {
                        // Пока вручную задаем тип ячейки String
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        int cellType = cell.getCellType();

                        switch (cellType) {
                            case Cell.CELL_TYPE_STRING:
                                txtTemp = cell.getStringCellValue();
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                txtTemp = String.valueOf(cell.getNumericCellValue());
                                break;
                            case Cell.CELL_TYPE_FORMULA:
                                txtTemp = String.valueOf(cell.getNumericCellValue());
                                break;
                        }
                    }

                    rowsList.add(txtTemp);
                }

                lists.add(rowsList);
            }

            // В первой строке первой ячейки находится идентификатор хранилища
            lists.remove(0);
        } else {
            errors.add("Файл не предназначен для загрузки в это хранилище!<br/>" +
                    "Идентификатор хранилища считывается из файла в первой ячейке.<br/>" +
                    "Идентификатор этого хранилища = \"" + object + "\"");
        }

        is.close();
        outputBuffer.close();

        Map<String, Object> map = new HashMap<>();
        map.put("data", lists);
        map.put("errors", errors);
        map.put("success", "Импорт данных успешно завершен.");

        return map;
    }

    public Map<String, Object> getDataMap(String object) {
        try {
            dataMap = processingStream(object, stream);
        } catch (IOException e) {
            e.printStackTrace();
            ((List) dataMap.get("errors")).add(e.getCause().getMessage());
        } finally {
            stream = null;
        }

        return dataMap;
    }
}