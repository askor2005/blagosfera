package ru.radom.kabinet.services.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.radom.kabinet.document.services.DocumentService;

import java.util.Collections;

/**
 *
 * Created by vgusev on 16.12.2015.
 */
@Transactional
@Service
public class TestService {

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    public void testTransactionalEvent() {
        BpmRaiseSignalEvent bpmRaiseSignalEvent = new BpmRaiseSignalEvent(this, "testTransaction111", Collections.emptyMap());
        System.err.println("qwdqwdqwd");
        blagosferaEventPublisher.publishEvent(bpmRaiseSignalEvent);
        System.err.println("22222");
    }
}
