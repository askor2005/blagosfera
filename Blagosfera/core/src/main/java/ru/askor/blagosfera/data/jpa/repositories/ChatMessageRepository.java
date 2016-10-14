package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.chat.ChatMessage;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.model.chat.FileChatMessageState;

import java.util.Date;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    //List<ChatMessage> findByDialog_IdAndReadOrderByDateDesc(Long dialogId, boolean read);
    //List<ChatMessage> findByDialog_IdAndReadAndSenderIdOrderByDateDesc(Long dialogId, boolean read, Long senderId);
    //List<ChatMessage> findByDialog_IdAndReadAndSenderIdNotOrderByDateDesc(Long dialogId, boolean read, Long senderId);

    List<ChatMessage> findTop20ByIdLessThanAndDialog_IdOrderByDateDesc(Long id, Long dialogId);
    List<ChatMessage> findTop20ByDialog_IdOrderByDateDesc(Long dialogId);

    /**
     * Загрузить все сообщения с файлами у которых дата создания меньше date
     * и статус загрузки fileChatMessageState
     * @param date
     * @param fileChatMessageState
     * @return
     */
    List<ChatMessage> findByDateIsLessThanAndFileChatMessageState(Date date, FileChatMessageState fileChatMessageState);

    List<ChatMessage> findDistinctChatMessageTop20ByDialog_IdAndSender_IdOrDialog_IdAndChatMessageReceivers_Receiver_IdOrderByDateDesc(Long dialogId, Long senderId, Long altDialogId, Long altSenderId);
    List<ChatMessage> findByDialogOrderByDateAsc(DialogEntity dialog);

    List<ChatMessage> findByDialog_IdOrderByDateAsc(Long dialogId);

    List<ChatMessage> findByDialog_IdAndDateGreaterThanAndDateIsLessThanOrderByDateAsc(Long dialogId, Date startDate, Date endDate);




    List<ChatMessage> findByIdLessThanAndDialog_Id(Long id, Long dialogId, Pageable pageable);

    List<ChatMessage> findDistinctChatMessageByDialog_IdAndSender_IdOrDialog_IdAndChatMessageReceivers_Receiver_Id(Long dialogId, Long senderId, Long altDialogId, Long altSenderId, Pageable pageable);


}
