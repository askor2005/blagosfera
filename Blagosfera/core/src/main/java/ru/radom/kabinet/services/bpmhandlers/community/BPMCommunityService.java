package ru.radom.kabinet.services.bpmhandlers.community;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityData;
import ru.askor.blagosfera.domain.community.OkvedDomain;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldFile;
import ru.askor.blagosfera.domain.field.FieldsGroup;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.OkvedDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.model.OkvedEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldFileEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMCreateCommunityDto;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.jcr.JcrFilesService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.VarUtils;

import java.util.*;

/**
 *
 * Created by vgusev on 20.01.2016.
 */
@Service
@Transactional
public class BPMCommunityService {

    private static final Logger logger = LoggerFactory.getLogger(BPMCommunityService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private CommunityDataService communityDomainService;

    @Autowired
    private UserDataService userDataService;

    /*@Autowired
    private SharerDao sharerDao;

    @Autowired
    private CommunityDao communityDao;*/

    @Autowired
    private OkvedDao okvedDao;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private JcrFilesService jcrFilesService;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    // -TODO Переделать на BPMHandler

    /*@RabbitListener(queues = "community.create")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void handleCreateCommunity(Message message,
                                      @Header(value = BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId) {
        try {
            // Транзакцию запускаем обособленно
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    logger.info("Задача " + taskId + ". Запуск создания объединения.");
                    Map<String, Object> data = (Map<String, Object>) rabbitTemplate.getMessageConverter().fromMessage(message);
                    Long communityId = -1l;
                    try {
                        BPMCreateCommunityDto bpmCommunityDto = serializeService.toObject(data, BPMCreateCommunityDto.class);
                        logger.info("Задача " + taskId + ". Название создаваемого объединения: " + bpmCommunityDto.getFullName());
                        validateData(bpmCommunityDto);
                        Community community = createCommunity(bpmCommunityDto);
                        if (community != null && community.getId() != null) {
                            communityId = community.getId();
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, communityId));
                        if (communityId == -1l) {
                            logger.info("Задача " + taskId + ". При создании объединения прозошла ошибка.");
                        } else {
                            logger.info("Задача " + taskId + ". Объединение создано успешно.");
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }*/

    private void validateData(BPMCreateCommunityDto bpmCommunityDto) {
        //
    }

    // -TODO Переделать на BPMHandler
    /*@RabbitListener(queues = "community.add.files")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addFilesToCommunity(Message message,
                                    @Header(value = BPMBlagosferaUtils.TASK_ID_HEADER_KEY) String taskId) {
        try {
            // Транзакцию запускаем обособленно
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    logger.info("Задача " + taskId + ". Запуск создания файлов объединения.");
                    Map<String, Object> data = (Map<String, Object>) rabbitTemplate.getMessageConverter().fromMessage(message);
                    List<ResponseJcrFile> result = new ArrayList<>();
                    Community community = null;
                    boolean hasError = true;
                    try {
                        // Прочитать параметры из таска - взять ИД объединения, сохранить файлы объединения
                        // Положить ссылки в файлы объединения
                        BPMAddFilesToCommunityDto bpmCommunityFilesDto = serializeService.toObject(data, BPMAddFilesToCommunityDto.class);
                        if (bpmCommunityFilesDto != null && bpmCommunityFilesDto.getCommunityFiles() != null) {
                            community = communityDomainService.getByIdMediumData(bpmCommunityFilesDto.getCommunityId());
                            logger.info("Задача " + taskId + ". Объединение в которое добавляются файлы: " + community.getFullRuName() + " (ИД: " + community.getId() + ").");
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
                            hasError = false;
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("files", result);
                        blagosferaEventPublisher.publishEvent(new BpmFinishTaskEvent(this, taskId, serializeService.toPrimitiveObject(resultMap)));
                        if (community != null) {
                            for (ResponseJcrFile responseJcrFile : result) {
                                blagosferaEventPublisher.publishEvent(new CommunityCreateFileEvent(this, CommunityEventType.CREATE_FILE, community, responseJcrFile.getFileName(), responseJcrFile.getLink(), community.getCreator()));
                            }
                        }
                        if (hasError) {
                            logger.info("Задача " + taskId + ". При добавлении в объединение файлов произошла ошибка.");
                        } else {
                            logger.info("Задача " + taskId + ". В объединение: " + community.getFullRuName() + " (ИД: " + community.getId() + ") файлы добавлены успешно.");
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }*/
}
