package ru.radom.kabinet.tools.cyberbrain;

import java.io.OutputStream;
import java.util.Map;

public interface ExcelProcessor {
    public Map<String, Object> getDataList(String object, OutputStream data, boolean withHeaders);
}