package ru.radom.kabinet.web.chat;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.core.services.contacts.ContactsService;
import ru.askor.blagosfera.core.services.security.RosterService;
import ru.askor.blagosfera.data.jpa.repositories.ChatMessageReceiverRepository;
import ru.askor.blagosfera.data.jpa.repositories.DialogsRepository;
import ru.radom.kabinet.dao.ContactDao;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.model.ContactEntity;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.ChatMessage;
import ru.radom.kabinet.model.chat.ChatMessageReceiver;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.ChatException;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.web.chat.dto.ContactDto;
import ru.radom.kabinet.web.chat.dto.DialogDto;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping
public class ChatController {
    private static final Logger logger = LoggerFactory.createLogger(ChatController.class);
    @Autowired
	private ChatService chatService;

	@Autowired
	private ContactDao contactDao;

    @Autowired
    private SharerDao sharerDao;

	@Autowired
    private DialogsRepository dialogsRepository;

    @Autowired
    private ChatMessageReceiverRepository chatMessageReceiverRepository;

    @Autowired
    private RosterService rosterService;

    @Autowired
    private ContactsService contactsService;

    private static final int COUNT_CONTACTS_IN_PAGE = 10;

    @RequestMapping(value = "/chat/{ikp}", method = RequestMethod.GET)
	public String showChatPage(@PathVariable("ikp") String ikp) {
        DialogEntity dialog = chatService.getDialogByCompanion(SecurityUtils.getUser().getId(), ikp);
		return "redirect:/chat#" + String.valueOf(dialog.getId());
	}

    /**
     * Загрузить даилог по собеседнику
     * @param companionId
     * @return
     */
    @RequestMapping(value = "/chat/getDialogByCompanion.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public DialogDto getDialogByCompanion(@RequestParam(value = "id") Long companionId) {
        DialogEntity dialog = chatService.getDialogByCompanion(SecurityUtils.getUser().getId(), companionId);
        return DialogDto.toDto(dialog, SecurityUtils.getUser(), getOnlineMap(dialog));
    }

	@RequestMapping(value = {"/chat", "/radom/chat", "/razum/chat", "/raven/chat"}, method = RequestMethod.GET)
	public String showChatPage(Model model) {
        List<ContactEntity> contacts = contactDao.getContacts(SecurityUtils.getUser().getId(), null, false);
        model.addAttribute("chatPage", true);
        model.addAttribute("userContacts", contacts);
        model.addAttribute("chatNameMaxLength", chatService.getChatNameMaxLength());
		return "chat";
	}

	@RequestMapping(value = "/chat/add_message.json", method = RequestMethod.POST)
    @ResponseBody
	public SuccessResponseDto addMessage(@RequestParam(value = "dialog_id", required = true) Long dialogId,
                                         @RequestParam(value = "text", required = true) String text,
                                         @RequestParam(value = "uuid", required = true) String uuid,
                                         @RequestParam(value = "file_size", required = false) Long fileSize) {
        DialogEntity dialog = dialogsRepository.findOne(dialogId);

        if (dialog.hasUser(SecurityUtils.getUser().getId())) {
            chatService.addMessage(SecurityUtils.getUser().getId(), dialog, text, uuid, fileSize);
            return SuccessResponseDto.get();
        } else {
            throw new ChatException(ChatService.ERROR_CURRENT_SHARER_NOT_IN_CHAT);
        }
	}

	@RequestMapping(value = "/chat/edit_message.json", method = RequestMethod.POST)
    @ResponseBody
	public ChatMessage editMessage(@RequestParam(value = "message_id") Long messageId, @RequestParam(value = "text") String text) {
        return chatService.editMessage(SecurityUtils.getUser().getId(), messageId, text);
	}

	@RequestMapping(value = "/chat/delete_message.json", method = RequestMethod.POST)
    @ResponseBody
	public Object deleteMessage(@RequestParam(value = "message_id") Long messageId) {
        ChatMessage chatMessage = chatService.deleteMessage(SecurityUtils.getUser().getId(), messageId);

        boolean found = false;
        for (ChatMessageReceiver chatMessageReceiver : chatMessage.getChatMessageReceivers()) {
            if (chatMessageReceiver.getReceiver().getId().equals(SecurityUtils.getUser().getId())) {
                found = true;
                break;
            }
        }

        if (found)
            return chatMessage;

        return "\"deleted\"";
	}

    private Map<Long, Boolean> getOnlineMap(DialogEntity dialog) {
        return getOnlineMap(Collections.singletonList(dialog));
    }

