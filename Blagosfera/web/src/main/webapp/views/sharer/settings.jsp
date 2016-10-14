<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">
div.tooltip {
	display: block;
}

.slider.slider-horizontal {
	display : block;
	width : auto;
} 
</style>

<h2>${profile.shortName}</h2>
<hr />
<h3>Настройки</h3>
<hr />

<ul class="nav nav-tabs" role="tablist">
	<li class="active"><a href="#security" role="tab" data-toggle="tab">Безопасность</a></li>
	<li><a href="#profile" role="tab" data-toggle="tab">Профиль</a></li>
	<li><a href="#interface" role="tab" data-toggle="tab">Интерфейс</a></li>
	<!--li><a href="#profile" role="tab" data-toggle="tab">Profile</a></li>
	<li><a href="#messages" role="tab" data-toggle="tab">Messages</a></li>
	<li><a href="#settings" role="tab" data-toggle="tab">Settings</a></li-->
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="security">
		<form id="multiple-sessions">
			<div class="form-group"></div>
			<div class="form-group">
				<label>Параллельные сессии</label>
				<br/>
				<label class="radio-inline">
					<input type="radio" name="allow" value="true" <c:if test="${sharer.allowMultipleSessions}">checked="checked"</c:if> > Разрешить
				</label>
				<label class="radio-inline">
					<input type="radio" name="allow" value="false" <c:if test="${not sharer.allowMultipleSessions}">checked="checked"</c:if> > Запретить
				</label>		
			</div>
		</form>
		<hr/>
		<div class="form-group">
			<a class="btn btn-primary close-other-sessions" href="#">Закрыть все сессии кроме этой</a>
		</div>
		<hr/>
		<h4>Мои сессии</h4>
		<table class="table sessions">
			<tr>
				<th>Тип устройства</th>
				<th>Операционная система</th>
				<th>Браузер</th>
				<th>IP-адрес</th>
				<th>
				</th>
			</tr>
			<c:forEach items="${activeLoginLogEntries}" var="i">
				<tr <c:if test="${i.sessionId == currentSessionId}">class="success"</c:if> <c:if test="${i.sessionId != currentSessionId}">class="other"</c:if> >
					<td>${i.device}</td>
					<td>${i.os}</td>
					<td>${i.browser}</td>
					<td>${i.ip}</td>
					<td>
						<c:if test="${i.sessionId != currentSessionId}">
							<a class="btn btn-primary btn-xs close-session" href="#" data-login-log-entry-id="${i.id}">Закрыть сессию</a>
						</c:if>
					</td>
				</tr>
			</c:forEach>			
		</table>
		
		<hr/>
		
		<form id="change-password">
			<h4>Изменить пароль</h4>
			
			<p class="text-muted">Для смены пароля необходимо ввести старый пароль, а также новый пароль и подтверждение. Новый пароль и подтверждение должны совпадать.</p>
			
			<div class="form-group">
				<label for="old-password">Старый пароль</label>
				<input type="password" class="form-control" id="old-password" placeholder="Старый пароль" name="old_password" />
  			</div>
			<div class="form-group">
				<label for="new-password">Новый пароль</label>
				<input type="password" class="form-control" id="new-password" placeholder="Новый пароль" name="new_password" />
  			</div>
			<div class="form-group">
				<label for="new-password-confirm">Подтверждение нового пароля</label>
				<input type="password" class="form-control" id="new-password-confirm" placeholder="Подтверждение нового пароля" name="new_password_confirm" />
  			</div>  	
			<div class="form-group">
				<a href="#" onclick="return changePassword();" class="btn btn-primary">Применить</a>
			</div>
		</form>
		
		<hr/>
		
		<form id="change-email">
			<h4>Изменить e-mail</h4>
			
			<p class="text-muted">Смена адреса электронной почты, на который зарегистриован аккаунт возможно только после ввода проверочного кода из письма, которое будет выслано на старый адрес. Для отправки такого письма нажмите кнопку "Отправить проверочный код". После получения проверочного кода введите его и новый адрес электронной почты в поля ниже, и нажмите кнопку "Изменить e-mail".</p>
			<div class="form-group">
				<a href="#" onclick="return initChangeEmail();" class="btn btn-primary">Отправить проверочный код</a>
			</div>			
			<hr/>
			<div class="form-group">
				<label for="code">Проверочный код</label>
				<input type="text" class="form-control" id="code" placeholder="Проверочный код" name="code" />
  			</div>
			<div class="form-group">
				<label for="new-email">Новый e-mail</label>
				<input type="text" class="form-control" id="new-email" placeholder="Новый e-mail" name="new_email" />
  			</div>
 	
			<div class="form-group">
				<a href="#" onclick="return completeChangeEmail();" class="btn btn-primary">Изменить e-mail</a>
			</div>
		</form>
		
	</div>
	<div class="tab-pane" id="profile">
		<div class="form-group"></div>

		<div class="form-group">
			<label for="sharer_short_link_name">Короткое имя участника для использования в символических ссылках</label>
			<div class="input-group">
				<div class="input-group-addon community-seo-link-addon"></div>
				<script type="text/javascript">
					// Сразу, без document.ready, выставляем домен.
					// Иначе поле появляется, но правильное здачение выставляется заметно позже.
					$(".community-seo-link-addon").html(window.location.origin+"/sharer/");
				</script>
				<input id="sharer_short_link_name" type="text" class="form-control" placeholder='Короткое имя участника' value="${sharerShortLink}" />
			</div>
		</div>

		<div class="form-group">
			<a href="#" onclick="return changeSharerSeoLink();" class="btn btn-primary">Применить</a>
		</div>

		<script type="text/javascript">
			function changeSharerSeoLink(){
				$.radomJsonPost("/sharer/change_sharer_short_link_name.json",
						{sharer_short_link_name: $("input#sharer_short_link_name").val()},
						function(r) {
							bootbox.alert("Ссылка на страницу профиля профиль успешно изменена");
							$("input#sharer_short_link_name").val(r.link);
						},function(r) {
							if(r && r.message) {
								bootbox.alert(r.message);
							} else {
								bootbox.alert("Ошибка");
							}
						});
				return false;
			}
		</script>

		<hr/>

		<div class="form-group">
			<label for="time-zone">Часовой пояс</label>
			<select class="form-control" id="time-zone">
				<option value=""></option>
				<c:forEach items="${timezones}" var="timezone">
					<option value="${timezone}" <c:if test='${radom:getSetting("profile.timezone", "") eq timezone}'>selected="selected"</c:if> >${timezone} (${timezoneOffsets[timezone]})</option>
				</c:forEach>
			</select>
		</div>

		<div class="form-group">
			<a href="#" onclick="return changeTimezone();" class="btn btn-primary">Применить</a>
		</div>

		<script type="text/javascript">
			function changeTimezone(){
				RadomUtils.setSetting("profile.timezone", $("#time-zone").val(), function() {
					bootbox.alert("Часовой пояс успешно изменен")
				});
				return false;
			}
		</script>

		<hr/>
		
		<div class="form-group">
			<label for="show-email">Показывать мой e-mail</label>
    		<select class="form-control" id="show-email" >
    			<option <c:if test='${currentShowEmailMode == "ALL"}'>selected="selected"</c:if> value="ALL">Всем</option>
    			<option <c:if test='${currentShowEmailMode == "CONTACTS"}'>selected="selected"</c:if> value="CONTACTS">Всем моим контактам</option>
    			<option <c:if test='${currentShowEmailMode == "LISTS"}'>selected="selected"</c:if> value="LISTS">Всем моим контактам из выбранных организационных списков</option>
    			<option <c:if test='${currentShowEmailMode == "NOBODY"}'>selected="selected"</c:if> value="NOBODY">Никому</option>
    		</select>
		</div>
		
		<div class="form-group" id="show-email-lists" <c:if test='${currentShowEmailMode != "LISTS"}'>style="display : none;"</c:if> >
			<c:forEach items="${contactsGroups}" var="g">
				<label class="checkbox-inline">
	  				<input type="checkbox" name="show_email_list" value="${g.id}" <c:if test="${currentShowEmailListIds.contains(g.id)}">checked="checked"</c:if> /> ${g.name}
				</label>			
			</c:forEach>
		</div>
		
		<hr/>
		<div class="form-group">
			<a class="btn btn-danger" href="#" onclick="deleteProfile(); return false;">Удалить профиль</a>		
		</div>
		<hr/>
	</div>
	
	<div class="tab-pane" id="interface">
		<div class="form-group"></div>
		<div class="checkbox">
			<label class="radio-inline">
				<input type="radio" name="enable_tooltips" id="enable-tooltips" value="true" <c:if test='${radom:getBooleanSetting("interface.tooltip.enable", true)}'>checked="checked"</c:if> > Включить всплывающие подсказки
			</label>
			<label class="radio-inline">
  				<input type="radio" name="enable_tooltips" id="enable-tooltips" value="false" <c:if test='${not radom:getBooleanSetting("interface.tooltip.enable", true)}'>checked="checked"</c:if> > Выключить всплывающие подсказки
			</label>
		</div>
		<div class="form-group">
			<label for="show-delay">Задержка перед появлением всплывающих подсказок: <span id="show-delay-value">${radom:getSetting("interface.tooltip.delay.show", "2")} ${radom:getDeclension(radom:getLongSetting("interface.tooltip.delay.show", 2), "секунда", "секунды", "секунд")}</span></label>
			<input type="text" class="form-control" id="show-delay" data-slider-min="0" data-slider-max="10" data-slider-step="1" data-slider-value='${radom:getSetting("interface.tooltip.delay.show", "2")}' />
		</div>
		<div class="form-group">
			<label for="hide-delay">Задержка перед исчезновением всплывающих подсказок: <span id="hide-delay-value">${radom:getSetting("interface.tooltip.delay.hide", "0")} ${radom:getDeclension(radom:getLongSetting("interface.tooltip.delay.show", 0), "секунда", "секунды", "секунд")}</span></label>
			<input type="text" class="form-control" id="hide-delay" data-slider-min="0" data-slider-max="10" data-slider-step="1" data-slider-value='${radom:getSetting("interface.tooltip.delay.hide", "0")}' />
		</div>		
		<hr/>
	</div>	
	<!--div class="tab-pane" id="profile">...</div>
	<div class="tab-pane" id="messages">...</div>
	<div class="tab-pane" id="settings">...</div-->
