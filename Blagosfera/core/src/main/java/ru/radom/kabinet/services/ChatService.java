package ru.radom.kabinet.services;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.ChatMessageReceiverRepository;
import ru.askor.blagosfera.data.jpa.repositories.ChatMessageRepository;
import ru.askor.blagosfera.data.jpa.repositories.DialogsRepository;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.chat.ChatEvent;
import ru.askor.blagosfera.domain.events.chat.ChatEventType;
import ru.askor.blagosfera.domain.events.chat.DialogEvent;
import ru.askor.blagosfera.domain.events.chat.DialogEventType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.ChatMessage;
import ru.radom.kabinet.model.chat.ChatMessageReceiver;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.model.chat.FileChatMessageState;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.*;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

// сервис по работе с чатом

@Transactional
@Service("chatService")
public class ChatService {
	// ограничение на возраст сообщения в часах, после которого недопустимо
	// редактирование и удаление
	public static final int MESSAGE_EDIT_DELETE_AGE_THRESHOLD = 24;

    public static final String CHAT_NAME_MAX_LENGTH = "chat_name_max_length";
    public static final String ERROR_CURRENT_SHARER_NOT_IN_CHAT = "Вы не являетесь участником данного чата.";

    public static final int MAX_CHAT_MESSAGES_TO_LOAD = 20;

    // Сообщение - загрузка файла отменена
    public static final String FILE_CHAT_MESSAGE_UPLOAD_CANCEL_STOMP_PATH = "file_chat_message_upload_cancel";

    // Сообщение - процент загрузки файлов обновлён
    public static final String FILE_CHAT_MESSAGE_UPLOAD_UPDATE_PERCENT_STOMP_PATH = "file_chat_message_upload_update_percent";

    // Сообщение - файл загружен
    public static final String FILE_CHAT_MESSAGE_UPLOAD_FINISH_STOMP_PATH = "file_chat_message_upload_finish";

    // Системная настройка - разрешённые форматы файлов для загрузки
    public static final String TRUSTED_FILE_EXTENSIONS_SYS_ATTR_NAME = "chat.file.extensions";

    // Максимальный размер загружаемого файла в чат
    public static final String MAX_UPLOADED_FILE_SIZE = "chat.file.max.size";

    @Autowired
	private BlagosferaEventPublisher blagosferaEventPublisher;

	@Autowired
	private StompService stompService;

	@Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatMessageReceiverRepository chatMessageReceiverRepository;

    @Autowired
    private DialogsRepository dialogsRepository;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private RequestContext radomRequestContext;

    private int chatNameMaxLength = 20;

    @PostConstruct
    private void init() {
        try {
            String value = settingsManager.getSystemSetting(CHAT_NAME_MAX_LENGTH);
            chatNameMaxLength = Integer.valueOf(value);
        } catch (Throwable e) {}
    }

