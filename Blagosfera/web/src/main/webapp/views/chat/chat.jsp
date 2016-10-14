<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">
    div.chat-messages-list {
        height: 400px;
        overflow-y: scroll;
        overflow-x: hidden;
        margin-bottom: 10px;
    }

    div.chat-messages-list hr {
        margin-top: 1px;
        margin-bottom: 1px;
    }

    #dialogs-contents {
        margin-top: 20px;
    }

    div.dialogs-list-item {
        position: relative;
        padding-top: 10px;
        padding-bottom: 0;
    }

    div.dialogs-list-item p.last-message-date {
        display: inline-block;
        margin-right: 20px;
    }

    div.chat-message-item.for-interlocutor {
        background-color: #f3f3f3;
    }

    div.dialogs-list-item.unread {
        background-color: #d9edf7;
    }

    div.chat-message-item.selected,
    div.chat-message-item.unread.selected {
        background-color: #B5D2E0;
    }

    div.dialogs-list-item + hr {
        margin-top: 0;
        margin-bottom: 0;
    }

    div.dialogs-list-item div.unread-mark {
        background-color: #fff;
        color: red;
        display: none;
        font-size: 12px;
        left: 80px;
        padding: 1px 2px;
        position: absolute;
        top: 75px;
        z-index: 1000;

        -webkit-border-radius: 3px;
        -moz-border-radius: 3px;
        border-radius: 3px;
    }

    div.dialogs-list-item.unread div.unread-mark {
        display: block;
    }

    div.chat-message-item {
        padding: 1px 15px 1px 0;
        cursor: pointer;
    }

    div.chat-message-item .fa-spinner {
        visibility: hidden;
    }

    div.chat-message-item.local .fa-spinner {
        visibility: visible;
    }

    div.chat-message-item .avatar-wrapper {
        padding-right: 0;
        padding-left: 16px;
    }

    td.avatar-wrapper {
        padding-right: 0;
        padding-left: 16px;
    }

    div.chat-message-item .date-wrapper {
        padding-top: 2px;
    }

    div.chat-message-item .date-para {
        font-size: 12px;
        margin-bottom: 0;
        line-height: 12px
    }

    div.chat-message-item .actions {
        margin-bottom: 0;
        height: 10px;
        opacity: 0;
        -webkit-transition: opacity 0.3s linear 0s;
        -moz-transition: opacity 0.3s linear 0s;
        -o-transition: opacity 0.3s linear 0s;
        transition: opacity 0.3s linear 0s;
    }

    div.chat-message-item:hover .actions {
        opacity: 1;
        -webkit-transition: opacity 0.3s linear 0s;
        -moz-transition: opacity 0.3s linear 0s;
        -o-transition: opacity 0.3s linear 0s;
        transition: opacity 0.3s linear 0s;
    }

    div.chat-message-item .actions a {
        line-height: 10px;
        font-size: 10px;
        top: -2px;
    }

    div.chat-message-item.non-first a.avatar-link {
        display: none
    }

    div.chat-message-item.non-first a.name-link {
        display: none
    }

    div.chat-message-item.non-first .date {
        opacity: 0;
        -webkit-transition: opacity 0.3s linear 0s;
        -moz-transition: opacity 0.3s linear 0s;
        -o-transition: opacity 0.3s linear 0s;
        transition: opacity 0.3s linear 0s;
    }

    div.chat-message-item.non-first:hover .date {
        opacity: 1;
        -webkit-transition: opacity 0.3s linear 0s;
        -moz-transition: opacity 0.3s linear 0s;
        -o-transition: opacity 0.3s linear 0s;
        transition: opacity 0.3s linear 0s;
    }

    div.chat-message-item.unread {
        background-color: #d9edf7;
    }

    ul#dialogs-tabs li div.unread-mark {
        position: absolute;
        z-index: 1000;
        top: 3px;
        left: 3px;
        font-size: 10px;
        color: red;
        display: none;
    }

    ul#dialogs-tabs.nav > li > a {
        padding: 3px 5px;
    }

    ul#dialogs-tabs.nav > li > a > span.glyphicon-remove {
        cursor: pointer;
    }

    ul#dialogs-tabs.nav > li.active > a > span.glyphicon-remove:hover {
        color: #333;
    }

    ul#dialogs-tabs li.unread div.unread-mark {
        display: block;
    }

    ul#dialogs-tabs li.unread {
        background-color: #d9edf7;
    }

    p.interlocutor-print {
        margin-bottom: 0;
        margin-left: 55px;
        font-size: 12px;
        color: #fff;
    }

    p.interlocutor-print span.glyphicon-pencil {
        width: 24px;
    }

    .chat-message-item.edit {
        background-color: #FFFDD8 !important;
    }

    .tab-pane {
        border: 1px solid transparent;
    }

    .tab-pane textarea.edit {
        background-color: #FFFDD8;
    }

    .chat-message-item .edit-message-count {
        cursor: pointer;
    }

    .deleted-text {
        font-style: italic;
        color: #999;
    }

    p.message-text {
        width: 520px;
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>

<script id="dialogs-list-item-template" type="x-tmpl-mustache">
	<div class="row dialogs-list-item">
		<div class="unread-mark glyphicon glyphicon-envelope"></div>
		<div class="col-xs-2">
			<a href="&#35;{{dialog.id}}">
				<img style="width : 84px; height : 84px;" class="img-thumbnail" src="{{dialog.companion.avatar84}}"/>
			</a>
		</div>
		<div class="col-xs-10">
            <h5 id="dialog-title-{{dialog.id}}">{{dialog.name}}</h5>
			<p class="text-muted last-message-text">
				{{lastMessagePrefix}}
				{{lastMessageText}}
			</p>
			<p class="text-muted last-message-date">{{lastMessageDate}}</p>

			<a class="btn btn-default btn-xs pull-right" href="&#35;" id="button-remove-dialog-{{dialog.id}}">Удалить диалог</a>
			<a class="btn btn-default btn-xs pull-right" href="&#35;{{dialog.id}}">Перейти к диалогу</a>
		</div>
	</div>
	<hr/>
</script>

<script id="nav-tab-template" type="x-tmpl-mustache">
	<li>
		<a href="#chat-content-{{dialog.id}}" id="chat-content-link-{{dialog.id}}" role="tab" data-toggle="tab">
			<span id="chat-content-title-{{dialog.id}}">{{dialog.name}}</span>
			<span class="glyphicon glyphicon-remove" id="dialog-tab-remove-{{dialog.id}}" data-toggle="tooltip" data-placement="top" title="Закрыть диалог"></span>
		</a>
	</li>
</script>

<script id="tab-content-template" type="x-tmpl-mustache">
	<div class="tab-pane" id="chat-content-{{dialog.id}}">
		<div class="chat-messages-list">
		    <div class="row list-loader-animation" id="historyDataLoaderAnimation-{{dialog.id}}" style="display: block;"></div>
			<p class="text-muted interlocutor-print" id="interlocutor-print-{{dialog.id}}"><span class="glyphicon glyphicon-pencil"></span><span id="interlocutor-print-{{dialog.id}}-content"/><span class="dots">...</span></p>
		</div>
		<div class="well">
			<div class="row">
                <table width="100%">
                    <tr>
                        <td class="col-xs-2" valign="top">
                            <a href="{{sharer.link}}">
                                <img class="img-thumbnail" src='{{sharer.avatarc77}}' />
                            </a>
                        </td>
                        <td valign="top" id="companion-message-container-{{dialog.id}}">
                            <form>
                                <table width="100%">
                                <tr><td>
                                    <div class="form-group">
                                        <div style="position: relative; float: left; width: 100%;">
                                            <textarea name="text" class="form-control" rows="3" id="message-edit-{{dialog.id}}" {{#dialog.closed}}disabled="disabled"{{/dialog.closed}}></textarea>
                                            {{^dialog.closed}}
                                                <span style="position: absolute; right: 0px; bottom: 0px; margin: 3px; cursor: pointer;"
                                                      id="toggle-smiles-table-{{dialog.id}}"
                                                      data-toggle="tooltip"
                                                      title="Библиотека смайлов">
                                                    <span id="toggle-smiles-icon-{{dialog.id}}">:-)</span>
                                                </span>
                                            {{/dialog.closed}}
                                        </div>
                                    </div>
                                </td></tr>
                                <tr><td>
                                    {{#dialog.closed}}
                                        <span class="help-block" style='color: red;'>Диалог закрыт</span>
                                    {{/dialog.closed}}
                                    {{^dialog.closed}}
                                        <span class="help-block">enter - отправить, ctrl+enter или shift+enter - новая строка</span>
                                        <span class="help-block">для отправки файла перетащите его в область диалога</span>
                                    {{/dialog.closed}}
                                </td></tr>
                                <tr><td>
                                    {{^dialog.closed}}
                                        <input type="button" class="btn btn-info send-message-button" value="Отправить" />
                                        <input type="button" class="btn btn-info apply-edit-message-button" value="Сохранить" style="display : none;" />
                                        <input type="button" class="btn btn-default cancel-edit-message-button" value="Отменить" style="display : none;" />
                                    {{/dialog.closed}}
                                </td></tr>
                                </table>
                            </form>
                        </td>
                        <td class="col-xs-2" valign="top" id="companion-info-container-{{dialog.id}}">
                            <a href="{{dialog.companion.link}}" id="companion-info-link-{{dialog.id}}">
                                <img class="img-thumbnail" src='{{dialog.companion.avatar77}}' id="companion-info-avatar-{{dialog.id}}"/>
                            </a>
                        </td>
                    </tr>
                </table>
			</div>
		</div>
	</div>
</script>

<script id="message-template" type="x-tmpl-mustache">
	<div class="row chat-message-item{{^message.read}} unread{{/message.read}}{{#forMe}} for-me{{/forMe}}{{^forMe}} for-interlocutor{{/forMe}}{{^date}} local{{/date}}"
	     data-for-id="{{message.sender.id}}" data-date="{{date}}" data-message-id="{{message.id}}" data-uuid="{{uuid}}">
		<table width="100%" border="0">
		<tr>
		    <td valign="top" rowspan="2" class="avatar-wrapper" style="width: 75px;">
                <a class="avatar-link" href="{{message.sender.link}}">
                    <img class="img-thumbnail" src="{{message.sender.avatar}}" />
                </a>
            </td>
            <td valign="top">
                <a class="name-link" href="{{message.sender.link}}">{{message.sender.shortName}}</a>
            </td>
            <td valign="top" rowspan="2" class="text-right date-wrapper" style="width: 55px;">
                {{^message.deleted}}
                    {{#message.date}}
                        <div class="text-muted date-para">
                            {{#message.editDate}}
                                <div>
                                    <i class="glyphicon glyphicon-tags edit-message-count"></i>
                                    &nbsp;
                                </div>
                            {{/message.editDate}}
                            <div class="date">{{time}}</div>
                        </div>
                        <div class="text-muted actions">
                            {{^forMe}}
                                {{^message.dialogClosed}}
                                    {{#message.allowEdit}}
                                        <a href="#" class="glyphicon glyphicon-pencil edit-message-link"></a>
                                    {{/message.allowEdit}}
                                {{/message.dialogClosed}}
                            {{/forMe}}
                            {{^message.dialogClosed}}
                                {{#canDelete}}
                                    <a href="#" class="glyphicon glyphicon-remove delete-message-link" data-message-id="{{message.id}}"></a>
                                {{/canDelete}}
                            {{/message.dialogClosed}}
                        </div>
                    {{/message.date}}
                    <i class="fa fa-spinner faa-spin animated"></i>
                {{/message.deleted}}
            </td>
        </tr>
        <tr>
            <td>
                {{#message.deleted}}
                    <p class="text-muted deleted-text">Сообщение удалено</p>
                {{/message.deleted}}
                {{^message.deleted}}
                    {{#message.fileMessage}}
                        {{#message.uploadInProcess}}
                            <p class="message-text" message_id="{{id}}">{{{message.text}}} ({{message.fileSize}})</p>
                            <p class="text-muted deleted-text">Загружается. Загружено <span class="loadedPercent">{{message.fileLoadedPercent}}</span>%</p>
                        {{/message.uploadInProcess}}
                        {{#message.uploadCancel}}
                            <p class="text-muted deleted-text">Загрузка файла отменена</p>
                        {{/message.uploadCancel}}
                        {{#message.uploaded}}
                            <p class="message-text" message_id="{{id}}">{{{message.text}}} ({{message.fileSize}})</p>
                        {{/message.uploaded}}
                    {{/message.fileMessage}}
                    {{^message.fileMessage}}
                        <p class="text-muted message-text">{{{message.text}}}</p>
                    {{/message.fileMessage}}
                {{/message.deleted}}
            </td>
        </tr>
        </table>
	</div>
</script>

<script id="dialog-contact-template" type="x-tmpl-mustache">
    <li class="list-group-item sidebar-new-contacts-item" id="sidebar-dialog-contacts-item-{{contact.id}}">
        <a href="{{contact.link}}" class="avatar-link"><img class="img-thumbnail" src="{{contact.avatar}}"/></a>
        <a href="{{contact.link}}" class="name-link">{{contact.shortName}}</a>

        <span class="glyphicon glyphicon-remove" style="cursor: pointer;" id="sidebar-dialog-contacts-item-remove-{{contact.id}}" data-toggle="tooltip" data-placement="top" title="Удалить"></span>
    </li>
</script>

<script type="text/javascript">
    var dialogsListItemTemplate = $('#dialogs-list-item-template').html();
    var navTabTemplate = $('#nav-tab-template').html();
    var tabContentTemplate = $('#tab-content-template').html();
    var messageTemplate = $('#message-template').html();
    var dialogContactTemplate = $('#dialog-contact-template').html();

    Mustache.parse(dialogsListItemTemplate);
    Mustache.parse(navTabTemplate);
    Mustache.parse(tabContentTemplate);
    Mustache.parse(messageTemplate);
    Mustache.parse(dialogContactTemplate);

    var userData = null;
    var sharerId = null;
    var sharerShortName = null;
    var sharerAvatar = null;
    var sharerLink = null;

    var chatNameMaxLength = "${chatNameMaxLength}";

    var userContacts = [
        <c:forEach items="${userContacts}" var="userContact" varStatus="status">
        {
            "name": "${userContact.other.mediumName}",
            "id": "${userContact.other.id}",
            "avatar": "${userContact.other.avatar}"
        }
        <c:if test="${!status.last}">, </c:if>
        </c:forEach>
    ];

    function createImageFromUrlWithFancybox(url, successFunction, errorFunction) {
        var a = $("<a/>", {
            href: url,
            alt: "",
            align: "middle",
            title: url
        });

        var img = $("<img/>", {
            src: url,
            alt: "...",
            style: "max-width: 100%; height: auto;",
            error: function () {
                if (errorFunction) errorFunction(url);
            },
            load: function () {
                if (successFunction) successFunction(this);
            }
        });

        a.append(img);

        a.click(function (event) {
            event.stopPropagation();
        });

        a.fancybox({helpers : {
            title: {
                type: 'outside',
                position: 'bottom'
            }
        }});

        a.addClass("trusted-link");

        return a;
    }

    function validateImageUrl(url) {
        return /^https?:\/\/.+\.(gif|png|jpg|jpeg)$/i.test(url);
    }

    function parseYoutubeUrl(url) {
        var youTubeId = false;

        // regex from : https://gist.github.com/FinalAngel/1876898
        /*Tested examples:
         http://www.youtube.com/user/Scobleizer#p/u/1/1p3vcRhsYGo
         http://www.youtube.com/watch?v=cKZDdG9FTKY&feature=channel
         http://www.youtube.com/watch?v=yZ-K7nCVnBI&playnext_from=TL&videos=osPknwzXEas&feature=sub
         http://www.youtube.com/ytscreeningroom?v=NRHVzbJVx8I
         http://www.youtube.com/user/SilkRoadTheatre#p/a/u/2/6dwqZw0j_jY
         http://youtu.be/6dwqZw0j_jY
         http://www.youtube.com/watch?v=6dwqZw0j_jY&feature=youtu.be
         http://youtu.be/afa-5HQHiAs
         http://www.youtube.com/user/Scobleizer#p/u/1/1p3vcRhsYGo?rel=0
         http://www.youtube.com/watch?v=cKZDdG9FTKY&feature=channel
         http://www.youtube.com/watch?v=yZ-K7nCVnBI&playnext_from=TL&videos=osPknwzXEas&feature=sub
         http://www.youtube.com/ytscreeningroom?v=NRHVzbJVx8I
         http://www.youtube.com/embed/nas1rJpm7wY?rel=0
         http://www.youtube.com/watch?v=peFZbP64dsU*/

        var regExp = /(youtu(?:\.be|be\.com)\/(?:.*v(?:\/|=)|(?:.*\/)?)([\w'-]+))/i;
        var match = url.match(regExp);

        if (match && match[2].length == 11) {
            youTubeId = match[2];
        }

        return youTubeId;
    }

    function getDeclension(number, string1, string24, string50) {
        digit = (number < 0 ? (number - number * 2) : number) % 100;
        digit = digit > 20 ? digit % 10 : digit;
        return (digit == 1 ? string1 : (digit > 4 || digit < 1 ? string50 : string24));
    }

    function getDialogsItemMarkup(dialog) {
        /*var count = dialog.sharers.length;

         for (var i = count - 1; i > -1; i--) {
         if (dialog.sharers[i].ikp == currentIkp) {
         dialog.sharers.splice(i, 1);
         }
         }*/

        dialog.companion = {
            id : dialog.companionId,
            avatar : dialog.companionAvatar,
            link : dialog.companionLink,
        };
        if (dialog.lastMessageText != null) {
            dialog.lastMessage = {
                sender: {id: dialog.lastMessageSenderId},
                text: dialog.lastMessageText,
                date: dialog.lastMessageDate
            };
        } else {
            dialog.lastMessage = null;
        }
        /*if (dialog.sharers.length == 2) {
            if (dialog.sharers[0].id != sharerId) {
                dialog.companion = dialog.sharers[0];
            } else {
                dialog.companion = dialog.sharers[1];
            }

            dialog.name = dialog.companion.mediumName;
        } else if (dialog.lastMessage) {
            dialog.companion = dialog.lastMessage.sender;
        } else if (dialog.sharers.length) {
            dialog.companion = dialog.sharers[0];

            for (var i = 0; i < dialog.sharers.length; i++) {
                if (dialog.sharers[i].id != sharerId) {
                    dialog.companion = dialog.sharers[i];
                    break;
                }
            }
        } else {
            dialog.companion = {"link": sharerLink, "avatar": sharerAvatar};
        }*/

        dialog.companion.avatar84 = Images.getResizeUrl(dialog.companion.avatar, "c84");
        dialog.companion.avatar77 = Images.getResizeUrl(dialog.companion.avatar, "c77");

        if (!dialog.name) {
            dialog.name = "Без названия";
        }

        if (dialog.name.length > chatNameMaxLength) {
            dialog.name = dialog.name.substr(0, chatNameMaxLength) + "...";
        }

        var model = {
            dialog: dialog,
            lastMessagePrefix: (dialog.lastMessage) ? (dialog.lastMessage.sender.id == sharerId ? "Вы: " : "") : "",
            lastMessageText: (dialog.lastMessage) ? ((dialog.lastMessage.text.length > 50) ? dialog.lastMessage.text.substr(0, 50) + "..." : dialog.lastMessage.text) : "",
            lastMessageDate: (dialog.lastMessage) ? dateFormat(dialog.lastMessage.date, "dd.mm.yyyy HH:MM:ss") : ""
        };

        var $item = $(Mustache.render(dialogsListItemTemplate, model));
        $item.data("dialog", dialog);

        /*if (!window.dialogs) window.dialogs = {};

        window.dialogs[dialog.id] = dialog;*/

        return $item;
    }

    function sortSharers(sharers) {
        sharers.sort(function (a, b) {
            if (a.shortName > b.shortName) return -1;
            if (a.shortName < b.shortName) return 1;
            return 0;
        });

        sharers.sort(function (a, b) {
            if (a.online && !b.online) return 1;
            if (!a.online && b.online) return -1;
            return 0;
        });

        sharers.sort(function (a, b) {
            if (a.id == sharerId) return -1; else return 1;
        });
    }

    function showDialogs(callback) {
        $("div#chats-list").empty();
        Chat.loadDialogs(function (response) {
            if (window.dialogs) {
                $.each(window.dialogs, function (index, dialog) {
                    var found = false;

                    for (var j = 0; j < response.length; j++) {
                        if (response[j].id == dialog.id) {
                            found = true;
                        }
                    }

                    if (!found) $("span#dialog-tab-remove-" + dialog.id).click();
                });
            }

            //window.dialogs = {};

            $.each(response, function (index, dialog) {
                var dialogsItem = getDialogsItemMarkup(dialog)
                $("div#chats-list").append(dialogsItem);

                dialogsItem.find("p.last-message-text").emoticonize();

                var removeDialogButton = $("a#button-remove-dialog-" + dialog.id);
                removeDialogButton.data("dialogId", dialog.id);
                removeDialogButton.click(function () {
                    var dialogId = $(this).data("dialogId");

                    Chat.deleteDialog(dialogId, function(){
                        loadDialogs();
                    });
                });

                var isAdmin = false;
                var noAdmin = true;

                if (dialog.adminId) {
                    noAdmin = false;
                }

                if (!noAdmin && (dialog.adminId == sharerId)) {
                    isAdmin = true;
                }

                if (isAdmin) {
                    $("h5#dialog-title-" + dialog.id).editable(function (value) {
                        if (value) {
                            Chat.renameDialog(dialog.id, value, function(){
                                loadDialogs();
                            });
                        }

                        return value;
                    }, {type: 'text', height: 25, width: 270, onblur: 'submit', event: "dblclick", cssclass: 'jeditable'});
                }
            });

            if (callback) callback();
        });
    }

    function getNavTabMarkup(dialog) {
        var model = {
            dialog: dialog
        };

        var $markup = $(Mustache.render(navTabTemplate, model));

        var $tabLink = $markup.find("a[role=tab]");
        $tabLink.data("dialog", dialog);
        $tabLink.on('shown.bs.tab', function () {
            var $this = $(this);
            var dialog = $(this).data("dialog");
            ChatPage.openedDialogId = dialog.id;
            ChatPage.openedDialog = dialog;
            window.location.hash = "#" + dialog.id;
            $this.parents("li").removeClass("unread");
            var $chatMessagesList = $($this.attr("href") + " .chat-messages-list");
            $chatMessagesList.scrollTop(9999);
            markAsRead(dialog);
        });

        $markup.find("span.glyphicon-remove").click(function () {
            $("ul#sidebar-dialog-contacts").html("");

            var $this = $(this);
            $this.tooltip("hide");
            var $li = $this.parents("li");
            var $name = $li.find("a[role=tab]");
            var $content = $($name.attr("href"));
            var $prev = $li.prev();
            var $next = $li.next();
            if ($next.length) {
                $next.find("a").click();
            } else {
                $prev.find("a").click();
            }
            $li.remove();
            $content.remove();
            saveActiveDialogs();
        }).radomTooltip({
            container: "body"
            //delay : { "show": 2000, "hide": 100 }
        });

        return $markup;
    }

    function getMessageMarkup(message) {
        message.sender.avatar = Images.getResizeUrl(message.sender.avatar, "c38");

        message.dialogClosed = false;
        if (ChatPage.openedDialog != null && ChatPage.openedDialog.id == message.dialogId) {
            message.dialogClosed = ChatPage.openedDialog.closed;
        }

        var canDelete = false;

        if (message.fileMessage == null) {
            message.fileMessage = false;
        }
        if (message.fileMessage) {
            canDelete = false;
            message.allowEdit = false;
            message.fileSize = ChatView.humanFileSize(message.fileSize);
            message.uploadInProcess = false;
            message.uploadCancel = false;
            message.uploaded = false;
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
        } else {
            canDelete = message.id != null ? true : false
        }

        if (new String(message.date).indexOf(".") == -1) { // Число
            message.date = dateFormat(new Date(message.date), "dd.mm.yyyy HH:MM:ss");
        }

        if (message.id != null) {
            message.uuid = MESSAGE_ID_TO_UUID[message.id];
        }

        var markup = Mustache.render(messageTemplate, {
            message: message,
            forMe: (message.sender.id != sharerId),
            date: message.date ? message.date.split(" ")[0] : null,
            time: message.date ? message.date.split(" ")[1] : null,
            uuid: message.uuid,
            canDelete: canDelete
        });

        var $markup = $(markup);

        if (!message.deleted) {
            var $text = $markup.find("p.message-text");
            var text = $text.html();
            if (text == null) {
                text = "";
            }
            var words = text.split(new RegExp("[ \n]"));
            var replacements = [];

            $.each(words, function (index, word) {
                word = word.replace("\n", "");
                var matches = word.match(/^(https?:\/\/|ssh:\/\/|ftp:\/\/|file:\/|www\.|(?:mailto:)?[A-Z0-9._%+\-]+@)(.+)$/i);

                if (matches) {
                    if (matches[1] == 'www.') {
                        matches[1] = 'http://www.';
                    } else if (/@$/.test(matches[1]) && !/^mailto:/.test(matches[1])) {
                        matches[1] = 'mailto:' + matches[1];
                    }

                    var href = matches[1] + matches[2];

                    var replacementExists = false;

                    for (var i = 0; i < replacements.length; i++) {
                        if (replacements[i].word === word) {
                            replacementExists = true;
                            break;
                        }
                    }

                    if (!replacementExists) replacements.push({"word": word, "replacement": "<a href='" + href + "'>" + word + "</a>"});
                }
            });

            for (var i = 0; i < replacements.length; i++) {
                text = text.split(replacements[i].word).join(replacements[i].replacement);
            }

            text = text.replace(new RegExp("\n", "g"), "<br/>");
            $text.html(text);

            $text.emoticonize();

            $.each($text.find("a"), function (index, linkNode) {
                var link = $(linkNode);
                var href = link.prop('href');
                var title = link.html();

                var isImage = validateImageUrl(href);

                if (isImage) {
                    link.replaceWith(createImageFromUrlWithFancybox(href));
                } else {
                    var youTubeId = parseYoutubeUrl(href);

                    if (youTubeId) {
                        var youTubePlaceholder = $("<div></div>");
                        link.replaceWith(youTubePlaceholder);

                        youTubePlaceholder.prettyEmbed({
                            videoID: youTubeId,
                            previewSize: 'default',
                            showInfo: false,
                            showControls: true,
                            loop: false,
                            colorScheme: 'dark',
                            showRelated: false,
                            useFitVids: true
                        });
                    }
                }
            });

            $.each($text.find("a:not(.trusted-link)"), function (index, link) {
                var $link = $(link);
                var href = $link.attr("href");
                $link.attr("target", "_blank");
                var trusted = false;

                if ((href.indexOf("http") == -1 && href.indexOf("http") == -1) || href.indexOf(window.location.protocol + "//" + window.location.hostname) == 0) {
                    trusted = true;
                }

                if (!trusted) {
                    $link.click(function () {
                        bootbox.confirm("Данная ссылка ведет за пределы сайта. Переход по ней может быть потенциально опасным и требует подтверждения. Если Вы хотите открыть данную ссылку, нажмите кнопку Подтвердить. Если Вы не хотите открывать данную ссылку - нажмите кнопку Отменить.", function (result) {
                            if (result) {
                                window.open(href, '_blank');
                            }
                        });

                        return false;
                    });
                }
            });
        }

        $markup.find("a.edit-message-link").click(function () {
            var $tab = $markup.parents(".tab-pane");
            $tab.find("input.send-message-button").hide();
            $tab.find("input.apply-edit-message-button").data("message", message).show();
            $tab.find("input.cancel-edit-message-button").show();
            $tab.find("textarea").addClass("edit").val(message.text).focus();
            $markup.addClass("edit");
            return false;
        }).radomTooltip({
            title: "Редактировать сообщение",
            container: "body"
            //delay: { "show": 500, "hide": 100 }
        });

        $markup.find("a.delete-message-link").click(function () {
            var $link = $(this);
            $link.tooltip('destroy');

            $("body div.tooltip").remove();

            var messageId = $link.attr("data-message-id");

            Chat.deleteMessage(messageId, function(message){
                if (message === "deleted") {
                    $("div.chat-message-item[data-message-id=" + messageId + "]").remove();
                    showDialogs();
                } else {
                    if (message.date) {
                        message.date = dateFormat(message.date, "dd.mm.yyyy HH:MM:ss");
                    }

                    var $newMessageMarkup = getMessageMarkup(message);
                    $("div.chat-message-item[data-message-id=" + message.id + "]").replaceWith($newMessageMarkup);
                    checkMessageForNonFirst($newMessageMarkup);
                }
            });

            return false;
        }).radomTooltip({
            title: "Удалить сообщение",
            container: "body"
            //delay: { "show": 500, "hide": 100 }
        });

        $markup.find("i.edit-message-count").radomTooltip({
            title: "Сообщение редактировалось " + message.editCount + " " + getDeclension(message.editCount, "раз", "раза", "раз") + "<br/>" + (message.editCount > 1 ? "Последний: " : "") + message.editDate,
            html: true,
            container: "body"
            //delay: { "show": 500, "hide": 100 }
        });

        $markup.click(function () {
            $(this).toggleClass("selected");
        });

        return $markup;
    }

    function getTabContentMarkup(dialog, sharer) {
        sharer.avatarc77 = Images.getResizeUrl(sharer.avatar, "c77");

        var $markup = $(Mustache.render(tabContentTemplate, {
            dialog: dialog,
            sharer: sharer
        }));

        $markup.data("dialog", dialog);

        if (dialog.countSharers != 2) $markup.find("td#companion-info-container-" + dialog.id).hide();

        $markup.find("span#toggle-smiles-table-" + dialog.id).click(function (event) {
            event.stopPropagation();
            ChatView.showSmilesTable($(this), dialog.id, event, function(smileText){
                var textarea = $("textarea#message-edit-" + dialog.id);
                var position = textarea.getCursorPosition();
                var content = textarea.val();
                var newContent = content.substr(0, position) + " " + smileText + " " + content.substr(position);
                textarea.val(newContent);
            });
        });

        var spanToggleSmileIcon = $markup.find("span#toggle-smiles-icon-" + dialog.id);
        spanToggleSmileIcon.emoticonize();
        spanToggleSmileIcon.parent().tooltip();

        $markup.find("textarea").keydown(function (e) {
            var code = (e.keyCode ? e.keyCode : e.which);
            var ctrlKey = e.ctrlKey;
            var shiftKey = e.shiftKey;
            var $this = $(this);
            if (code == 13) {
                if (ctrlKey || shiftKey) {
                    e.preventDefault();
                    $markup.find("textarea").insertAtCaret("\n");
                } else {
                    e.preventDefault();
                    if (!$this.hasClass("edit")) {
                        $this.parents("form").find("input.send-message-button").click();
                    } else {
                        $this.parents("form").find("input.apply-edit-message-button").click();
                    }
                }
            }
            if (!$this.data("disallow")) {
                $this.data("disallow", true);

                setTimeout(function () {
                    $this.data("disallow", false);
                }, 3000);

                Chat.sendPrintChatMessageToServer(dialog.id);
            }
        });

        $markup.find("div.chat-messages-list").scroll(function () {
            var $this = $(this);
            var dialog = $this.parents("div.tab-pane").data("dialog");

            if ($this.scrollTop() == 0) {
                loadHistory(dialog, true);
            }
        });

        $markup.find("form input.send-message-button").click(function () {
            var $this = $(this);
            var $textarea = $this.parents("form").find("textarea");
            var text = $textarea.val();
            $textarea.val("");

            if (text.replace(new RegExp("\n", "g"), "")) {
                var $tabPane = $this.parents("div.tab-pane");
                var dialogId = $tabPane.data("dialog").id;
                var $chatMessagesList = $tabPane.find("div.chat-messages-list");

                var uuid = Chat.generateUUID();

                var localMessage = {
                    text: text,
                    sender: {
                        id: sharerId,
                        shortName: sharerShortName,
                        avatar: sharerAvatar,
                        link: sharerLink
                    },
                    dialog: {
                        id: dialogId
                    },
                    date: dateFormat("dd.mm.yyyy HH:MM:ss"),
                    uuid: uuid
                };

                var $localMessage = $(getMessageMarkup(localMessage));
                $localMessage.addClass("local");
                $localMessage.addClass("local-message");
                $chatMessagesList.find("p.interlocutor-print").before($localMessage);
                checkMessageForNonFirst($localMessage);
                var height = $chatMessagesList[0].scrollHeight - $chatMessagesList.height();
                $chatMessagesList.scrollTop(height);

                Chat.addMessage(text, dialogId, uuid, function(){
                    $textarea.removeClass("marked-as-error");
                }, function () {
                    $textarea.addClass("marked-as-error");
                    $textarea.val(text);
                    $localMessage.remove();
                });
            }
        });

        $markup.find("form input.apply-edit-message-button").click(function () {
            var $this = $(this);
            var $textarea = $this.parents("form").find("textarea");
            var text = $textarea.val();
            if (text.replace(new RegExp("\n", "g"), "")) {
                Chat.editMessage($this.data("message").id, text, function (message) {
                    $markup.find("form input.send-message-button").show();
                    $markup.find("form input.apply-edit-message-button").data("message", null).hide();
                    $markup.find("form input.cancel-edit-message-button").hide();
                    $markup.find("form textarea").removeClass("edit").val("");
                    $markup.find("div.chat-message-item.edit").removeClass("edit");
                });
            }
            return false;
        });

        $markup.find("form input.cancel-edit-message-button").click(function () {
            $markup.find("form input.send-message-button").show();
            $markup.find("form input.apply-edit-message-button").data("message", null).hide();
            $markup.find("form input.cancel-edit-message-button").hide();
            $markup.find("form textarea").removeClass("edit").val("");
            $markup.find("div.chat-message-item.edit").removeClass("edit");
            return false;
        });

        $markup.radomDropUpload(
                "/dialogfiles/upload.json?dialogId=" + dialog.id,
                function() { // dragOverCallBack - обрабочик наведения зажатого файла над областью
                    $markup.css("border-color", "red");
                },
                function() { // dragLeaveCallBack - обрабочик уведения зажатого файла из области
                    $markup.css("border-color", "transparent");
                },
                function(file) { // dropCallBack - обрабочик отпускания файла над областью
                    $markup.css("border-color", "transparent");

                    var dialogId = dialog.id;
                    var $chatMessagesList = $markup.find("div.chat-messages-list");

                    var uuid = Chat.generateUUID();

                    var localMessage = {
                        text : file.name,
                        fileSize: file.size,
                        fileLoadedPercent: 0,
                        fileMessage: true,
                        fileChatMessageState: "UPLOAD_IN_PROCESS",
                        sender: {
                            id: sharerId,
                            shortName: sharerShortName,
                            avatar: sharerAvatar,
                            link: sharerLink
                        },
                        dialog: {
                            id: dialogId
                        },
                        date: dateFormat("dd.mm.yyyy HH:MM:ss"),
                        uuid: uuid
                    };

                    var $localMessage = $(getMessageMarkup(localMessage));
                    $localMessage.addClass("local");
                    $localMessage.addClass("local-message");
                    $chatMessagesList.find("p.interlocutor-print").before($localMessage);
                    checkMessageForNonFirst($localMessage);
                    var height = $chatMessagesList[0].scrollHeight - $chatMessagesList.height();
                    $chatMessagesList.scrollTop(height);

                    Chat.addFileMessage(file.name, dialogId, uuid, file.size, function (response) {
                        //
                    }, function (response) {
                        $localMessage.find(".deleted-text").remove();
                        $localMessage.find(".fa-spinner").remove();
                        $localMessage.find(".message-text").css("color", "red");
                        $localMessage.find(".message-text").text(response.message);
                    });

                    return uuid;
                },
                function(uuid, percent) { // statusCallBack - обновление процента загрузки файла
                    // Если сообщение уже добавлено, то отправляем событие изменения процента загрузки
                    var messageBlock = $(".chat-message-item[data-uuid=" + uuid + "]");
                    if (messageBlock.length > 0 && messageBlock.attr("data-message-id") != null && messageBlock.attr("data-message-id") != "") {
                        var messageId = messageBlock.attr("data-message-id");
                        // Обновляем процент загрузки файла
                        Chat.updateUploadPercent(messageId, percent, function(){
                            //
                        });
                    }
                },
                function(uuid, response) { // uploadSuccessCallBack - файл загружен
                    var functionFinish = function(uuid, response) {
                        var messageBlock = $(".chat-message-item[data-uuid=" + uuid + "]");
                        if (messageBlock.length > 0 && messageBlock.attr("data-message-id") != null && messageBlock.attr("data-message-id") != "") {
                            var messageId = messageBlock.attr("data-message-id");
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

        return $markup;
    }

    function markAsRead(dialog) {
        var $chatMessagesList = $("#chat-content-" + dialog.id);
        var $forMeUnreadMessages = $chatMessagesList.find(".chat-message-item.unread.for-me:not(.processing)");
        $forMeUnreadMessages.addClass("processing");

        if ($forMeUnreadMessages.length) {
            Chat.markDialogAsRead(dialog.id, function () {
                ChatView.showDialogsInHeader();
                $forMeUnreadMessages.animate({
                    backgroundColor: "#fff"
                }, 2000, function () {
                    $forMeUnreadMessages.removeClass("unread").removeClass("processing").removeAttr("style");
                });
            });
        }
    }

    function getDateSeparator(date) {
        return "<div class='news-date-separator'><span>" + date + "</span><hr/></div>";
    }

    function checkMessageForNonFirst($message) {
        var forId = $message.attr("data-for-id");
        var currentDate = $message.attr("data-date");

        var $next = $message.next();
        if ($next.length) {
            if ($next.hasClass("chat-message-item")) {
                var nextDate = $next.attr("data-date");

                if (currentDate != nextDate) {
                    $message.after(getDateSeparator(nextDate));
                } else {
                    if ($next.attr("data-for-id") == forId) {
                        $next.addClass("non-first");
                    } else {
                        $next.removeClass("non-first");
                    }
                }
            }
        }

        var $previous = $message.prev();
        if ($previous.hasClass("local-message")) {
            $previous = $previous.prev();
        }

        if ($previous.length) {
            if ($previous.hasClass("chat-message-item")) {
                var previousDate = $previous.attr("data-date");
                if (currentDate != previousDate) {
                    $message.before(getDateSeparator(currentDate));
                } else {
                    if ($previous.attr("data-for-id") == forId) {
                        $message.addClass("non-first");
                    } else {
                        $message.removeClass("non-first");
                    }
                }
            } else {
                $message.removeClass("non-first");
            }
        } else {
            $message.before(getDateSeparator(currentDate));
        }
    }

    function loadHistory(dialog, show) {
        var $tabPane = $("div.tab-pane#chat-content-" + dialog.id);
        var $chatMessagesList = $tabPane.find("div.chat-messages-list");
        var lastLoadChatMessage = $tabPane.data("lastLoadChatMessage");
        var lastLoadChatMessageId = lastLoadChatMessage ? lastLoadChatMessage.id : -1;

        Chat.loadHistory(dialog.id, lastLoadChatMessageId, function(response){
            $tabPane.data("lastLoadChatMessage", response[response.length - 1]);
            var scrollToBottom = $chatMessagesList.find("div.chat-message-item").length == 0;
            var oldScrollHeight = $chatMessagesList[0].scrollHeight;

            var $messages = [];

            $.each(response, function (index, message) {
                if (message.date) {
                    message.date = dateFormat(message.date, "dd.mm.yyyy HH:MM:ss");
                }

                var $message = $(getMessageMarkup(message));

                var existingMessage = $("div[data-message-id='" + message.id + "']");
                if (existingMessage.length) {
                    existingMessage.replaceWith($message);
                } else {
                    var row = $chatMessagesList.find("div.row:first");

                    if (row.length) {
                        row.before($message);
                    } else {
                        $chatMessagesList.find("p.interlocutor-print").before($message);
                    }
                }

                $messages.push($message);
            });

            $.each($messages, function (index, $message) {
                checkMessageForNonFirst($message);
            });

            var newScrollHeight = $chatMessagesList[0].scrollHeight;

            if (scrollToBottom) {
                var height = $chatMessagesList[0].scrollHeight - $chatMessagesList.height();
                $chatMessagesList.scrollTop(height);
            } else {
                $chatMessagesList.scrollTop(newScrollHeight - oldScrollHeight);
            }
            $("#historyDataLoaderAnimation-" + dialog.id).hide();
            if (show) markAsRead(dialog);
        });
    }

    function saveActiveDialogs() {
        var activeChats = [];

        $.each($("ul#dialogs-tabs a[role=tab]"), function (index, a) {
            var $a = $(a);
            var dialog = $a.data("dialog");

            if (dialog) {
                activeChats.push(dialog.id);
            }
        });

        radomLocalStorage.setItem("activeChats", activeChats.join(","));
    }

    function addDialog(dialogId, show) {
        window.dialogs = window.dialogs == null ? {} : window.dialogs;
        var dialog = window.dialogs[dialogId];
        if (dialog == null) {
            Chat.loadDialog(dialogId, function (response) {
                window.dialogs[dialogId] = response;
                showDialog(response, show);
            });
        } else {
            showDialog(dialog, show);
        }
    }

    function showDialog(dialog, show) {
        if (!dialog) return;
        var dialogId = dialog.id;

        if (dialog.users.length == 2) {
            if (dialog.users[0].id != sharerId) {
                dialog.companion = dialog.users[0];
            } else {
                dialog.companion = dialog.users[1];
            }

            dialog.name = dialog.companion.mediumName;
        } else if (dialog.lastMessage) {
            dialog.companion = dialog.lastMessage.sender;
        } else if (dialog.users.length) {
            dialog.companion = dialog.users[0];

            for (var i = 0; i < dialog.users.length; i++) {
                if (dialog.users[i].id != sharerId) {
                    dialog.companion = dialog.users[i];
                    break;
                }
            }
        } else {
            dialog.companion = {"link": sharerLink, "avatar": sharerAvatar};
        }

        dialog.companion.avatar84 = Images.getResizeUrl(dialog.companion.avatar, "c84");
        dialog.companion.avatar77 = Images.getResizeUrl(dialog.companion.avatar, "c77");

        if (!dialog.name) {
            dialog.name = "Без названия";
        }

        if (dialog.name.length > chatNameMaxLength) {
            dialog.name = dialog.name.substr(0, chatNameMaxLength) + "...";
        }


        var isAdmin = false;
        var noAdmin = true;

        if (dialog.adminId) {
            noAdmin = false;
        }

        if (!noAdmin && (dialog.adminId == sharerId)) {
            isAdmin = true;
        }

        var dialogButton = $("a[href=#chat-content-" + dialogId + "]");

        if (dialogButton.length == 0) {
            $("ul#dialogs-tabs").append(getNavTabMarkup(dialog));
            $("div#dialogs-contents").append(getTabContentMarkup(dialog, userData));

            dialogButton = $("a[href=#chat-content-" + dialogId + "]");

            dialogButton.dblclick(function (e) {
                e.stopPropagation();
            });
        } else {
            $("span#chat-content-title-" + dialog.id).html(dialog.name);
        }

        if (show) {
            // Загружаем чат, когда перешли в него в первый раз
            if (show && jQuery.data(dialogButton.get(0), "loaded") == null) {
                loadHistory(dialog, show);
                jQuery.data(dialogButton.get(0), "loaded", "true");
            }

            sortSharers(dialog.users);

            if (dialog.users.length != 2) {
                $("td#companion-info-container-" + dialog.id).hide();
                $("td#companion-message-container-" + dialog.id).attr("style", "padding-right: 15px");
            } else {
                $("a#companion-info-link-" + dialog.id).attr("href", dialog.companion.link);
                $("img#companion-info-avatar-" + dialog.id).attr("src", dialog.companion.avatar77);
                $("td#companion-message-container-" + dialog.id).attr("style", "padding-right: 0px");
                $("td#companion-info-container-" + dialog.id).show();
            }

            var panel = $("div#dialog-contacts-block");
            panel.show();
            dialogButton.click();

            var dialogContacts = $("ul#sidebar-dialog-contacts");
            dialogContacts.html("");

            for (var i = 0; i < dialog.users.length; i++) {
                var sharer = dialog.users[i];
                // Размер иконки 30х30
                sharer.avatar = Images.getResizeUrl(sharer.avatar, "c30");
                var model = {
                    contact: sharer
                };

                var $item = $(Mustache.render(dialogContactTemplate, model));
                $item.data("contact", sharer);

                dialogContacts.append($item);

                var contactItemRemoveButton = $("span#sidebar-dialog-contacts-item-remove-" + sharer.id);

                contactItemRemoveButton.data("dialogId", dialog.id);
                contactItemRemoveButton.data("sharerId", sharer.id);

                if (isAdmin || (sharer.id == sharerId)) {
                    contactItemRemoveButton.show();
                    contactItemRemoveButton.click(function () {
                        var dialogId = $(this).data("dialogId");
                        var sharerId = $(this).data("sharerId");

                        Chat.deleteSharerFromDialog(dialogId, sharerId, function(){
                            loadDialogs();
                        });
                    });
                } else {
                    contactItemRemoveButton.hide();
                }
            }

            var textarea = $("textarea#message-edit-" + dialog.id);
            var chatParticipantsCount = $("li.sidebar-new-contacts-item").length;

            if (chatParticipantsCount) {
                $("span#sidebar-dialog-contacts-count").html('Участники чата (' + chatParticipantsCount + ')');
                textarea.prop('readonly', false);
                textarea.prop('placeholder', '');
                panel.addClass('panel-info').removeClass('panel-warning');

                if (noAdmin || isAdmin) {
                    $("div#dialog-contacts-container").show();
                } else {
                    $("div#dialog-contacts-container").hide();
                }
            } else {
                $("span#sidebar-dialog-contacts-count").html('Чат не активен');
                textarea.prop('readonly', true);
                textarea.prop('placeholder', 'Чат не активен');
                panel.removeClass('panel-info').addClass('panel-warning');
                $("div#dialog-contacts-container").hide();
            }

            if (noAdmin || isAdmin) {
                var dialogContactsSelector = $("select#new-dialog-contacts-select");
                dialogContactsSelector.html("");

                for (var i = 0; i < userContacts.length; i++) {
                    var userContact = userContacts[i];
                    var allow = true;

                    for (var j = 0; j < dialog.users.length; j++) {
                        var sharer = dialog.users[j];

                        if (sharer.id == userContact.id) {
                            allow = false;
                            break;
                        }
                    }

                    if (!allow) continue;

                    var option = $('<option>' + userContact.name + '</option>');
                    option.data("avatar", Images.getResizeUrl(userContact.avatar, "c30"));
                    option.data("name", userContact.name);
                    option.data("sharerId", userContact.id);
                    option.data("dialogId", dialog.id);
                    dialogContactsSelector.append(option);
                }
            }

            setTimeout(function () {
                markAsRead(dialog);
            }, 500);

            if (isAdmin) {
                $("span#chat-content-title-" + dialog.id).editable(function (value) {
                    if (value) {
                        Chat.renameDialog(dialog.id, value, function(){
                            loadDialogs();
                        });
                    }

                    return value;
                }, {type: 'text', height: 25, width: 270, event: 'dblclick', onblur: 'submit', cssclass: 'jeditable'});
            }
        }

        saveActiveDialogs();
    }

    var selectNewContact = function (sharerId, dialogId) {
        Chat.addSharerToDialog(dialogId, sharerId, function () {
            loadDialogs();
        });
    };

    var clearContacts = function () {
        $("ul#sidebar-dialog-contacts").html("");
        $("select#new-dialog-contacts-select").html("");
        $("div#dialog-contacts-block").hide();
    };

    var isDialogsLoaded = false;

    // Загрузить открытые даилоги и показать активный диалог
    var loadDialogs = function () {
        $(window).hashchange(function () {
            var dialogId = window.location.hash.substr(1);
            if (dialogId) {
                addDialog(dialogId, true);
            } else {
                if (!isDialogsLoaded) {
                    $("#dialogsDataLoaderAnimation").show();
                    showDialogs(function () {
                        isDialogsLoaded = true;
                        $("#dialogsDataLoaderAnimation").hide();
                    });
                }
                clearContacts();
            }
        });

        var activeChats = radomLocalStorage.getItem("activeChats");

        if (activeChats != null) {
            $.each(activeChats.split(","), function (index, dialogId) {
                if (dialogId) addDialog(dialogId, false);
            });
        }

        $(window).hashchange();
    };

    $(document).ready(function () {
        $(eventManager).bind("inited", function(event, user) {
            userData = user;
            sharerId = userData.id;
            sharerShortName = userData.shortName;
            sharerAvatar = userData.avatar;
            sharerLink = userData.link;

            setInterval(function () {
                var $dots = $("p.interlocutor-print .dots");
                var $pencil = $("p.interlocutor-print .glyphicon-pencil");
                var html = $dots.html();
                if (html == ".") {
                    $dots.html("..");
                    $pencil.css("padding-left", "5px");
                } else if (html == "..") {
                    $dots.html("...");
                    $pencil.css("padding-left", "10px");
                } else {
                    $dots.html(".");
                    $pencil.css("padding-left", "0");
                }
            }, 300);

            loadDialogs();

            Chat.subscribeToNewMessage(function (message) {
                MESSAGE_ID_TO_UUID[message.id] = message.uuid;
                var dialogId = message.dialog;
                var dialog = window.dialogs[dialogId];
                showDialogs();
                var chatTab = $("a[href=#chat-content-" + dialogId + "]");
                chatTab.parents("li:not(.active)").addClass("unread");
                var tabPane = $("div.tab-pane#chat-content-" + dialogId);
                var chatMessagesList = tabPane.find("div.chat-messages-list");
                var messageDiv = false;
                var shouldMarkAsRead = message.sender.id != sharerId;

                if (message.uuid) {
                    messageDiv = $("div[data-uuid='" + message.uuid + "']");
                    messageDiv.attr("data-message-id", message.id);
                    messageDiv.find("a.delete-message-link").attr("data-message-id", message.id);
                } else {
                    messageDiv = $("div[data-message-id='" + message.id + "']");
                }

                if (chatMessagesList.length) {
                    if (messageDiv.length) {
                        messageDiv.removeClass("local").removeClass("local-message");
                    } else {
                        messageDiv = $(getMessageMarkup(message));
                        chatMessagesList.find("p.interlocutor-print").before(messageDiv);
                    }

                    if (messageDiv) checkMessageForNonFirst(messageDiv);

                    var height = chatMessagesList[0].scrollHeight - chatMessagesList.height();
                    chatMessagesList.scrollTop(height);
                }

                if (chatTab.parents("li.active").length) {
                    if (shouldMarkAsRead) {
                        setTimeout(function () {
                            markAsRead(dialog);
                        }, 200);
                    }
                }
            });

            // Загрузка файла завершена
            Chat.subscribeToFinishUploadFile(function(message){
                var $markup = $(getMessageMarkup(message));
                $(".chat-message-item[data-message-id=" + message.id + "]").replaceWith($markup);
            });

            // Обновлен процент загрузки файла
            Chat.subscribeToUpdateFilePercent(function(message){
                // Здесь делаем простую замену
                $(".chat-message-item[data-message-id=" + message.id + "]").find(".loadedPercent").text(message.fileLoadedPercent);
            });

            // Отмена загрузки файла
            Chat.subscribeToCancelUploadFile(function(message){
                var $markup = $(getMessageMarkup(message));
                $(".chat-message-item[data-message-id=" + message.id + "]").replaceWith($markup);
            });

            Chat.subscribeToMarkAsReadDialog(function (dialogId) {
                var $forInterlocutorUnreadMessages = $("div#chat-content-" + dialogId + " .chat-message-item.unread.for-interlocutor:not(.processing)");
                $forInterlocutorUnreadMessages.addClass("processing");
                $forInterlocutorUnreadMessages.animate({
                    backgroundColor: "#fff"
                }, 2000, function () {
                    $(this).removeClass("unread").removeClass("processing").removeAttr("style");
                });
            });

            Chat.subscribeToPrintChatMessage(function (data) {
                var $p = $("p#interlocutor-print-" + data.dialogId);
                var content = $("span#interlocutor-print-" + data.dialogId + "-content");
                content.html(data.senderName + " набирает сообщение");
                var timeout = $p.data("timeout");

                if (timeout) {
                    clearTimeout(timeout);
                }

                timeout = setTimeout(function () {
                    $p.css("color", "#fff");
                }, 3000);

                $p.css("color", "#777");
            });

            Chat.subscribeToChatStateChanged(function () {
                loadDialogs();
            });

            $(radomEventsManager).bind("contact.online", function (event, data) {
                $("div#online-label-" + data.id).html("В сети");
            });

            $(radomEventsManager).bind("contact.offline", function (event, data) {
                $("div#online-label-" + data.id).html("Не в сети");
            });

            // Сообщение отредактировано
            Chat.subscribeToEditMessage(function(message) {
                var $newMessageMarkup = getMessageMarkup(message);
                $newMessageMarkup.addClass("non-first");
                $("div.chat-message-item[data-message-id=" + message.id + "]").replaceWith($newMessageMarkup);
                checkMessageForNonFirst($newMessageMarkup);
            });
            // Сообщение удалено
            Chat.subscribeToDeleteMessage(function(message){
                var $newMessageMarkup = getMessageMarkup(message);
                $newMessageMarkup.addClass("non-first");
                $("div.chat-message-item[data-message-id=" + message.id + "]").replaceWith($newMessageMarkup);
                checkMessageForNonFirst($newMessageMarkup);
            });

            $("select#new-dialog-contacts-select").radomCombobox({
                inputPlaceholder: "Добавить участника",
                buttonTooltip: "Все контакты",
                clearOnSelect: true,
                itemRenderFunction: function (ul, item) {
                    var avatar = $(item.option).data("avatar");
                    var name = $(item.option).data("name");

                    var li = $('<li></li>');
                    li.attr("data-value", name);

                    var img = $('<img class="img-thumbnail" src="' + Images.getResizeUrl(avatar, "c30") + '" alt="' + name + '"></img>');
                    var span = $('<span>' + name + '</span>');
                    li.append(img);
                    li.append(span);

                    return li.appendTo(ul);
                },
                onSelect: function (event, ui) {
                    event.preventDefault();
                    event.stopPropagation();

                    var dialogId = $(ui.item.option).data("dialogId");
                    var sharerId = $(ui.item.option).data("sharerId");

                    selectNewContact(sharerId, dialogId);
                }
            });
        });
    });
</script>

<ul class="nav nav-tabs" role="tablist" id="dialogs-tabs">
    <li class="active"><a href="#chats-list" role="tab" data-toggle="tab" onclick="ChatPage.openedDialogId = -1; window.location.hash='';">Список диалогов</a></li>
</ul>

<div class="tab-content" id="dialogs-contents">
    <div class="tab-pane active" id="chats-list"></div>
</div>
<div class="row list-loader-animation" id="dialogsDataLoaderAnimation"></div>