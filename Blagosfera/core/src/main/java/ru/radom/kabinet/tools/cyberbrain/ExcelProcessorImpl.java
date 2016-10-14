package ru.radom.kabinet.tools.cyberbrain;

import org.apache.commons.io.output.DeferredFileOutputStream;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Component
public class ExcelProcessorImpl implements ExcelProcessor {

    @Override
    public Map<String, Object> getDataList(String object, OutputStream data, boolean withHeaders) {
        DeferredFileOutputStream deferredFileOutputStream = (DeferredFileOutputStream) data;

        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        Map<String, Object> dataMap = null;

        try {
            outputBuffer.write(deferredFileOutputStream.getData());
            dataMap = getListExcelData(object, outputBuffer, withHeaders);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataMap;
    }

    /**
     * Получить коллекцию данных из Excel
     * @param outputBuffer байт массив
     */
    private Map<String, Object> getListExcelData(String object, ByteArrayOutputStream outputBuffer, boolean withHeaders) {
        ProcessingExcelFile processingExcelFile = new ProcessingExcelFile(outputBuffer);
        Map<String, Object> dataMap =  processingExcelFile.getDataMap(object);

        if (!withHeaders) {
            if (((List) dataMap.get("errors")).size() == 0) {
                // Подразумевается что в первой строке заголовки
                ((List<List<String>>) dataMap.get("data")).remove(0);
            }
        }

        return dataMap;
    }
}