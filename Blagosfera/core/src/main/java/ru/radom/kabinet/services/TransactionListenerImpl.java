package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.askor.blagosfera.domain.events.bpm.BpmFinishTaskEvent;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalEvent;
import ru.askor.blagosfera.domain.events.bpm.BpmRaiseSignalsEvent;
import ru.askor.blagosfera.domain.events.community.PublishCommunityMemberEventsCallbackEvent;
import ru.askor.blagosfera.domain.events.document.NotifyUnsignDocumentCallbackEvent;
import ru.askor.blagosfera.domain.events.notification.PushToDevicesCallbackEvent;
import ru.askor.blagosfera.domain.events.voting.BatchVotingStateChangeCallbackEvent;
import ru.radom.blagosferabp.activiti.BPMBlagosferaUtils;

@Component("transactionListener")
@Transactional
public class TransactionListenerImpl implements TransactionListener {

    @Autowired
    private BPMBlagosferaUtils bpmBlagosferaUtils;

    public TransactionListenerImpl () {
    }

    @EventListener
    public void raiseBpmSignal(BpmRaiseSignalEvent bpmEvent) {
        bpmBlagosferaUtils.raiseSignal(bpmEvent.getSignal(), bpmEvent.getPayload());
    }

    @EventListener
    public void raiseBpmSignals(BpmRaiseSignalsEvent bpmEvent) {
        for (BpmRaiseSignalEvent event : bpmEvent.getEvents()) {
            bpmBlagosferaUtils.raiseSignal(event.getSignal(), event.getPayload());
        }
    }

    @TransactionalEventListener
    public void finishBpmTask(BpmFinishTaskEvent bpmEvent) {
        bpmBlagosferaUtils.finishTask(bpmEvent.getTaskId(), bpmEvent.getPayload());
    }

    @TransactionalEventListener
    public void notifyUnsignDocument(NotifyUnsignDocumentCallbackEvent notifyUnsignDocumentCallbackEvent) {
        notifyUnsignDocumentCallbackEvent.doCallback();
    }

    @EventListener
    public void batchVotingStateChange(BatchVotingStateChangeCallbackEvent batchVotingStateChangeCallbackEvent) {
        batchVotingStateChangeCallbackEvent.doCallback();
    }

    @TransactionalEventListener
    public void pushToDevices(PushToDevicesCallbackEvent pushToDevicesCallbackEvent) {
        pushToDevicesCallbackEvent.doCallback();
    }

    @TransactionalEventListener
    public void publishCommunityMemberEvents(PublishCommunityMemberEventsCallbackEvent publishCommunityMemberEventsCallbackEvent) {
        publishCommunityMemberEventsCallbackEvent.doCallback();
    }
}
