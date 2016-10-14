package ru.radom.kabinet.services.bpmhandlers.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SystemSettingsService;

import java.util.Collection;
import java.util.Map;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("getSystemSettingsHandler")
@Transactional
public class BPMGetSystemSettingsHandler implements BPMHandler {

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        Collection<String> keys = ((Map<String, ?>)parameters).keySet();
        return systemSettingsService.getByKeysWithSmartConversion(keys);
    }
}