    /**
     * Отправка сообщения. Если fileSize не null, то считаем, что загружается файл
     * @param sender
     * @param dialog
     * @param text
     * @param uuid
     * @param fileSize
     * @return
     */
    private ChatMessage addMessageInternal(UserEntity sender, DialogEntity dialog, String text, String uuid, Long fileSize) {
		if (dialog == null) throw new ChatException("Чат не существует");
        if (!dialog.getUsersIds().contains(sender.getId())) throw new ChatException("Вы не являетесь участником чата");
        if (StringUtils.isEmpty(text)) throw new ChatException("Текст сообщения не может быть пустым");

        ChatMessage chatMessage;
        if (fileSize == null) {
            chatMessage = new ChatMessage(sender, dialog, text);
        } else {
            String[] extensions = getFileExtensions();
            Long maxFileSize = getMaxFileSize();
            if (extensions != null && extensions.length > 0) {
                String extension = FilenameUtils.getExtension(text);
                if (!StringUtils.containsCaseInsensitive(extension, extensions)) {
                    throw new RuntimeException("Недопустимый формат файла " + extension + ". Допустимы следующие форматы: " + org.apache.commons.lang3.StringUtils.join(extensions, ", ") + ".");
                }
            }

            if (fileSize != null && maxFileSize != null && fileSize > maxFileSize) {
                throw new RuntimeException("Максимальный размер загружаемого файла должен быть не более " + HumansStringUtils.getFileSize(maxFileSize));
            }
            chatMessage = new ChatMessage(sender, dialog, text, fileSize);
        }
        chatMessage.setUuid(uuid);
        chatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        List<ChatMessageReceiver> chatMessageReceivers = new ArrayList<>();
        for (Long receiverId : dialog.getUsersIds()) {
            if (sender.getId().equals(receiverId)) continue;
            UserEntity receiver =  sharerDao.getById(receiverId);
            ChatMessageReceiver chatMessageReceiver = new ChatMessageReceiver(receiver, chatMessage, false);
            chatMessageReceiverRepository.save(chatMessageReceiver);
            chatMessageReceivers.add(chatMessageReceiver);
        }
        chatMessage.getChatMessageReceivers().addAll(chatMessageReceivers);
		return chatMessage;
	}

    public ChatMessage addMessage(Long userId, DialogEntity dialog, String text, String uuid, Long fileSize) {
        ExceptionUtils.check(dialog.isClosed(), "Диалог закрыт. Добавление сообщений невозможно.");
        ChatMessage chatMessage = addMessageInternal(sharerDao.getById(userId), dialog, text, uuid, fileSize);
        blagosferaEventPublisher.publishEvent(new ChatEvent(this, ChatEventType.ADD, chatMessage));
        return chatMessage;
    }

	// проверка возраста сообщения на предмет того, может ли оно редактироваться
	// и удаляться или уже нет

	public boolean checkAge(ChatMessage message) {
		return !DateUtils.isOlderThan(message.getDate(), Calendar.HOUR_OF_DAY, MESSAGE_EDIT_DELETE_AGE_THRESHOLD);
	}

	// редактирование сообщения
    public ChatMessage editMessage(Long userId, Long messageId, String text) {
		ChatMessage message = chatMessageRepository.findOne(messageId);
        DialogEntity dialog = message.getDialog();
        ExceptionUtils.check(dialog.isClosed(), "Диалог закрыт. Редактирование сообщений невозможно.");

        if (message == null) throw new ChatException("Сообщение не существует");
        if (userId == null) throw new ChatException("Участник не существует");
		if (StringUtils.isEmpty(text)) throw new ChatException("Текст сообщения не может быть пустым");
        if (!userId.equals(message.getSender().getId())) throw new ChatException("Нельзя редактировать чужое сообщение");
		if (!checkAge(message)) throw new ChatException("Нельзя редактировать сообщение, написанное более суток назад");
        if (message.getFileMessage() != null && message.getFileMessage()) {
            throw new ChatException("Редактировать сообщение с файлом нельзя");
        }

		message.setText(text);
		message.setEditDate(new Date());
		message.setEditCount(message.getEditCount() + 1);

        message = chatMessageRepository.save(message);

        blagosferaEventPublisher.publishEvent(new ChatEvent(this, ChatEventType.EDIT, message));
		return message;
	}

