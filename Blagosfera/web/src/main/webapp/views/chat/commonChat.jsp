<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<div class="smiles-table" id="smiles-table-source">
    <table border="0" cellpadding="1">
        <tr>
            <td>:-)</td>
            <td>:o)</td>
            <td>:c)</td>
            <td>:^)</td>
            <td>:-D</td>
            <td>:-(</td>
            <td>:-9</td>
            <td>;-)</td>
            <td>:-P</td>
            <td>:-p</td>
        </tr>
        <tr>
            <td>:-Þ</td>
            <td>:-b</td>
            <td>:-O</td>
            <td>:-/</td>
            <td>:-X</td>
            <td>:-#</td>
            <td>:'(</td>
            <td>B-)</td>
            <td>8-)</td>
            <td>;*(</td>
        </tr>
        <tr>
            <td>:-*</td>
            <td>:-\</td>
            <td>?-)</td>
            <td>: )</td>
            <td>: ]</td>
            <td>= ]</td>
            <td>= )</td>
            <td>8 )</td>
            <td>: }</td>
            <td>: D</td>
        </tr>
        <tr>
            <td>8 D</td>
            <td>X D</td>
            <td>x D</td>
            <td>= D</td>
            <td>: (</td>
            <td>: [</td>
            <td>: {</td>
            <td>= (</td>
            <td>; )</td>
            <td>; ]</td>
        </tr>
        <tr>
            <td>; D</td>
            <td>: P</td>
            <td>: p</td>
            <td>= P</td>
            <td>= p</td>
            <td>: b</td>
            <td>: Þ</td>
            <td>: O</td>
            <td>8 O</td>
            <td>: /</td>
        </tr>
        <tr>
            <td>= /</td>
            <td>: S</td>
            <td>: #</td>
            <td>: X</td>
            <td>B )</td>
            <td>: |</td>
            <td>: \</td>
            <td>= \</td>
            <td>: *</td>
            <td>: ></td>
        </tr>
        <tr>
            <td>: <</td>
            <td colspan="9"></td>
        </tr>
        <tr>
            <td>:)</td>
            <td>:]</td>
            <td>=]</td>
            <td>=)</td>
            <td>8)</td>
            <td>:}</td>
            <td>:D</td>
            <td>:(</td>
            <td>:[</td>
            <td>:{</td>
        </tr>
        <tr>
            <td>=(</td>
            <td>;)</td>
            <td>;]</td>
            <td>;D</td>
            <td>:P</td>
            <td>:p</td>
            <td>=P</td>
            <td>=p</td>
            <td>:b</td>
            <td>:Þ</td>
        </tr>
        <tr>
            <td>:O</td>
            <td>:/</td>
            <td>=/</td>
            <td>:S</td>
            <td>:#</td>
            <td>:X</td>
            <td>B)</td>
            <td>:|</td>
            <td>:\</td>
            <td>=\</td>
        </tr>
        <tr>
            <td>:*</td>
            <td>:></td>
            <td>:<</td>
            <td colspan="7"></td>
        </tr>
        <tr>
            <td>>:)</td>
            <td>>;)</td>
            <td>>:(</td>
            <td>>: )</td>
            <td>>; )</td>
            <td>>: (</td>
            <td>;(</td>
            <td><3</td>
            <td>O_O</td>
            <td>o_o</td>
        </tr>
        <tr>
            <td>0_o</td>
            <td>O_o</td>
            <td>T_T</td>
            <td>^_^</td>
            <td>O:)</td>
            <td>O: )</td>
            <td>8D</td>
            <td>XD</td>
            <td>xD</td>
            <td>=D</td>
        </tr>
        <tr>
            <td>8O</td>
            <td colspan="2">[+=..]</td>
            <td colspan="7"></td>
        </tr>
    </table>
</div>

