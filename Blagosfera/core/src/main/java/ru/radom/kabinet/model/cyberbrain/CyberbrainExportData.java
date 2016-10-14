package ru.radom.kabinet.model.cyberbrain;

import org.springframework.stereotype.Component;

@Component
public interface CyberbrainExportData {
    public String getExportData(boolean withHeaders);
}