	// удаление сообщения
    public ChatMessage deleteMessage(Long userId, Long messageId) {
        ChatMessage message = chatMessageRepository.findOne(messageId);

        DialogEntity dialog = message.getDialog();
        ExceptionUtils.check(dialog.isClosed(), "Диалог закрыт. Удаление сообщений невозможно.");

        if (message == null) throw new ChatException("Сообщение не существует");
		if (userId == null) throw new ChatException("Участник не существует");

        if (message.getFileMessage() != null && message.getFileMessage()) {
            throw new ChatException("Удалять сообщение с файлом нельзя");
        }

        if (checkAge(message) && userId.equals(message.getSender().getId())) {
            message.setDeleted(true);
            message.setText("");
            message = chatMessageRepository.save(message);
		} else{
            //throw new ChatException("Нельзя удалить	сообщение, написанное более суток назад");
            ChatMessageReceiver foundChatMessageReceiver = null;
            for(ChatMessageReceiver chatMessageReceiver : message.getChatMessageReceivers()) {
                if (chatMessageReceiver.getReceiver().getId().equals(userId)) {
                    foundChatMessageReceiver = chatMessageReceiver;
                    break;
                }
            }
            if (foundChatMessageReceiver != null) {
                message.getChatMessageReceivers().remove(foundChatMessageReceiver);
            }
            //message.getReceivers().remove(sharer.getId());
        }

        blagosferaEventPublisher.publishEvent(new ChatEvent(this, ChatEventType.DELETE, message));
		return message;
	}

	// отмечаем сообщения от одного участника к другому как прочитанные
    public void markAsRead(Long currentUserId, DialogEntity dialog) {
        List<ChatMessageReceiver> chatMessageReceivers = chatMessageReceiverRepository.findByChatMessage_Dialog_IdAndReceiver_IdAndRead(dialog.getId(), currentUserId, false);
        for (ChatMessageReceiver chatMessageReceiver : chatMessageReceivers) {
            chatMessageReceiver.setRead(true);
            chatMessageReceiverRepository.save(chatMessageReceiver);
        }
        stompService.send(userDataService.getByIdMinData(currentUserId).getEmail(), "mark_as_read", dialog.getId());

        /*List<ChatMessage> messages = chatMessageRepository.findByDialog_IdAndReadOrderByDateDesc(dialog.getId(), false);
        Set<Sharer> senders = new HashSet<>();

        for (ChatMessage message : messages) {
            if (message.getReceivers().contains(currentUserId)) {
                message.setRead(true);
                chatMessageRepository.save(message);

                senders.add(message.getSender());
            }
        }*/
        // Самому себе мы тоже отправляем событие, что причитаны сообщения, чтобы в других вкладках они скрылись
        //senders.add(sharerDao.getById(currentUserId));
/*
        for (Sharer sender : senders) {
            stompService.send(sender, "mark_as_read", dialog.getId());
        }*/
	}

	// обработка stomp-сообщения о том что кто-то в данный момент набирает
	// сообщение