</div>
	
<script type="text/javascript">
	$(document).ready(function(){
		$("table.table").fixMe();

		$("form#multiple-sessions input[name=allow]").change(function() {
			$.radomJsonPost("/sharer/set_allow_multiple_sessions.json", $("form#multiple-sessions").serialize());

            if ($("form#multiple-sessions input[name=allow][value=false]:checked").length > 0) {
				$("table.sessions tr.other").fadeOut(function() {
					$("table.sessions tr.other").remove();
				});
			}
		});
		
		radomStompClient.subscribeToUserQueue("session_open", function(messageBody){
			showSession(messageBody);
		});
		
		radomStompClient.subscribeToUserQueue("session_close", function(messageBody){
			removeSession(messageBody);
		});
		
		$("table.sessions").on("click", "a.close-session", function(){
			var $link = $(this);
			bootbox.confirm("Вы уверены?", function(result){
				if (result) {
					$.radomJsonPost("/sharer/close_session.json", {
						login_log_entry_id : $link.attr("data-login-log-entry-id")
					}, function() {
						$link.parents("tr").slideUp(function(){
							$link.parents("tr").remove();
						});
					});
				}
			});
			return false;
		});
		
		$("a.close-other-sessions").click(function(){
			bootbox.confirm("Вы уверены?", function(result){
				if (result) {
					var $link = $(this);
					$.radomJsonPost("/sharer/close_other_sessions.json", {
						
					}, function() {
						$("table.sessions tr.other").slideUp(function(){
							$("table.sessions tr.other").remove();
						});					
					});
				}
			});
			return false;
		});
		
		function showSession(loginLogEntry) {
            var markup = "<tr class='other'><td>" + loginLogEntry.device + "</td><td>" + loginLogEntry.os + "</td><td>" + loginLogEntry.browser + "</td><td>" + loginLogEntry.ip + "</td><td><a class='btn btn-primary btn-xs close-session' href='#' data-login-log-entry-id='" + loginLogEntry.id + "'>Закрыть сессию</a></td></tr>";
			$("table.sessions").append(markup);
		}

		function removeSession(loginLogEntry) {
			var $tr = $("a[data-login-log-entry-id='" + loginLogEntry.id + "']").parents("tr");
			$tr.slideUp(function(){
				$tr.remove();
			});
		}
		
		$("input#new-email").emailInput();

		// TODO почемуто поломалась инициализация слайдера через jquery
//		$("input#show-delay").slider({
//			tooltip : "hide",
//			formatter: function(value) {
//				return value + " " + RadomUtils.getDeclension(value, "секунда", "секунды", "секунд");
//			}
//		});
		var sliderShowDelay = new Slider("input#show-delay", {
			tooltip : "hide",
			formatter: function(value) {
				return value + " " + RadomUtils.getDeclension(value, "секунда", "секунды", "секунд");
			}
		});
		
		$("input#show-delay").on("slide", function(slideEvt) {
			$("span#show-delay-value").text(slideEvt.value + " " + RadomUtils.getDeclension(slideEvt.value, "секунда", "секунды", "секунд"));
		});
		
		$("input#show-delay").on("slideStop", function(slideEvt) {
			RadomUtils.setSetting("interface.tooltip.delay.show", slideEvt.value, function() {
				RadomTooltipSettings.showDelay = slideEvt.value * 1000;
				$("[data-radom-tooltiped=true]").radomReTooltip();				
			});

		});

		// TODO почемуто поломалась инициализация слайдера через jquery
//		$("input#hide-delay").slider({
//			tooltip : "hide",
//			formatter: function(value) {
//				return value + " " + RadomUtils.getDeclension(value, "секунда", "секунды", "секунд");
//			}
//		});
		var sliderHideDelay = new Slider("input#hide-delay", {
			tooltip : "hide",
			formatter: function(value) {
				return value + " " + RadomUtils.getDeclension(value, "секунда", "секунды", "секунд");
			}
		});
		
		$("input#hide-delay").on("slide", function(slideEvt) {
			$("span#hide-delay-value").text(slideEvt.value + " " + RadomUtils.getDeclension(slideEvt.value, "секунда", "секунды", "секунд"));
		});
		
		$("input#hide-delay").on("slideStop", function(slideEvt) {
			RadomUtils.setSetting("interface.tooltip.delay.hide", slideEvt.value, function() {
				RadomTooltipSettings.hideDelay = slideEvt.value * 1000;
				$("[data-radom-tooltiped=true]").radomReTooltip();
			});
		});
		
		$("input#enable-tooltips").change(function() {
			var value = ($("input#enable-tooltips:checked").val() == "true");

			RadomUtils.setSetting("interface.tooltip.enable", value, function() {
				RadomTooltipSettings.enable = value;
				$("[data-radom-tooltiped=true]").radomReTooltip();
			});
		});
		
		$("select#show-email").change(function() {
			$.radomJsonPost("/sharer/setting/set.json", {
				key : "profile.show-email.mode",
				value : $("select#show-email").val() 
			}, function() {
				if ($("select#show-email").val() == "LISTS") {
					$("div#show-email-lists").slideDown();
				} else {
					$("div#show-email-lists").slideUp();
				}
			});
		});
		
		$("input[name=show_email_list]").change(function() {
			var ids = [];
			$.each($("input[name=show_email_list]:checked"), function(index, checkbox) {
				ids.push($(checkbox).val());
			});
			$.radomJsonPost("/sharer/setting/set.json", {
				key : "profile.show-email.lists",
				value : ids.join(",")
			}, function() {
				
			});
		});		
		
	});
	
	function deleteProfile() {
		bootbox.confirm("Подтвердите удаление профиля. В дальнейшем это действие нельзя будет отменить, профиль будет удален безвозвратно.", function(result){
			if (result) {
				$.radomJsonPost("/sharer/delete_profile.json", {}, function() {
					window.location = "/security/logout";
				});
			}
		});
	}	
	
	function changePassword() {
		if (!$("form#change-password input#new-password").val()) {
			bootbox.alert("Новый пароль не введен");
		} else if ($("form#change-password input#new-password").val() != $("form#change-password input#new-password-confirm").val()) {
			bootbox.alert("Новый пароль и его подтвеждение не совпадают");
		} else {
			$.radomJsonPost("/sharer/change_password.json", $("form#change-password").serialize(), function() {
				$("form#change-password input").val("");
				bootbox.alert("Пароль успешно изменен");
			});
		}
		return false;
	}
	
	function initChangeEmail() {
		$.radomJsonPost("/sharer/init_change_email.json", {}, function() {
			bootbox.alert("Проверочный код отправлен на Ваш старый e-mail");
		});
		return false;
	}
	
	function completeChangeEmail() {
		$.radomJsonPost("/sharer/complete_change_email.json", $("form#change-email").serialize(), function() {
			bootbox.alert("E-mail успешно изменен");
		});
		return false;
	}
	
</script>