<script id="header-dialogs-template" type="x-tmpl-mustache">
    {{#dialogs}}
        <li class="header-chat-dialog">
            <div class="row">
                <div class="col-xs-12">
                    <span class="headerDialogName" data-toggle="tooltip" title="{{name}}">{{name}}</span>
                </div>
                {{#lastMessage}}
                    <div class="col-xs-12">
                        {{#lastMessage.sender.isOwner}}
                            <small class="text-muted">Вы:</small>
                        {{/lastMessage.sender.isOwner}}
                        {{^lastMessage.sender.isOwner}}
                            <a href="/chat&#35;{{dialogId}}">{{lastMessage.sender.shortName}}</a>:
                        {{/lastMessage.sender.isOwner}}
                        <small class="text-muted"> {{{lastMessage.text}}}</small>
                    </div>
                {{/lastMessage}}
                {{#countUnreadMessages}}
                    <div class="col-xs-12">
                        <small class="text-muted"> Новых сообщений: {{countUnreadMessages}}</small>
                    </div>
                {{/countUnreadMessages}}
                <div class="col-xs-12">
                    <div class="form-group buttons">
                        <a href="/chat&#35;{{id}}" class="btn btn-xs btn-primary">Перейти в диалог</a>
                        <button dialog_id="{{id}}" dialog_name="{{name}}" class="chatButton btn btn-xs btn-primary">Чат</button>
                        {{#countUnreadMessages}}
                            <a href="javascript:void(0)" dialog_id="{{id}}" class="btn btn-xs btn-default mark-as-read">Отметить как прочитанное</a>
                        {{/countUnreadMessages}}
                    </div>
                </div>
            </div>
            <hr/>
        </li>
	{{/dialogs}}
</script>

<script id="pop-up-dialog-my-message-template" type="x-tmpl-mustache">
<div class="myMessageInDialog messageInDialog" uuid="{{uuid}}" message_id="{{id}}">
    <div style="position: absolute; right: 212px; bottom: 2px;">
        {{#showWaiter}}
            <i class="fa fa-spinner faa-spin animated"></i>
        {{/showWaiter}}
    </div>
    <div class="messageTools">
        {{^showWaiter}}
            {{^deleted}}
                {{^dialogClosed}}
                    {{#allowEdit}}
                        <a href="javascript:void(0)" class="glyphicon glyphicon-pencil edit-message-link" message_id="{{id}}"></a>
                    {{/allowEdit}}
                    {{#allowDelete}}
                        <a href="javascript:void(0)" class="glyphicon glyphicon-remove delete-message-link" message_id="{{id}}"></a>
                    {{/allowDelete}}
                {{/dialogClosed}}
            {{/deleted}}
        {{/showWaiter}}
        {{#editDate}}
            {{^deleted}}
                <i class="glyphicon glyphicon-tags edit-message-count"></i>
            {{/deleted}}
        {{/editDate}}
    </div>
    <div class="myMessageInDialogContent">
        <div class="messageContentBlock">
            {{#deleted}}
                <p class="text-muted deleted-text">Сообщение удалено</p>
            {{/deleted}}
            {{^deleted}}
                {{#fileMessage}}
                    {{#uploadInProcess}}
                        <p class="messageText" message_id="{{id}}">{{{text}}} ({{fileSize}})</p>
                        <p class="text-muted deleted-text">Загружается. Загружено <span class="loadedPercent">{{fileLoadedPercent}}</span>%</p>
                    {{/uploadInProcess}}
                    {{#uploadCancel}}
                        <p class="text-muted deleted-text">Загрузка файла отменена</p>
                    {{/uploadCancel}}
                    {{#uploaded}}
                        <p class="messageText" message_id="{{id}}">{{{text}}} ({{fileSize}})</p>
                    {{/uploaded}}
                {{/fileMessage}}
                {{^fileMessage}}
                    <p class="messageText" message_id="{{id}}">{{{text}}}</p>
                {{/fileMessage}}
            {{/deleted}}
        </div>
        <div class="messageTime">{{dateTime}}</div>
    </div>
    <div class="triangleRight"></div>
    <div style="clear: both;"></div>
</div>
</script>
<script id="pop-up-dialog-other-message-template" type="x-tmpl-mustache">
<div class="otherMessageInDialog messageInDialog" uuid="{{uuid}}" message_id="{{id}}" sender_id={{sender.id}}>
    <a href="/sharer/{{sender.ikp}}" class="otherSenderLink"><img data-src="holder.js/25x/25" alt="25x25"
			 src="{{sender.avatar25}}" data-holder-rendered="true"
			 class="media-object img-thumbnail tooltiped-avatar"
			 data-sharer-ikp="{{sender.ikp}}" data-placement="left" /></a>
    <div class="otherMessageInDialogContent">
        <div class="messageContentBlock">
            {{#deleted}}
                <p class="text-muted deleted-text">Сообщение удалено</p>
            {{/deleted}}
            {{^deleted}}
                {{#fileMessage}}
                    {{#uploadInProcess}}
                        <p class="messageText" message_id="{{id}}">{{{text}}} ({{fileSize}})</p>
                        <p class="text-muted deleted-text">Загружается. Загружено <span class="loadedPercent">{{fileLoadedPercent}}</span>%</p>
                    {{/uploadInProcess}}
                    {{#uploadCancel}}
                        <p class="text-muted deleted-text">Загрузка отменена</p>
                    {{/uploadCancel}}
                    {{#uploaded}}
                        <p class="messageText" message_id="{{id}}">{{{text}}} ({{fileSize}})</p>
                    {{/uploaded}}
                {{/fileMessage}}
                {{^fileMessage}}
                    <p class="messageText" message_id="{{id}}">{{{text}}}</p>
                {{/fileMessage}}
            {{/deleted}}
        </div>
        <div class="messageTime">{{dateTime}}</div>
    </div>
    <div class="messageTools">
        {{#editDate}}
            {{^deleted}}
                <i class="glyphicon glyphicon-tags edit-message-count"></i>
            {{/deleted}}
        {{/editDate}}
    </div>
    <div class="triangleLeft"></div>
    <div style="clear: both;"></div>
</div>
</script>
<script id="pop-up-dialog-waiter-template" type="x-tmpl-mustache">
<div style="text-align: center;" id="chatDialogWaiter">
    <img src="/i/search-ajax-loader.gif" />
</div>
</script>
<script id="full-info-dialog-template" type="x-tmpl-mustache">
{{#users}}
    <div class="fullInfoSharer" sharer_id="{{id}}">
        <div class="fullInfoImgSharerLink">
            <a href="/sharer/{{ikp}}"><img data-src="holder.js/30x/30" alt="30x30"
                 src="{{avatarSmall}}" data-holder-rendered="true"
                 class="media-object img-thumbnail tooltiped-avatar"
                 data-sharer-ikp="{{ikp}}" data-placement="left" /></a>
        </div>
        <div class="sharerStatus {{#online}}sharerStatusOnline{{/online}} {{^online}}sharerStatusOffline{{/online}}">
        </div>
    </div>
{{/users}}
</script>
<script id="chat-contacts-template" type="x-tmpl-mustache">
{{#contacts}}
    <div class="fullInfoSharer" sharer_id="{{id}}">
        <div class="fullInfoImgSharerLink">
            <div>
                <img data-src="holder.js/50x/50" alt="50x50"
                 src="{{avatarC50}}" data-holder-rendered="true"
                 class="media-object img-thumbnail tooltiped-avatar"
                 data-sharer-ikp="{{ikp}}" data-placement="left" />
            </div>
        </div>
        <div class="fullInfoSharerLink">{{shortName}}</div>
        <div class="sharerStatus {{#online}}sharerStatusOnline{{/online}} {{^online}}sharerStatusOffline{{/online}}">
        </div>
    </div>
{{/contacts}}
</script>
<script id="opened-fast-contacts-template" type="x-tmpl-mustache">
{{#contacts}}
    <div sharer_id="{{id}}" class="fastContactItem" dialog_id={{dialogId}}>
        <span class="countMessages {{#existsMessages}}existsMessages{{/existsMessages}}">{{countMessages}}</span>
        <i class="fa fa-times closeOpenedFastContact"></i>
        <img data-src="holder.js/35x/35" alt="35x35"
         src="{{avatarC35}}" data-holder-rendered="true"
         class="media-object img-thumbnail tooltiped-avatar"
         data-sharer-ikp="{{ikp}}" data-placement="left" />
        <span class="openedFastContactStatus {{#online}}openedFastContactOnline{{/online}} {{^online}}openedFastContactOffline{{/online}}"></span>
    </div>
{{/contacts}}
</script>

<div id="dialogModal" class="dialogModal" style="width: 275px;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header resizeBox" resize_from="dragging">
                <div class="resizeBox resizeBoxLeftTop" resize_from="lefttop"></div>
                <button type="button" class="closeModal headerButton glyphicon glyphicon-remove" data-toggle="tooltip" title="Закрыть"></button>
                <button type="button" class="dialogInfo headerButton glyphicon glyphicon-info-sign" data-toggle="tooltip" title="Информация о диалоге"></button>
                <button type="button" class="openDialogPage headerButton glyphicon glyphicon-resize-full" data-toggle="tooltip" title="Перейти в диалог"></button>
                <button type="button" class="minimize headerButton glyphicon glyphicon-minus" data-toggle="tooltip" title="Свернуть"></button>
                <button type="button" class="maximize headerButton glyphicon glyphicon-modal-window" style="display: none;" data-toggle="tooltip" title="Развернуть"></button>
                <h4 class="modal-title dialogName" ></h4>
                <div class="resizeBox resizeBoxRightTop" resize_from="righttop"></div>
            </div>
            <div class="modal-body">
                <div class="messagesContainer"></div>
                <div class="fullDialogInfoContainer"></div>
            </div>
            <div class="modal-footer">
                <div class="resizeBox resizeBoxLeftBottom" resize_from="leftbottom"></div>
                <div class="shortDialogInfoContainer"></div>
                <div class="printTextBox">
                    <div class="printParticipantPencilContainer"><span class="glyphicon glyphicon-pencil printParticipantPencil"></span></div>
                    <div class="printParticipantText">набирает сообщение</div>
                </div>
                <textarea class="form-control chatMessage" placeholder="Введите сообщение" dialog_id=""></textarea>
                <span class="smilesInInput"
                      data-toggle="tooltip"
                      title="Библиотека смайлов">
                    <span class="css-emoticon animated-emoticon">:-)</span>
                </span>
                <div class="resizeBox resizeBoxRightBottom" resize_from="rightbottom"></div>
            </div>
        </div>
    </div>
</div>
<div id="contactsModal" class="dialogModal" style="width: 275px;" dialog_id="contactsModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header resizeBox" resize_from="dragging">
                <button type="button" class="closeModal headerButton glyphicon glyphicon-remove" data-toggle="tooltip" title="Закрыть"></button>
                <h4 class="modal-title dialogName">Онлайн</h4>
            </div>
            <div class="modal-body">
                <div id="contactsContainer"></div>
            </div>
            <div class="modal-footer">
                <div>
                    <i class="fa fa-search"></i>
                    <input type="text" id="searchContacts" placeholder="Начните вводить имя" />
                    <span id="showOnline" title="Показать контакты онлайн"></span>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    var CurrentSharer = {
    };
    var MESSAGE_ID_TO_UUID = {}; // Кеш хешей сообщений к ИД сообщений
    var countContactsOnline = 0;
    var jqContactsModal = null;// Модальное окно с контактами

    var headerDialogsTemplate = $("#header-dialogs-template").html();
    var popupUpDialogMyMessageTemplate = $("#pop-up-dialog-my-message-template").html();
    var popupUpDialogOtherMessageTemplate = $("#pop-up-dialog-other-message-template").html();
    var popupUpDialogWaiterTemplate = $("#pop-up-dialog-waiter-template").html();
    var fullInfoDialogTemplate = $("#full-info-dialog-template").html();
    var chatContactsTemplate = $("#chat-contacts-template").html();
    var openedFastContactsTemplate = $("#opened-fast-contacts-template").html();// Шаблон с контактами, которые открыты для быстрого чата

    Mustache.parse(headerDialogsTemplate);
    Mustache.parse(popupUpDialogMyMessageTemplate);
    Mustache.parse(popupUpDialogOtherMessageTemplate);
    Mustache.parse(popupUpDialogWaiterTemplate);
    Mustache.parse(fullInfoDialogTemplate);
    Mustache.parse(chatContactsTemplate);
    Mustache.parse(openedFastContactsTemplate);

    // Методы для работы с чатом
    var Chat = {

        // Метод загрузки данных в 2 этапа - первый из локального хранилища, затем с сервера с обновлением данных
        // локального хранилища
        jsonLoad: function(method, url, data, callBack, errorCallBack, useCache, useOneCallBack){
            if (useOneCallBack == null) {
                useOneCallBack = false;
            }
            var callBackHandled = false;
            if (useCache) {
                var key = url + "_" + CurrentSharer.id;
                if (data != null) {
                    for (var index in data) {
                        var value = data[index];
                        key += index + "_" + value;
                    }
                }
                var storageData = radomLocalStorage.getItem(key);
                if (storageData != null) {
                    try {
                        var result = JSON.parse(storageData);
                        callBack(result, true);
                        callBackHandled = true;
                    } catch (e) {
                    }
                }
            }
            if (callBackHandled && useOneCallBack) {
                return;
            }
            method(url,
                    data,
                    function(response){
                        if (useCache) {
                            try {
                                radomLocalStorage.setItem(key, JSON.stringify(response));
                            } catch (e) {
                            }
                        }
                        callBack(response, false);
                    },
                    errorCallBack
            );
        },

        jsonPostLoad: function(url, data, callBack, errorCallBack, useCache, useOneCallBack){
            this.jsonLoad($.radomJsonPost, url, data, callBack, errorCallBack, useCache, useOneCallBack);
        },

        jsonGetLoad: function(url, data, callBack, errorCallBack, useCache, useOneCallBack){
            this.jsonLoad($.radomJsonGet, url, data, callBack, errorCallBack, useCache, useOneCallBack);
        },

        // Загрузить контакты участника
        getContacts: function(page, searchString, showOnlyOnline, callBack, errorCallBack) {
            var useCache = false;
            if (searchString == null && page == 0) {
                useCache = true;
            }
            this.jsonPostLoad("/chat/contacts.json", {
                page: page,
                search_string: searchString,
                show_only_online: showOnlyOnline
            }, callBack, errorCallBack, useCache);
        },

        //
        getCountOnlineContacts: function(callBack, errorCallBack) {
            this.jsonPostLoad("/chat/count_online_contacts.json", {
            }, callBack, errorCallBack, true);
        },

        // Загрузить диалог по собеседнику
        getDialogByCompanion: function(companionId, callBack, errorCallBack) {
            this.jsonPostLoad("/chat/getDialogByCompanion.json", {
                id: companionId
            }, callBack, errorCallBack, false, true);
        },

        // Загрузить непрочитанные сообщения
        loadUnreadMessage: function(callBack, errorCallBack){
            $.radomJsonGet("/chat/unread.json",
                    {},
                    callBack,
                    errorCallBack
            );
        },

        // Загрузить диалоги
        loadDialogs: function(callBack, errorCallBack){
            $.radomJsonGet("/chat/dialogs.json",
                    {},
                    callBack,
                    errorCallBack
            );
        },

        // Загрузить диалог
        loadDialog: function(dialogId, callBack, errorCallBack){
            $.radomJsonGet("/chat/dialog.json",
                    {
                        dialog_id: dialogId
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Удалить диалог
        deleteDialog: function(dialogId, callBack, errorCallBack){
            $.radomJsonPostWithWaiter("/chat/delete_dialog.json",
                    {dialog_id: dialogId},
                    callBack,
                    errorCallBack
            );
        },

        // Переименовать диалог
        renameDialog: function(dialogId, name, callBack, errorCallBack) {
            $.radomJsonPostWithWaiter("/chat/rename_dialog.json", {
                        dialog_id: dialog.id,
                        name: name
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Удалить сообщение
        deleteMessage: function(messageId, callBack, errorCallBack) {
            $.radomJsonPost("/chat/delete_message.json", {
                        message_id: messageId
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Добавить сообщение
        addMessage: function(text, dialogId, uuid, callBack, errorCallBack) {
            $.radomJsonPost("/chat/add_message.json",
                    {
                        text: text,
                        dialog_id: dialogId,
                        uuid: uuid
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Непрочитанные сообщения от одного контакта
        getContactWithUnreadMessages: function(sharerId, callBack, errorCallBack) {
            $.radomJsonPost("/chat/contact_with_messages.json",
                    {
                        sharer_id : sharerId
                    },
                    callBack,
                    errorCallBack
            );
        },

        getContactByIdsAndWithUnreadMessages: function(ids, withUnreadMessages,  callBack, errorCallBack) {
            this.jsonPostLoad("/chat/search_contacts.json",
                    {
                        "ids[]": ids,
                        with_unread_messages: withUnreadMessages
                    },
                    callBack,
                    errorCallBack,
                    false
            );
        },

        // Добавить файл в чат
        addFileMessage: function(text, dialogId, uuid, fileSize, callBack, errorCallBack) {
            $.radomJsonPost("/chat/add_message.json",
                    {
                        text: text,
                        dialog_id: dialogId,
                        uuid: uuid,
                        file_size: fileSize
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Обновить процент загрузки файла
        updateUploadPercent: function(messageId, percent, callBack, errorCallBack){
            $.radomJsonPost("/chat/file_update_upload_percent.json",
                    {
                        message_id: messageId,
                        percent: percent
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Отменена загрузка файла
        cancelUploadFile: function(messageId, callBack, errorCallBack){
            $.radomJsonPost("/chat/cancel_upload_file.json",
                    {
                        message_id: messageId,
                        percent: percent
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Завершена загрузка файла
        finishUploadFile: function(messageId, text, callBack, errorCallBack){
            $.radomJsonPost("/chat/finish_upload_file.json",
                    {
                        message_id: messageId,
                        messageText: text
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Редактировать сообщение
        editMessage: function(messageId, text, callBack, errorCallBack){
            $.radomJsonPost("/chat/edit_message.json", {
                        message_id: messageId,
                        text: text
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Пометить диалог как прочитанный
        markDialogAsRead: function(dialogId, callBack, errorCallBack){
            $.radomJsonPost("/chat/mark_as_read.json",
                    {
                        dialog_id: dialogId
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Загрузить историю диалога
        loadHistory: function(dialogId, lastLoadChatMessageId, callBack, errorCallBack, useCache) {
            if (useCache == null) {
                useCache = false;
            }
            this.jsonGetLoad("/chat/history.json", {
                dialog_id: dialogId,
                last_load_chat_message_id: lastLoadChatMessageId
            }, callBack, errorCallBack, useCache);
        },

        // Удалить участника из диалога
        deleteSharerFromDialog: function(dialogId, sharerId, callBack, errorCallBack) {
            $.radomJsonPostWithWaiter("/chat/delete_sharer.json",
                    {
                        dialog_id: dialogId,
                        sharer_id: sharerId
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Добавить участника в диалог
        addSharerToDialog: function(dialogId, sharerId, callBack, errorCallBack) {
            $.radomJsonPostWithWaiter("/chat/add_sharer.json",
                    {
                        dialog_id: dialogId,
                        sharer_id: sharerId
                    },
                    callBack,
                    errorCallBack
            );
        },

        // Подписаться на событие нового сообщения в чате
        subscribeToNewMessage: function(callBack) {
            // message
            radomStompClient.subscribeToUserQueue("new_chat_message", callBack);
        },

        // Диалог прочитан
        subscribeToMarkAsReadDialog: function(callBack){
            // dialogId
            radomStompClient.subscribeToUserQueue("mark_as_read", callBack);
        },

        // Отправить событие набора текста участником Участник набира
        sendPrintChatMessageToServer: function(dialogId) {
            radomStompClient.send("print_chat_message_to_server", {dialogId: dialogId});
        },

        // Участник набирает сообщение
        subscribeToPrintChatMessage: function(callBack){
            // data
            radomStompClient.subscribeToUserQueue("print_chat_message_to_client", callBack);
        },

        // Событие изменения состояния чата
        subscribeToChatStateChanged: function(callBack){
            radomStompClient.subscribeToUserQueue("chat_state_changed", callBack);
        },

        // Участник диалога онлайн
        subscribeToDialogParticipantOnline: function(callBack){
            // SharerMessage
            radomStompClient.subscribeToUserQueue("dialog_participant_online", callBack);
        },

        // Участник диалога офлайн
        subscribeToDialogParticipantOffline: function(callBack){
            // SharerMessage
            radomStompClient.subscribeToUserQueue("dialog_participant_offline", callBack);
        },

        // Сообщение было отредактировано
        subscribeToEditMessage: function(callBack) {
            //messageBody
            radomStompClient.subscribeToUserQueue("edit_chat_message", callBack);
        },

        // Сообщение было удалено
        subscribeToDeleteMessage: function(callBack) {
            //messageBody
            radomStompClient.subscribeToUserQueue("delete_chat_message", callBack);
        },

        // Обновлен процент загрузки файла
        subscribeToUpdateFilePercent: function(callBack){
            //message
            radomStompClient.subscribeToUserQueue("file_chat_message_upload_update_percent", callBack);
        },

        // Отмена загрузки файла
        subscribeToCancelUploadFile: function(callBack){
            //message
            radomStompClient.subscribeToUserQueue("file_chat_message_upload_cancel", callBack);
        },

        // Загрузка файла завершена
        subscribeToFinishUploadFile: function(callBack){
            //message
            radomStompClient.subscribeToUserQueue("file_chat_message_upload_finish", callBack);
        },

        subscribeToContactOnline: function(callBack) {
            //sharer
            $(radomEventsManager).bind("contact.online", function (event, sharer) {
                callBack(sharer);
            });
        },

        subscribeToContactOffline: function(callBack) {
            //sharer
            $(radomEventsManager).bind("contact.offline", function (event, sharer) {
                callBack(sharer);
            });
        },

        // Сгенерировать уникальный код сообщения
        generateUUID: function () {
            var d = new Date().getTime();
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = (d + Math.random() * 16) % 16 | 0;
                d = Math.floor(d / 16);
                return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
            });
        },

        sortMessages: function(messages) {
            return messages.sort(function(a, b){
                var result = 0;
                if (a.id > b.id) {
                    result = 1;
                } else {
                    result = -1;
                }
                return result;
            })
        }
    };
    var ChatPage = {
        // ИД открытого диалога
        openedDialogId: -1,
        openedDialog : null,
        openedDialogs: [] // диалоги которые открыты на всех вкладках
    };
    // Методы отображения сообщений в шапке сайта
    var ChatView = {

        // Открытые диалоги инициализрованны
        dialogsInited: false,

        // Все диалоги участника
        currentDialogs: [],

        showChatModal: function(dialogModal) {
            dialogModal.trigger('beforeShow');
            $(window).resize();
            dialogModal.show();
            dialogModal.trigger('afterShow');
            $(window).resize();
            /*dialogModal.fadeIn("fast", function(){
                dialogModal.trigger('afterShow');
                $(window).resize();
            });*/
        },

        hideChatModal: function(dialogModal) {
            var dialogId = dialogModal.attr("dialog_id");
            hideDialogInStorage(dialogId);
            dialogModal.removeAttr("modal_resized");
            // очистить поле ввода в диалоге поиска контактов
            if ($('#searchContacts', dialogModal).length > 0) {
                $('#searchContacts', dialogModal).val("");
            }
            dialogModal.hide();
        },

        // Отобразить диалоги в шапку
        showDialogsInHeader: function() {
            // Загрузить все диалоги у текущего пользователя
            // Отбразить их списком
            // Рядом с диалогом где есть новое сообщение отобразить отправителя
            var self = this;
            Chat.loadDialogs(function(dialogs){
                //self.currentDialogs = dialogs;
                $("ul#header-chat-messages-menu li.header-chat-dialog").remove();

                var count = 0;
                for (var index in dialogs) {
                    var dialog = dialogs[index];

                    count += dialog.countUnreadMessages;

                    if (dialog.lastMessageText != null) {
                        dialog.lastMessage = {
                            sender: {id: dialog.lastMessageSenderId, shortName : dialog.lastMessageSenderShortName},
                            text: dialog.lastMessageText,
                            date: dialog.lastMessageDate
                        };
                    } else {
                        dialog.lastMessage = null;
                    }

                    if (dialog.lastMessage != null && dialog.lastMessage.sender.id == CurrentSharer.id) {
                        dialog.lastMessage.sender.isOwner = true;
                        if (dialog.lastMessage.text.length > 50) {
                            dialog.lastMessage.text = dialog.lastMessage.text.substring(0, 50) + "...";
                        }
                    }
                }

                var $markup = $(Mustache.render(headerDialogsTemplate, {dialogs : dialogs}));
                $("ul#header-chat-messages-menu").prepend($markup);

                var $counter = $("span#unread-chat-messages-count");

                if (count > 0) {
                    $counter.html(count).fadeIn();
                } else {
                    $counter.html("0").fadeOut();
                }

                if (!self.dialogsInited) {
                    // Если диалоги были открыты ранее, то переоткрываем
                    var dialogIds = getDialogIdsFromStorage();
                    for (var dialogId in dialogIds) {
                        if ($.isNumeric(dialogId)) {
                            var storedDialog = getDialogFromStorage(dialogId);
                            if (storedDialog != null && isDialogShowInStorage(dialogId)) {
                                self.showPopupChatDialog(dialogId, storedDialog.dialogName);
                            }
                        }
                    }
                    self.dialogsInited = true;
                }
            })
        },

        showOpenedDialogs: function() { // Отобразить ранее открытые диалоги
            // Если диалоги были открыты ранее, то переоткрываем
            var dialogIds = getDialogIdsFromStorage();
            for (var dialogId in dialogIds) {
                if ($.isNumeric(dialogId)) {
                    var storedDialog = getDialogFromStorage(dialogId);
                    if (storedDialog != null && isDialogShowInStorage(dialogId)) {
                        this.showPopupChatDialog(dialogId, storedDialog.dialogName);
                    }
                }
            }
        },

        isOpenedContactList: function() { // Был ли открыт диалог со списком контактов
            var dialogIds = getDialogIdsFromStorage();
            var result = false;
            for (var dialogId in dialogIds) {
                if (dialogId == "contactsModal" && isDialogShowInStorage(dialogId)) {
                    result = true;
                    break;
                }
            }
            return result;
        },

        fixAllChatMessages: function(parentNode) {
            // отображение склеиных блоков сообщений сделано только на скрипте, потому что в диалогах с 3 и более людей нет возможности написать css
            var prevChild = null;
            $(".messageTime", parentNode).show();
            $(".triangleLeft", parentNode).show();
            $(".triangleRight", parentNode).show();
            $(".otherSenderLink", parentNode).css("visibility", "visible");
            $(".otherSenderLink", parentNode).css("height", "auto");
            var countChild = parentNode.children().length;
            parentNode.children().each(function (i) {
                var $this = $(this);
                // склеиваем свои сообщения
                if (prevChild != null &&
                        (
                                ($this.hasClass("myMessageInDialog") &&
                                (prevChild.hasClass("myMessageInDialog")))
                        )
                ) {
                    $(".messageTime", prevChild).hide();
                    $(".triangleRight", prevChild).hide();

                    prevChild.css("margin-bottom", "0px");
                    $this.css("margin-top", "0px");

                    $(".myMessageInDialogContent", prevChild).css("border-bottom-left-radius", "0px");
                    $(".myMessageInDialogContent", prevChild).css("border-bottom-right-radius", "0px");
                    $(".myMessageInDialogContent", prevChild).css("border-bottom", "none");
                    $(".myMessageInDialogContent", $this).css("border-top-left-radius", "0px");
                    $(".myMessageInDialogContent", $this).css("border-top-right-radius", "0px");
                    $(".myMessageInDialogContent", $this).css("border-top", "none");
                }
                // Склеиваем сообщения оппонентов
                if (prevChild != null &&
                        (
                                $this.hasClass("otherMessageInDialog") &&
                                prevChild.hasClass("otherMessageInDialog") &&
                                $this.attr("sender_id") == prevChild.attr("sender_id")
                        )
                ) {
                    $(".messageTime", prevChild).hide();
                    $(".triangleLeft", prevChild).hide();
                    $(".otherSenderLink", prevChild).css("visibility", "hidden");
                    $(".otherSenderLink", prevChild).css("height", "5px");

                    prevChild.css("margin-bottom", "0px");
                    $this.css("margin-top", "0px");

                    $(".otherMessageInDialogContent", prevChild).css("border-bottom-left-radius", "0px");
                    $(".otherMessageInDialogContent", prevChild).css("border-bottom-right-radius", "0px");
                    $(".otherMessageInDialogContent", prevChild).css("border-bottom", "none");
                    $(".otherMessageInDialogContent", $this).css("border-top-left-radius", "0px");
                    $(".otherMessageInDialogContent", $this).css("border-top-right-radius", "0px");
                    $(".otherMessageInDialogContent", $this).css("border-top", "none");
                }

                // При смене отправителя сообщения, либо если сообщение последнее - отображать уголок, аватарку и дату
                if (prevChild != null &&
                        (
                                ($this.hasClass("myMessageInDialog") &&
                                (prevChild.hasClass("otherMessageInDialog"))) ||

                                ($this.hasClass("otherMessageInDialog") &&
                                (prevChild.hasClass("myMessageInDialog"))) ||

                                ($this.hasClass("otherMessageInDialog") &&
                                prevChild.hasClass("otherMessageInDialog") &&
                                $this.attr("sender_id") != prevChild.attr("sender_id"))
                        )

                ) {
                    $(".messageTime", prevChild).show();
                    $(".triangleRight", prevChild).show();
                    $(".triangleLeft", prevChild).show();
                    $(".otherSenderLink", prevChild).css("visibility", "visible");
                    $(".otherSenderLink", prevChild).css("height", "auto");
                }

                // Последнее сообщение в чате
                if (countChild == i + 1) {
                    $(".messageTime", $this).show();
                    $(".triangleRight", $this).show();
                    $(".triangleLeft", $this).show();
                    $(".otherSenderLink", $this).css("visibility", "visible");
                    $(".otherSenderLink", $this).css("height", "auto");
                }

                prevChild = $this;
            });
        },

        // Рендеринг данных истории диалога в верстку
        setPopupChatMarkupByMessages: function(messages, jqNode, nodeType) {
            var oldDate = "";
            var prevNode = null;
            for (var index in messages) {
                var message = messages[index];

                if (message.date) {
                    message.date = dateFormat(message.date, "dd.mm.yyyy HH:MM:ss");
                }

                //
                var currentDate = message.date.split(" ")[0];
                if (oldDate != "" && oldDate != currentDate) {
                    var separateNode = $("<div class='news-date-separator'><span>" + currentDate + "</span><hr></div>");;
                    if (prevNode == null) {
                        if (nodeType == "parent") {
                            jqNode.append(separateNode);
                        } else if (nodeType == "before") {
                            jqNode.before(separateNode);
                        }
                    } else {
                        prevNode.after(separateNode);
                    }
                    prevNode = separateNode;
                }
                var messageNode = this.getPopupChatMarkupByMessage(message);
                if (prevNode == null) {
                    if (nodeType == "parent") {
                        jqNode.append(messageNode);
                    } else if (nodeType == "before") {
                        jqNode.before(messageNode);
                    }
                } else {
                    prevNode.after(messageNode);
                }
                prevNode = messageNode;

                oldDate = currentDate;
            }

            if (prevNode != null) {
                this.fixAllChatMessages(prevNode.parent());
            }
        },

        // Размер файла
        humanFileSize : function(size) {
            var i = Math.floor( Math.log(size) / Math.log(1024) );
            return ( size / Math.pow(1024, i) ).toFixed(2) * 1 + ' ' + ['б', 'Кб', 'Мб', 'Гб', 'Тб'][i];
        },

        fixMessage: function(message) {
            try {
                if (new String(message.date).indexOf(".") == -1) { // Число
                    message.date = dateFormat(new Date(message.date), "dd.mm.yyyy HH:MM:ss");
                }
                if (message.id != null) {
                    message.uuid = MESSAGE_ID_TO_UUID[message.id];
                }

                //message.sender.avatarSmall = Images.getResizeUrl(message.sender.avatar, "c40");
                message.sender.avatar40 = Images.getResizeUrl(message.sender.avatar, "c40");
                message.sender.avatar25 = Images.getResizeUrl(message.sender.avatar, "c25");
                if (message.date != null) {
                    var dateParts = message.date.split(" ");
                    var dateTime = "";
                    if (dateParts != null && dateParts.length > 1) {
                        var fullDateTime = message.date.split(" ")[1];
                        dateTime = fullDateTime.substring(0, 5)
                    }
                    message.dateTime =dateTime;
                }

                message.chatLink = "/chat#" + message.dialog;
                if (message.editDate != null) {
                    message.editDate = dateFormat(message.editDate, "dd.mm.yyyy HH:MM:ss");
                }
                if (message.fileMessage == null) {
                    message.fileMessage = false;
                }
                if (message.fileMessage) {
                    message.fileSize = this.humanFileSize(message.fileSize);
                    message.uploadInProcess = false;
                    message.uploadCancel = false;
                    message.uploaded = false;
                    message.allowDelete = false;
                    message.allowEdit = false;
                    switch(message.fileChatMessageState) {
                        case "UPLOAD_IN_PROCESS":
                            message.uploadInProcess = true;
                            break;
                        case "UPLOAD_CANCEL":
                            message.uploadCancel = true;
                            break;
                        case "UPLOADED":
                            message.uploaded = true;
                            break;
                    }
                } else if (!message.deleted) {
                    message.allowDelete = true;
                    // Замена ссылок
                    message.text = message.text.replace(/(^|[ \n]*)(http|https|ssh|ftp)(:\/\/)([\S]+)([ \n]*|$)/g, "$1<a class='notTrusted' href='$2$3$4'>$2$3$4</a>$5");
                    // Замена мыла
                    message.text = message.text.replace(/(^|[ \n]*)([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+)([ \n]*|$)/g, "$1<a href='mailto:$2'>$2</a>$3");
                }
            } catch (e) {
            }
        },

        // Рендеринг данных истории диалога в верстку
        getPopupChatMarkupByMessage: function(message) {
            this.fixMessage(message);

            message.dialogClosed = false;
            if (ChatPage.openedDialog != null && ChatPage.openedDialog.id == message.dialogId) {
                message.dialogClosed = ChatPage.openedDialog.closed;
            }

            var popupMessageBlock = "";
            if (CurrentSharer.id != message.sender.id) {
                popupMessageBlock = $(Mustache.render(popupUpDialogOtherMessageTemplate, message));
                $("p", popupMessageBlock).emoticonize();
            } else {
                if (message.showWaiter == null) {
                    message.showWaiter = false;
                }
                popupMessageBlock = $(Mustache.render(popupUpDialogMyMessageTemplate, message));
                if (!message.showWaiter) {
                    $("p", popupMessageBlock).emoticonize();
                }
            }

            popupMessageBlock.find("i.edit-message-count").radomTooltip({
                title: "Сообщение редактировалось " + message.editCount + " " + stringForms(message.editCount, "раз", "раза", "раз") + "<br/>" + (message.editCount > 1 ? "Последний: " : "") + message.editDate,
                html: true,
                container: "body"
            });

            popupMessageBlock.find("a.delete-message-link").radomTooltip({
                title: "Удалить сообщение",
                container: "body"
            });

            popupMessageBlock.find("a.edit-message-link").radomTooltip({
                title: "Редактировать сообщение",
                container: "body"
            });

            $(".messageText", popupMessageBlock).find("a").click(function(){
                var href = $(this).attr("href");
                var trusted = false;
                if ((href.indexOf("http") == -1 && href.indexOf("http") == -1) || href.indexOf(window.location.protocol + "//" + window.location.hostname) == 0) {
                    trusted = true;
                }

                if (!trusted) {
                    bootbox.confirm("Данная ссылка ведет за пределы сайта. Переход по ней может быть потенциально опасным и требует подтверждения. Если Вы хотите открыть данную ссылку, нажмите кнопку Подтвердить. Если Вы не хотите открывать данную ссылку - нажмите кнопку Отменить.", function (result) {
                        if (result) {
                            window.open(href, '_blank');
                        }
                    });
                    return false;
                }
            });

            return popupMessageBlock;
        },

        /**
         * Сколько будем ожидать до показывание сообщения
         */
        pendingTimeout: 2000,

        /**
         * Сообщения которые ожидают своего показа
         */
        pendingPopups: {},

        /**
         * показать всплывающее окно чата с сообщением
         */
        showPopupMessage: function (message) {
            var res = {
                message: message,
                dialog:  message.dialog
            };
            var id = "popup" + new Date().getTime();
            var timeout = setTimeout(function () {
                delete ChatView.pendingPopups[id];
                noty({
                    data: {
                        sharer: message.sender,
                        date: message.date,
                        dialog: message.dialog,
                        dialogName: message.dialogName
                    },
                    text: message.text,
                    callback: {
                        onContentClick: function () {
                            ChatView.showPopupChatDialog(this.options.data.dialog, this.options.data.dialogName);
                        }
                    },
                    closeWith: ["button", "click"]
                });
            }, this.pendingTimeout);
            res.remove = function () {
                clearTimeout(timeout);
                delete ChatView.pendingPopups[id];
            };
            ChatView.pendingPopups[id] = res;
            return res;
        },

        //
        showPopupChatDialog: function(dialogId, dialogName) {
            if ($("#dialogModal_" + dialogId).is(":visible")) {
                this.hideChatModal($("#dialogModal_" + dialogId));
            } else {
                $("#dialogModal_" + dialogId).remove();
                var newDialogModal = $("#dialogModal").clone();
                newDialogModal.attr("id", "dialogModal_" + dialogId);
                $("body").append(newDialogModal);
                this.initDialogModal(newDialogModal);

                newDialogModal.attr("dialog_id", dialogId);
                newDialogModal.attr("dialog_name", dialogName);
                $(".dialogName", newDialogModal).html(dialogName);
                $(".dialogName", newDialogModal).attr("title", dialogName);
                $(".dialogName", newDialogModal).tooltip({container : "body"});
                //newDialogModal.modal("show");
                this.showChatModal(newDialogModal);
            }
        },

        // Закрыть непередвинутые модальные окна
        hideNotResizedModals: function(dialogId){
            var self = this;
            var openedNotMoveModals = $(".dialogModal:not([modal_resized=true]):visible"); // открытые не передвинутые модальные окна
            openedNotMoveModals.each(function(){
                if ($(this).attr("dialog_id") != dialogId) {
                    self.hideChatModal($(this));
                }
            });
        },

        // Инициализация фокуса модального окна
        initFocusModal: function(jqDialogModal) {
            $(".dialogModal").css("z-index", "1040");
            jqDialogModal.css("z-index", "1041");
            // Если по диалогу кликнули, то сделать его выше других
            jqDialogModal.on('mousedown', function(){
                $(".dialogModal").css("z-index", "1040");
                jqDialogModal.css("z-index", "1041");
            });
        },

        // Открыть диалог с участником
        showDialogWithSharer: function(sharerId) {
            var self = this;
            Chat.getDialogByCompanion(sharerId, function(dialog){
                self.currentDialogs.push(dialog);
                var dialogName = " ";
                for (var i in dialog.users) {
                    var sharer = dialog.users[i];
                    if (sharer.id == sharerId) {
                        dialogName = sharer.fullName;
                    }
                }
                if (dialogName.trim() != "") {
                    var companionNameParts = dialogName.split(" ");
                    if (companionNameParts != null && companionNameParts.length > 1) {
                        dialogName = companionNameParts[0] + " " + companionNameParts[1];
                    }
                }

                // Если открыт диалог у которого были изменены размеры
                if ($("#dialogModal_" + dialog.id + "[modal_resized=true]:visible").length > 0) {
                    //
                } else {
                    removeDialogFromStorage(dialog.id);
                    self.showPopupChatDialog(dialog.id, dialogName);
                }
                self.hideNotResizedModals(dialog.id);

                // Отобразить быстрый конитакт если он не открыт
                var jqFastContact = $(".fastContactItem[sharer_id=" + sharerId + "]:visible");
                if (jqFastContact.length == 0) {
                    Chat.getContactByIdsAndWithUnreadMessages([sharerId], false, function(contacts){
                        if (contacts.length > 0) {
                            ChatView.showOpenedContact(contacts[0]);
                        }
                    });
                }
            });
        },

        initDialogModal: function(dialogModal) {
            var self = this;

            ChatView.initFocusModal(dialogModal);

            dialogModal.bind("afterShow", function () {
                var dialogId = $(this).attr("dialog_id");
                //var dialogName = $(this).attr("dialog_name");
                var lastLoadChatMessageId = -1;
                var contentContainer = $(".messagesContainer",$(this));
                contentContainer.html(popupUpDialogWaiterTemplate);
                Chat.markDialogAsRead(dialogId, function(){});
                Chat.loadHistory(dialogId, lastLoadChatMessageId, function(response){
                    response = Chat.sortMessages(response);
                    contentContainer.empty();
                    ChatView.setPopupChatMarkupByMessages(response, contentContainer, "parent");
                    contentContainer.scrollTop(10000);
                }, null, false);

                var foundDialog = null;
                for (var index in ChatView.currentDialogs) {
                    var dialog = ChatView.currentDialogs[index];
                    if (dialog.id == dialogId) {
                        foundDialog = dialog;
                        break;
                    }
                }

                $('.chatMessage', dialogModal).focus();
            });

            // После открытия диалога
            dialogModal.bind("beforeShow", function () {
                $(".headerButton", $(this)).tooltip({container : "body"});
                $(document).off('focusin.modal');
                var dialogId = $(this).attr("dialog_id");

                var storageDialog = getDialogFromStorage(dialogId);
                var minimized = false;
                var showInfoPage = false;
                if (storageDialog != null) {
                    $(this).css("left", storageDialog.left);
                    $(this).css("top", storageDialog.top);
                    $(this).width(storageDialog.width);
                    $(".modal-body", $(this)).height(storageDialog.height);
                    minimized = storageDialog.minimized == "true";
                    showInfoPage = storageDialog.showInfoPage == "true";
                    $(this).attr("modal_resized", "true");
                } else {
                    var left = $(window).width() - 275 - $("#dialogs-menu").outerWidth() - 15;
                    var top = 10000; // Заведомо большое число
                    $(this).css("left", left + "px");
                    $(this).css("top", top + "px");
                }
                //setDialogInStorage(dialogId, dialogName, $(this).css("left"), $(this).css("top"), $(this).width(), $(".modal-body", $(this)).height(), minimized);

                var foundDialog = null;
                for (var index in ChatView.currentDialogs) {
                    var dialog = ChatView.currentDialogs[index];
                    if (dialog.id == dialogId) {
                        foundDialog = dialog;
                        break;
                    }
                }

                var drawDialogInModal = function(foundDialog, jqDialogNode){
                    if (foundDialog != null) {
                        ChatPage.openedDialog = foundDialog;
                        var onlineCount = 0;
                        // Информация по диалогу
                        for (var index in foundDialog.users) {
                            var sharer = foundDialog.users[index];
                            sharer.avatarSmall = Images.getResizeUrl(sharer.avatar, "c30");
                            onlineCount += sharer.online ? 1 : 0;
                        }

                        var fullInfoDialogBlock = $(Mustache.render(fullInfoDialogTemplate, foundDialog));

                        $(".fullDialogInfoContainer", jqDialogNode).empty();
                        $(".fullDialogInfoContainer", jqDialogNode).append(fullInfoDialogBlock);

                        $(".sharerStatusOnline", fullInfoDialogBlock).attr("title", "Онлайн");
                        $(".sharerStatusOffline", fullInfoDialogBlock).attr("title", "Оффлайн");
                        $(".sharerStatus", fullInfoDialogBlock).tooltip({container : "body"});

                        var shortDialogInfoText = "Онлайн [" + onlineCount + "/" + foundDialog.users.length +  "]";
                        $(".shortDialogInfoContainer", jqDialogNode).html(shortDialogInfoText);
                        $(".shortDialogInfoContainer", jqDialogNode).attr("title", shortDialogInfoText);
                        $(".shortDialogInfoContainer", jqDialogNode).tooltip({container : "body"});
                        $(".shortDialogInfoContainer", jqDialogNode).attr("common_count", foundDialog.users.length);
                        $(".shortDialogInfoContainer", jqDialogNode).attr("online_count", onlineCount);

                        if (foundDialog.closed) {
                            $('.chatMessage', jqDialogNode).prop("disabled", true);
                            $(".printTextBox", jqDialogNode).css("visibility", "visible").html("<span class='text-muted' style='color: red;'>Диалог закрыт</span>");

                            /*$(".edit-message-link", jqDialogNode).hide();
                            $(".delete-message-link", jqDialogNode).hide();*/
                        }
                    }
                }

                var jqDialogNode = $(this);
                if (foundDialog != null) {
                    drawDialogInModal(foundDialog, jqDialogNode);
                } else {
                    Chat.loadDialog(dialogId, function (foundDialog) {
                        drawDialogInModal(foundDialog, jqDialogNode);
                    });
                }

                // Если открыта страница с информацией об участниках
                if (showInfoPage) {
                    $('.dialogInfo', $(this)).click();
                }

                // Если чат свёрнут
                if (minimized) {
                    $('.minimize', $(this)).click();
                }

                dialogModal.radomDropUpload(
                    "/dialogfiles/upload.json?dialogId=" + dialogId,
                    function() { // dragOverCallBack - обрабочик наведения зажатого файла над областью
                        var modalContent = dialogModal.find(".modal-content");
                        if (modalContent.attr("source_border") == null || modalContent.attr("source_border") == "") {
                            modalContent.attr("source_border", modalContent.css("border-color"));
                        }
                        modalContent.css("border-color", "red");
                    },
                    function() { // dragLeaveCallBack - обрабочик уведения зажатого файла из области
                        var modalContent = dialogModal.find(".modal-content");
                        modalContent.css("border-color",  modalContent.attr("source_border"));
                    },
                    function(file) { // dropCallBack - обрабочик отпускания файла над областью
                        var modalContent = dialogModal.find(".modal-content");
                        modalContent.css("border-color",  modalContent.attr("source_border"));

                        // Создать сообщение с файлом
                        var uuid = Chat.generateUUID();
                        var dialogId = dialogModal.attr("dialog_id");
                        var localMessage = {
                            uuid : uuid,
                            text : file.name,
                            fileSize: file.size,
                            fileLoadedPercent: 0,
                            fileMessage: true,
                            fileChatMessageState: "UPLOAD_IN_PROCESS",
                            date: dateFormat("dd.mm.yyyy HH:MM:ss"),
                            sender: CurrentSharer,
                            showWaiter: true
                        };

                        var popupMessageBlock = ChatView.getPopupChatMarkupByMessage(localMessage);
                        var contentContainer = $(".messagesContainer", dialogModal);
                        contentContainer.append(popupMessageBlock);
                        ChatView.fixAllChatMessages(contentContainer);
                        contentContainer.scrollTop(contentContainer.prop('scrollHeight'));

                        Chat.addFileMessage(file.name, dialogId, uuid, file.size, function (response) {
                            //
                        }, function (response) {
                            popupMessageBlock.find(".deleted-text").remove();
                            popupMessageBlock.find(".fa-spinner").remove();
                            popupMessageBlock.find(".messageText").css("color", "red");
                            popupMessageBlock.find(".messageText").text(response.message);
                        });
                        return uuid;
                    },
                    function(uuid, percent) { // statusCallBack - обновление процента загрузки файла
                        // Если сообщение уже добавлено, то отправляем событие изменения процента загрузки
                        var messageBlock = $(".messageInDialog[uuid=" + uuid + "]");
                        if (messageBlock.length > 0 && messageBlock.attr("message_id") != null && messageBlock.attr("message_id") != "") {
                            var messageId = messageBlock.attr("message_id");
                            // Обновляем процент загрузки файла
                            Chat.updateUploadPercent(messageId, percent, function(){
                                //
                            });
                        }
                    },
                    function(uuid, response) { // uploadSuccessCallBack - файл загружен
                        var functionFinish = function(uuid, response) {
                            var messageBlock = $(".messageInDialog[uuid=" + uuid + "]");
                            if (messageBlock.length > 0 && messageBlock.attr("message_id") != null && messageBlock.attr("message_id") != "") {
                                var messageId = messageBlock.attr("message_id");
                                var text = "<a href='" + response.link + "'>" +response.fileName + "</a>";
                                // Обновляем процент загрузки файла
                                Chat.finishUploadFile(messageId, text, function(){
                                    //
                                });
                            } else {
                                setTimeout(function(){
                                    functionFinish(uuid, response);
                                }, 100);
                            }
                        }
                        functionFinish(uuid, response);
                    }
                );
            });
            // Загрузка истории при скроллинге
            $(".messagesContainer", dialogModal).scroll(function () {
                var $this = $(this);
                var dialogId = $this.closest(".dialogModal").attr("dialog_id");
                if ($this.scrollTop() == 0) {
                    var firstMessageInDialog = $($(".messageInDialog", $this).get(0));
                    if (firstMessageInDialog.length > 0) {
                        var messageId = firstMessageInDialog.attr("message_id");
                        Chat.loadHistory(dialogId, messageId, function(response){
                            response = Chat.sortMessages(response);
                            var oldFullHeight = $this.prop('scrollHeight');
                            ChatView.setPopupChatMarkupByMessages(response, firstMessageInDialog, "before");
                            var newFullHeight = $this.prop('scrollHeight');
                            var newScrollPosition = newFullHeight - oldFullHeight;
                            $this.scrollTop(newScrollPosition);
                        });
                    }
                }
            });

            var messageSended = false;
            dialogModal.on('keyup', '.chatMessage', function (event) {
                var keycode = (event.keyCode ? event.keyCode : event.which);
                if(keycode == 13 && messageSended) {
                    $(this).val("");
                    $(this).focus();
                    messageSended = false;
                }
            });

            dialogModal.on('keydown', '.chatMessage', function (event) {
                var keycode = (event.keyCode ? event.keyCode : event.which);
                var dialogModal = $(this).closest(".dialogModal");
                var text = $(this).val();
                if(keycode == 13 && text.trim() != "") {
                    messageSended = true;
                    $(this).blur();
                    $(this).css("background-color", "transparent");
                    var messageId = $(".chatMessage", dialogModal).attr("message_id");
                    $(".chatMessage", dialogModal).removeAttr("message_id", messageId);

                    $(this).val("");
                    $(this).focus();

                    if (messageId != null) { // Редактирование сообщения
                        Chat.editMessage(messageId, text, function (message) {
                        });
                    } else { // Добавление нового сообщения
                        var uuid = Chat.generateUUID();
                        var dialogId = dialogModal.attr("dialog_id");
                        var localMessage = {
                            uuid : uuid,
                            text : text,
                            date: dateFormat("dd.mm.yyyy HH:MM:ss"),
                            sender: CurrentSharer,
                            showWaiter: true
                        };

                        var popupMessageBlock = ChatView.getPopupChatMarkupByMessage(localMessage);
                        var contentContainer = $(".messagesContainer",dialogModal);
                        contentContainer.append(popupMessageBlock);
                        ChatView.fixAllChatMessages(contentContainer);
                        contentContainer.scrollTop(contentContainer.prop('scrollHeight'));

                        Chat.addMessage(text, dialogId, uuid, function (response) {
                            //
                        }, function (response) {
                            popupMessageBlock.find(".fa-spinner").slideToggle("fast");
                            popupMessageBlock.find(".messageText").css("color", "red").html(response.message);
                        });
                    }
                } else if (keycode == 13) {
                    return false;
                }
            });

            dialogModal.on('keydown', '.chatMessage', function (event) {
                var keycode = (event.keyCode ? event.keyCode : event.which);
                var $this = $(this);
                var dialogModal = $(this).closest(".dialogModal");
                var dialogId = dialogModal.attr("dialog_id");
                if (!$this.data("disallow") && keycode != 13) {
                    $this.data("disallow", true);

                    setTimeout(function () {
                        $this.data("disallow", false);
                    }, 3000);

                    Chat.sendPrintChatMessageToServer(dialogId);
                }
            });
            // Переход в чат из диалога
            dialogModal.on('click', '.openDialogPage', function (e) {
                // TODO Скорее всего не нужно ничего скрывать
                var dialogModal = $(this).closest(".dialogModal");
                var dialogId = dialogModal.attr("dialog_id");
                //hideDialogInStorage(dialogId);
                window.location.href= "/chat#" + dialogId;
            });
            // Свернуть диалог
            dialogModal.on('click', '.minimize', function (e) {
                var dialogModal = $(this).closest(".dialogModal");
                var dialogId = dialogModal.attr("dialog_id");
                var storageDialog = getDialogFromStorage(dialogId);

                setDialogInStorage(dialogId, storageDialog.dialogName, storageDialog.left, storageDialog.top, storageDialog.width, storageDialog.height, true);

                $(".maximize", dialogModal).show();
                $(".minimize", dialogModal).hide();

                $(".modal-body", dialogModal).hide();
                $(".chatMessage", dialogModal).hide();
                $(".smilesInInput", dialogModal).hide();

                $(".shortDialogInfoContainer", dialogModal).show();
            });
            // Развернуть диалог
            dialogModal.on('click', '.maximize', function (e) {
                var dialogModal = $(this).closest(".dialogModal");
                var dialogId = dialogModal.attr("dialog_id");
                var storageDialog = getDialogFromStorage(dialogId);

                setDialogInStorage(dialogId, storageDialog.dialogName, storageDialog.left, storageDialog.top, storageDialog.width, storageDialog.height, false);

                $(".maximize", dialogModal).hide();
                $(".minimize", dialogModal).show();

                $(".modal-body", dialogModal).show();
                $(".chatMessage", dialogModal).show();
                $(".smilesInInput", dialogModal).show();

                $(".shortDialogInfoContainer", dialogModal).hide();
            });
            // Информация о диалоге
            dialogModal.on('click', '.dialogInfo', function (e) {
                var dialogModal = $(this).closest(".dialogModal");
                var dialogId = dialogModal.attr("dialog_id");
                var showInfoPage = false;
                if ($(".fullDialogInfoContainer", dialogModal).is(":visible")) {
                    showInfoPage = false;
                    $(".fullDialogInfoContainer", dialogModal).hide();
                    $(".messagesContainer", dialogModal).css("right", "0px");
                } else {
                    showInfoPage = true;
                    $(".fullDialogInfoContainer", dialogModal).show();
                    $(".messagesContainer", dialogModal).css("right", "55px");
                }

                setDialogInfoPageInStorage(dialogId, showInfoPage);
            });
            // Закрыть диалог
            dialogModal.on('click', '.closeModal', function (event) {
                self.hideChatModal($(this).closest(".dialogModal"));
                event.stopPropagation();
            });

            // Удадить сообщение
            dialogModal.on('click', '.delete-message-link', function (e) {
                var messageId = $(this).attr("message_id");
                Chat.deleteMessage(messageId, function(message){
                });
            });

            // Редатировать сообщение
            dialogModal.on('click', '.edit-message-link', function (e) {
                var messageId = $(this).attr("message_id");
                var messageText = $(".messageText[message_id=" + messageId + "]").text();
                var dialogModal = $(this).closest(".dialogModal");
                $(".chatMessage", dialogModal).val(messageText);
                $(".chatMessage", dialogModal).css("background-color", "#FFFDD8");
                $(".chatMessage", dialogModal).attr("message_id", messageId);
            });

            // Навести мышку на сообщение и отобразить тулзы
            dialogModal.on('mouseover', '.myMessageInDialog', function (e) {
                $(".messageTools", $(this)).show();
            });
            dialogModal.on('mouseleave', '.myMessageInDialog', function (e) {
                $(".messageTools", $(this)).hide();
            });

            $('.smilesInInput', dialogModal).emoticonize();
            $('.smilesInInput', dialogModal).tooltip({container : "body"});
            $('.smilesInInput', dialogModal).click(function (event) {
                event.stopPropagation();
                var dialogModal = $(this).closest(".dialogModal");
                var dialogId = dialogModal.attr("dialog_id");
                self.showSmilesTable($(this), dialogId, event);
            });
        },
        showSmilesTable: function(smilesOpenButton, dialogId, e, clickFunction) {
            $("#smiles-table-" + dialogId).remove();
            var smilesTable = $("#smiles-table-source").clone();
            smilesTable.attr("id", "smiles-table-" + dialogId);
            smilesTable.attr("dialog_id", dialogId);
            $("body").append(smilesTable);

            smilesTable.css("visibility", "hidden");
            smilesTable.show();

            var evt = (e || event);
            smilesTable.css('top', evt.clientY + 5 + 'px');
            smilesTable.css('left', evt.clientX + 5 + 'px');

            // Вычисляем позицию для смайлов
            var windowHeight = $(window).height();
            var windowWidth = $(window).width();
            if ((smilesTable.position().left + smilesTable.width()) > windowWidth) {
                smilesTable.css("left", (windowWidth - smilesTable.width()) + "px");
            }
            if ((smilesTable.position().top + smilesTable.height()) > windowHeight) {
                smilesTable.css("top", (windowHeight - smilesTable.height()) + "px");
            }
            smilesTable.hide();
            smilesTable.css("visibility", "visible");
            smilesTable.toggle('slide', {
                duration: 200,
                direction: 'down'
            });

            if (clickFunction == null) {
                var dialogModal = smilesOpenButton.closest(".dialogModal");
                clickFunction = function(smileText) {
                    $(".chatMessage", dialogModal).val($(".chatMessage", dialogModal).val() + smileText);
                }
            }
            $(".animated-emoticon", smilesTable).click(function(event){
                event.stopPropagation();
                clickFunction($(this).text());
                smilesTable.remove();
            });
        },

        //
        showOpenedContact: function(contact){
            addOpenedContact(contact.id);
            this.showOpenedContacts([contact]);
        },

        // Отобразить быстрый доступ к диалогам контактов

        // Метод должен принимать уже готовые модели для отрисовки
        // Если изменений у элемента нет, то перерисовыввать его не надо, если есть, то заменять
        showOpenedContacts: function(contacts) {
            for (var index in contacts) {
                var contact = contacts[index];
                contact.existsMessages = contact.countMessages != null && contact.countMessages > 0;
                contact.avatarC35 = Images.getResizeUrl(contact.avatar, "c35");
                var nameParts = contact.fullName.split(" ");
                contact.shortName = nameParts[0] + " " + nameParts[1];
            }
            // Если ничего не загружено, то просто добавляем всё
            if ($("#scrollFastDialogs").children().length == 0) {
                $("#fastDialogs").hide();
                var openedFastContactsBlock = $(Mustache.render(openedFastContactsTemplate, {
                    contacts : contacts
                }));
                $("#scrollFastDialogs").append(openedFastContactsBlock);
                $("#fastDialogs").fadeIn();
            } else {
                for (var index in contacts) {
                    var contact = contacts[index];
                    var jqContactNode = $("[sharer_id=" + contact.id + "]", $("#scrollFastDialogs"));
                    if (jqContactNode.length > 0) {
                        var openedFastContactBlock = $(Mustache.render(openedFastContactsTemplate, {
                            contacts : [contact]
                        }));
                        jqContactNode.remove();
                        $("#scrollFastDialogs").prepend(openedFastContactBlock);
                        openedFastContactBlock.fadeIn();
                        $("#scrollFastDialogs").scrollTop(0);
                        removeOpenedContact(contact.id);
                        addOpenedContact(contact.id);
                    } else {
                        var openedFastContactBlock = $(Mustache.render(openedFastContactsTemplate, {
                            contacts : [contact]
                        }));
                        openedFastContactBlock.hide();
                        $("#scrollFastDialogs").prepend(openedFastContactBlock);
                        openedFastContactBlock.fadeIn();
                        $("#scrollFastDialogs").scrollTop(0);
                    }
                }
            }

            $(".fastContactItem", $("#fastDialogs")).off();
            $(".fastContactItem", $("#fastDialogs")).hover(function(){
                $(".closeOpenedFastContact", $(this)).fadeIn('fast');
            }, function(){
                $(".closeOpenedFastContact", $(this)).fadeOut('fast');
            });

            $(".fastContactItem", $("#fastDialogs")).click(function(event){
                var sharerId = $(this).attr("sharer_id");
                ChatView.showDialogWithSharer(sharerId);
            });

            $(".closeOpenedFastContact", $("#fastDialogs")).off();
            // TODO Надо помечать при закрытии быстрого контакта диалог с ним как прочитанный
            $(".closeOpenedFastContact", $("#fastDialogs")).click(function(event){
                event.preventDefault();
                event.stopPropagation();
                var sharerId = $(this).parent().attr("sharer_id");
                var dialogId = $(this).parent().attr("dialog_id");
                removeOpenedContact(sharerId);
                var jqContactNode = $(this).parent();
                jqContactNode.fadeOut("slow", function() {
                    jqContactNode.remove();
                });
                // Закрываем диалог с контактом
                var jqContactDialog = $("#dialogModal_" + dialogId + ":not([modal_resized=true]):visible");
                if (jqContactDialog.length > 0) {
                    ChatView.hideChatModal(jqContactDialog);
                }
                return false;
            });

            var self = this;
            $("#scrollFastDialogs", $("#fastDialogs")).off();
            $("#scrollFastDialogs", $("#fastDialogs")).scroll(function () {
                self.initOtherCountMessages($(this).scrollTop());
            });
            self.initOtherCountMessages(0);
        },

        initOtherCountMessages : function(scrollTop) {
            //debugger;
            var jqFastContacts = $(".fastContactItem", $("#fastDialogs"));
            var contactsCount = jqFastContacts.length;
            if (contactsCount > 6) {
                var hiddenContactsTop = parseInt(scrollTop / 45);
                var hiddenContactsBottom = contactsCount - 6 - hiddenContactsTop;
                var jqHiddenFastContactsTop = [];
                var jqHiddenFastContactsBottom = [];
                jqFastContacts.each(function(i){
                    if (i < hiddenContactsTop) {
                        jqHiddenFastContactsTop.push($(this));
                    } else if (i > (contactsCount - hiddenContactsBottom - 1)) {
                        jqHiddenFastContactsBottom.push($(this));
                    }
                });
                var countHiddenTopMessages = 0;
                for (var index in jqHiddenFastContactsTop) {
                    var jqHiddenFastContactTop = jqHiddenFastContactsTop[index];
                    if ($(".countMessages", jqHiddenFastContactTop).is(":visible")) {
                        countHiddenTopMessages += parseInt($(".countMessages", jqHiddenFastContactTop).text());
                    }
                }
                var countHiddenBottomMessages = 0;
                for (var index in jqHiddenFastContactsBottom) {
                    var jqHiddenFastContactBottom = jqHiddenFastContactsBottom[index];
                    if ($(".countMessages", jqHiddenFastContactBottom).is(":visible")) {
                        countHiddenBottomMessages += parseInt($(".countMessages", jqHiddenFastContactBottom).text());
                    }
                }

                if (countHiddenTopMessages > 0) {
                    $("#hiddenTopMessagesCount").text(countHiddenTopMessages);
                }
                var jqHiddenTopMessagesCountBlock = $("#hiddenTopMessagesCountBlock");
                if (countHiddenTopMessages > 0 && !jqHiddenTopMessagesCountBlock.is(":visible")) {
                    jqHiddenTopMessagesCountBlock.fadeIn();
                } else if (countHiddenTopMessages == 0 && jqHiddenTopMessagesCountBlock.is(":visible")) {
                    jqHiddenTopMessagesCountBlock.fadeOut();
                }
                if (countHiddenBottomMessages) {
                    $("#hiddenBottomMessagesCount").text(countHiddenBottomMessages);
                }
                var jqHiddenBottomMessagesCountBlock = $("#hiddenBottomMessagesCountBlock");
                if (countHiddenBottomMessages > 0 && !jqHiddenBottomMessagesCountBlock.is(":visible")) {
                    jqHiddenBottomMessagesCountBlock.fadeIn();
                } else if (countHiddenBottomMessages == 0 && jqHiddenBottomMessagesCountBlock.is(":visible")) {
                    jqHiddenBottomMessagesCountBlock.fadeOut();
                }
            }
        },

        // Загрузить контакты по ИДам и те контакты у которых к нам есть новые сообщения
        showAllOpenedContacts: function() {
            var self = this;
            var ids = getOpenedContacts();
            if (ids.length > 0) {
                Chat.getContactByIdsAndWithUnreadMessages(ids, true, function (contacts) {
                    // Не найденые контакты добавляем в конец массива
                    for(var index in contacts) {
                        var contact = contacts[index];
                        if ($.inArray(contact.id, ids) == -1) {
                            ids.push(contact.id);
                        }
                    }
                    setOpenedContacts(ids);
                    // Сортируем контакты так как они были добавлены
                    var sortedContacts = [];
                    for (var i = ids.length - 1; i > -1; i--) {
                        var id = ids[i];
                        var contact = findObjectInArray(contacts, "id", id);
                        if (contact) sortedContacts.push(contact);
                    }
                    self.showOpenedContacts(sortedContacts);
                });
            }
        },

        showSharerInOpenedContacts: function(sharerId) { // Отобразить новое сообщение в быстрых контактах
            var self = this;
            Chat.getContactWithUnreadMessages(sharerId, function(contact){
                self.showOpenedContact(contact);
            });
        },

        hideInFastContactCountMessages: function(dialogId) {
            // Если есть сообщения от контакта - то скрыть
            var jqFastContactCountMessagesNode = $(".existsMessages", $(".fastContactItem[dialog_id=" + dialogId + " ]"));
            jqFastContactCountMessagesNode.fadeOut("slow", function(){
                jqFastContactCountMessagesNode.removeClass("existsMessages");
            });
        }
    };

    var dialogModalIdsKey = "dialogModal_dialogIds_" + CurrentSharer.id;
    var dialogModalShowKey = "dialogModal_show_" + CurrentSharer.id + "_";
    var dialogModalLeftKey = "dialogModal_left_" + CurrentSharer.id + "_";
    var dialogModalTopKey = "dialogModal_top_" + CurrentSharer.id + "_";
    var dialogModalWidthKey = "dialogModal_width_" + CurrentSharer.id + "_";
    var dialogModalHeightKey = "dialogModal_height_" + CurrentSharer.id + "_";
    var dialogModalNameKey = "dialogModal_dialogName_" + CurrentSharer.id + "_";
    var dialogModalMinimizedKey = "dialogModal_minimized_" + CurrentSharer.id + "_";
    var dialogModalShowInfoPageKey = "dialogModal_showInfoPage_" + CurrentSharer.id + "_";
    function getDialogIdsFromStorage() {
        var dialogIds = radomLocalStorage.getItem(dialogModalIdsKey);
        if (dialogIds == null) {
            dialogIds = {};
        } else {
            try {
                dialogIds = JSON.parse(dialogIds);
            } catch (e) {
                dialogIds = {};
            }
        }
        return dialogIds;
    }
    function getDialogFromStorage(dialogId) {
        var result = null;
        var dialogIds = getDialogIdsFromStorage();
        if (dialogIds[dialogId] != null) {
            result = {
                dialogId: dialogId,
                show: radomLocalStorage.getItem(dialogModalShowKey + dialogId),
                left: radomLocalStorage.getItem(dialogModalLeftKey + dialogId),
                top: radomLocalStorage.getItem(dialogModalTopKey + dialogId),
                width: radomLocalStorage.getItem(dialogModalWidthKey + dialogId),
                height: radomLocalStorage.getItem(dialogModalHeightKey + dialogId),
                dialogName: radomLocalStorage.getItem(dialogModalNameKey + dialogId),
                minimized: radomLocalStorage.getItem(dialogModalMinimizedKey + dialogId),
                showInfoPage: radomLocalStorage.getItem(dialogModalShowInfoPageKey + dialogId)
            };
            result.left = parseInt(result.left);
            result.top = parseInt(result.top);
            result.left = (result.left < 0 ? 0 : result.left) + "px";
            result.top = (result.top < 0 ? 0 : result.top) + "px";
        }
        return result;
    }
    function setDialogInStorage(dialogId, dialogName, left, top, width, height, minimized) {
        var dialogIds = getDialogIdsFromStorage();
        dialogIds[dialogId] = true;
        radomLocalStorage.setItem(dialogModalIdsKey, JSON.stringify(dialogIds));

        radomLocalStorage.setItem(dialogModalShowKey + dialogId, "true");
        radomLocalStorage.setItem(dialogModalLeftKey + dialogId, left);
        radomLocalStorage.setItem(dialogModalTopKey + dialogId, top);
        radomLocalStorage.setItem(dialogModalWidthKey + dialogId, width);
        radomLocalStorage.setItem(dialogModalHeightKey + dialogId, height);
        radomLocalStorage.setItem(dialogModalNameKey + dialogId, dialogName);
        radomLocalStorage.setItem(dialogModalMinimizedKey + dialogId, minimized == true ? "true" : "false");
    }
    function setDialogInfoPageInStorage(dialogId, showInfoPage) {
        radomLocalStorage.setItem(dialogModalShowInfoPageKey + dialogId, showInfoPage == true ? "true" : "false");
    }

    function removeDialogFromStorage(dialogId) {
        var dialogIds = getDialogIdsFromStorage();
        delete dialogIds[dialogId];
        radomLocalStorage.setItem(dialogModalIdsKey, JSON.stringify(dialogIds));

        radomLocalStorage.removeItem(dialogModalShowKey + dialogId);
        radomLocalStorage.removeItem(dialogModalLeftKey + dialogId);
        radomLocalStorage.removeItem(dialogModalTopKey + dialogId);
        radomLocalStorage.removeItem(dialogModalWidthKey + dialogId);
        radomLocalStorage.removeItem(dialogModalHeightKey + dialogId);
        radomLocalStorage.removeItem(dialogModalNameKey + dialogId);
        radomLocalStorage.removeItem(dialogModalMinimizedKey + dialogId);
        radomLocalStorage.removeItem(dialogModalShowInfoPageKey + dialogId);
    }
    function hideDialogInStorage(dialogId) {
        removeDialogFromStorage(dialogId);
        //radomLocalStorage.setItem(dialogModalShowKey + dialogId, "false");
    }
    function isDialogShowInStorage(dialogId) {
        return radomLocalStorage.getItem(dialogModalShowKey + dialogId) == "true";
    }

    // Ключ списка контактов, которые добавлены для быстрого доступа в чат
    var openedContactsKey = "openedContacts_" + CurrentSharer.id;

    function setOpenedContacts(contactIds) {
        var openedContactsStr = JSON.stringify(contactIds);
        radomLocalStorage.setItem(openedContactsKey, openedContactsStr);
    }

    function addOpenedContact(sharerId) {
        sharerId = parseFloat(sharerId);
        var openedContactsStr = radomLocalStorage.getItem(openedContactsKey);
        if (openedContactsStr == null) {
            openedContactsStr = "[]";
        }
        var openedContacts = JSON.parse(openedContactsStr);
        if ($.inArray(sharerId, openedContacts) == -1) {
            openedContacts.push(sharerId);
        }
        openedContactsStr = JSON.stringify(openedContacts);
        radomLocalStorage.setItem(openedContactsKey, openedContactsStr);
    }

    function removeOpenedContact(sharerId) {
        sharerId = parseFloat(sharerId);
        var openedContactsStr = radomLocalStorage.getItem(openedContactsKey);
        if (openedContactsStr == null) {
            openedContactsStr = "[]";
        }
        var openedContacts = JSON.parse(openedContactsStr);
        var index = $.inArray(sharerId, openedContacts);
        if (index > -1) {
            delete openedContacts[index];
        }
        var resultArr = [];
        for (var index in openedContacts) {
            resultArr.push(openedContacts[index]);
        }
        openedContactsStr = JSON.stringify(resultArr);
        radomLocalStorage.setItem(openedContactsKey, openedContactsStr);
    }

    function getOpenedContacts() {
        var openedContactsStr = radomLocalStorage.getItem(openedContactsKey);
        if (openedContactsStr == null) {
            openedContactsStr = "[]";
        }
        return JSON.parse(openedContactsStr);
    }

    function findObjectInArray(arr, objectProperty, propertyValue) {
        var foundObj = null;
        for (var index in arr) {
            var obj = arr[index];
            if (obj[objectProperty] == propertyValue) {
                foundObj = obj;
                break;
            }
        }
        return foundObj;
    };

    function initChatKeys() {
        openedContactsKey = "openedContacts_" + CurrentSharer.id;
        dialogModalIdsKey = "dialogModal_dialogIds_" + CurrentSharer.id;
        dialogModalShowKey = "dialogModal_show_" + CurrentSharer.id + "_";
        dialogModalLeftKey = "dialogModal_left_" + CurrentSharer.id + "_";
        dialogModalTopKey = "dialogModal_top_" + CurrentSharer.id + "_";
        dialogModalWidthKey = "dialogModal_width_" + CurrentSharer.id + "_";
        dialogModalHeightKey = "dialogModal_height_" + CurrentSharer.id + "_";
        dialogModalNameKey = "dialogModal_dialogName_" + CurrentSharer.id + "_";
        dialogModalMinimizedKey = "dialogModal_minimized_" + CurrentSharer.id + "_";
        dialogModalShowInfoPageKey = "dialogModal_showInfoPage_" + CurrentSharer.id + "_";
    }

    $(document).ready(function() {
        $(eventManager).bind("inited", function(event, userData) {
            CurrentSharer = userData;
            initChatKeys();

            ChatView.showAllOpenedContacts();
            ChatView.showOpenedDialogs();
            Chat.subscribeToNewMessage(function(message) {
                if (CurrentSharer.id != message.sender.id && (ChatPage.openedDialogId != message.dialog)) {
                    // Если открыт popup диалог
                    if ($("#dialogModal_" + message.dialog).is(":visible")) {
                        // do nothing
                    } else {
                        ChatView.showPopupMessage(message);
                        //ChatView.showDialogsInHeader();
                        RadomSound.playDefault();
                    }
                }

                var modalDialog = $("#dialogModal_" + message.dialog);
                if (modalDialog.is(":visible")) {
                    // Если открыт чат, то помечать как прочитанные
                    Chat.markDialogAsRead(message.dialog);

                    var contentModal = $(".messagesContainer", modalDialog);
                    // Если открыт popup диалог
                    if (CurrentSharer.id != message.sender.id) {
                        contentModal.append(ChatView.getPopupChatMarkupByMessage(message));
                        ChatView.fixAllChatMessages(contentModal);
                    } else {
                        var oldMessageBox = $("[uuid=" + message.uuid + "]");
                        MESSAGE_ID_TO_UUID[message.id] = message.uuid;
                        if (oldMessageBox.length > 0) {
                            oldMessageBox.hide();

                            var popupMessageBlock = ChatView.getPopupChatMarkupByMessage(message);
                            oldMessageBox.replaceWith(popupMessageBlock);
                            ChatView.fixAllChatMessages(contentModal);

                            $(".fa-spinner", popupMessageBlock).hide();
                        } else {
                            contentModal.append(ChatView.getPopupChatMarkupByMessage(message));
                            ChatView.fixAllChatMessages(contentModal);
                        }
                    }
                    contentModal.scrollTop(contentModal.prop('scrollHeight'));
                } else { // Обновить информацию в быстрых диалогах
                    ChatView.showSharerInOpenedContacts(message.sender.id);
                }
            });

            Chat.subscribeToMarkAsReadDialog(function (dialogId) {
                // Скрыть сообщение как прочитанное
                var id, i;
                var store = $.noty.store;
                var notyToClose = [];
                for(var key in store) {
                    var noty = store[key];
                    var data = noty.options.data;
                    if(data && data.dialog == dialogId) {
                        notyToClose.push(noty);
                    }
                }
                for (i = 0; i < notyToClose.length; i++) {
                    notyToClose[i].close();
                }

                //удалить еще не отображенные сообщения
                var pendingPopups = ChatView.pendingPopups;
                var toCleanUp = [];
                for(id in pendingPopups) {
                    if(pendingPopups.hasOwnProperty(id)) {
                        var res = pendingPopups[id];
                        if(res.dialog == dialogId) {
                            toCleanUp.push(res);
                        }
                    }
                }
                for (i = 0; i < toCleanUp.length; i++) {
                    toCleanUp[i].remove();
                }
                ChatView.hideInFastContactCountMessages(dialogId);
            });

            Chat.subscribeToEditMessage(function(message){
                var $markup = ChatView.getPopupChatMarkupByMessage(message);
                $(".messageInDialog[message_id=" + message.id + "]", ".messagesContainer").replaceWith($markup);
            });

            Chat.subscribeToDeleteMessage(function(message){
                var $markup = ChatView.getPopupChatMarkupByMessage(message);
                var jqMessageContainer = $(".messageInDialog[message_id=" + message.id + "]", ".messagesContainer");
                var jqMessagesContainer = jqMessageContainer.closest(".messagesContainer");
                jqMessageContainer.replaceWith($markup);
                ChatView.fixAllChatMessages(jqMessagesContainer);
            });

            // Обновлен процент загрузки файла
            Chat.subscribeToUpdateFilePercent(function(message){
                // Здесь делаем простую замену
                $(".messageInDialog[message_id=" + message.id + "]", ".messagesContainer").find(".loadedPercent").text(message.fileLoadedPercent);
            });

            // Отмена загрузки файла
            Chat.subscribeToCancelUploadFile(function(message){
                var $markup = ChatView.getPopupChatMarkupByMessage(message);
                var jqMessageContainer = $(".messageInDialog[message_id=" + message.id + "]", ".messagesContainer");
                var jqMessagesContainer = jqMessageContainer.closest(".messagesContainer");
                jqMessageContainer.replaceWith($markup);
                ChatView.fixAllChatMessages(jqMessagesContainer);
            });

            // Загрузка файла завершена
            Chat.subscribeToFinishUploadFile(function(message){
                var $markup = ChatView.getPopupChatMarkupByMessage(message);
                var jqMessageContainer = $(".messageInDialog[message_id=" + message.id + "]", ".messagesContainer");
                var jqMessagesContainer = jqMessageContainer.closest(".messagesContainer");
                jqMessageContainer.replaceWith($markup);
                ChatView.fixAllChatMessages(jqMessagesContainer);
            });

            // Загрузить список контактов
            // Должны быть данные по контактам онлайн
            // Сделать обработку клика по ссылке чатами для открытия списка контактов
            // Сделать обработку клика по контакту
            // Загрузить контакты у которых есть непрочитанные сообщения
            // Загрузить контакты с котормыми были открыты диалоги
            // Отобразить их над кнопкой с открытием контактов
            // При поступлении нового сообщения добавлять контакт в этот список

            var currentPage = 0;
            var cachedContacts = {};
            var showContacts = function(page, searchString, showOnlyOnline) {
                currentPage = page;
                Chat.getContacts(page, searchString, showOnlyOnline, function(contacts){
                    for (var index in contacts) {
                        var contact = contacts[index];
                        contact.avatarC50 = Images.getResizeUrl(contact.avatar, "c50");
                        var nameParts = contact.fullName.split(" ");
                        contact.shortName = nameParts[0] + " " + nameParts[1];
                        cachedContacts[contact.id] = contact;
                    }
                    var chatContactsBlock = $(Mustache.render(chatContactsTemplate, {
                        contacts : contacts
                    }));
                    if (page == 0) {
                        $("#contactsContainer").empty();
                        $("#contactsContainer").off();
                        // Загрузка истории при скроллинге
                        $("#contactsContainer").scroll(function(event) {
                            var $this = $(this);
                            if ($this.height() + $this.scrollTop() == $this.prop('scrollHeight')) { // Промотали в самый низ
                                showContacts(currentPage + 1, searchString, showOnlyOnline);
                            }
                        });
                        $("#contactsContainer").on("click", ".fullInfoSharer", function(){
                            //$('.closeModal', jqContactsModal).click();
                            var sharerId = $(this).attr("sharer_id");
                            ChatView.showDialogWithSharer(sharerId);
                            // Обновить контакт без перезагрузки списка
                            ChatView.showOpenedContact(cachedContacts[sharerId]);
                        });
                    }
                    $("#contactsContainer").append(chatContactsBlock);

                    $(".fullInfoSharer", $("#contactsContainer")).each(function(){
                        var jqSharersClone = $(".fullInfoSharer[sharer_id=" + $(this).attr("sharer_id") + "]", $("#contactsContainer"));
                        if (jqSharersClone.length > 1) {
                            jqSharersClone.each(function(i){
                                if (i > 0) {
                                    $(this).hide();
                                }
                            });
                        }
                    });

                    if (searchString != null) {
                        var regex = new RegExp( '(' + searchString + ')', 'gi' );
                        $(".fullInfoSharerLink", $("#contactsContainer")).each(function () {
                            var sharerName = $(this).text();
                            if (!regex.test(sharerName)) {
                                $(this).closest(".fullInfoSharer").remove();
                            } else {
                                $(this).html(sharerName.replace(regex, "<i style='background-color:#FFFF00;'>$1</i>" ));
                            }
                        })
                    }
                }, function(){

                });
            };

            var jqContactsModal = $("#contactsModal");
            var showOnlyOnline = false;

            $("#dialogs-menu").click(function(event){
                if (jqContactsModal.is(":visible")){
                    ChatView.hideChatModal(jqContactsModal);
                } else {
                    ChatView.hideNotResizedModals(jqContactsModal.attr("dialog_id"));
                    ChatView.initFocusModal(jqContactsModal);

                    jqContactsModal.show();
                    var left = $(window).width() - jqContactsModal.width() - $("#dialogs-menu").outerWidth() - 15;
                    var top = 10000; // Заведомо большое число
                    jqContactsModal.css("left", left);
                    jqContactsModal.css("top", top);
                    $(window).resize();
                    showContacts(0, null, showOnlyOnline);
                }
            });
            var setCountOnline = function(countOnline, sharer){
                var countOnlineStr = "";
                if (countOnline != 0) {
                    if (countOnline > 99) {
                        countOnlineStr = "99+";
                    } else {
                        countOnlineStr = countOnline;
                    }
                }
                $("#countOnline", $("#dialogs-menu")).text(countOnlineStr);
                $(".modal-title", jqContactsModal).text("Онлайн " + countOnline + " " + stringForms(countOnline, "друг", "друга", "друзей"));
                if (sharer != null) {
                    var jqStatusNode = $(".fullInfoSharer[sharer_id=" + sharer.id + "]", $("#contactsContainer")).find(".sharerStatus");
                    if (sharer.online) {
                        jqStatusNode.removeClass("sharerStatusOffline");
                        jqStatusNode.addClass("sharerStatusOnline");
                    } else {
                        jqStatusNode.removeClass("sharerStatusOnline");
                        jqStatusNode.addClass("sharerStatusOffline");
                    }
                }
            };

            /*Chat.getCountOnlineContacts(function(countOnline){
                countContactsOnline = countOnline;
                setCountOnline(countContactsOnline);
            });*/

            Chat.subscribeToContactOnline(function(sharer){
                countContactsOnline += 1;
                setCountOnline(countContactsOnline, sharer);
            });
            Chat.subscribeToContactOffline(function(sharer){
                countContactsOnline -= 1;
                setCountOnline(countContactsOnline, sharer);
            });
            // Закрыть диалог
            jqContactsModal.on('click', '.closeModal', function (event) {
                ChatView.hideChatModal(jqContactsModal);
                event.stopPropagation();
            });
            $(".closeModal", jqContactsModal).tooltip({container : "body"});
            // Поиск контактов
            jqContactsModal.on('input', '#searchContacts', function(){
                showContacts(0, $(this).val(), showOnlyOnline);
            });
            jqContactsModal.on('keydown', '#searchContacts', function (event) {
                var keycode = (event.keyCode ? event.keyCode : event.which);
                if (keycode == 27) {
                    $(this).val("");
                    showContacts(0, null, showOnlyOnline);
                }
            });
            $('#showOnline', jqContactsModal).tooltip({container : "body"});
            jqContactsModal.on('click', '#showOnline', function (event) {
                var $this = $(this);
                if ($this.hasClass("showOnlyOnline")) {
                    $this.removeClass("showOnlyOnline");
                    showOnlyOnline = false;
                } else {
                    $this.addClass("showOnlyOnline");
                    showOnlyOnline = true;
                }
                $("#searchContacts", jqContactsModal).val("");
                showContacts(0, null, showOnlyOnline);
            });

            if (ChatView.isOpenedContactList()) {
                jqContactsModal.show();
                var storageContactsDialog = getDialogFromStorage("contactsModal");
                var left = 0;
                var top = 0;
                if (storageContactsDialog != null) {
                    left = storageContactsDialog.left;
                    top = storageContactsDialog.top;

                    jqContactsModal.css("left", left);
                    jqContactsModal.css("top", top);
                    $(window).resize();
                    left = parseInt(jqContactsModal.css("left"));
                    top = parseInt(jqContactsModal.css("top"));
                    setDialogInStorage("contactsModal", "contactsModal", left, top, 0, 0, false);
                    showContacts(0, null, showOnlyOnline);
                }
            }

            $("a#header-unread-chat-messages-icon").radomTooltip({
                container : "body",
                title : "Мои сообщения",
                placement : "bottom",
                trigger : "manual"
            });
            $("a#header-unread-chat-messages-icon").on("mouseenter", function() {
                var $this = $(this);
                var timeout = $this.data("timeout");
                if (timeout) {
                    clearTimeout(timeout);
                }
                timeout = setTimeout(function() {
                    $this.tooltip("show");
                }, RadomTooltipSettings.showDelay);
                $this.data("timeout", timeout);
            }).on("mouseleave", function() {
                var $this = $(this);
                var timeout = $this.data("timeout");
                if (timeout) {
                    clearTimeout(timeout);
                }
                timeout = setTimeout(function() {
                    $this.tooltip("hide");
                }, RadomTooltipSettings.hideDelay);
                $this.data("timeout", timeout);
            }).on("click", function() {
                var $this = $(this);
                var timeout = $this.data("timeout");
                if (timeout) {
                    clearTimeout(timeout);
                }
                $this.tooltip("hide");
            });

            Chat.subscribeToPrintChatMessage(function (data) {
                var dialogModal = $("#dialogModal_" + data.dialogId);
                if (dialogModal.is(":visible")) {
                    var content = $(".printParticipantText", dialogModal);
                    content.html(data.senderName + " набирает сообщение");
                    setTimeout(function () {
                        $(".printTextBox", dialogModal).css("visibility", "hidden");
                    }, 3000);
                    $(".printTextBox", dialogModal).css("visibility", "visible");
                }
            });

            //
            $('body').on('click', '.chatButton', function (e) {
                var dialogId = $(this).attr("dialog_id");
                var dialogName = $(this).attr("dialog_name");
                ChatView.showPopupChatDialog(dialogId, dialogName);
            });

            // Отметить как прочитанное
            $('body').on('click', 'a.mark-as-read', function (e) {
                var dialogId = $(this).attr("dialog_id");
                Chat.markDialogAsRead(dialogId, function () {});
            });

            //-----------
            // Изменение размеров диалога чата
            var resizedModal = null;
            var startTop = 0;
            var startLeft = 0;
            var startBottom = 0;
            var startRight = 0;
            var startTopCursor = 0;
            var startLeftCursor = 0;
            var startWidth = 0;
            var startHeight = 0;
            var windowHeight = 0;
            var windowWidth = 0;
            var resizeDialogModalFrom = "lefttop";
            var minDialogModalWidth = 275;
            var minDialogModalHeight = 218;
            $('body').on('mousedown', '.resizeBox', function (e) {
                if ($(e.target).prop("tagName").toUpperCase() == "BUTTON") { // Если нажали на дочернюю кнопку
                    return;
                }
                if (resizedModal == null) {
                    var resizeBox = $(this);
                    resizeDialogModalFrom = resizeBox.attr("resize_from");
                    resizedModal = resizeBox.closest(".dialogModal");
                    var evt = (e || event);
                    startTopCursor = evt.clientY;
                    startLeftCursor = evt.clientX;
                    startTop = resizedModal.position().top;
                    startLeft = resizedModal.position().left;

                    startWidth = resizedModal.width();
                    startHeight = $(".modal-body", resizedModal).height();

                    startBottom = startTop + resizedModal.height();
                    startRight = startLeft + startWidth;

                    windowHeight = $(window).height();
                    windowWidth = $(window).width();
                }
            });
            $('body').on('mouseup', function (e) {
                if (resizedModal != null) {
                    var width = resizedModal.width();
                    var height = $(".modal-body", resizedModal).height();
                    var left = resizedModal.css("left");
                    var top = resizedModal.css("top");

                    var dialogId = resizedModal.attr("dialog_id");
                    // Если это диалог со списком контактов
                    if (resizedModal.attr("id") == "contactsModal") {// Если это диалог со списоком контактов
                        setDialogInStorage("contactsModal", "contactsModal", left, top, width, height, false);
                    } else { // Если это диалог с чатом
                        var dialogName = resizedModal.attr("dialog_name");
                        var minimized = false;
                        var storedDialog = getDialogFromStorage(dialogId);
                        if (storedDialog != null) {
                            minimized = storedDialog.minimized == "true";
                            dialogName = storedDialog.dialogName;
                        }
                        setDialogInStorage(dialogId, dialogName, left, top, width, height, minimized);
                    }

                    resizedModal = null;
                }
            });
            $('body').on('mousemove', function (e) {
               if (resizedModal != null) {
                   var evt = (e || event);
                   var deltaX = evt.clientX - startLeftCursor;
                   var deltaY = evt.clientY - startTopCursor;

                   var newTop = parseInt(resizedModal.css("top"));
                   var newLeft = parseInt(resizedModal.css("left"));
                   var newHeight = $(".modal-body", resizedModal).height();
                   var newWidth = resizedModal.width();

                   switch (resizeDialogModalFrom) {
                       case "dragging": // Простко перетаскивание
                           newTop = fixTopDialogModal(startTop + deltaY, resizedModal, windowHeight);
                           newLeft = fixLeftDialogModal(startLeft + deltaX, resizedModal, windowWidth);
                           break;
                       case "lefttop": // Изменение размеров с левого верхнего угла
                           newTop = fixTopDialogModal(startTop + deltaY, resizedModal, windowHeight);
                           newLeft = fixLeftDialogModal(startLeft + deltaX, resizedModal, windowWidth);
                           if (newLeft + minDialogModalWidth > startRight) {
                               newLeft = startRight - minDialogModalWidth;
                           }
                           if (newTop + minDialogModalHeight > startBottom) {
                               newTop = startBottom - minDialogModalHeight;
                           }
                           newWidth = startWidth + (startLeft - newLeft);
                           newHeight = startHeight + (startTop - newTop);
                           break;
                       case "righttop":
                           newTop = fixTopDialogModal(startTop + deltaY, resizedModal, windowHeight);
                           newLeft = startLeft;
                           newWidth = (startWidth + deltaX) < minDialogModalHeight ? minDialogModalHeight : (startWidth + deltaX);
                           if (newTop + minDialogModalHeight > startBottom) {
                               newTop = startBottom - minDialogModalHeight;
                           }
                           newHeight = startHeight + (startTop - newTop);
                           break;
                       case "leftbottom":
                           newTop = startTop;
                           newLeft = fixLeftDialogModal(startLeft + deltaX, resizedModal, windowWidth);
                           if (newLeft + minDialogModalWidth > startRight) {
                               newLeft = startRight - minDialogModalWidth;
                           }
                           newWidth = startWidth + (startLeft - newLeft);
                           newHeight = startHeight + deltaY;
                           break;
                       case "rightbottom":
                           newTop = startTop;
                           newLeft = startLeft;
                           newWidth = startWidth + deltaX;
                           newHeight = startHeight + deltaY;
                           break;
                   }
                   resizedModal.css("left", newLeft + "px");
                   resizedModal.css("top", newTop + "px");
                   resizedModal.width(newWidth);
                   resizedModal.attr("modal_resized", "true");
                   $(".modal-body", resizedModal).height(newHeight);
                   return false;
               }
            });
            function fixLeftDialogModal(left, resizedModal, windowWidth) {
                return left < 0 ? 0 : (left + resizedModal.width() > windowWidth ? windowWidth - resizedModal.width() : left);
            }
            function fixTopDialogModal(top, resizedModal, windowHeight) {
                return top < 0 ? 0 : (top + resizedModal.height() > windowHeight ? windowHeight - resizedModal.height() : top);
            }


            $("#smiles-table-source").find("table td").emoticonize();

            $(document).click(function (e) {
                $('.smiles-table:visible').remove();
            });

            // Участник диалога онлайн
            Chat.subscribeToDialogParticipantOnline(function(sharerMessage){
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).removeClass("sharerStatusOffline");
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).removeClass("sharerStatusOnline");
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).addClass("sharerStatusOnline");
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).attr("data-original-title", "Онлайн");
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).tooltip({container : "body"});

                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).each(function(){
                    var dialogModalBox = $(this).closest(".dialogModal");
                    var commonCount = $(".shortDialogInfoContainer", dialogModalBox).attr("common_count");
                    var onlineCount = parseInt($(".shortDialogInfoContainer", dialogModalBox).attr("online_count")) + 1;

                    var shortDialogInfoText = "Онлайн [" + onlineCount + "/" + commonCount +  "]";
                    $(".shortDialogInfoContainer", dialogModalBox).html(shortDialogInfoText);
                    $(".shortDialogInfoContainer", dialogModalBox).attr("data-original-title", shortDialogInfoText);
                    $(".shortDialogInfoContainer", dialogModalBox).tooltip({container : "body"});
                    $(".shortDialogInfoContainer", dialogModalBox).attr("online_count", onlineCount);
                });
            });

            // Участник диалога офлайн
            Chat.subscribeToDialogParticipantOffline(function(sharerMessage){
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).removeClass("sharerStatusOffline");
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).removeClass("sharerStatusOnline");
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).addClass("sharerStatusOffline");
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).attr("data-original-title", "Оффлайн");
                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).tooltip({container : "body"});

                $(".sharerStatus", $(".fullInfoSharer[sharer_id=" + sharerMessage.id + "]")).each(function(){
                    var dialogModalBox = $(this).closest(".dialogModal");
                    var commonCount = $(".shortDialogInfoContainer", dialogModalBox).attr("common_count");
                    var onlineCount = parseInt($(".shortDialogInfoContainer", dialogModalBox).attr("online_count")) - 1;

                    var shortDialogInfoText = "Онлайн [" + onlineCount + "/" + commonCount +  "]";
                    $(".shortDialogInfoContainer", dialogModalBox).html(shortDialogInfoText);
                    $(".shortDialogInfoContainer", dialogModalBox).attr("data-original-title", shortDialogInfoText);
                    $(".shortDialogInfoContainer", dialogModalBox).tooltip({container : "body"});
                    $(".shortDialogInfoContainer", dialogModalBox).attr("online_count", onlineCount);
                });
            });

            var countPencilFirstPosition = 0;
            setInterval(function(){
                var padding = parseInt($(".printParticipantPencil").css("padding-left"));
                if (padding > 15) {
                    padding = 0;
                    countPencilFirstPosition = 1;
                } else {
                    if (countPencilFirstPosition > 4) {
                        padding += 4;
                    }
                    countPencilFirstPosition++;
                }
                $(".printParticipantPencil").css("padding-left", padding + "px");
            }, 150);

            // При изменении размера окна двигаем открытые диалоги
            $(window).resize(function() {
                repositionOpenedDialogs();
            });
        });
    });

    function repositionOpenedDialogs() {
        // Нужно оставить на месте те диалоги, у которых изменён размер
        // Те диалоги у которых размер не менялся, нужно прибить к низу

        var dialogModals = $(".dialogModal:not([modal_resized=true]):visible");
        var windowWidth = $(window).width();
        var windowHeight = $(window).height();
        dialogModals.each(function(){
            var dialogModal = $(this);
            var left = windowWidth - 275 - $("#dialogs-menu").outerWidth() - 15;
            var top = windowHeight - dialogModal.height();
            dialogModal.css("left", left + "px");
            dialogModal.css("top", top + "px");
        });
    }
</script>
<div id="fastDialogs">
    <div id="hiddenTopMessagesCountBlock" style="position: relative; height: 20px; display: none;">
        <div id="hiddenTopMessagesCount" class="countMessages" style="display: block;">123</div>
    </div>
    <div id="scrollFastDialogs"></div>
    <div id="hiddenBottomMessagesCountBlock" style="position: relative; height: 20px; display: none;">
        <div id="hiddenBottomMessagesCount" class="countMessages" style="display: block;">123</div>
    </div>
</div>
<div id="dialogs-menu">
    <span id="countOnline"></span>
	<span class="header-glyphicon-link fa fa-comments-o" id="header-unread-chat-messages-icon">
	</span>
</div>