package ru.radom.kabinet.module.blagosfera.bp.util;

/**
 * Created by Otts Alexey on 06.11.2015.<br/>
 * Сигналы, которые отправляются через Rabbit в BPM
 */
public class BPMRabbitSignals {

    /**
     * Юзер стал online
     */
    public final static String USER_BECOME_ONLINE = "userBecomeOnline";

    /**
     * Юзер стал offline
     */
    public final static String USER_BECOME_OFFLINE = "userBecomeOffline";

    /**
     * Был создан новый документ
     */
    public final static String DOCUMENT_CREATED = "documentCreated";

    /**
     * Документ подписан каким то пользователем
     */
    public final static String DOCUMENT_SIGNED_BY_SHARER = "documentSignedBySharer";

    /**
     * Документ подписан всеми
     */
    public final static String DOCUMENT_SIGNED = "documentSigned";

    public final static String SHARER_VERIFIED = "sharerVerifiedEvent";
}