	public void processPrintMessage(User sender, User receiver, DialogEntity dialog) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("senderId", sender.getId());
        resultJson.put("senderName", sender.getMediumName());
        resultJson.put("dialogId", dialog.getId());
		String payload = resultJson.toString();
		stompService.send(receiver.getEmail(), "print_chat_message_to_client", payload);
	}

	// получение количества диалогов пользователя, в которых есть непрочитанные
	// сообщения

	public int getUnreadDialogsCount(UserEntity userEntity) {
        List<DialogEntity> dialogs = getVisibleDialogs(userEntity.getId());
        int count = 0;

        for (DialogEntity dialog : dialogs) {
            //List<ChatMessage> messages = chatMessageRepository.findByDialog_IdAndReadAndSenderIdNotOrderByDateDesc(dialog.getId(), false, sharer.getId());

            List<ChatMessageReceiver> chatMessageReceivers = chatMessageReceiverRepository.findByChatMessage_Dialog_IdAndReceiver_IdAndRead(dialog.getId(), userEntity.getId(), false);
            count += chatMessageReceivers.size();
            //chatMessageReceiverRepository.findByReceiver_IdAndRead()
            /*for (ChatMessage message : messages) {
                if (message.getReceivers().contains(sharer.getId())) {
                    count++;
                    break;
                }
            }*/
        }

		return count;
	}

    public void deleteSharer(Long currentSharerId, Long sharerId, Long dialogId) throws ChatException {
        DialogEntity dialog = dialogsRepository.getOne(dialogId);

        if (dialog == null) throw new ChatException("Диалог не найден");
        if (!dialog.hasUser(currentSharerId)) throw new ChatException(ERROR_CURRENT_SHARER_NOT_IN_CHAT);
        if (!dialog.hasUser(sharerId)) throw new ChatException("Участник не найден");
        if (!currentSharerId.equals(sharerId) && !currentSharerId.equals(dialog.getAdminId())) throw new ChatException("Вы не можете удалять участников");

        UserEntity userEntity = dialog.getUser(sharerId);

        //dialog.getSharers().remove(sharer);
        for (Iterator<UserEntity> iterator = dialog.getUsers().iterator(); iterator.hasNext();) {
            if(iterator.next().getId().equals(userEntity.getId())) iterator.remove();
        }

        if ((dialog.getAdminId() != null) && (dialog.getAdminId().equals(sharerId))) {
            Iterator<UserEntity> iterator = dialog.getUsers().iterator();
            if (iterator.hasNext()) {
                dialog.setAdminId(iterator.next().getId());
            } else {
                dialog.setAdminId(null);
            }
        }

        userEntity.getDialogs().remove(dialog);
        sharerDao.save(userEntity);

        dialogsRepository.save(dialog);

        blagosferaEventPublisher.publishEvent(new DialogEvent(this, DialogEventType.SHARER_DELETED, dialog, currentSharerId, userEntity));
    }

    public void addSharer(Long currentSharerId, Long sharerId, Long dialogId) throws ChatException {
        if (currentSharerId.equals(sharerId)) throw new ChatException("Вы не можете добавить сами себя");

        DialogEntity dialog = dialogsRepository.getOne(dialogId);

        if (dialog == null) throw new ChatException("Диалог не найден");
        if (!dialog.hasUser(currentSharerId)) throw new ChatException(ERROR_CURRENT_SHARER_NOT_IN_CHAT);
        if (dialog.hasUser(sharerId)) throw new ChatException("Участник уже добавлен");
        if ((dialog.getAdminId() != null) && !currentSharerId.equals(dialog.getAdminId())) throw new ChatException("Вы не можете добавлять участников");

        if (dialog.getName() == null) {
            for (UserEntity userEntity : dialog.getUsers()) {
                if (!userEntity.getId().equals(currentSharerId)) {
                    dialog.setName(userEntity.getMediumName());
                    break;
                }
            }
        }

        UserEntity userEntity = sharerDao.getById(sharerId);

        if (dialog.getName() == null) dialog.setName(userEntity.getMediumName());

        dialog.getUsers().add(userEntity);
        dialog.setAdminId(currentSharerId);
        dialogsRepository.save(dialog);

        userEntity.getVisibleDialogs().add(dialog.getId());
        sharerDao.update(userEntity);

        blagosferaEventPublisher.publishEvent(new DialogEvent(this, DialogEventType.SHARER_ADDED, dialog, currentSharerId, userEntity));
    }

    public void renameDialog(Long currentSharerId, String name, Long dialogId) throws ChatException {
        DialogEntity dialog = dialogsRepository.getOne(dialogId);

        if (dialog == null) throw new ChatException("Диалог не найден");
        if (!currentSharerId.equals(dialog.getAdminId())) throw new ChatException("Вы не можете переименовать диалог");
        if ((name == null) || name.isEmpty()) throw new ChatException("Название не может быть пустым");

        dialog.setName(name);
        dialogsRepository.save(dialog);

        blagosferaEventPublisher.publishEvent(new DialogEvent(this, DialogEventType.RENAMED, dialog, currentSharerId, null));
    }

    public List<DialogEntity> getVisibleDialogs(Long userId) {
        UserEntity userEntity = sharerDao.getById(userId);
        List<DialogEntity> dialogs = dialogsRepository.findByIdInAndUsers_Id(userEntity.getVisibleDialogs(), userEntity.getId());
        for (DialogEntity dialog : dialogs) {
            if (dialog.getName() == null) {
                for (UserEntity companion : dialog.getUsers()) {
                    if (!companion.getId().equals(userEntity.getId())) {
                        dialog.setName(companion.getFullName());
                        break;
                    }
                }
            }
        }
        return dialogs;
    }

    public DialogEntity getDialog(Long dialogId, Long userId) {
        return dialogsRepository.getByIdAndUsers_Id(dialogId, userId);
    }

    public void deleteVisibleDialog(Long dialogId, Long sharerId) {
        UserEntity userEntity = sharerDao.getById(sharerId);
        userEntity.getVisibleDialogs().remove(dialogId);
        sharerDao.update(userEntity);
    }

    public List<ChatMessage> filterSharerMessages(List<ChatMessage> messages, Long sharerId) {
        List<ChatMessage> result = new ArrayList<>();

        for (ChatMessage message : messages) {
            boolean found = message.getSender().getId().equals(sharerId);
            if (!found) {
                for (ChatMessageReceiver chatMessageReceiver : message.getChatMessageReceivers()) {
                    if (chatMessageReceiver.getReceiver().getId().equals(sharerId)) {
                        found = true;
                        break;
                    }
                }
            }
            //if (!message.getReceivers().contains(sharerId)) continue;
            if (!found) continue;
            result.add(message);
        }

        return result;
    }

    public List<ChatMessage> getHistory(Long dialogId, Long userId, Long lastLoadChatMessageId) {
        List<ChatMessage> result = new ArrayList<>();
        List<ChatMessage> chatMessages;
        Pageable pageable = new PageRequest(0, 20, Sort.Direction.DESC, "date");
        if (lastLoadChatMessageId < 0) {
            result = chatMessageRepository.findDistinctChatMessageByDialog_IdAndSender_IdOrDialog_IdAndChatMessageReceivers_Receiver_Id(dialogId, userId, dialogId, userId, pageable);
        }

        if (result.size() < MAX_CHAT_MESSAGES_TO_LOAD) {
            do {
                chatMessages = filterSharerMessages(chatMessageRepository.findByIdLessThanAndDialog_Id(lastLoadChatMessageId, dialogId, pageable), userId);
                if (chatMessages.size() > 0) lastLoadChatMessageId = chatMessages.get(chatMessages.size() - 1).getId();

                for (ChatMessage message : chatMessages) {
                    if (result.size() < MAX_CHAT_MESSAGES_TO_LOAD) {
                        result.add(message);
                    } else {
                        break;
                    }
                }
            } while ((result.size() < MAX_CHAT_MESSAGES_TO_LOAD) && (chatMessages.size() > 0));
        }

        for (ChatMessage message : result) {
            message.setAllowEdit(checkAge(message));
        }

        return result;
    }

	public int getChatNameMaxLength() {
        return chatNameMaxLength;
    }

    /**
     * Создать диалог по участникам
     * @param dialogName наименование диалога
     * @param sharerIds участники
     * @return диалог
     */
    public DialogEntity createDialog(String dialogName, List<Long> sharerIds) {
        DialogEntity dialog;
        if (sharerIds.size() == 2) { // Если это диалог между двумя пользователями, то возвращаем их чат
            UserEntity userEntity1 = sharerDao.getById(sharerIds.get(0));
            UserEntity userEntity2 = sharerDao.getById(sharerIds.get(1));
            dialog = getDialogByCompanion(userEntity1.getId(), userEntity2);
        } else {
            dialog = new DialogEntity();
            dialog.setAdminId(null);
            dialog.setName(dialogName);

            List<UserEntity> userEntities = sharerDao.getByIds(sharerIds);

            dialog.getUsers().addAll(userEntities);
            dialog = dialogsRepository.save(dialog);

            for (UserEntity userEntity : userEntities) {
                userEntity.getVisibleDialogs().add(dialog.getId());
                sharerDao.update(userEntity);
            }
        }
        return dialog;
    }

    /**
     * Получить диалог с участником
     * @param companionIkp
     * @return
     */
    public DialogEntity getDialogByCompanion(Long userId, String companionIkp) {
        UserEntity companion = sharerDao.getByIkp(companionIkp);
        return getDialogByCompanion(userId, companion);
    }

    /**
     * Получит диалог с участником
     * @param companionId
     * @return
     */
    public DialogEntity getDialogByCompanion(Long userId, Long companionId) {
        UserEntity companion = sharerDao.getById(companionId);
        return getDialogByCompanion(userId, companion);
    }

    /**
     * Получить диалог с участником
     * @param companion
     * @return
     */
    public DialogEntity getDialogByCompanion(Long userId, UserEntity companion) {
        UserEntity currentUser = sharerDao.getById(userId);
        DialogEntity dialog = null;
        for (DialogEntity userDialog : currentUser.getDialogs()) {
            if (userDialog.hasUser(companion.getId()) && (userDialog.getUsers().size() == 2)) {
                dialog = userDialog;
                break;
            }
        }

        if (dialog == null) {
            dialog = new DialogEntity();
            dialog.setAdminId(null);
            dialog.getUsers().add(currentUser);
            dialog.getUsers().add(companion);
            dialog = dialogsRepository.save(dialog);

            currentUser.getVisibleDialogs().add(dialog.getId());
            sharerDao.update(currentUser);

            companion.getVisibleDialogs().add(dialog.getId());
            sharerDao.update(companion);
        }
        return dialog;
    }

    public boolean isRead(ChatMessage chatMessage) {
        boolean read = false;

        for (ChatMessageReceiver chatMessageReceiver : chatMessage.getChatMessageReceivers()) {
            // TODO Может как то по другому сделать
            if (chatMessageReceiver != null && SecurityUtils.getUserDetails() != null && chatMessageReceiver.getId().equals(SecurityUtils.getUser().getId())) {
                read = chatMessageReceiver.isRead();
            }
        }

        return read;
    }

    /**
     * Обновление процентов загрузки файла.
     * с методом finishFileUpload
     * @param chatMessageId
     * @param userId
     * @param percent
     */
    public void updateFileUploadPercent(Long chatMessageId, Long userId, int percent) {
        ChatMessage chatMessage = chatMessageRepository.findOne(chatMessageId);
        if (chatMessage == null) {
            throw new RuntimeException("Сообщение не найдено");
        }
        if (!chatMessage.getSender().getId().equals(userId)) {
            throw new RuntimeException("Вы не являетесь автором сообщения");
        }
        if (chatMessage.getFileChatMessageState() != FileChatMessageState.UPLOAD_IN_PROCESS) {
            throw new RuntimeException("Файл уже не загружается");
        }
        chatMessage.setFileLoadedPercent(percent);
        chatMessage.setFileDateUpdatePercent(new Date());
        chatMessageRepository.save(chatMessage);
        // Обновлён процент загрузки файла
        // TODO Переделать
        stompService.send(chatMessage.getDialog().getUsers().stream().map(userEntity -> userEntity.toDomain()).collect(Collectors.toList()), FILE_CHAT_MESSAGE_UPLOAD_UPDATE_PERCENT_STOMP_PATH, chatMessage);
    }

    public void cancelFileUpload(Long chatMessageId, Long userId) {
        ChatMessage chatMessage = chatMessageRepository.findOne(chatMessageId);
        if (chatMessage == null) {
            throw new RuntimeException("Сообщение не найдено");
        }
        if (!chatMessage.getSender().getId().equals(userId)) {
            throw new RuntimeException("Вы не являетесь автором сообщения");
        }
        if (chatMessage.getFileChatMessageState() != FileChatMessageState.UPLOAD_IN_PROCESS) {
            throw new RuntimeException("Файл уже не загружается");
        }
        chatMessage.setFileChatMessageState(FileChatMessageState.UPLOAD_CANCEL);
        chatMessageRepository.save(chatMessage);
        // Отменена загрузка файла
        // TODO Переделать
        stompService.send(chatMessage.getDialog().getUsers().stream().map(userEntity -> userEntity.toDomain()).collect(Collectors.toList()),
                FILE_CHAT_MESSAGE_UPLOAD_CANCEL_STOMP_PATH, chatMessage);
    }

    /**
     * Сохранение ссылки на файл.
     * @param chatMessageId
     * @param messageText
     * @param userId
     */
    public void finishFileUpload(Long chatMessageId, String messageText, Long userId) {
        ChatMessage chatMessage = chatMessageRepository.findOne(chatMessageId);
        if (chatMessage == null) {
            throw new RuntimeException("Сообщение не найдено");
        }
        if (!chatMessage.getSender().getId().equals(userId)) {
            throw new RuntimeException("Вы не являетесь автором сообщения");
        }
        if (chatMessage.getFileChatMessageState() != FileChatMessageState.UPLOAD_IN_PROCESS) {
            throw new RuntimeException("Файл уже не загружается");
        }
        chatMessage.setText(messageText);
        chatMessage.setFileChatMessageState(FileChatMessageState.UPLOADED);
        chatMessageRepository.save(chatMessage);
        // Загрузка файла завершена
        // TODO Переделать
        stompService.send(chatMessage.getDialog().getUsers().stream().map(userEntity -> userEntity.toDomain()).collect(Collectors.toList()), FILE_CHAT_MESSAGE_UPLOAD_FINISH_STOMP_PATH, chatMessage);
    }


    // Метод, который следит за сообщениями с вложенными файлами
    // Загружаются все сообщения с файлами у которых статус - идёт загрузка и дата создания сообщения - меньше текущей на n минут
    // Все эти сообщения помечаем как с отменённой отправкой файла
    public void checkFileMessages() {
        Date date = new Date();
        date.setTime(date.getTime() - 1000 * 60 * 60); // Час на загрузку файлов думаю достаточно

        // Загрузка сообщений в стадии загрузки
        List<ChatMessage> fileChatMessages = chatMessageRepository.findByDateIsLessThanAndFileChatMessageState(date, FileChatMessageState.UPLOAD_IN_PROCESS);
        if (fileChatMessages != null) {
            for (ChatMessage fileChatMessage : fileChatMessages) {
                fileChatMessage.setFileChatMessageState(FileChatMessageState.UPLOAD_CANCEL);
                chatMessageRepository.save(fileChatMessage);
                // Отменена загрузка файла
                // TODO Переделать
                //stompService.send(fileChatMessage.getDialog().getSharers(), FILE_CHAT_MESSAGE_UPLOAD_CANCEL_STOMP_PATH, fileChatMessage);
            }
        }
    }

    /**
     * Форматы файлов для загрузки в чат
     * @return
     */
    public String[] getFileExtensions() {
        String[] result = null;
        String extensions = settingsManager.getSystemSetting(TRUSTED_FILE_EXTENSIONS_SYS_ATTR_NAME, "pdf,jpg,txt");
        if (extensions != null && !extensions.equals("")) {
            result = org.apache.commons.lang3.StringUtils.split(extensions, ",");
        }
        return result;
    }

    /**
     * Максимальный размер загружаемого файла в байтах
     * @return
     */
    public Long getMaxFileSize() {
        Long result = null;
        String maxFileSizeBytes = settingsManager.getSystemSetting(MAX_UPLOADED_FILE_SIZE, "10000");
        if (maxFileSizeBytes != null && !maxFileSizeBytes.equals("")) {
            result = VarUtils.getLong(maxFileSizeBytes, null);
        }
        return result;
    }

    /**
     * Закрыть диалог
     * @param dialogId
     * @param minCountUsers минимальное количество участников при котором нужно закрыть диалог
     */
    public void closeDialog(Long dialogId, int minCountUsers) {
        DialogEntity dialog = dialogsRepository.findOne(dialogId);
        if (dialog.getUsers().size() >= minCountUsers) {
            dialog.setClosed(true);
            dialogsRepository.save(dialog);
        }
    }
}
