package ru.radom.kabinet.services.emailsender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.ParticipantsTypes;
import ru.askor.blagosfera.domain.document.DocumentTemplate;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.flowofdocuments.DocumentTemplateDao;
import ru.radom.kabinet.document.dto.FlowOfDocumentDTO;
import ru.radom.kabinet.document.generator.CreateDocumentParameter;
import ru.radom.kabinet.document.generator.ParticipantCreateDocumentParameter;
import ru.radom.kabinet.document.services.DocumentService;
import ru.radom.kabinet.document.services.DocumentTemplateDataService;
import ru.radom.kabinet.services.EmailService;
import ru.radom.kabinet.services.StompService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Сервис для рассылки сообщений на почту нескольким пользователям из шаблона документа
 * в котором фигурирует только 1 участник - физ лицо
 * Created by vgusev on 25.12.2015.
 */
@Service
@Transactional
public class EmailSenderService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class);

    @Autowired
    private DocumentTemplateDao documentTemplateDao;

    @Autowired
    private DocumentTemplateDataService documentTemplateDataService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private StompService stompService;


    /**
     * Количество загружаемых шаблонов при поиске
     */
    private static final int MAX_COUNT_FIND_TEMPLATES = 10;

    /**
     * Максимальное количество загружаемых участников для одного прохода отправки писем
     */
    private static final int MAX_COUNT_SHARERS_IN_PAGE = 10;

    private class SendToActiveSharersTask implements Runnable {

        private User currentUser;

        private String templateCode;

        private String participantName;

        private String mailSubject;

        private String mailFrom;

        private Function<Integer, List<User>> getSharersFunction;

        private Function<Object, Integer> getCountSharersFunction;

        public SendToActiveSharersTask(
                User currentUser,
                String templateCode, String participantName, String mailSubject,
                String mailFrom, Function<Integer, List<User>> getSharersFunction,
                Function<Object, Integer> getCountSharersFunction) {
            this.currentUser = currentUser;
            this.templateCode = templateCode;
            this.participantName = participantName;
            this.mailSubject = mailSubject;
            this.mailFrom = mailFrom;
            this.getSharersFunction = getSharersFunction;
            this.getCountSharersFunction = getCountSharersFunction;
        }

        @Override
        public void run() {
            int page = 0;
            int loadedCount;
            int countInPage = 10;
            long currentTimeStamp = System.currentTimeMillis();
            int countSharers = getCountSharersFunction.apply(new Object());
            int i = 0;
            do {
                List<User> sharersForSend = getSharersFunction.apply(page);
                for (User sharer : sharersForSend) {
                    sendToUser(currentUser, sharer, templateCode, participantName, mailSubject, mailFrom, currentTimeStamp, countSharers, i);
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                    i++;
                }
                loadedCount = sharersForSend.size();
                page++;
            } while (loadedCount == countInPage);
        }
    }

    /**
     * Функция загрузки пользователей - всех которые не удалены постранично
     */
    private Function<Integer, List<User>> getNotDeletedSharersByPage = page -> userDataService.getNotDeletedByPage(page, MAX_COUNT_SHARERS_IN_PAGE);

    private Function<Integer, List<User>> getNotDeletedManByPage = page -> userDataService.getNotDeletedManByPage(page, MAX_COUNT_SHARERS_IN_PAGE);

    private Function<Integer, List<User>> getNotDeletedWomenByPage = page -> userDataService.getNotDeletedWomenByPage(page, MAX_COUNT_SHARERS_IN_PAGE);

    private Function<Object, Integer> getCountNotDeletedSharersByPage = emptyObject -> userDataService.getTotalCount();

    private Function<Object, Integer> getCountNotDeletedManByPage = emptyObject -> userDataService.getCountNotDeletedManByPage();

    private Function<Object, Integer> getCountNotDeletedWomenByPage = emptyObject -> userDataService.getCountNotDeletedWomenByPage();

    /**
     * Функция загрузки пользователей - по ИДам и по странице
     * @param sharerIds ИДы
     * @return список пользователей
     */
    private Function<Integer, List<User>> getBySharerIdsByPage(List<Long> sharerIds) {
        return page -> userDataService.getByIdsAndPage(sharerIds, page, MAX_COUNT_SHARERS_IN_PAGE);
    }

    private Function<Object, Integer> getCountBySharerIdsByPage(List<Long> sharerIds) {
        return emptyObject -> sharerIds.size();
    }

    /**
     * Найти шаблоны документов на основе которых будут отправлены письма
     * @param templateName название шаблона
     * @return список шаблонов
     */
    @Transactional(readOnly = true)
    public List<DocumentTemplate> findTemplates(String templateName){
        return documentTemplateDataService.getFilteredTemplate(templateName, null, 0, MAX_COUNT_FIND_TEMPLATES);
        //return documentTemplateDao.getList(templateName, ParticipantsTypes.INDIVIDUAL, MAX_COUNT_FIND_TEMPLATES);
    }

    private DocumentTemplate getDocumentTemplate(String templateCode) {
        DocumentTemplate documentTemplate = documentTemplateDataService.getByCode(templateCode);
        ExceptionUtils.check(documentTemplate == null, "Шаблон не найден");
        ExceptionUtils.check(documentTemplate.getDocumentClass() == null, "Не определены источники данных");
        ExceptionUtils.check(documentTemplate.getDocumentClass().getDataSources().size() != 1, "В шаблоне должен быть 1 источник данных");
        ParticipantsTypes participantType = documentTemplate.getDocumentClass().getDataSources().get(0).getType();
        ExceptionUtils.check(!ParticipantsTypes.INDIVIDUAL.equals(participantType), "В шаблоне должен быть источник данных с типом 'физ. лицо'");
        return documentTemplate;
    }

    /**
     * Отправить письма активным участникам системы
     * @param templateCode код шаблона
     * @param mailSubject тема письма
     * @param mailFrom отправитель
     */
    public void sendToActiveSharers(User currentSharer, String templateCode, String mailSubject, String mailFrom) {
        DocumentTemplate documentTemplate = getDocumentTemplate(templateCode);
        String participantName = documentTemplate.getDocumentClass().getDataSources().get(0).getName();
        taskExecutor.execute(new SendToActiveSharersTask(
                currentSharer, templateCode, participantName, mailSubject, mailFrom, getNotDeletedSharersByPage, getCountNotDeletedSharersByPage
        ));
    }

    /**
     * Отправить письма мужчинам
     * @param templateCode код шаблона
     * @param mailSubject тема письма
     * @param mailFrom отправитель
     */
    public void sendToActiveToMan(User currentSharer, String templateCode, String mailSubject, String mailFrom) {
        DocumentTemplate documentTemplate = getDocumentTemplate(templateCode);
        String participantName = documentTemplate.getDocumentClass().getDataSources().get(0).getName();
        taskExecutor.execute(new SendToActiveSharersTask(
                currentSharer, templateCode, participantName, mailSubject, mailFrom, getNotDeletedManByPage, getCountNotDeletedManByPage
        ));
    }

    /**
     * Отправить письма женшинам
     * @param templateCode код шаблона
     * @param mailSubject тема письма
     * @param mailFrom отправитель
     */
    public void sendToActiveWomen(User currentSharer, String templateCode, String mailSubject, String mailFrom) {
        DocumentTemplate documentTemplate = getDocumentTemplate(templateCode);
        String participantName = documentTemplate.getDocumentClass().getDataSources().get(0).getName();
        taskExecutor.execute(new SendToActiveSharersTask(
                currentSharer, templateCode, participantName, mailSubject, mailFrom, getNotDeletedWomenByPage, getCountNotDeletedWomenByPage
        ));
    }

    /**
     * Отправить письмо выбранным пользователям
     * @param templateCode код шаблона
     * @param mailSubject тема письма
     * @param mailFrom отправитель
     * @param sharerIds ИДы пользователей
     */
    public void sendToUsers(User currentSharer, String templateCode, String mailSubject, String mailFrom, List<Long> sharerIds) {
        DocumentTemplate documentTemplate = getDocumentTemplate(templateCode);
        String participantName = documentTemplate.getDocumentClass().getDataSources().get(0).getName();
        taskExecutor.execute(new SendToActiveSharersTask(
                currentSharer, templateCode, participantName, mailSubject, mailFrom, getBySharerIdsByPage(sharerIds), getCountBySharerIdsByPage(sharerIds)
        ));
    }

    /**
     * Отправить сообщение пользователю
     * @param currentSharer
     * @param receiver получатель
     * @param documentTemplateCode код шаблона документа
     * @param participantName название участника в шаблоне
     * @param mailSubject тема письма
     * @param mailFrom отправитель
     * @param currentTimeStamp время отправки запроса
     * @param countSharers
     */
    private void sendToUser(
            User currentSharer, User receiver, String documentTemplateCode,
            String participantName, String mailSubject, String mailFrom,
            long currentTimeStamp, int countSharers, int sharerIndex) {
        try {
            List<CreateDocumentParameter> createDocumentParameters = new ArrayList<>();
            ParticipantCreateDocumentParameter receiverParameter = new ParticipantCreateDocumentParameter(ParticipantsTypes.INDIVIDUAL.getName(), receiver.getId(), participantName);
            CreateDocumentParameter createDocumentParameter = new CreateDocumentParameter(receiverParameter, Collections.emptyList());
            createDocumentParameters.add(createDocumentParameter);
            FlowOfDocumentDTO flowOfDocumentDTO = documentService.generateDocumentDTO(documentTemplateCode, createDocumentParameters);
            emailService.sendTo(receiver, mailSubject, flowOfDocumentDTO.getContent(), mailFrom);
            //logger.info("Пользователю " + receiver.getFullName() + " " + receiver.getId() + " успешно отправлено письмо из шаблона " + documentTemplateCode);
            System.err.println("Пользователю " + receiver.getFullName() + " " + receiver.getId() + " успешно отправлено письмо из шаблона " + documentTemplateCode);
            SendSharerMailDto sendSharerMailDto = new SendSharerMailDto(currentTimeStamp, receiver.getFullName(), true, null, countSharers, sharerIndex);
            stompService.send(currentSharer.getEmail(), "send_sharer_mail", sendSharerMailDto);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                logger.error("При отправке письма ползьователю " + receiver.getFullName() + " возникла ошибка: " + e.getMessage());
                SendSharerMailDto sendSharerMailDto = new SendSharerMailDto(currentTimeStamp, receiver.getFullName(), false, e.getMessage(), countSharers, sharerIndex);
                stompService.send(receiver.getEmail(), "send_sharer_mail", sendSharerMailDto);
            } catch (Exception exc) {
                // давим всё
            }
        }
    }
}
