package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.events.community.CommunityEvent;
import ru.askor.blagosfera.domain.events.document.RameraFlowOfDocumentEvent;
import ru.askor.blagosfera.domain.events.document.RameraFlowOfDocumentEventType;
import ru.askor.blagosfera.domain.events.user.SharerEvent;
import ru.askor.blagosfera.domain.events.user.SharerEventType;
import ru.radom.kabinet.dao.log.*;
import ru.radom.kabinet.model.log.CommunityMemberVerifiableLog;
import ru.radom.kabinet.model.log.DocumentSignVerifiableLog;
import ru.radom.kabinet.model.log.RecoveryPasswordVerifiableLog;

/**
 * Сервис по логированию проверяемых действий.
 *
 * Created by ebelyaev on 12.08.2015.
 */
// TODO reimplement in logging module
@Deprecated
@Service("verifiableLogService")
public class VerifiableLogService {

    @Autowired
    LoginSuccessVerifiableLogDao loginSuccessVerifiableLogDao;

    @Autowired
    LoginFailureVerifiableLogDao loginFailureVerifiableLogDao;

    @Autowired
    DocumentSignVerifiableLogDao documentSignVerifiableLogDao;

    @Autowired
    CommunityMemberVerifiableLogDao communityMemberVerifiableLogDao;

    @Autowired
    RecoveryPasswordVerifiableLogDao recoveryPasswordVerifiableLogDao;

    @EventListener
    public void onBlagosferaEvent(BlagosferaEvent event) {
        if(event instanceof SharerEvent) {
            SharerEvent sharerEvent = (SharerEvent) event;
            onSharerEvent(sharerEvent);
        } else if(event instanceof CommunityEvent) {
            CommunityEvent communityEvent = (CommunityEvent) event;
            onCommunityEvent(communityEvent);
        } else if(event instanceof RameraFlowOfDocumentEvent) {
            RameraFlowOfDocumentEvent rameraFlowOfDocumentEvent = (RameraFlowOfDocumentEvent) event;
            onRameraFlowOfDocumentEvent(rameraFlowOfDocumentEvent);
        }
    }

    private void onRameraFlowOfDocumentEvent(RameraFlowOfDocumentEvent rameraFlowOfDocumentEvent) {
        // логирование подписи документа
        if(rameraFlowOfDocumentEvent.getDocumentEventType().equals(RameraFlowOfDocumentEventType.SIGN_DOCUMENT)) {
            DocumentSignVerifiableLog documentSignVerifiableLog = new DocumentSignVerifiableLog(rameraFlowOfDocumentEvent);
            documentSignVerifiableLogDao.saveOrUpdate(documentSignVerifiableLog);
        }
    }

    private void onCommunityEvent(CommunityEvent communityEvent) {
        // логирование вступлений и выходов из объединений
        if(communityEvent.getType().equals(CommunityEventType.JOIN) || communityEvent.getType().equals(CommunityEventType.LEAVE)) {
            CommunityMemberVerifiableLog communityMemberVerifiableLog = new CommunityMemberVerifiableLog(communityEvent);
            communityMemberVerifiableLogDao.saveOrUpdate(communityMemberVerifiableLog);
        }

    }

    private void onSharerEvent(SharerEvent sharerEvent) {
        // логирование восстановления пароля
        if(sharerEvent.getType().equals(SharerEventType.RECOVERY_PASSWORD_COMPLETE) || sharerEvent.getType().equals(SharerEventType.RECOVERY_PASSWORD_INIT)) {
            RecoveryPasswordVerifiableLog recoveryPasswordVerifiableLog = new RecoveryPasswordVerifiableLog(sharerEvent);
            recoveryPasswordVerifiableLogDao.saveOrUpdate(recoveryPasswordVerifiableLog);
        }
    }
}
