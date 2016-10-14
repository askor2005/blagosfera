package ru.radom.kabinet.services.notification;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.askor.blagosfera.domain.events.account.transaction.TransactionEvent;
import ru.askor.voting.business.event.VotingEvent;


/**
 * Created by mnikitin on 25.05.2016.
 */

public interface CommonNotificationService {

    void init() throws Exception;

    void onBlagosferaEvent(BlagosferaEvent event);

    void onVotingEvent(VotingEvent event);

    void onTransactionEvent(TransactionEvent transactionEvent);
}
