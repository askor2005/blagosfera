package ru.radom.kabinet.services.bpmhandlers.community;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.community.CommunityCreateFileEvent;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMAddFilesToCommunityDto;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.jcr.JcrFilesService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.web.jcr.dto.ResponseJcrFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("addFilesToCommunityHandler")
@Transactional
public class BPMAddFilesToCommunityHandler implements BPMHandler {

    private static final Logger logger = LoggerFactory.getLogger(BPMAddFilesToCommunityHandler.class);

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private JcrFilesService jcrFilesService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        logger.info("Запуск создания файлов объединения.");
        List<ResponseJcrFile> result = new ArrayList<>();
        Community community = null;
        try {
            // Прочитать параметры из таска - взять ИД объединения, сохранить файлы объединения
            // Положить ссылки в файлы объединения
            BPMAddFilesToCommunityDto bpmCommunityFilesDto = serializeService.toObject(parameters, BPMAddFilesToCommunityDto.class);
            if (bpmCommunityFilesDto != null && bpmCommunityFilesDto.getCommunityFiles() != null) {
                community = communityDataService.getByIdMediumData(bpmCommunityFilesDto.getCommunityId());
                logger.info("Объединение в которое добавляются файлы: " + community.getFullRuName() + " (ИД: " + community.getId() + ").");
                for (String fileName : bpmCommunityFilesDto.getCommunityFiles().keySet()) {
                    InputStream fileInputStream = null;
                    try {
                        String filePath = bpmCommunityFilesDto.getCommunityFiles().get(fileName);
                        File file = new File(filePath);
                        long fileSize = file.length();
                        fileInputStream = new FileInputStream(file);

                        ResponseJcrFile responseJcrFile = jcrFilesService.saveFile(fileName, fileSize, fileInputStream, community, null, null, community.getCreator());
                        result.add(responseJcrFile);
                    } finally {
                        IOUtils.closeQuietly(fileInputStream);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ExceptionUtils.check(true, e.getMessage());
        }

        if (community != null) {
            for (ResponseJcrFile responseJcrFile : result) {
                blagosferaEventPublisher.publishEvent(new CommunityCreateFileEvent(this, CommunityEventType.CREATE_FILE, community, responseJcrFile.getFileName(), responseJcrFile.getLink(), community.getCreator()));
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("files", result);
        return serializeService.toPrimitiveObject(resultMap);
    }

}
