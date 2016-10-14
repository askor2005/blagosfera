<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style type="text/css">
	.form-control .x-field {
		width : 100% !important;
	}
</style>

<h1>Список уведомлений</h1>


<div class="alert alert-danger" style="display: none;" id="blockingNotify" role="alert">У Вас имеются непрочитанные блокирующие уведомления. До их прочтения дальнейшая работа с системой невозможна. Пожалуйста, ознакомьтесь с уведомлениями из списка ниже.</div>


<div class="row">
	<div class="col-xs-6">
		<div class="form-group">
			<label>Начиная с даты</label>
			<a href="#" class="pull-right no-underline" id="start-date-remove-link"><span class="glyphicon glyphicon-remove"></span>&nbsp;Очистить</a>
			<div id="start-date" class="form-control"></div>
		</div>
	</div>
	<div class="col-xs-6">
		<div class="form-group">
			<a href="#" class="pull-right no-underline" id="end-date-remove-link"><span class="glyphicon glyphicon-remove"></span>&nbsp;Очистить</a>
			<label>Заканчивая датой</label>
			<div id="end-date" class="form-control"></div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-xs-6">
		<div class="form-group">
			<label>Фильтр</label>
			<select class="form-control" id="select-read">
				<option value="0">Прочитанные и новые</option>
				<option value="1">Только новые</option>
				<option value="2">Только прочитанные</option>
			</select>
		</div>
	</div>
	<div class="col-xs-6">
		<label>Приоритет</label>
		<select class="form-control" id="select-priority">
			<option value="">Все</option>
			<option value="LOW">Низкий</option>
			<option value="NORMAL">Обычный</option>
			<option value="HIGH">Высокий</option>
			<option value="CRITICAL">Критический</option>
			<option value="BLOCKING">Блокирующий</option>
		</select>
	</div>
</div>

<hr />

<div id="notifications-list"></div>

<div class="row list-not-found" id="notifications-not-found">
	<div class="panel panel-default">
		<div class="panel-body">Поиск не дал результатов</div>
	</div>
</div>

<div class="row list-loader-animation"></div>

