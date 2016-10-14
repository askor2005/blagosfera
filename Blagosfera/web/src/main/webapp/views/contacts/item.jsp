<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<script id="contacts-list-item-template" type="x-tmpl-mustache">
<div class="row sharer-item" data-sharer-id="{{contact.other.id}}">
	<div class="col-xs-2">
		<div class="row">
			<div class="col-xs-12">
				<a class="sharer-item-avatar-link" href="{{contact.other.link}}">
					<img src="{{resizedAvatar}}" class="img-thumbnail" style="height:84px;width:84px;{{#contact.other.verified}}background-color : rgb(51, 142, 207);{{/contact.other.verified}}" />
					<img src="{{onlineIconSrc}}" class="sharer-item-online-icon" />
				</a>
				<span class="{{onlineTextClass}}">{{onlineText}}</span>
			</div>
		</div>
	</div>
	<div class="col-xs-10">
		<h3 style="margin-top : 0;">
			<a href="{{contact.other.link}}">{{contact.other.fullName}}</a>
			<security:authorize access="hasRole('ROLE_BLAGOSFERA_SHARERS_DELETER')">
				<a href="#" class="glyphicon glyphicon-remove pull-right delete-sharer" data-title="Удалить профиль участника" style="text-decoration : none !important; position : relative; top : 1px;"></a>
			</security:authorize>
			<a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{contact.other.id}}')" class="fa fa-comments-o pull-right go-to-chat" data-title="Написать сообщение" style="text-decoration : none !important;"></a>
			<a href="#" class="fa fa-money pull-right do-accounts-move" onclick="return false" data-title="Перевести средства" style="text-decoration : none !important;"></a>
			<security:authorize access="hasRole('ROLE_SUPERADMIN')">
				<a href="/admin/roles/{{contact.other.ikp}}" class="fa fa-th-list pull-right edit-roles" data-title="Управление правами" style="text-decoration : none !important; position : relative; top : 1px; margin-left: 5px;"></a>
			</security:authorize>
		</h3>
		<p class="text-muted">Дата регистрации {{contact.other.registeredAtFormatted}}</p>
		<security:authorize access="hasRole('ROLE_SUPERADMIN')">
		<p class="text-muted last-login-wrapper"><span class="last-login-message">Был в сети</span> <span>{{contact.other.lastLoginFormatted}}</span></p>
		</security:authorize>
		<p class="text-muted in-list" style="margin-bottom : 15px;">
			{{{inListText}}}
			{{#contactGroups}}
				<span class="group-color-example group-color-example-{{color}}" style="font-size : 0.8em; font-weight : bold;">{{name}}</span>
			{{/contactGroups}}
		</p>
		<p class="text-muted in-list" style="margin-bottom : 15px;">
		    {{#contact.actualCountry}}
			{{contact.actualCountry}}{{#contact.actualCity}}, {{contact.actualCity}}{{/contact.actualCity}}
			{{/contact.actualCountry}}
		</p>
		<div class="row">
			{{#showAddDropdown}}
				<div class="col-xs-6">
					<div class="dropdown add-group-button">
	  					<a class="btn btn-primary btn-xs btn-block" id="add-label-{{contact.other.id}}" role="button" data-toggle="dropdown" data-target="#" href="#">
    						{{addContactGroupDefaultText}} &nbsp; <span class="caret"></span>
  						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="add-label-{{contact.other.id}}">
						    {{^contactGroups}}
							<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-id="0">Список по умолчанию</a></li>
							 {{/contactGroups}}
							{{#groups}}
								<li role="presentation"><a role="menuitem" href="#" class="{{^userHas}}add-group-select-link{{/userHas}}{{#userHas}}delete-group-select-link{{/userHas}}" data-group-id="{{id}}">{{name}}&nbsp;{{#userHas}}<i class="fa fa-check "></i> {{/userHas}}</a></li>
							{{/groups}}
						</ul>
					</div>
					<a class="btn btn-xs btn-block btn-link edit-contacts-groups-link" style="margin-top : 3px; opacity : 0; text-align : right;" href="/contacts/lists"> <span class="glyphicon glyphicon-pencil"></span> Управление списками</a>
				</div>
			{{/showAddDropdown}}
			{{#showAddButton}}
				<div class="col-xs-6">
					<a class="btn btn-primary btn-xs btn-block add-group-select-link add-group-button" role="button" href="#">{{addContactGroupDefaultText}}</a>
					<a class="btn btn-xs btn-block edit-contacts-groups-link" style="margin-top : 3px; opacity : 0; text-align : right;" href="/contacts/lists"> <span class="glyphicon glyphicon-pencil"></span> Управление списками</a>
				</div>
			{{/showAddButton}}
			{{#deleteContactText}}
				<div class="col-xs-6"><a href="#" class="btn btn-default btn-xs btn-block delete-contact-link">{{deleteContactText}} <span class="glyphicon glyphicon-remove"></span></a></div>
			{{/deleteContactText}}
		</div>
	</div>
</div>
</script>

<script id="user-template" type="x-tmpl-mustache">
<div class="row sharer-item" data-sharer-id="{{sharer.id}}">
	<div class="col-xs-2">
		<div class="row">
			<div class="col-xs-12">
				<a class="sharer-item-avatar-link" href="{{sharer.link}}">
					<img src="{{resizedAvatar}}" class="img-thumbnail" style="height:84px;width:84px;{{#sharer.verified}}background-color : rgb(51, 142, 207);{{/sharer.verified}}" />
					<img src="{{onlineIconSrc}}" class="sharer-item-online-icon" />
				</a>
				<span class="{{onlineTextClass}}">{{onlineText}}</span>
			</div>
		</div>
	</div>
	<div class="col-xs-10">
		<h3 style="margin-top : 0;">
			<a href="{{sharer.link}}">{{sharer.fullName}}</a>
			<security:authorize access="hasRole('ROLE_BLAGOSFERA_SHARERS_DELETER')">
    <a href="#" class="glyphicon glyphicon-remove pull-right delete-sharer" data-title="Удалить профиль участника" style="text-decoration : none !important; position : relative; top : 1px;"></a>
</security:authorize>
			<a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{sharer.id}}');" class="fa fa-comments-o pull-right go-to-chat" data-title="Написать сообщение" style="text-decoration : none !important;"></a>
			<a href="#" class="fa fa-money pull-right do-accounts-move" onclick="return false" data-title="Перевести средства" style="text-decoration : none !important;"></a>
			<security:authorize access="hasRole('ROLE_SUPERADMIN')">
    <a href="/admin/roles/{{sharer.ikp}}" class="fa fa-th-list pull-right edit-roles" data-title="Управление правами" style="text-decoration : none !important; position : relative; top : 1px; margin-left: 5px;"></a>
</security:authorize>
		</h3>
		<p class="text-muted">Дата регистрации {{sharer.registeredAt}}</p>
		<security:authorize access="hasRole('ROLE_SUPERADMIN')">
    <p class="text-muted last-login-wrapper"><span class="last-login-message">Был в сети</span> <span>{{sharer.lastLogin}}</span></p>
</security:authorize>
		<p class="text-muted in-list" style="margin-bottom : 15px;">
			{{{inListText}}}
			{{#contactGroups}}
				<span class="group-color-example group-color-example-{{color}}" style="font-size : 0.8em; font-weight : bold;">{{name}}</span>
			{{/contactGroups}}
		</p>
		<p class="text-muted in-list" style="margin-bottom : 15px;">
		    {{#sharer.actualCountry}}
			{{sharer.actualCountry}}{{#sharer.actualCity}}, {{sharer.actualCity}}{{/sharer.actualCity}}
			{{/sharer.actualCountry}}
		</p>
		<div class="row">
			{{#showAddDropdown}}
				<div class="col-xs-6">
					<div class="dropdown add-group-button">
	  					<a class="btn btn-primary btn-xs btn-block" id="add-label-{{sharer.id}}" role="button" data-toggle="dropdown" data-target="#" href="#">
    						{{addContactGroupDefaultText}} &nbsp; <span class="caret"></span>
  						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="add-label-{{sharer.id}}">
						  {{^contactGroups}}
							<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-id="0">Список по умолчанию</a></li>
							{{/contactGroups}}
							{{#groups}}
								<li role="presentation"><a role="menuitem" href="#" class="{{^userHas}}add-group-select-link{{/userHas}}{{#userHas}}delete-group-select-link{{/userHas}}" data-group-id="{{id}}">{{name}}&nbsp;{{#userHas}}<i class="fa fa-check "></i> {{/userHas}}</a></li>
							{{/groups}}
						</ul>
					</div>
					<a class="btn btn-xs btn-block btn-link edit-contacts-groups-link" style="margin-top : 3px; opacity : 0; text-align : right;" href="/contacts/lists"> <span class="glyphicon glyphicon-pencil"></span> Управление списками</a>
				</div>
			{{/showAddDropdown}}
			{{#showAddButton}}
				<div class="col-xs-6">
					<a class="btn btn-primary btn-xs btn-block add-group-select-link add-group-button" role="button" href="#">{{addContactGroupDefaultText}}</a>
					<a class="btn btn-xs btn-block edit-contacts-groups-link" style="margin-top : 3px; opacity : 0; text-align : right;" href="/contacts/lists"> <span class="glyphicon glyphicon-pencil"></span> Управление списками</a>
				</div>
			{{/showAddButton}}
			{{#deleteContactText}}
				<div class="col-xs-6"><a href="#" class="btn btn-default btn-xs btn-block delete-contact-link">{{deleteContactText}} <span class="glyphicon glyphicon-remove"></span></a></div>
			{{/deleteContactText}}
		</div>
	</div>
</div>
</script>

<script type="text/javascript">
	
var $searchInput = null;
	
function initSearchInput($input, callback) {
    $searchInput = $input;
    $input.keyup(function () {

        var timeout = $input.data("timeout");
        if (timeout) {
            clearTimeout(timeout);
        }
        timeout = setTimeout(function () {
            var newValue = $input.val();
            var oldValue = $input.data("old-value");
            if (newValue != oldValue) {
                if ((newValue.length >= 2) || (newValue.length == 0)) {
                    $input.data("old-value", newValue);
                    callback(newValue);
                }
            }
        }, 300);
        $input.data("timeout", timeout);
    });
}	
	
var contactsListItemTemplate = $('#contacts-list-item-template').html();
var userTemplate = $('#user-template').html();
Mustache.parse(contactsListItemTemplate);
Mustache.parse(userTemplate);
	
var groups = null;

function getGroups() {
    if (!groups) {
        $.ajax({
            async: false,
            type: "get",
            dataType: "json",
            url: "/contacts/contacts/lists.json",
            success: function (response) {
                groups = response;
            },
            error: function () {
                console.log("ajax error");
            }
        });
    }
    return groups;
}
	
function getContactMarkup(contact,$oldRow) {
	var currentSharerId = "${sharer.id}";
	var model = {};
	model.contact = contact;
	model.onlineIconSrc = contact.other.online ? "/i/icon-online.png" : "/i/icon-offline.png";
	model.onlineText = contact.other.online ? "В сети" : "Не в сети";
	model.onlineTextClass = contact.other.online ? "sharer-item-online-status" : "sharer-item-online-status text-muted";
	model.contact.requestDateFormatted= (model.contact.requestDate != null) ? dateFormat(model.contact.requestDate,'dd.mm.yyyy HH:mm:ss') : "";
	model.contact.other.lastLoginFormatted= (model.contact.other.lastLogin != null) ? dateFormat(model.contact.other.lastLogin,'dd.mm.yyyy HH:mm') : "";
	model.contact.other.registeredAtFormatted= (model.contact.other.registeredAt != null) ? dateFormat(model.contact.other.registeredAt,'dd.mm.yyyy') : "";
	if (contact.other.id == currentSharerId) {
		model.inListText = "Это Вы";
    	model.deleteContactText = false;
    	model.showAddDropdown = false;
    	model.showAddButton = false;
	} else {
	    if (((contact.sharerStatus == "NEW") && (contact.otherStatus == "NEW"))) {
	    	model.inListText = "Не в списке Ваших контактов";
	    	model.deleteContactText = false;
	    } else if ((contact.sharerStatus == "ACCEPTED") && (contact.otherStatus == "ACCEPTED")) {
	    	model.inListText = "В списке Ваших контактов: ";
	    	model.deleteContactText = "Удалить из контактов";
	    } else if ((contact.sharerStatus == "ACCEPTED") && (contact.otherStatus == "NEW")) {
	    	model.inListText = "Вы отправили заявку на добавление <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + contact.requestDateFormatted + "'>" + (contact.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(contact.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
	    	model.deleteContactText = "Отменить заявку";
	    } else if ((contact.sharerStatus == "NEW") && (contact.otherStatus == "ACCEPTED")) {
	    	model.inListText = "Вы получили заявку на добавление <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + contact.requestDateFormatted + "'>" + (contact.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(contact.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
	    	model.deleteContactText = "Отклонить заявку";
	    }
	    if (((contact.sharerStatus == "NEW") && (contact.otherStatus == "NEW")) || ((contact.sharerStatus == "NEW") && (contact.otherStatus == "ACCEPTED"))) {
	    	model.addContactGroupDefaultId = 0;
	    	model.addContactGroupDefaultText = "Добавить в список контактов";

			model.contactGroups = null;
	    } else {
	    	model.addContactGroupDefaultId = -1;
	    	model.addContactGroupDefaultText = "Перенести в списки контактов";

			model.contactGroups = contact.contactGroups;
			if (model.contactGroups.length == 0) {
				model.contactGroups.push({color : 0,name:"Список по умолчанию"});
			}
	    } 
	    /*if ((contact.sharerStatus != "ACCEPTED") || (contact.otherStatus != "ACCEPTED")) {
			 model.contactGroups = null;
	    	//model.groupColor = null;
	    	//model.groupName = null;
	    } else {
			model.contactGroups = contact.contactGroups;
			if (model.contactGroups.length == 0) {
				model.contactGroups.push({color : 0,name:"Список по умолчанию"});
			}
			//model.groupColor = (contact.contactsGroup) ? contact.contactsGroup.color : 0;
			//model.groupName = (contact.contactsGroup) ? contact.contactsGroup.name : "Список по умолчанию";
	    }*/
		model.groups = getGroups();
		var groupsIds = {};
		if (model.contactGroups) {
			for (var i in model.contactGroups) {
				groupsIds[model.contactGroups[i].id] = model.contactGroups[i];
			}
		}
		for (var i in model.groups) {
				if (groupsIds[model.groups[i].id]){
					model.groups[i].userHas = true;
				}
				else {
					model.groups[i].userHas = false;
				}
		}
		model.showAddDropdown = getGroups().length > 0;
		model.showAddButton = (getGroups().length == 0) && ((!contact) || (contact.sharerStatus == "NEW"));
	}
	model.resizedAvatar = Images.getResizeUrl(contact.other.avatar, "c84");
	
	var rendered = Mustache.render(contactsListItemTemplate, model);
    var $row = null;
	if ($oldRow) {
		$row = $oldRow;
		$row.html($(rendered).html());
	}
	else {
		$row = $(rendered);
	}


	if(!contact.other.online) {
		$row.find("p.last-login-wrapper").show();
		if (contact.other.sex) {
			$row.find("span.last-login-message").html("Был в сети");
		} else {
			$row.find("span.last-login-message").html("Была в сети");
		}
	} else {
		$row.find("p.last-login-wrapper").hide();
	}


	$row.data("contact", contact);
	
	$row.find("span.request-distance").radomTooltip({
		placement : "top",
		container : "body"
	});
	
	$row.find("a.go-to-chat, a.edit-roles, a.do-accounts-move, a.delete-account").radomTooltip({
		placement : "top",
		container : "body"
	});
	
	$row.find("a.do-accounts-move").accountsMoveDialog(contact.other);

	$row.find("a.delete-sharer").click(function() {
		bootbox.confirm("Подтвердите удаление профиля участника", function(result) {
			if (result) {
				$.radomJsonPost("/sharer/delete_other_profile.json", {
					sharer_id : contact.other.id
				}, function() {
					var $div = $("div.sharer-item[data-sharer-id='" + contact.other.id + "']");
					$div.slideUp(function() {
						$div.remove();
					});
					bootbox.alert("Профиль участника удален");
				});
			}
		});
		return false;
	});
	
    $row.find("a.add-group-select-link").click(function () {
    	$(this).addClass("disabled");
    	
    	$(this).parents("div.add-group-button").removeClass("open");
    	$(this).parents("div.add-group-button").find("a.btn[data-toggle=dropdown]").addClass("disabled");
    	
        var $sharerItem = $(this).parents("div.sharer-item");
        var contact = $sharerItem.data("contact");
        var groupId = $(this).attr("data-group-id");
        if (groupId == -1) {
            alert("Выберите список контактов");
        } else {
        	$.radomJsonPost("/contacts/add.json", {
                other_id: contact.other.id,
                group_id: groupId
            }, function(response) {
				//alert(getContactMarkup(response).html());
            	//$row.html(getContactMarkup(response));
				getContactMarkup(response,$row);
            });
        }
        return false;
    });
	$row.find("a.delete-group-select-link").click(function () {
		$(this).addClass("disabled");

		$(this).parents("div.add-group-button").removeClass("open");
		$(this).parents("div.add-group-button").find("a.btn[data-toggle=dropdown]").addClass("disabled");

		var $sharerItem = $(this).parents("div.sharer-item");
		var contact = $sharerItem.data("contact");
		var groupId = $(this).attr("data-group-id");
		if (groupId == -1) {
			alert("Выберите список контактов");
		} else {
			$.radomJsonPost("/contacts/deleteGroup.json", {
				other_id: contact.other.id,
				group_id: groupId
			}, function(response) {
				getContactMarkup(response,$row);
			});
		}
		return false;
	});

    $row.find("a.delete-contact-link").click(function () {
    	$(this).addClass("disabled");
        var $sharerItem = $(this).parents("div.sharer-item");
        var contact = $sharerItem.data("contact");
        $.radomJsonPost("/contacts/delete.json", {
            other_id: contact.other.id
        }, function(response) {
			$row.remove();
        });
        return false;
    });

    var $editContactsGroups = $row.find("a.btn-block.edit-contacts-groups-link");
    
    $row.find(".add-group-button").on("mouseenter", function() {
    	$editContactsGroups.animate({opacity: 1});
    	var timeout = $editContactsGroups.data("hide-timeout");
    	if (timeout) {
    		clearTimeout(timeout);
    	}
    }).on("mouseleave", function() {
    	var timeout = setTimeout(function() {
    		$editContactsGroups.animate({opacity: 0});
    	}, 1000);
    	$editContactsGroups.data("hide-timeout", timeout);
    });
    
    $editContactsGroups.on("mouseenter", function() {
    	var timeout = $editContactsGroups.data("hide-timeout");
    	if (timeout) {
    		clearTimeout(timeout);
    	}
    }).on("mouseleave", function() {
    	var timeout = setTimeout(function() {
    		$editContactsGroups.animate({opacity: 0});
    	}, 1000);
    	$editContactsGroups.data("hide-timeout", timeout);
    });
    
    $row.find("a.edit-contacts-groups-link.glyphicon-pencil").radomTooltip({
		container : "body",
		title : "Управление списками контактов",
		palcement : "top"
		
	});
    
    $row.find("a.edit-contacts-groups-link").click(function() {
    	$.cookie("EDIT_CONTACTS_LISTS_REFERER", window.location.pathname, {path : "/"});
    	return true;
    });
    return $row;
}
function getSharerMarkup(sharer,$oldRow) {
	var currentSharerId = "${sharer.id}";

	var model = {};
	model.sharer = sharer;
	model.onlineIconSrc = sharer.online ? "/i/icon-online.png" : "/i/icon-offline.png";
	model.onlineText = sharer.online ? "В сети" : "Не в сети";
	model.onlineTextClass = sharer.online ? "sharer-item-online-status" : "sharer-item-online-status text-muted";

	if (sharer.id == currentSharerId) {
		model.inListText = "Это Вы";
		model.deleteContactText = false;
		model.showAddDropdown = false;
		model.showAddButton = false;
	} else {
		if ((!sharer.contact) || ((sharer.contact.sharerStatus == "NEW") && (sharer.contact.otherStatus == "NEW"))) {
			model.inListText = "Не в списке Ваших контактов";
			model.deleteContactText = false;
		} else if ((sharer.contact.sharerStatus == "ACCEPTED") && (sharer.contact.otherStatus == "ACCEPTED")) {
			model.inListText = "В списке Ваших контактов: ";
			model.deleteContactText = "Удалить из контактов";
		} else if ((sharer.contact.sharerStatus == "ACCEPTED") && (sharer.contact.otherStatus == "NEW")) {
			model.inListText = "Вы отправили заявку на добавление <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + sharer.contact.requestDate + "'>" + (sharer.contact.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(sharer.contact.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
			model.deleteContactText = "Отменить заявку";
		} else if ((sharer.contact.sharerStatus == "NEW") && (sharer.contact.otherStatus == "ACCEPTED")) {
			model.inListText = "Вы получили заявку на добавление <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + sharer.contact.requestDate + "'>" + (sharer.contact.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(sharer.contact.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
			model.deleteContactText = "Отклонить заявку";
		}
		if ((!sharer.contact) || ((sharer.contact.sharerStatus == "NEW") && (sharer.contact.otherStatus == "NEW")) || ((sharer.contact.sharerStatus == "NEW") && (sharer.contact.otherStatus == "ACCEPTED"))) {
			model.addContactGroupDefaultId = 0;
			model.addContactGroupDefaultText = "Добавить в список контактов";

			model.contactGroups = null;
		} else {
			model.addContactGroupDefaultId = -1;
			model.addContactGroupDefaultText = "Перенести в списки контактов";

			model.contactGroups =  sharer.contact.contactGroups;
			if ((model.contactGroups) && (model.contactGroups.length == 0)) {
				model.contactGroups.push({color : 0,name:"Список по умолчанию"});
			}
		}
		/*if ((!sharer.contact) || (sharer.contact.sharerStatus != "ACCEPTED") || (sharer.contact.otherStatus != "ACCEPTED")) {
			//model.groupColor = null;
			//model.groupName = null;
			model.contactGroups = null;
		} else {
			//model.groupColor = (sharer.contact && sharer.contact.group) ? sharer.contact.group.color : 0;
			//model.groupName = (sharer.contact && sharer.contact.group) ? sharer.contact.group.name : "Список по умолчанию";
		}*/
		model.groups = getGroups();
		var groupsIds = {};
		if (model.contactGroups) {
			for (var i in model.contactGroups) {
				groupsIds[model.contactGroups[i].id] = model.contactGroups[i];
			}
		}
		for (var i in model.groups) {
			if (groupsIds[model.groups[i].id]){
				model.groups[i].userHas = true;
			}
			else {
				model.groups[i].userHas = false;
			}
		}
		model.showAddDropdown = getGroups().length > 0;
		model.showAddButton = (getGroups().length == 0) && ((!sharer.contact) || (sharer.contact.sharerStatus == "NEW"));
	}
	model.resizedAvatar = Images.getResizeUrl(sharer.avatar, "c84");

	var rendered = Mustache.render(userTemplate, model);
	var $row = null;
    if ($oldRow) {
		$row = $oldRow;
		$row.html( $(rendered).html());
	}
	else {
		var $row = $(rendered);
	}
	if(!sharer.online) {
		$row.find("p.last-login-wrapper").show();
		if (sharer.sex) {
			$row.find("span.last-login-message").html("Был в сети");
		} else {
			$row.find("span.last-login-message").html("Была в сети");
		}
	} else {
		$row.find("p.last-login-wrapper").hide();
	}


	$row.data("sharer", sharer);

	$row.find("span.request-distance").radomTooltip({
		placement : "top",
		container : "body"
	});

	$row.find("a.go-to-chat, a.edit-roles, a.do-accounts-move, a.delete-account").radomTooltip({
		placement : "top",
		container : "body"
	});

	$row.find("a.do-accounts-move").accountsMoveDialog(sharer);

	$row.find("a.delete-sharer").click(function() {
		bootbox.confirm("Подтвердите удаление профиля участника", function(result) {
			if (result) {
				$.radomJsonPost("/sharer/delete_other_profile.json", {
					sharer_id : sharer.id
				}, function() {
					var $div = $("div.sharer-item[data-sharer-id='" + sharer.id + "']");
					$div.slideUp(function() {
						$div.remove();
					});
					bootbox.alert("Профиль участника удален");
				});
			}
		});
		return false;
	});

	$row.find("a.add-group-select-link").click(function () {
		$(this).addClass("disabled");

		$(this).parents("div.add-group-button").removeClass("open");
		$(this).parents("div.add-group-button").find("a.btn[data-toggle=dropdown]").addClass("disabled");

		var $sharerItem = $(this).parents("div.sharer-item");
		var sharer = $sharerItem.data("sharer");
		var groupId = $(this).attr("data-group-id");
		if (groupId == -1) {
			alert("Выберите список контактов");
		} else {
			$.radomJsonPost("/contacts/add.json", {
				other_id: sharer.id,
				group_id: groupId
			}, function(response) {
				sharer.contact = response;
				getSharerMarkup(sharer,$row);
				loadRoster();
			});
		}
		return false;
	});
	$row.find("a.delete-group-select-link").click(function () {
		$(this).addClass("disabled");

		$(this).parents("div.add-group-button").removeClass("open");
		$(this).parents("div.add-group-button").find("a.btn[data-toggle=dropdown]").addClass("disabled");

		var $sharerItem = $(this).parents("div.sharer-item");
		var sharer = $sharerItem.data("sharer");
		var groupId = $(this).attr("data-group-id");
		if (groupId == -1) {
			alert("Выберите список контактов");
		} else {
			$.radomJsonPost("/contacts/deleteGroup.json", {
				other_id: sharer.id,
				group_id: groupId
			}, function(response) {
				getContactMarkup(response,$row);
				loadRoster();
			});
		}
		return false;
	});

	$row.find("a.delete-contact-link").click(function () {
		$(this).addClass("disabled");
		var $sharerItem = $(this).parents("div.sharer-item");
		var sharer = $sharerItem.data("sharer");
		$.radomJsonPost("/contacts/delete.json", {
			other_id: sharer.id
		}, function(response) {
			sharer.contact = null;
			getSharerMarkup(sharer,$row);
		});
		return false;
	});

	var $editContactsGroups = $row.find("a.btn-block.edit-contacts-groups-link");

	$row.find(".add-group-button").on("mouseenter", function() {
		$editContactsGroups.animate({opacity: 1});
		var timeout = $editContactsGroups.data("hide-timeout");
		if (timeout) {
			clearTimeout(timeout);
		}
	}).on("mouseleave", function() {
		var timeout = setTimeout(function() {
			$editContactsGroups.animate({opacity: 0});
		}, 1000);
		$editContactsGroups.data("hide-timeout", timeout);
	});

	$editContactsGroups.on("mouseenter", function() {
		var timeout = $editContactsGroups.data("hide-timeout");
		if (timeout) {
			clearTimeout(timeout);
		}
	}).on("mouseleave", function() {
		var timeout = setTimeout(function() {
			$editContactsGroups.animate({opacity: 0});
		}, 1000);
		$editContactsGroups.data("hide-timeout", timeout);
	});

	$row.find("a.edit-contacts-groups-link.glyphicon-pencil").radomTooltip({
		container : "body",
		title : "Управление списками контактов",
		palcement : "top"

	});

	$row.find("a.edit-contacts-groups-link").click(function() {
		$.cookie("EDIT_CONTACTS_LISTS_REFERER", window.location.pathname, {path : "/"});
		return true;
	});

	return $row;
}

</script>