<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<script id="header-notification-template" type="x-tmpl-mustache">
<li class="header-notification">
	<div class="row" id="header-unread-notification-{{notification.id}}">
		<div class="col-xs-3">
			<a href="{{notification.sender.link}}">
				<img class="img-thumbnail" src="{{notification.sender.avatar}}" />
			</a>
		</div>
		<div class="col-xs-9">
			<span class="subject">{{{notification.shortText}}}</span>
			<a class="name text-muted" href="{{notification.sender.link}}">{{notification.sender.name}}</a>
			<div class="row">
				<div class="col-xs-5 date text-muted">
					{{notification.date}}
				</div>
				<div class="col-xs-7 priority text-muted text-right">
					Приоритет:
					<span class="notification-priority notification-priority-{{notification.priority}}">
						{{priorityName}}
					</span>
				</div>
			</div>            						
		</div>
		<div class="col-xs-12">
			<div class="form-group buttons">
				{{#notification.links}}
					<a href="{{url}}" class="btn btn-xs btn-{{type}}" data-ajax="{{ajax}}" data-type="{{type}}" data-mark-as-read="{{markAsRead}}" data-notification-id="{{notification.id}}">{{title}}</a>
				{{/notification.links}}
			</div>
		</div>
	</div>
<hr/>
</li>
</script>

<%@include file="../chat/commonChat.jsp" %>
<script type="text/javascript" src="/js/notifications/notificationApiForChat.js?v=${buildNumber}" ></script>
<c:if test="${not empty showDialogToChangePassword and showDialogToChangePassword == true}">
	<%@include file="changeUserPasswordDialog.jsp" %>
</c:if>

<c:if test="${not empty needGcmSwUpdate and needGcmSwUpdate == true}">
	<script type="text/javascript" src="/js/notifications/gcm-manager.js?v=${buildNumber}"></script>
</c:if>

<script type="text/javascript">
	var headerNotificationTemplate = $("#header-notification-template").html();
	Mustache.parse(headerNotificationTemplate);

	function getPriorityName(notification) {
		switch (notification.priority) {
		case "LOW":
			return "Низкий";
			break;
		case "NORMAL":
			return "Обычный";
			break;
		case "HIGH":
			return "Высокий";
			break;
		case "CRITICAL":
			return "Критический";
			break;
		case "BLOCKING":
			return "Блокирующий";
			break;
		default:
			return "";
			break;
		}
	}

	function getHeaderNotificationMarkup(notification) {
		notification.sender.avatar = Images.getResizeUrl(notification.sender.avatar, "c90");
		var $markup = $(Mustache.render(headerNotificationTemplate, {
			notification : notification,
			priorityName : getPriorityName(notification)
		}));

		// В спане .subject есть символы &nbsp; которые препятствуют корректному переходу строк в спане,
		// т.ч. убираем их от туда
		var $subject = $markup.find(".subject");
		var shortText = $subject.html().replace(/&nbsp;/gi,' ');
		$subject.html(shortText);

		$.each($markup.find("div.buttons").find("a"), function(index, a) {
			var $a = $(a);
			var type = $a.attr("data-type").toLowerCase();
			$a.addClass("btn-" + type);
			var ajax = $a.attr("data-ajax") == "true";
			var url = $a.attr("href");
			var markAsRead = $a.attr("data-mark-as-read") == "true";
			var notificationId = $a.attr("data-notification-id");

            $a.click(function(event) {
                event.preventDefault();

                if (url) {
                    if (ajax) {
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
                                var $li = $a.parents("li.header-notification");
                                $li.fadeOut(function() {
                                    $li.next("hr").remove();
                                    $li.remove();
                                    showHeaderUnreadNotifications();
                                })
                            }, function(errorMessage) {
                                bootbox.alert(errorMessage);
                            });
                        }
                    } else {
                        if (markAsRead) {
                            $.radomJsonPost("/notifications/markasread.json", {
                                notification_id : notificationId
                            }, function() {
                                window.location = url;
                            }, function(errorMessage) {
                                bootbox.alert(errorMessage);
                            });
                        } else {
                            window.location = url;
                        }
                    }
                } else {
                    if (markAsRead) {
                        $.radomJsonPost("/notifications/markasread.json", {
                            notification_id : notificationId
                        }, function() {

                        }, function(errorMessage) {
                            bootbox.alert(errorMessage);
                        });
                    }
                }
			});
		});

		return $markup;
	}

	function showHeaderUnreadNotifications() {
		$.ajax({
			type : "get",
			dataType : "json",
			url : "/notifications/unread.json",
			data : {
				count : 10
			},
			success : function(response) {
				if (response != null && response.notifications != null) {
					$("ul#header-notifications-menu li.header-notification").remove();
					var count = response.count;
					$.each(response.notifications.reverse(), function (index, notification) {
						$("ul#header-notifications-menu").prepend(getHeaderNotificationMarkup(notification));
					});
					var $counter = $("span#unread-notifications-counter");
					if (count > 0) {
						$counter.html(count).fadeIn();
					} else {
						$counter.html("0").fadeOut();
					}
				}
			},
			error : function() {
				console.log("ajax error");
			}
		});
	}

	$(document).ready(function() {
		showHeaderUnreadNotifications();

		$(radomEventsManager).bind("notification.delete", function(event, notification) {
			var $li = $("div#header-unread-notification-" + notification.id).parents("li.header-notification");
			$li.fadeOut(function() {
				$li.next("hr").remove();
				$li.remove();
				showHeaderUnreadNotifications();
			})
		});

		$(radomEventsManager).bind("notification.mark-as-read", function(event, notification) {
			var $li = $("div#header-unread-notification-" + notification.id).parents("li.header-notification");
			$li.fadeOut(function() {
				$li.next("hr").remove();
				$li.remove();
				showHeaderUnreadNotifications();
			})
		});

		radomStompClient.subscribeToUserQueue("new_notification", function(notification) {
			$("ul#header-notifications-menu").prepend(getHeaderNotificationMarkup(notification));
			var $counter = $("#unread-notifications-counter");
			var unreadCount = parseInt($counter.html());
			unreadCount = unreadCount + 1;
			$counter.html(unreadCount);
			$counter.fadeIn();
			//showHeaderUnreadNotifications();
			RadomSound.playDefault();
		});

		$("a#header-unread-notifications-icon").radomTooltip({
			container : "body",
			title : "Мои уведомления",
			placement : "bottom",
			trigger : "manual"
		});
		$("a#header-unread-notifications-icon").on("mouseenter", function() {
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

		$("a#header-cyberbrain-icon").radomTooltip({
			container : "body",
			title : "КиберМозг",
			placement : "bottom",
			trigger : "manual"
		});
		$("a#header-cyberbrain-icon").on("mouseenter", function() {
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

		$("a#header-admin-icon").radomTooltip({
			container : "body",
			title : "Настройки системы",
			placement : "bottom",
			trigger : "manual"
		});
		$("a#header-admin-icon").on("mouseenter", function() {
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

		/*$("#ra-menu a").click(function() {
			var $link = $(this);
			var selectedSubportal = $link.attr("data-subportal");
			$.cookie("RADOM_ACTIVE_SUBPORTAL", selectedSubportal, {path : "/"});
			return true;
		});*/

		$("#mark-all-as-read").click(function() {
			$.radomJsonPost("/notifications/mark_all_as_read.json", {}, function () {
				// Удаляем все уведомления
				$("ul#header-notifications-menu li.header-notification").remove();

				// Зануляем и скрываем счётчик
				var $counter = $("span#unread-notifications-counter");
				$counter.html("0").fadeOut();
			});
		});

		$.each($("#ra-menu a[data-title]"), function(index, link) {
			var $link = $(link);
			$link.radomTooltip({
				placement : "bottom"
			});
		});

		// Загрузка баланса
		$.radomJsonGet(
				"/sharer/balance.json",
				{},
				function(response){
					$("#commonBalanceValue").text(response.balance);
				},
				function(){
					$("#header-my-balance").html("Ошибка получения баланса");
				}
		);
	});
</script>

<div class="navbar navbar-default navbar-fixed-top" role="navigation">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="/sharer"> <img src="<c:if test="${buildBranch eq 'develop'}">/i/logo-50-dev.png</c:if><c:if test="${buildBranch ne 'develop'}">/i/logo-50.png</c:if>"/></a>
		</div>

		<ul class="nav navbar-nav" id="ra-menu"></ul>

		<ul class="nav navbar-nav navbar-right">
			<c:if test="${radom:hasRole('ROLE_SUPERADMIN')}">
				<li>
					<a id = "header-admin-icon" class="header-glyphicon-link fa fa-cog" href="/admin/systemSettings"></a>
				</li>
			</c:if>

			<li>
				<a id = "header-cyberbrain-icon" class="header-glyphicon-link fa fa-line-chart" href="/cyberbrain/taskManagement"></a>
			</li>

			<li class="dropdown"><a
				class="header-glyphicon-link glyphicon glyphicon-flag" href="javascript:void(0);"
				data-toggle="dropdown" id="header-unread-notifications-icon">
				<span class="counter" id="unread-notifications-counter" style="display: none;"></span>
			</a>
				<ul class="dropdown-menu" id="header-notifications-menu" role="menu">
					<li><a href="/notify">Все уведомления</a></li>
					<li><a href="javascript:void(0);" id="mark-all-as-read">Отметить все уведомления как прочитанные</a></li>
                    <c:if test="${radom:hasRole('ROLE_ADMIN')}">
                        <li><a href="/admin/notifications">Редактировать шаблоны</a></li>
                    </c:if>
				</ul></li>

			<li class="dropdown">
				<a	class="header-glyphicon-link fa fa-rub" href="javascript:void(0);" data-toggle="dropdown" id="header-balance-icon"></a>
				<ul class="dropdown-menu" id="header-balance-menu" role="menu" style="width: 350px;">
					<span style="color: red; display: block; font-size: 13px; margin-right: 20px; margin-left: 20px; background-color: #f5f5f5; margin-top: 20px;
    margin-bottom: 20px;"><span style="display: block;padding-left: 20px;padding-right: 20px; padding-bottom: 20px; padding-top: 20px; text-align: justify;border: 1px solid #e7e7e7; border-radius: 3px;">  <b>ВНИМАНИЕ!</b> В настоящий момент Учётно-Расчётная система "Ра" находится в тестовом режиме.
Учетная единица "Ра" не является платежным средством. Применять единицу "Ра" для совершения реальных платежей нельзя. По завершению тестирования системы
все созданные единицы "Ра" будут аннулированы. </span> </span>
					<li class="divider"></li>
					<li>
						<a href="/ng/#/account" target="_blank">
							<b id="header-my-balance" class="pull-right"><span id="commonBalanceValue"></span> Ра</b>
							Мой баланс:
						</a>
					</li>
					<li class="divider"></li>
					<c:forEach items="${accountTypes}" var="at">
						<li data-account-type-id="${at.id}">
							<a href="/account">
								<c:if test="${not empty accountsMap[at]}"><span class="pull-right">(${radom:formatMoney(accountsMap[at].balance)} Ра)</span></c:if>
								<c:if test="${empty accountsMap[at]}"><span class="pull-right">(0.00 Ра)</span></c:if>
								${at.name}:
							</a>
						</li>
					</c:forEach>

					<li class="divider"></li>
					<li>
						<a href="javascript:void(0);" onclick="SelfAccountsMoveDialog.show(); return false;">Перевод между своими счетами</a>
					</li>

					<li class="divider"></li>
					<li>
						<a href="javascript:void(0);" onclick="IncomingPaymentDialog.show(); return false;">Пополнение счёта</a>
					</li>

					<li class="divider"></li>
					<li>
						<a href="javascript:void(0);"  onclick="OutgoingPaymentDialog.show(); return false;">Вывод средств</a>
					</li>
				</ul>
			</li>

			<script type="text/javascript">
				$(document).ready(function() {
					//$("a#header-balance-icon").click(function() {
					//	Accounts.refresh();
					//});
					$(radomEventsManager).bind("accounts.refresh", function(event, data) {
						var $block = $("ul#header-balance-menu");
						$block.find("b#header-my-balance").html(data.total + " Ра");
						$.each(data.accounts, function(index, account) {
							//$block.find("li[data-account-type-id=" + account.type.id + "]").html("<a href=\"/account\">"+account.type.name + ": (" + account.balance + " Ра)"+"</a>");
							$block.find("li[data-account-type-id=" + account.type.id + "] span").html("(" + account.balance + " Ра)");
						});
					});
				});
			</script>


			<li class="dropdown"><a
				class="header-avatar-link dropdown-toggle" href="javascript:void(0);"
				data-toggle="dropdown"> ${sharer.shortName} <img
					class="header-avatar img-thumbnail" src='${radom:resizeImage(sharer.avatar, "c48")}' />
			</a>
				<ul id="header-avatar-dropdown" class="dropdown-menu" role="menu">
                    <li><a href="/sharer">Мой профиль&nbsp;&nbsp;<span
                            class="glyphicon glyphicon-user"></span>
                    </a></li>
					<!--<li><a href="/ng/#/user/profile/${sharer.ikp}" target="_blank">Мой профиль&nbsp;&nbsp;<span
							class="glyphicon glyphicon-user"></span>
					</a></li>-->
					<li><a href="/ng/#/user/settings" target="_blank">Мои настройки&nbsp;&nbsp;<span
                            class="glyphicon glyphicon-cog"></span>
                    </a></li>
					<li><a href="/document/service/documentListPage">Мои документы&nbsp;&nbsp;<span
							class="glyphicon glyphicon-list-alt"></span>
					</a></li>					
					<li><a href="/ng/#/account" target="_blank">Мой баланс&nbsp;&nbsp;<span
							class="glyphicon glyphicon-credit-card"></span>
					</a></li>
					<li><a href="/ng/#/invites" target="_blank">Пригласить пользователя&nbsp;&nbsp;<span
								class="fa fa-user-plus"></span>
					</a></li>
					<!--li><a href="javascript:void(0);" onclick="return false;"> <c:if
								test="${biometric}">Авторизован с использованием биометрии&nbsp;&nbsp;<span
									class="glyphicon glyphicon-ok-circle"></span>
							</c:if> <c:if test="${not biometric}">Авторизован без использования биометрии&nbsp;&nbsp;<span
									class="glyphicon glyphicon-ban-circle"></span>
							</c:if>
					</a></li-->
					<li class="divider"></li>
					<li><a href="javascript:void(0)" onclick="doLogout()">Выход&nbsp;&nbsp;<span
							class="glyphicon glyphicon-log-out"></span></a></li>
				</ul></li>
		</ul>
	</div>
</div>

<script type="application/javascript">
    function doLogout() {
        $.get("/api/user/logout.json", {}).done(function (data) {
            window.location = '/';
        }).fail(function (data) {
            window.location = '/';
        }).always(function () {
        });
    }
</script>