<script type="text/javascript">

	Ext
	.onReady(function() {
		Ext
				.create(
						'Ext.form.field.Date',
						{
							renderTo : 'start-date',
							width : 120,
							xtype : 'datefield',
							name : 'start_date',
							value : '',
							format : 'd.m.Y',
							listeners: {
								change: function () {
									initScrollListener();
								}
							}
						});
		Ext
				.create(
						'Ext.form.field.Date',
						{
							renderTo : 'end-date',
							width : 120,
							xtype : 'datefield',
							name : 'end_date',
							value : '',
							format : 'd.m.Y',
							listeners: {
								change: function () {
									initScrollListener();
								}
							}						
						});
	
	
	});

	function getParams() {
		var params = {
			priority : $("select#select-priority").val()
		};
		$selectRead = $("select#select-read");
		if ($selectRead.val() == 1) {
			params.include_read = false;
		}
		if ($selectRead.val() == 2) {
			params.include_unread = false;
		}
		var startDate = $("input[name=start_date]").val();
		if (startDate) {
			params.start_date = startDate;
		}
		var endDate = $("input[name=end_date]").val();
		if (endDate) {
			params.end_date = endDate;
		}
		return params;
	}

	function getNotificationListMarkup(notification) {
		var $row = $("<div class='row notification-item' id='notification-item-" + notification.id + "'></div>");
		var $col3 = $("<div class='col-xs-3'><a class='sender-avatar-link' href='" +  notification.sender.link + "'><img src='" + Images.getResizeUrl(notification.sender.avatar, "c130") + "' class='img-thumbnail'></a></div>");
		$row.append($col3);
		var $col9 = $("<div class='col-xs-9'></div>");
		var $col9row = $("<div class='row'></div>");
		$col9row.append("<div class='col-xs-7'><h4>" + notification.subject	+ "</h4></div>");
		$col9row.append("<div class='col-xs-5 text-right'>Приоритет: <span class='notification-priority notification-priority-" + notification.priority + "'>"	+ Notifications.getPriorityName(notification) + "</span></div>");
		$col9row.append("<div class='col-xs-7'>Отправитель: <a class='sender-name-link' href='" + notification.sender.link + "'>" + notification.sender.name + "</a></div>");
		$col9row.append("<div class='col-xs-5 text-muted text-right'>" + notification.date + "</div>");
		
		$col9row.append("<div class='col-xs-12 short-text'>" + (notification.shortText ? notification.shortText : "") + "</div>");
		$col9row.append("<div class='col-xs-12 text' style='display : none;'>" + (notification.text ? notification.text : "") + "</div>");

		function notificationLinkCallback() {
			var ajax = $(this).attr("data-ajax") == "true";
			var url = $(this).attr("href");
			var markAsRead = $(this).attr("data-mark-as-read") == "true";
			var notificationId = $(this).attr("data-notification-id");

			if (url && ajax) {
				// если надо выполнить какое то действие в js
				if (url.search("javascript:") == 0) {
					// Выполняем js скрипт
					eval(url.substr("javascript:".length, url.length));
				} else {
					$.radomJsonPost(url, {}, function() {
					});
				}
				if (markAsRead) {
					$.radomJsonPost("/notifications/markasread.json", {
						notification_id : notificationId
					}, function() {
						//var $li = $a.parents("li.header-notification");
						//$li.fadeOut(function() {
						//	$li.next("hr").remove();
						//	$li.remove();
						//	showHeaderUnreadNotifications();
						//})
					}, function(errorMessage) {
						bootbox.alert(errorMessage);
					});
				}
			}

			if (url && !ajax && markAsRead) {
				$.radomJsonPost("/notifications/markasread.json", {
					notification_id : notificationId
				}, function() {
				}, function(errorMessage) {
					console.log(errorMessage);
				});
				window.location = url;
			}

			if (url && !ajax && !markAsRead) {
				window.location = url;
			}

			if (!url && markAsRead) {
				$.radomJsonPost("/notifications/markasread.json", {
					notification_id : notificationId
				}, function() {

				}, function(errorMessage) {
					bootbox.alert(errorMessage);
				});
			}
			return false;
		}

		function createNotificationLinkButton(notificationLink) {
			var $a = $("<a class='btn btn-xs btn-" + notificationLink.type.toLowerCase() + "' href='" + notificationLink.url + "' class='mark-as-read notification-object-link'>" + notificationLink.title + "</a>");
			$a.attr("data-ajax", notificationLink.ajax);
			$a.attr("href", notificationLink.url);
			$a.attr("data-mark-as-read", notificationLink.markAsRead);
			$a.attr("data-notification-id", notification.id);
			$a.click(notificationLinkCallback);
			return $a;
		}

		if (notification.links && notification.links.length > 0) {
			var $buttons = [];

			for(var i=0; i<notification.links.length; i++) {
				var l = notification.links[i];
				// Кнопки "Скрыть", "Принять" и "Отклонить" из notification.links не отображаем.
				// Вместо скрыть дальше в шаблоне формируется своя кнопка "Прочитано"/"Отметить как прочитанное"
				// "Принять" и "Отклонить" не светим, т.к.пока нет механизма отслеживания принято ли то, с чем связанно уведомление, или отклонено
				if(l.title !== "Скрыть" && l.title !== "Принять" && l.title !== "Отклонить") {
					var $button = createNotificationLinkButton(l);
					$buttons.push($button);
				}
			}

			//--------------------
			// В столбик
//			for(var i=0; i<$buttons.length; i++) {
//				var colXs = 12;
//				if(i == notification.links.length - 2) {
//					colXs = 6;
//				}
//				var $line = $("<div class='col-xs-"+ colXs + "'></div>");
//				var $group = $("<div class='form-group'></div>");
//				$group.append($buttons[i]);
//				$line.append($group);
//				$col9row.append($line);
//			}

			//--------------------
			// В линии по 2 элемента

			if($buttons.length > 1) {
				var $line = $("<div class='col-xs-12'></div>");
				var $group = $("<div class='form-group'></div>");
				$line.append($group);
				for(var i=0; i<$buttons.length-1; i+=2) {
					$group.append($buttons[i]);
					$group.append('&nbsp;');
					$group.append($buttons[i+1]);
				}
				$col9row.append($line);
			}

			var $line = $("<div class='col-xs-6'></div>");
			var $group = $("<div class='form-group'></div>");
			$group.append($buttons[$buttons.length-1]);
			$line.append($group);
			$col9row.append($line);

			//--------------------
		}

		$col9row.append("<div class='col-xs-6'><a href='#' class='show-text'><span class='glyphicon glyphicon-folder-open'></span>&nbsp;&nbsp;Открыть</a><a href='#' class='hide-text'><span class='glyphicon glyphicon-folder-close'></span>&nbsp;&nbsp;Закрыть</a></div>");
		if (notification.read) {
			$col9row.append("<div class='col-xs-6'><p class='text-muted pull-right'><span class='glyphicon glyphicon-ok'></span>&nbsp;&nbsp;Прочитано</p></div>");
		} else {
			$col9row.append("<div class='col-xs-6'><a href='#' class='mark-as-read pull-right notification-default-link'><span class='glyphicon glyphicon-ok'></span>&nbsp;&nbsp;Отметить как прочитанное</a></div>");
		}

		$col9.append($col9row);
		$row.append($col9);
		
		$row.data("notification", notification);
		
		$row.append("<hr/>");
		
		return $row;
	}

	function initNotificationItem(notification) {
		var $item = $("div#notification-item-" + notification.id);
		var $senderAvatarLink = $item.find("a.sender-avatar-link");
		if ($senderAvatarLink.attr("href") == "#") {
			$senderAvatarLink.click(function() {
				return false;
			});
		}
		var $senderNameLink = $item.find("a.sender-name-link");
		if ($senderNameLink.attr("href") == "#") {
			$senderNameLink.click(function() {
				return false;
			});
		}
		
		var $showText = $item.find("a.show-text");
		var $hideText = $item.find("a.hide-text");
		var $markAsRead = $item.find("a.mark-as-read");
		var $text = $item.find("div.text");
		
		$markAsRead.click(function() {
			var $link = $(this);
			var $parent = $(this).parents(".notification-item");
			var notification = $parent.data("notification");
			$.ajax({
				type : "get",
				dataType : "json",
				url : "/notifications/markasread.json",
				data : {
					notification_id : notification.id
				},
				success : function(response) {
					if (response.result == "success") {
						showHeaderUnreadNotifications();
						$link.replaceWith("<p class='text-muted pull-right'><span class='glyphicon glyphicon-ok'></span>&nbsp;&nbsp;Прочитано</p>");
						if ($link.hasClass("notification-object-link")) {
							window.location = $link.attr("href");
						}
					} else {
						console.log("mark as read error");
					}
				},
				error : function() {
					console.log("ajax error");
				}
			});
			return false;
		});
		
		if ($text.html() == "") {
			$showText.hide();
			$hideText.hide();
		} else {
			$showText.click(function(){
				var $parent = $(this).parents(".notification-item");
				var $text = $parent.find("div.text");
				$text.slideDown(function() {
					$parent.find("a.hide-text").show();
					$parent.find("a.show-text").hide();
				});
				return false;				
			});
			$hideText.click(function(){
				var $parent = $(this).parents(".notification-item");
				var $text = $parent.find("div.text");
				$text.slideUp(function() {
					$parent.find("a.show-text").show();
					$parent.find("a.hide-text").hide();
				});
				return false;				
			});
			$hideText.hide();
		}
	}
	
	function initScrollListener() {
		var $list = $("div#notifications-list").empty();
		ScrollListener.init("/notifications/list.json", "get", getParams,
			function() {
				$(".list-loader-animation").fadeIn();
			}, function(response) {
				var $list = $("div#notifications-list");
				$.each(response, function(index, notification) {
					$list.append(getNotificationListMarkup(notification));
					initNotificationItem(notification);
					$list.append("<hr/>");
				});
				
				var hash = window.location.hash.substr(1);
				if (hash) {
					var $item = $("#notification-item-" + hash);
					if ($item.length > 0) {
						$('html, body').scrollTop($item.offset().top);
					}
				}
				
				if ($("div.row.notification-item").length == 0) {
					$("div#notifications-not-found").show();
				} else {
					$("div#notifications-not-found").hide();
				}
				$(".list-loader-animation").fadeOut();
			});
	}

	function loadNotifyPageData(callBack) {
		$.radomJsonPost("/notifications/page_data.json", {}, callBack);
	}

	$(document).ready(function() {
		loadNotifyPageData(function(response){
			if (response.hasUnreadBlockingNotifications) {
				$("#blockingNotify").show();
			}
			initNotifyPage();
		});
	});

	function initNotifyPage() {
		
		$("a#start-date-remove-link").click(function(){
			$("input[name=start_date]").val("");
			initScrollListener();
			return false;
		});

		$("a#end-date-remove-link").click(function(){
			$("input[name=end_date]").val("");
			initScrollListener();
			return false;
		});
		
		$("select#select-priority").change(initScrollListener);
		$("select#select-read").change(initScrollListener);
		
		initScrollListener();
		
		radomStompClient.subscribeToUserQueue("new_notification", function(notification){
			var $list = $("div#notifications-list");
			$list.prepend("<hr/>");
			$list.prepend(getNotificationListMarkup(notification));
			initNotificationItem(notification);
			$("div#notifications-not-found").hide();
		});
		
	}
</script>