    private Map<Long, Boolean> getOnlineMap(List<DialogEntity> dialogs) {
        Map<Long, Boolean> result = new HashMap<>();
        if (dialogs != null) {
            for (DialogEntity dialog : dialogs) {
                if (dialog.getUsers() != null) {
                    for (UserEntity userEntity : dialog.getUsers()) {
                        if (!result.containsKey(userEntity.getId())) {
                            boolean online = rosterService.isUserOnline(userEntity.getEmail());
                            result.put(userEntity.getId(), online);
                        }
                    }
                }
            }
        }
        return result;
    }
	
	@RequestMapping(value = "/chat/dialogs.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
	public List<DialogDto> getDialogs() {
        List<DialogEntity> dialogs = chatService.getVisibleDialogs(SecurityUtils.getUser().getId());
        for (DialogEntity dialog : dialogs) {
            List<ChatMessage> history = chatService.getHistory(dialog.getId(), SecurityUtils.getUser().getId(), -1L);
            if (history.size() > 0) {
                dialog.setLastMessage(history.get(0));
            }
            int countNotReadMessages = chatMessageReceiverRepository.countReceiversByParam(dialog.getId(), SecurityUtils.getUser().getId(), false);
            dialog.setCountUnreadMessages(countNotReadMessages);
        }



        return DialogDto.toDtoList(dialogs, SecurityUtils.getUser(), getOnlineMap(dialogs));
	}

    @RequestMapping(value = "/chat/dialog.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public DialogDto getDialog(@RequestParam("dialog_id") Long dialogId) {
        DialogEntity dialog = chatService.getDialog(dialogId, SecurityUtils.getUser().getId());
        /*List<ChatMessage> history = chatService.getHistory(dialog.getId(), currentUser.getId(), -1L);
        if (history.size() > 0) {
            dialog.setLastMessage(history.get(0));
        }
        int countNotReadMessages = chatMessageReceiverRepository.countReceiversByParam(dialog.getId(), currentUser.getId(), false);
        dialog.setCountUnreadMessages(countNotReadMessages);*/
        return DialogDto.toDto(dialog, SecurityUtils.getUser(), getOnlineMap(dialog));
    }

	@RequestMapping(value = "/chat/history.json", method = RequestMethod.GET)
    @ResponseBody
	public List<ChatMessage> getHistory(@RequestParam(value = "dialog_id") Long dialogId, @RequestParam(value = "last_load_chat_message_id", defaultValue = "-1") Long lastLoadChatMessageId) {
        return chatService.getHistory(dialogId, SecurityUtils.getUser().getId(), lastLoadChatMessageId);
	}

    @RequestMapping(value = "/chat/unread.json", method = RequestMethod.GET)
    @ResponseBody
	public List<ChatMessage> getUnread() {
        List<DialogEntity> dialogs = chatService.getVisibleDialogs(SecurityUtils.getUser().getId());
        List<ChatMessage> chatMessages = new ArrayList<>();

        for (DialogEntity dialog : dialogs) {
            List<ChatMessageReceiver> chatMessageReceivers = chatMessageReceiverRepository.findByChatMessage_Dialog_IdAndReceiver_IdAndRead(dialog.getId(), SecurityUtils.getUser().getId(), false);
            for (ChatMessageReceiver chatMessageReceiver : chatMessageReceivers) {
                chatMessages.add(chatMessageReceiver.getChatMessage());
            }
            /*List<ChatMessage> messages = chatMessageRepository.findByDialog_IdAndReadOrderByDateDesc(dialog.getId(), false);

            messages = chatService.filterSharerMessages(messages, currentUser.getId());

            for (ChatMessage message : messages) {
                if (!message.getSender().getId().equals(currentUser.getId())) {
                    chatMessages.add(message);
                }
            }*/
        }

        return chatMessages;
	}

	@RequestMapping(value = "/chat/mark_as_read.json", method = RequestMethod.POST)
    @ResponseBody
	public String markAsRead(@RequestParam(value = "dialog_id") Long dialogId) {
        DialogEntity dialog = dialogsRepository.findOne(dialogId);
        if (dialog != null) chatService.markAsRead(SecurityUtils.getUser().getId(), dialog);
        return "{\"result\":\"success\"}";
	}

    @RequestMapping(value = "/chat/delete_sharer.json", method = RequestMethod.POST)
    public @ResponseBody String deleteSharer(@RequestParam(value = "dialog_id") Long dialogId,
                                             @RequestParam(value = "sharer_id") Long sharerId) {
        chatService.deleteSharer(SecurityUtils.getUser().getId(), sharerId, dialogId);
        return "{\"result\":\"success\"}";
    }

    @RequestMapping(value = "/chat/add_sharer.json", method = RequestMethod.POST)
    public @ResponseBody String addSharer(@RequestParam(value = "dialog_id") Long dialogId,
                                          @RequestParam(value = "sharer_id") Long sharerId) {
        chatService.addSharer(SecurityUtils.getUser().getId(), sharerId, dialogId);
        return "{\"result\":\"success\"}";
    }

    @RequestMapping(value = "/chat/rename_dialog.json", method = RequestMethod.POST)
    public @ResponseBody String renameDialog(@RequestParam(value = "dialog_id") Long dialogId,
                                             @RequestParam(value = "name") String name) {
        chatService.renameDialog(SecurityUtils.getUser().getId(), name, dialogId);
        return "{\"result\":\"success\"}";
    }

    @RequestMapping(value = "/chat/delete_dialog.json", method = RequestMethod.POST)
    public @ResponseBody String deleteDialog(@RequestParam(value = "dialog_id") Long dialogId) {
        try {
            chatService.deleteSharer(SecurityUtils.getUser().getId(), SecurityUtils.getUser().getId(), dialogId);
        } catch (ChatException e) {}

        chatService.deleteVisibleDialog(dialogId, SecurityUtils.getUser().getId());
        return "{\"result\":\"success\"}";
    }

    @ExceptionHandler(ChatException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String handleError(HttpServletRequest req, Exception exception) {
        return "{\"result\":\"error\",\"message\":\"" + exception.getMessage() + "\"}";
    }

    /**
     * Обновить процент загрузки файла
     * @param messageId
     * @param percent
     * @return
     */
    @RequestMapping(value = "/chat/file_update_upload_percent.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public String updateFileUploadPercent(
            @RequestParam(value = "message_id", required = true) Long messageId,
            @RequestParam(value = "percent", required = true) Integer percent) {
        chatService.updateFileUploadPercent(messageId, SecurityUtils.getUser().getId(), percent);
        return JsonUtils.getSuccessJson().toString();
    }

    /**
     * Отмена загрузки файла
     * @param messageId
     * @return
     */
    @RequestMapping(value = "/chat/cancel_upload_file.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public String cancelFileUpload(@RequestParam(value = "message_id", required = true) Long messageId) {
        chatService.cancelFileUpload(messageId, SecurityUtils.getUser().getId());
        return JsonUtils.getSuccessJson().toString();
    }

    /**
     * Окончание загрузки файла
     * @param messageId
     * @param messageText
     * @return
     */
    @RequestMapping(value = "/chat/finish_upload_file.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public String finishFileUpload(
            @RequestParam(value = "message_id", required = true) Long messageId,
            @RequestParam(value = "messageText", required = true) String messageText) {
        chatService.finishFileUpload(messageId, messageText, SecurityUtils.getUser().getId());
        return JsonUtils.getSuccessJson().toString();
    }

    /**
     * Контакты пользователя для чата
     * @param page
     * @param searchString
     * @return
     */
    @RequestMapping(value = "/chat/contacts.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<ContactDto> getContacts(
            @RequestParam(value = "page", required = true) int page,
            @RequestParam(value = "search_string", required = false) String searchString,
            @RequestParam(value = "show_only_online", required = false, defaultValue = "false") boolean showOnlyOnline) {
        return contactsService.searchContacts(SecurityUtils.getUser().getId(), searchString, page,
                COUNT_CONTACTS_IN_PAGE, showOnlyOnline);
    }

    /**
     * Количество контактов онлайн
     * @return
     */
    /*@RequestMapping(value = "/chat/count_online_contacts.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public long countOnlineContacts() {
        return contactDao.getOnlineCount(SecurityUtils.getUser());
    }*/

    /**
     * Контакт с непрочитанными сообщениями
     * @return
     */
    @RequestMapping(value = "/chat/contact_with_messages.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public ContactDto getContactWithUnreadMessages(@RequestParam(value = "sharer_id", required = true) Long userId) {
        return contactsService.getContactWithUnreadMessages(SecurityUtils.getUser().getId(), userId);
    }

    /**
     * Загрузить контакты участника по ИДам, а также те контакты у которые есть непрочитанные сообщения
     * @param ids
     * @return
     */
    @RequestMapping(value = "/chat/search_contacts.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<ContactDto> searchContacts(
            @RequestParam(value = "ids[]", required = true) List<Long> ids,
            @RequestParam(value = "with_unread_messages", required = false) boolean withUnreadMessages) {
        return contactsService.getContactsByIdsOrUnreadMessages(SecurityUtils.getUser().getId(), ids, withUnreadMessages);
    }

}