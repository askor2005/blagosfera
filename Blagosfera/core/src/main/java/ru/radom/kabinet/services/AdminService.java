package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.radom.kabinet.dao.SmtpServerDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldPossibleValueDao;
import ru.radom.kabinet.model.SmtpServer;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldPossibleValueEntity;
import ru.radom.kabinet.web.admin.SystemSettingForm;

import java.util.*;

@Service("adminService")
public class AdminService {

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private SmtpServerDao smtpServerDao;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private FieldPossibleValueDao fieldPossibleValueDao;

    public void saveSystemSetting(SystemSettingForm systemSettingForm) {
        settingsManager.setSystemSetting(systemSettingForm.getKey(), systemSettingForm.getValue(), systemSettingForm.getDescription());
    }

    public void saveSmtpServer(SmtpServer smtpServerForm) {
        SmtpServer smtpServer = smtpServerDao.getById(smtpServerForm.getId());
        if (smtpServer == null) {
            smtpServer = new SmtpServer();
        }
        smtpServer.setHost(smtpServerForm.getHost());
        smtpServer.setPort(smtpServerForm.getPort());
        smtpServer.setProtocol(smtpServerForm.getProtocol());
        smtpServer.setUsername(smtpServerForm.getUsername());
        smtpServer.setPassword(smtpServerForm.getPassword());
        smtpServer.setUsing(smtpServerForm.isUsing());
        smtpServer.setDebug(smtpServerForm.isDebug());
        smtpServerDao.saveOrUpdate(smtpServer);

        if (smtpServerForm.isUsing()) {
            for (SmtpServer server : smtpServerDao.findAll()) {
                if (!Objects.equals(server.getId(), smtpServer.getId())) {
                    server.setUsing(false);
                    smtpServerDao.update(server);
                }
            }
        }
    }

    public void saveField(FieldEntity field) {
        if (field.getId() == -1) {
            field.setId(null);
        }
        fieldDao.saveOrUpdate(field);

        //предыдущие значения
        List<FieldPossibleValueEntity> oldPossibleValues = fieldPossibleValueDao.getByField(field);
        List<FieldPossibleValueEntity> currentPossibleValues = field.getPossibleValues();
        if (currentPossibleValues == null) {
            currentPossibleValues = new ArrayList<>();
        }
        List<Long> currentPossibleValuesIds = new ArrayList<>();
        Map<FieldPossibleValueEntity, Long> currentPossibleValuesMap = new HashMap<>();

        //зачистим пустые элементы
        for (FieldPossibleValueEntity possibleValue : currentPossibleValues) {
            currentPossibleValuesMap.put(possibleValue, possibleValue.getId());
        }
        for(Map.Entry<FieldPossibleValueEntity, Long> entry : currentPossibleValuesMap.entrySet()) {
            if (entry.getValue() == null) {
                currentPossibleValues.remove(entry.getKey());
            }
        }
        currentPossibleValuesMap.clear();

        //сохраняем текущие значение
        for (FieldPossibleValueEntity possibleValue : currentPossibleValues) {
            if (possibleValue.getId() < 0 ) {
                possibleValue.setId(null);
            }
            possibleValue.setField(field);
            fieldPossibleValueDao.saveOrUpdate(possibleValue);
            currentPossibleValuesIds.add(possibleValue.getId());
        }

        //удаляем убранных значения
        for (FieldPossibleValueEntity participant : oldPossibleValues) {
            if (!currentPossibleValuesIds.contains(participant.getId())) {
                fieldPossibleValueDao.delete(participant);
            }
        }
        oldPossibleValues.clear();
        currentPossibleValues.clear();
        currentPossibleValuesIds.clear();
    }
}