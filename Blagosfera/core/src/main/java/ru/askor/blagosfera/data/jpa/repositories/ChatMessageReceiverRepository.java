package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.chat.ChatMessageReceiver;

import java.util.List;

/**
 *
 * Created by vgusev on 02.10.2015.
 */
public interface ChatMessageReceiverRepository extends JpaRepository<ChatMessageReceiver, Long> {

    // Получить список участников сообщения
    List<ChatMessageReceiver> findByChatMessage_Dialog_IdAndReceiver_IdAndRead(Long dialogId, Long receiverId, boolean read);

    @Query("select count(attr) from ChatMessageReceiver attr where attr.chatMessage.dialog.id = :dialogId and attr.receiver.id = :receiverId and attr.read = :read")
    int countReceiversByParam(@Param("dialogId") Long dialogId, @Param("receiverId") Long receiverId, @Param("read") boolean read);

    @Query(
            "select count(cmr) from ChatMessageReceiver cmr "+
                    " where cmr.chatMessage.dialog.id = :dialogId and " +
                    " cmr.chatMessage.sender.id = :senderId and " +
                    " cmr.receiver.id = :receiverId and " +
                    " cmr.read = false"
    )
    int countUnredMessages(@Param("dialogId") Long dialogId, @Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
}
