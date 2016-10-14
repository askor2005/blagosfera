<%@ page language="java" contentType="text/html; charset=utf-8"
		 pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<script id="contacts-list-item-template" type="x-tmpl-mustache">
<div class="row sharer-item" data-sharer-id="{{contact.other.id}}">
		<h3>
		    {{^contact.other.verified}}
			<a data-title="Данные этого пользователя не проверены Регистратором и Система не гарантирует, что он тот за кого себя выдает" style="cursor: default;opacity: 0.5;" class="sender-link" href="#">{{contact.other.fullName}}</a>
			{{/contact.other.verified}}
	        {{#contact.other.verified}}
			<a class="sender-link" href="#">{{contact.other.fullName}}</a>
			{{/contact.other.verified}}
		</h3>
	</div>
</script>

<c:if test="${profileExists}">
<jsp:include page="../fields/fileField.jsp" />
<jsp:include page="../fields/addressFields.jsp" />
<style type="text/css">
	.modal .modal-body {
		max-height: 620px;
		overflow-y: auto;
	}

	div.tooltip {
		display: block;
	}

	div#profile-accordion div.tooltip {
		min-width : 200px;
	}

	.form-group[data-field-type=SELECT] .form-control-feedback {
		right : 15px;
	}

	a.form-control-input-link {
		font-size : 16px;
		text-decoration : none !important;
	}

	a.communications-link {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		font-size : 16px;
		text-decoration : none !important;
		display : block;
	}

	.has-warning .form-control {
		border-color: #F9C53F;
	}

	.has-warning .control-label {
		color: #F9C53F;
	}

	.has-warning .form-control-feedback {
		color: #F9C53F;
	}

	.has-warning .help-block {
		color: #F9C53F;
	}

	.has-warning .form-control:focus {
		border-color: #FAC15C;
		-webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 6px #F4EA30;
		box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 6px #F4EA30;
	}

	.panel-group input[type=hidden] + .panel {
		margin-top: 5px;
	}

	#save-link {
		position: relative;
		left: 50%;
		margin-left: -96px;
		padding: 10px 20px;
		font-size: 16px;
		display: none;
	}

	.help-block {
		font-size : 0.9em;
	}

	.table-timetable {
		margin-bottom : 20px;
		-webkit-touch-callout: none;
		-webkit-user-select: none;
		-khtml-user-select: none;
		-moz-user-select: none;
		-ms-user-select: none;
		user-select: none;
	}

	.table-timetable .cell-timetable {
		width : 21px;
		height : 20px;
		text-align : center;
		cursor : pointer !important;
	}

	.table-timetable .cell-timetable[data-selected='false'] {
		background-color : #f2dede;
	}

	.table-timetable .cell-timetable[data-selected='false']::after {
		content : "-";
	}

	.table-timetable .cell-timetable[data-selected='true'] {
		background-color : #dff0d8;
	}

	.table-timetable .cell-timetable[data-selected='true']::after {
		content : "+";
	}

	.table.table-timetable>thead>tr>th,
	.table.table-timetable>tbody>tr>th,
	.table.table-timetable>tfoot>tr>th,
	.table.table-timetable>thead>tr>td,
	.table.table-timetable>tbody>tr>td,
	.table.table-timetable>tfoot>tr>td {
		padding : 0;
	}

	.table.table-timetable td.day-timetable {
		padding : 0 5px 0 5px;
	}

	.table.table-timetable td.hour-timetable {
		position: relative;
		height: 41px;
		padding-left: 5px;
		overflow: hidden;
		left: -2px;
	}

	.table.table-timetable td.hour-timetable span.dash {
		padding-left : 5px;
	}

	.table.table-timetable td.hour-timetable input.changeHour {
		margin-left : 1px;
	}

	.table.table-timetable td.hour-timetable div {
		/*filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=0.083);
        -ms-filter: "progid:DXImageTransform.Microsoft.BasicImage(rotation=0.083)";
        -moz-transform: rotate(-90.0deg);
        -ms-transform: rotate(-90.0deg);
        -o-transform: rotate(-90.0deg);
        /!* -webkit-transform: rotate(-90.0deg); *!/
        /!* transform: rotate(-90.0deg); *!/*/
		position: absolute;
		left: 5px;
		top: 20px;
		font-size: 13px;
		width: 80px;
	}
	.sharerStatus {
		font-size: 14px;
		font-weight: bold;
	}
	.verificationBlock {
		margin-bottom: 5px;
	}
</style>

<div class="row" id="profile-page">
	<div class="col-xs-7 ">
		<h2>${profile.fullName}<span  class="dropdown" style="display:inline-block;
float: right;"> <c:if test="${viewerVerified}"><span id="sendContact" data-title="Отправить контакт" data-toggle="dropdown" class="glyphicon glyphicon-envelope" style="
  cursor: pointer;"></span></c:if><c:if test="${not viewerVerified}"><span id="sendContact" data-title="Отправить контакт (данная функция доступна только идентифицированным пользователям)" data-toggle="dropdown" class="glyphicon glyphicon-envelope disabled" style="
  cursor: pointer;opacity: 0.5;"></span></c:if><ul class="dropdown-menu">
			<li><a href="#" id="sendContactToEmail">Отправить себе на почту</a></li>
			<li class="disabled"><a href="#" disabled>Отправить себе на мобильный</a></li>
			<li><a href="#" id="sendContactToFriendEmail">Отправить другу на почту</a></li>
			<li class="disabled"><a href="#">Отправить на другой мобильный</a></li>
			<li><a id="downloadContact" href="#">Загрузить</a></li>
			<li><a id="downloadContactForWindows" href="#">Загрузить(для windows)</a></li>
		</ul></span></h2>
		<hr/>
		<h3>Профиль участника</h3>
		<c:if test="${not empty registratorLevel}">
			<div><span class="sharerStatus">${registratorLevel}.</span></div>
			<span class="text-muted"><c:if test="${not empty activeRegistrator && !activeRegistrator}">(Для активации функций регистратора необходимо заполнить поля офиса регистратора)</c:if></span>
		</c:if>
		<c:if test="${profile.verified}">
			<div><span class="sharerStatus">Идентифицированный пользователь Системы.</span></div>
		</c:if>
		<c:if test="${!profile.verified}">
			<div><span class="sharerStatus">Не идентифицированный пользователь Системы.</span></div>
		</c:if>
		<hr />
		<div class="form-group white-readonly">
			<label>Ссылка на страницу профиля</label> <input type="text"
															 class="form-control" id="profileLink" readonly="readonly" value="" />
		</div>
		<c:if test="${not empty inviter}">
			<div class="verificationBlock">
				<c:if test="${profile.sex}">Приглашён </c:if>
				<c:if test="${not profile.sex}">Приглашена </c:if>
				в систему <fmt:formatDate pattern="dd MMMM yyyy" value="${profile.registeredAt}" /> пользователем <br/>
				<a href="${inviter.link}">${inviterFullName}</a>
			</div>
		</c:if>
		<c:if test="${profile.verified}">
			<div class="verificationBlock">
				${identifiedWord} <fmt:formatDate value="${profile.verificationDate}" type="date" dateStyle="long" />
					${profileVerifierLevel}
				<br/><a href="${profileVerifierLink}">${profileVerifierFullName}</a>
			</div>
		</c:if>
		<c:if test="${empty inviter}">
			<div class="verificationBlock">
				Регистрация без приглашения
			</div>
		</c:if>

        <!--<div class="askor-editor">
            this is editable area 1
        </div>

        <div class="askor-editor">
            this is editable area 2
        </div>

        <script type="application/javascript">
            $(document).ready(function() {
                $('div.askor-editor').askorTinymce();
            });
        </script>-->
	</div>

    <div class="col-xs-5">
		<div class="row">
			<div class="col-xs-12" id="profile-avatar-wrapper">
				<c:if test="${not empty profile.avatar}">
					<a href="${profile.avatar}" id="avatar-original-link">
						<img class="img-thumbnail" src='${radom:resizeImage(profile.avatar, "c254")}' />
					</a>
				</c:if>
				<c:if test="${empty profile.avatar}">
					<a href="${radom:resizeImage(profile.avatar, "c254")}" id="avatar-original-link">
						<img class="img-thumbnail" src='${radom:resizeImage(profile.avatar, "c254")}' />
					</a>
				</c:if>
				<div class="image-bottom-links-wrapper" style="padding : 0 12px 5px 15px;">
					<c:if test="${isAllowSave}">
						<a href="#" id="avatar-upload-link" class="pull-left" style="line-height : 28px;">
							<span class="glyphicon glyphicon-open"></span>&nbsp;&nbsp;Загрузить фото
						</a>
					</c:if>
					<c:if test="${(sharer.id != profile.id)}">
						<a href='/chat/${profile.ikp}' class='glyphicon glyphicon-envelope go-to-chat-link pull-right' style="font-size : 24px; margin : 2px 0 0 0;"></a>
						<a href='#' onclick="return false" class='fa fa-money do-accounts-move-link pull-right' style="font-size : 28px; position : relative; top : 1px; margin-right : 3px;"></a>
					</c:if>
				</div>
			</div>
		</div>

		<c:if test="${sharer.id == profile.id}">
			<c:if test="${registrationRequest != null}">
				<div class="text-center" id="registration-request-block">
					Отправлена заявка
					<fmt:formatDate value="${registrationRequest.created}" pattern="dd.MM.yyyy HH:mm" />
					<br />
					регистратору
					<br />
					[<a href="${registrationRequest.registrator.link}">${registrationRequest_padeg3}</a>]
					<br />
					<a href='#' onclick="cancelRequest(${registrationRequest.id}); return false" class="btn btn-primary btn-block">Отменить заявку</a>
				</div>
			</c:if>
			<c:if test="${not profile.verified}">
				<div class="text-center" id="registrator-select-block" style="${(registrationRequest == null) ? '' : 'display: none'}">
					<div id="registrator-select-block-ok" style="${(profileFilling.percent == 100) ? '' : 'display: none'}">
						<a href="/registrator/select" class="btn btn-primary">
							Перейти к выбору Регистратора<br/> для идентификации
						</a>
						<div class="alert alert-info" role="alert" style="padding: 5px; margin: 10px;">
							Для подачи заявки на идентификацию перейдите на страницу выбора регистратора.
						</div>
					</div>
					<div id="registrator-select-block-not" style="${(profileFilling.percent < 100) ? '' : 'display: none'}">
						<div class="alert alert-info" role="alert" style="padding: 5px; margin: 10px;">
							Для выбора регистратора и подачи заявки на идентификацию необходимо заполнить профиль на 100%.
						</div>
					</div>
				</div>
			</c:if>
		</c:if>


		<c:if test="${sharer.id != profile.id}">
			<div class="row">
				<div class='col-xs-12 text-center' style="margin-top : 5px;">
					<c:if test="$ {profile.online}">Сейчас в сети</c:if>
					<c:if test="$ {not profile.online and not empty lastLoginLogEntry and not empty lastLoginLogEntry.logoutDate}">
						${radom:getSharerTextBySex(profile, "Был", "Была")} в сети <fmt:formatDate pattern="dd.MM.yyyy" value="${lastLoginLogEntry.logoutDate}" /> в <fmt:formatDate pattern="HH:mm" value="${lastLoginLogEntry.logoutDate}" />
					</c:if>
				</div>
			</div>
		</c:if>
	</div>

</div>
<hr />

<c:if test="${sharer.id != profile.id}">

	<c:forEach items="${fieldsGroups}" var="g">
		<c:if test="${g.internalName == 'PERSON_COMMUNICATIONS'}">
			<c:set var="filledFieldsCount" value="${0}"></c:set>
			<c:forEach items="${g.fields}" var="f">
				<c:if test="${not empty fieldValueMap.get(f).stringValue and fieldsStates[f].visible}">
					<c:set var="filledFieldsCount" value="${filledFieldsCount + 1}"></c:set>
				</c:if>
			</c:forEach>
			<c:if test="${filledFieldsCount > 0}">
				<div class="row">
					<c:forEach items="${g.fields}" var="f">
						<c:if test="${not empty fieldValueMap.get(f).stringValue and fieldsStates[f].visible}">
							<div class="col-xs-3">
								<c:choose>
									<c:when test="${f.internalName == 'WWW'}">
										<a data-field-internal-name="${f.internalName}" data-field-value="${fieldValueMap.get(f).stringValue}" class="communications-link" href="${radom:checkWebsitePrefix(fieldValueMap.get(f).stringValue)}"><i class="fa fa-cloud"></i> ${fieldValueMap.get(f).stringValue}</a>
									</c:when>
									<c:when test="${f.internalName == 'SKYPE'}">
										<a data-field-internal-name="${f.internalName}" data-field-value="${fieldValueMap.get(f).stringValue}" class="communications-link" href="skype:${fieldValueMap.get(f).stringValue}?call"><i class="fa fa-skype"></i> ${fieldValueMap.get(f).stringValue}</a>
									</c:when>
									<c:when test="${f.internalName == 'HOME_TEL'}">
										<a data-field-internal-name="${f.internalName}" data-field-value="${fieldValueMap.get(f).stringValue}" class="communications-link" href="callto:${fieldValueMap.get(f).stringValue}"><i class="fa fa-phone"></i> ${fieldValueMap.get(f).stringValue}</a>
									</c:when>
									<c:when test="${f.internalName == 'MOB_TEL'}">
										<a data-field-internal-name="${f.internalName}" data-field-value="${fieldValueMap.get(f).stringValue}" class="communications-link" href="callto:${fieldValueMap.get(f).stringValue}"><i class="fa fa-mobile"></i> ${fieldValueMap.get(f).stringValue}</a>
									</c:when>
									<c:otherwise>
										<label>${fieldValueMap.get(f).stringValue}</label>
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>
					</c:forEach>
				</div>
			</c:if>
		</c:if>
	</c:forEach>

	<hr/>
</c:if>

<c:if test="${sharer.id != profile.id}">
	<div class="row">
		<div class="edit-contact" id="edit-contact-new-new"
				<c:if
						test="${contact != null}"> style="display : none;" </c:if>>
			<div class="col-xs-12">
				<p>Этот участник не в списке Ваших контактов</p>
				<hr />
			</div>

			<c:if test="${contactsGroups.size() > 0}">
				<div class="col-xs-6">
					<div class="dropdown">
						<a class="btn btn-primary btn-sm btn-block" id="add-label-new-new" role="button" data-toggle="dropdown" data-target="#" href="#">
							Добавить в список контактов &nbsp; <span class="caret"></span>
						</a>
						<ul  class="dropdown-menu" role="menu" aria-labelledby="add-label-new-new">
							<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-id="0">Список по умолчанию</a></li>
							<c:forEach items="${contactsGroups}" var="g">
								<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-id="${g.id}" data-group-name="${g.name}">${g.name}</a></li>
							</c:forEach>
						</ul>
					</div>
				</div>
			</c:if>
			<c:if test="${contactsGroups.size() == 0}">
				<div class="col-xs-6">
					<a class="btn btn-sm btn-primary btn-block add-group-select-link" data-group-id="0">Добавить в список контактов</a>
				</div>
			</c:if>

		</div>

		<div class="edit-contact" id="edit-contact-new-accepted"
				<c:if
						test="${!((contact != null) && (contact.sharerStatus == 'NEW') && (contact.otherStatus == 'ACCEPTED'))}"> style="display : none;" </c:if>>
			<div class="col-xs-12">
				<p>
					Вы получили заявку на добавление в список контактов от этого
					участника   <span id="contactGroupsGot">
        <c:if test="${(contact == null) || (contact.contactsGroups.size() == 0)}">
		<span
				class="group-color-example group-color-example-0">
						Список по умолчанию</span>
		</c:if>
		 <c:if test="${(contact != null) && (contact.contactsGroups.size() != 0)}">
			 <c:forEach items="${contact.contactsGroups}" var="gr">
		<span

				class="group-color-example group-color-example-${gr.color}">
				${gr.name}</span>
			 </c:forEach>

		 </c:if>
		</span>
				</p>
				<hr />
			</div>

			<c:if test="${contactsGroups.size() > 0}">
				<div class="col-xs-6">
					<div class="dropdown">
						<a class="btn btn-primary btn-sm btn-block" id="add-label-new-new" role="button" data-toggle="dropdown" data-target="#" href="#">
							Добавить в список контактов &nbsp; <span class="caret"></span>
						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="add-label-new-new">
							<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-id="0">Список по умолчанию</a></li>
							<c:forEach items="${contactsGroups}" var="g">
								<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-name="${g.name}" data-group-id="${g.id}">${g.name}</a></li>
							</c:forEach>
						</ul>
					</div>
				</div>
			</c:if>
			<c:if test="${contactsGroups.size() == 0}">
				<div class="col-xs-6">
					<a class="btn btn-sm btn-primary btn-block add-group-select-link" data-group-id="0">Добавить в список контактов</a>
				</div>
			</c:if>
			<div class="col-xs-6">
				<div class="form-group">
					<a href="#" id="deleteContactLink" class="btn btn-block btn-default">Отклонить
						заявку <span class="glyphicon glyphicon-remove"></span>
					</a>
				</div>
			</div>
		</div>

		<div class="edit-contact" id="edit-contact-accepted-new"
				<c:if
						test="${!((contact != null) && (contact.sharerStatus == 'ACCEPTED') && (contact.otherStatus == 'NEW'))}"> style="display : none;" </c:if>>
			<div class="col-xs-12">
				<p>
					Вы отправили заявку на добавление в список контактов этому участнику   <span id="contactGroupsSent">
        <c:if test="${(contact == null) || (contact.contactsGroups.size() == 0)}">
		<span
				class="group-color-example group-color-example-0">
						Список по умолчанию</span>
		</c:if>
		 <c:if test="${(contact != null) && (contact.contactsGroups.size() != 0)}">
			 <c:forEach items="${contact.contactsGroups}" var="gr">
		<span

				class="group-color-example group-color-example-${gr.color}">
				${gr.name}</span>
			 </c:forEach>

		 </c:if>
		</span>

				</p>
				<hr />
			</div>
			<c:if test="${contactsGroups.size() > 0}">
				<div class="col-xs-6">
					<div class="dropdown">
						<a class="btn btn-primary btn-sm btn-block" id="add-label-new-new" role="button" data-toggle="dropdown" data-target="#" href="#">
							Добавить в список контактов &nbsp; <span class="caret"></span>
						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="add-label-new-new">
							<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-id="0">Список по умолчанию</a></li>
							<c:forEach items="${contactsGroups}" var="g">
								<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link"  data-group-id="${g.id}"  data-group-name="${g.name}">${g.name}</a></li>
							</c:forEach>
						</ul>
					</div>
				</div>
			</c:if>
			<div class="col-xs-6">
				<div class="form-group">
					<a href="#" id="deleteContactLink" class="btn btn-block btn-default">Отменить
						заявку <span class="glyphicon glyphicon-remove"></span>
					</a>
				</div>
			</div>
		</div>

		<div class="edit-contact" id="edit-contact-accepted-accepted"
				<c:if
						test="${!((contact != null) && (contact.sharerStatus == 'ACCEPTED') && (contact.otherStatus == 'ACCEPTED'))}"> style="display : none;" </c:if>>
			<div class="col-xs-12">
				<p>
					Этот участник в списке Ваших контактов   <span id="contactGroups">
        <c:if test="${(contact == null) || (contact.contactsGroups.size() == 0)}">
		<span
				class="group-color-example group-color-example-0">
						Список по умолчанию</span>
		</c:if>
		 <c:if test="${(contact != null) && (contact.contactsGroups.size() != 0)}">
			 <c:forEach items="${contact.contactsGroups}" var="gr">
		<span

				class="group-color-example group-color-example-${gr.color}">
				${gr.name}</span>
			 </c:forEach>

		 </c:if>
		</span>
				</p>
				<hr />
			</div>
			<c:if test="${contactsGroups.size() > 0}">
				<div class="col-xs-6">
					<div class="dropdown">
						<a class="btn btn-primary btn-sm btn-block" id="add-label-new-new" role="button" data-toggle="dropdown" data-target="#" href="#">
							Перенести в списки контактов &nbsp; <span class="caret"></span>
						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="add-label-new-new">
							<c:forEach items="${contactsGroups}" var="g">
								<c:if test="${not contact.contactsGroups.contains(g)}">
								<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-id="${g.id}" data-group-name="${g.name}">${g.name}</a></li>
								</c:if>
								<c:if test="${contact.contactsGroups.contains(g)}">
									<li role="presentation"><a role="menuitem" href="#" class="delete-group-select-link" data-group-id="${g.id}" data-group-name="${g.name}">${g.name}<i class="fa fa-check"></i></a></li>
								</c:if>
							</c:forEach>
						</ul>
					</div>
				</div>
			</c:if>
			<div class="col-xs-6">
				<div class="form-group">
					<a href="#" id="deleteContactLink" class="btn btn-block btn-default">Удалить
						из списка контактов <span class="glyphicon glyphicon-remove"></span>
					</a>
				</div>
			</div>
		</div>
	</div>
	<hr/>
</c:if>

<c:if test="${profile.id != sharer.id}">
	<label>
		<a href="${profile.link}/contacts">Контакты (всего ${contactsCount})</a>
	</label>
	<br/><br/>
	<div class="row">
		<c:forEach items="${randomContacts}" var="c">
			<div class="col-xs-2">
				<a href="${c.other.link}" class="avatar-link">
					<img style="width : 84px; height : 84px;" class="img-thumbnail tooltiped-avatar" data-sharer-ikp="${c.other.ikp}" src='${radom:resizeImage(c.other.avatar, "c84")}' />
				</a>
			</div>
		</c:forEach>
	</div>
	<hr/>
</c:if>

<c:if test="${(not empty communities) and (profile.id != sharer.id)}">
	<div class="form-group sharer-communities-list" id="sharer-communities-list">
		<label>Состоит в ${communities.size()} ${radom:getDeclension(communities.size(), "объединении", "объединениях", "объединениях")}</label>
		<br/>
		<c:forEach items="${communities}" var="c">
			<a class="btn btn-link" href="${c.link}">${c.name}</a>
		</c:forEach>
	</div>
	<div class="show-all-sharer-communities-wrapper">
		<a href="#" class="show-all-sharer-communities" data-container-id="sharer-communities-list">Показать все</a>
	</div>
	<hr />
</c:if>

<c:if test="${(profile.id == sharer.id)}">
	<c:if test="${(not empty creatorCommunities)}">
		<div class="form-group sharer-communities-list" id="creator-communities-list">
			<c:if test="${empty memberCommunities}">
				<div id="profile-accordion-anchor"></div>
			</c:if>
			<label>Вы организовали ${creatorCommunities.size()} ${radom:getDeclension(creatorCommunities.size(), "объединение", "объединения", "объединений")}</label>
			<br/>
			<c:forEach items="${creatorCommunities}" var="c">
				<c:if test="${not empty c.link}">
					<a class="btn btn-link" href="${c.link}">${c.name}</a>
				</c:if>
				<c:if test="${empty c.link}">
					<a class="btn btn-link" href="/group/${c.id}">${c.name}</a>
				</c:if>
			</c:forEach>
		</div>
		<div class="show-all-sharer-communities-wrapper">
			<a href="#" class="show-all-sharer-communities" data-container-id="creator-communities-list">Показать все</a>
		</div>
		<hr />
	</c:if>
	<c:if test="${(not empty memberCommunities)}">
		<div class="form-group sharer-communities-list" id="member-communities-list">
			<div id="profile-accordion-anchor"></div>
			<label>Вы состоите в ${memberCommunities.size()} ${radom:getDeclension(memberCommunities.size(), "объединении", "объединениях", "объединениях")}</label>
			<br/>
			<c:forEach items="${memberCommunities}" var="c">
				<c:if test="${not empty c.link}">
					<a class="btn btn-link" href="${c.link}">${c.name}</a>
				</c:if>
				<c:if test="${empty c.link}">
					<a class="btn btn-link" href="/group/${c.id}">${c.name}</a>
				</c:if>
			</c:forEach>
		</div>
		<div class="show-all-sharer-communities-wrapper">
			<a href="#" class="show-all-sharer-communities" data-container-id="member-communities-list">Показать все</a>
		</div>
		<hr />
	</c:if>
</c:if>

<c:if test="${sharer.id == profile.id}">
	<div class="panel panel-info" id="profile-filling-info-panel" <c:if test="${profileFilling.percent > profileFilling.treshold or profileFillingInfoClosed}">style="display : none;"</c:if> >
		<div class="panel-heading">
			<h4 class="panel-title">Информация по заполнению профиля</h4>
		</div>
		<div class="panel-body">
			<p>В полях разделов ниже следует ввести данные о себе. Для начала работы достаточно ввести фамилию, имя, отчество и загрузить фото. Остальные данные можно ввести позднее, однако в дальнейшем следует заполнить как можно больше полей.</p>
			<p>По мере заполнения профиля расчитывается процент его заполнения.</p>
			<p>Ряд полей являются обязательными и без них расчт не производится. Если оставить такое поле пустым, оно будет выделено <span class="text-danger"> красным цветом и отмечено знаком <i class="glyphicon glyphicon-remove"></i> </span>.</p>
			<p>Некоторые поля не являются обязательными, но также участвуют в расчете процента заполнения. Такие поля, будучи пустыми, выделяются <span class="text-warning"> желтым цветом и знаком <i class="glyphicon glyphicon-warning-sign"></i> </span>.</p>
			<p>Поля, не участвующие в расчете процента заполнения, не будут никак выделяться, даже если оставить их пустыми. </p>
		</div>
		<div class="panel-footer text-right">
			<a class="btn btn-info btn-xs" href="#" id="profile-filling-info-close-link">Закрыть</a>
		</div>
	</div>
</c:if>


<ul class="nav nav-tabs nav-justified" id="fieldsTabs">
	<li role="presentation" class="active"><a data-toggle="tab" href="#registration-data">Регистрационные данные</a></li>
	<li role="presentation"><a data-toggle="tab" href="#verification-data">Идентификационные данные</a></li>
	<c:if test="${not empty registratorLevel}"><li role="presentation"><a data-toggle="tab" href="#registrator-data">Реквизиты Регистратора</a></li></c:if>
	<li role="presentation"><a data-toggle="tab" href="#additional-data">Доп. данные</a></li>
</ul>

<br>

<form role="form" id="fields-form" autocomplete="off">
	<input type="hidden" name="sharer_id" value="${profile.id}" />
	<div class="tab-content">
		<div id="registration-data" class="tab-pane fade in active">
			<div class="panel-group" id="profile-accordion">

				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title">
							Учетные данные
							<a data-toggle="tooltip" data-placement="left"
							   title='Скрыть блок' href="#"
							   onclick="return slideBlock($(this));"
							   class="glyphicon glyphicon-arrow-up hidden-group-eye"></a>
						</h4>
					</div>

					<div id="collapse-profile-0" class="panel-collapse collapse in">
						<div class="panel-body">
							<form role="form" autocomplete="off">
								<c:if test="${showEmail}">
									<div class="form-group">
										<label>E-mail</label> <input type="text" class="form-control" readonly="readonly" value="${profile.email}" data-changes-checker-ignore="true"/>
									</div>
								</c:if>

								<c:if test="${sharer.id == profile.id}">
									<c:if test="${not empty inviter}">
										<div class="form-group">
											<label>Участник, который пригласил в систему</label>
											<div class="input-group">
												<input type="text" class="form-control" readonly="readonly" value="${inviter.fullName}" data-changes-checker-ignore="true" />
												<span class="input-group-addon">
													<a href="/sharer/${inviter.ikp}">Смотреть профиль</a>
												</span>
											</div>
										</div>
									</c:if>
								</c:if>

								<div class="form-group">
									<label>ЛИК</label> <input type="text" class="form-control" readonly="readonly" value="${profile.ikp}" data-changes-checker-ignore="true" />
									<span class="help-block">Номер Личного Информационного Кабинета</span>
								</div>

								<c:if test="${showRegisteredAt}">
									<div class="form-group">
										<label>Дата регистрации</label>
										<input type="text" class="form-control"	readonly="readonly" value="<fmt:formatDate pattern="dd.MM.yyyy" value="${profile.registeredAt}" />" data-changes-checker-ignore="true" />
									</div>
								</c:if>

							</form>
						</div>
					</div>
				</div>
			</div>

			<c:set var="fieldsGroups" value="${registrationFieldsGroups}" scope="request" />
			<c:set var="fieldsStates" value="${registrationFieldsStates}" scope="request" />
			<t:insertTemplate template="/views/sharer/fieldsGroups.jsp" />
		</div>
		<div id="verification-data" class="tab-pane fade">
			<c:set var="fieldsGroups" value="${verificationFieldsGroups}" scope="request" />
			<c:set var="fieldsStates" value="${verificationFieldsStates}" scope="request" />
			<t:insertTemplate template="/views/sharer/fieldsGroups.jsp" />
		</div>
		<c:if test="${not empty registratorLevel}">
			<div id="registrator-data" class="tab-pane fade">
				<c:set var="fieldsGroups" value="${registratorFieldsGroups}" scope="request" />
				<c:set var="fieldsStates" value="${registratorFieldsStates}" scope="request" />
				<t:insertTemplate template="/views/sharer/fieldsGroups.jsp" />
			</div>
		</c:if>
		<div id="additional-data" class="tab-pane fade">
			<c:set var="fieldsGroups" value="${additionalFieldsGroups}" scope="request" />
			<c:set var="fieldsStates" value="${additionalFieldsStates}" scope="request" />
			<t:insertTemplate template="/views/sharer/fieldsGroups.jsp" />
		</div>
	</div>
</form>

<c:if test="${showCertificationFiles and fn:length(certificationFilesNames) > 0}">
	<hr>
	<div class="form-group">
		<label>Файлы идентификации</label>
		<ul>
			<c:forEach items="${certificationFilesNames}" var="n">
				<a class="btn btn-link" href="/sharer/${profile.ikp}/certification-files/${n}">${n}</a>
			</c:forEach>
		</ul>
	</div>
</c:if>

<hr>
<div class="form-group">
	<c:if test="${isAllowSave}">
		<a class="btn btn-primary btn-sm" href="#" id="save-link" onclick="return save();">Сохранить профиль</a>
	</c:if>
</div>
<div class="form-group">

	<a class="btn btn-primary btn-sm" href="#" id="set-verified-link" <c:if test="${not isAllowSetVerified}">style="display : none;"</c:if> >Идентифицировать профиль</a>
	<a class="btn btn-primary btn-sm" href="#" id="write-card-link" <c:if test="${not isAllowWriteCard}">style="display : none;"</c:if> >Записать карту</a>
	<a class="btn btn-primary btn-sm" href="#" id="save-finger-link" <c:if test="${not isAllowSaveFinger}">style="display : none;"</c:if> >Записать отпечатки</a>
</div>

<script type="text/javascript">
	var contactsListItemTemplate = $('#contacts-list-item-template').html();
	var groups = null;
	var factCountryListItem = null;
	var regCountryListItem = null;
	var registratorCountryListItem = null;
	<c:if test="${not empty factCountryListItem}">
	factCountryListItem = {
		text: "${factCountryListItem.text}",
		code: "${factCountryListItem.mnemoCode}"
	};
	</c:if>
	<c:if test="${not empty regCountryListItem}">
	regCountryListItem = {
		text: "${regCountryListItem.text}",
		code: "${regCountryListItem.mnemoCode}"
	};
	</c:if>
	<c:if test="${not empty registratorCountryListItem}">
	registratorCountryListItem = {
		text: "${registratorCountryListItem.text}",
		code: "${registratorCountryListItem.mnemoCode}"
	};
	</c:if>

	/*
	 Инициализация таблиц с расписанием
	 */
	function getContactMarkup(contact) {
		var currentSharerId = "${sharer.id}";
		var model = {};
		model.contact = contact;
		var rendered = Mustache.render(contactsListItemTemplate, model);

		var $row = $(rendered);
		if (contact.other.verified) {
        $row.find("a.sender-link").click(function(){
			$("#sendContactModal").modal("hide");
			$.radomJsonGet("/contacts/send/to/email.json?userId=${profile.id}&receiverId="+contact.other.id,{} ,function(response) {
				if(response.result === 'success') {
					bootbox.alert("Контакт отправлен Вашему другу на почту!");
				}
			});
		});
		}
		$row.find('a.sender-link').radomTooltip({
			placement : "top",
			container : "body"
		});
		return $row;
	}
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
	function getParams() {
		var params = {};
		params.query = $("#search-input").val();
		params.group_id = -1;
		params.order_by = "searchString";
		params.asc = "true";

		return params;
	}
	function initSearchInput($input, callback) {
		$searchInput = $input;
		$searchInput.val("");
		$input.keyup(function () {

			var timeout = $input.data("timeout");
			if (timeout) {
				clearTimeout(timeout);
			}
			timeout = setTimeout(function () {
				var newValue = $input.val();
				var oldValue = $input.data("old-value");
				if (newValue != oldValue) {
					if ((newValue.length >= 4) || (newValue.length == 0)) {
						$input.data("old-value", newValue);
						callback(newValue);
					}
				}
			}, 300);
			$input.data("timeout", timeout);
		});
	}
	function initScrollListener() {
		$("#contacts").empty();
		ScrollListener.init("/contacts/list.json", "post", getParams, function() {
			$(".list-loader-animation").fadeIn();
		}, function(response) {
			var $list = $("#contacts");
			$.each(response, function(index, contact) {
				$list.append(getContactMarkup(contact));
				$list.append("<hr style='margin-top : 5px;' />");
			});
			if ($("div.row.sharer-item").length == 0) {

				if (($("#search-input").val() == "") && ($("select#filter-select").val() == 0) && ($("select#group-select").val() == -1)) {
					$("div#contacts-empty").show();
					$("div#contacts-not-found").hide();
				} else {
					$("div#contacts-not-found").show();
					$("div#contacts-empty").hide();
				}
			} else {
				$("div#contacts-not-found").hide();
				$("div#contacts-empty").hide();
			}
			$(".list-loader-animation").fadeOut();
		}, null);
	}
	$(document).ready(function() {
		$("#sendContact").radomTooltip({
			placement : "top",
			container : "body"
		});
		$("#sendContactToEmail").click(function() {
			$.radomJsonGet("/contacts/send/to/email.json?userId=${profile.id}",{} ,function(response) {
				if(response.result === 'success') {
					bootbox.alert("Контакт отправлен Вам на почту!");
				}
			});
		});
		$("#sendContactToFriendEmail").click(function() {
			initSearchInput($("#search-input"), function() {
				initScrollListener();
			});
			initScrollListener();
			$("#sendContactModal").modal("show");
		});
		$("#downloadContact").click(function() {
			var url = "/contacts/download?userId=${profile.id}";
			$("#downloadIframe").attr("src",url);
		});
		$("#downloadContactForWindows").click(function() {
			var url = "/contacts/download/windows?userId=${profile.id}";
			$("#downloadIframe").attr("src",url);
		});
		$("#contactSendTo").selectpicker("val",null);
		/*$("#sendContact").click(function(){
			$("#contactSendTo").selectpicker("val",null);
			$("#contactSendTo").selectpicker("refresh");
			$("#sendContactModal").modal("show");
		});*/
        $(".table-timetable[data-editable='true']").each(function(index, table) {
			var $table = $(table);
			$table.find("input[type=checkbox]").attr("data-changes-checker-ignore", true);
			$("input[type='hidden']").attr("data-changes-checker-ignore", true);

			$.each($table.find("td.cell-timetable"), function(index, cell) {
				var $cell = $(cell);
				if($cell.attr("data-selected") === "true") {
					$cell.attr("default-value", true);
				} else {
					$cell.attr("default-value", false);
				}
			});

			function updateInput() {

				var daysCounts = [0,0,0,0,0,0,0];
				var hoursCounts = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
				var data = {};
				data.days = [
					[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],
					[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],
					[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],
					[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],
					[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],
					[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],
					[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]
				];

				$.each($table.find("td.cell-timetable"), function(index, cell) {
					var $cell = $(cell);
					var selected = $cell.attr("data-selected") === "true";
					if (selected) {
						var day = parseInt($cell.attr("data-day"));
						var hour = parseInt($cell.attr("data-hour"));
						daysCounts[day]++;
						hoursCounts[hour]++;
						data.days[day][hour] = true;
					}
				});

				$.each($table.find("input[type=checkbox]"), function(index, input) {
					var $input = $(input);
					var day = parseInt($input.attr("data-day"));
					var hour = parseInt($input.attr("data-hour"));

					if (!isNaN(day)) {
						$input.prop("checked", daysCounts[day] == 24);
					}
					if (!isNaN(hour)) {
						$input.prop("checked", hoursCounts[hour] == 7);
					}
				});

				var $input = $("input[type='hidden'][data-field-id='" + $table.attr("data-field-id") + "']");
				$input.val(JSON.stringify(data)).change();
				var $block = $input.closest('.panel-body');
				checkBlockInputs($block);
			}

			function changeCell($cell) {
				var day = $cell.attr("data-day");
				var hour = $cell.attr("data-hour");
				var selected = $cell.attr("data-selected") === "true";
				selected = !selected;
				$cell.attr("data-selected", selected);
				updateInput();
			}

			$table.find(".cell-timetable").mousedown(function() {
				var $cell = $(this);
				var selected = $cell.attr("data-selected") === "true";
				selected = !selected;

				$table.data("drag", true);
				$table.data("dragAction", selected);
				changeCell($cell);
			});

			$table.find(".cell-timetable").mouseup(function() {
				$table.data("drag", false);
				$table.data("dragAction", null);
			});

			$table.find(".cell-timetable").mouseover(function() {
				if ($table.data("drag")) {
					var $cell = $(this);
					var selected = $cell.attr("data-selected") === "true";
					var dragAction = $table.data("dragAction");
					if (selected != dragAction) {
						changeCell($cell);
					}
				}
			});

			$table.mouseleave(function() {
				$table.data("drag", false);
			});

			$table.find("input[type=checkbox][data-day]").click(function() {
				var checked = $(this).prop("checked");
				var day = $(this).attr("data-day");
				//alert(day + " " + checked);
				$table.find(".cell-timetable[data-day=" + day + "]").attr("data-selected", checked);
				updateInput();
			});

			$table.find("input[type=checkbox][data-hour]").click(function() {
				var checked = $(this).prop("checked");
				var hour = $(this).attr("data-hour");
				//alert(hour + " " + checked);
				$table.find(".cell-timetable[data-hour=" + hour + "]").attr("data-selected", checked);
				updateInput();
			});

		});
	});

	$(document).ready(function() {
		$('#sendContactModal').on('shown.bs.modal', function () {
			$('#search-input').focus();
		})
		/*window.onload=function(){
		 var $div = $('.panel-body');
		 var $select = $div.find('select');
		 $.each($select, function (key, select) {
		 $(select).attr('data-field-value', $(select, "option:selected").val());
		 });
		 }*/

		$("a#avatar-original-link").fancybox();
		//$("a#avatar-original-link").click(function() {
		//	a.fancybox();
		//	return false;
		//});

		$("a#avatar-upload-link").click(function() {
			SharerUploadDialog.show();
			return false;
		});

		$("a#set-verified-link").click(function() {
			$.radomSharerCertification({
				sharerId : "${profile.id}",
				successCallback : function(response) {
					if (response.result == "error") {

					} else {
						if (response.isAllowSave) {
							$("a#save-link").show();
						} else {
							$("a#save-link").hide();
						}
						if (response.isAllowSaveAndSetVerified) {
							$("a#save-and-set-verified-link").show();
						} else {
							$("a#save-and-set-verified-link").hide();
						}
						if (response.isAllowSetVerified) {
							$("a#set-verified-link").show();
						} else {
							$("a#set-verified-link").hide();
						}
					}
				}
			});
			return false;
		});

		$("a#write-card-link").click(function() {
			$.radomSharerCertification({
				sharerId : "${profile.id}",
				stages : ["card"]
			});
			return false;
		});

		$("a#save-finger-link").click(function() {
			$.radomSharerCertification({
				sharerId : "${profile.id}",
				stages : ["fingers"]
			});
			return false;
		});

	});

	function copyPersonAddressRegistrationFromActual() {
		$('#COUNTRY_CL select').val($('#FCOUNTRY_CL select').val()).change().blur();
		//$('[data-field-name="CITY_TL"]').val($('[data-field-name="FCITY_TL"]').val()).change();
		//$('[data-field-name="STREET"]').val($('[data-field-name="FSTREET"]').val()).change();
		//$('[data-field-name="HOUSE"]').val($('[data-field-name="FHOUSE"]').val()).change();
		//$('[data-field-name="SUBHOUSE"]').val($('[data-field-name="FSUBHOUSE"]').val()).change();
		if (regAddressRu) {
			setKladrValueWithCheck($('[data-field-name="REGION_RL"]'), $('[data-field-name="FREGION_RL"]').val(), function () {
				setKladrValueWithCheck($('[data-field-name="AREA_AL"]'), $('[data-field-name="FAREA_AL"]').val(), function () {
					setKladrValueWithCheck($('[data-field-name="CITY_TL"]'), $('[data-field-name="FCITY_TL"]').val(), function () {
						setKladrValueWithCheck($('[data-field-name="STREET"]'), $('[data-field-name="FSTREET"]').val(), function () {
							setKladrValueWithCheck($('[data-field-name="HOUSE"]'), $('[data-field-name="FHOUSE"]').val(), function () {
								setKladrValueWithCheck($('[data-field-name="SUBHOUSE"]'), $('[data-field-name="FSUBHOUSE"]').val(), function () {

								});
							});
						});
					});
				});
			});
		}
		else {
			$('[data-field-name="REGION_RL"]').val($('[data-field-name="FREGION_RL"]').val()).blur();
			$('[data-field-name="AREA_AL"]').val($('[data-field-name="FAREA_AL"]').val()).blur();
			$('[data-field-name="CITY_TL"]').val($('[data-field-name="FCITY_TL"]').val()).blur();
			$('[data-field-name="STREET"]').val($('[data-field-name="FSTREET"]').val()).blur();
			$('[data-field-name="HOUSE"]').val($('[data-field-name="FHOUSE"]').val()).blur();
			$('[data-field-name="SUBHOUSE"]').val($('[data-field-name="FSUBHOUSE"]').val()).blur();
		}
		$('[data-field-name="ROOM"]').val($('[data-field-name="FROOM"]').val()).change().blur();
		$('[data-field-name="POSTAL_CODE"]').val($('[data-field-name="FPOSTAL_CODE"]').val()).change().blur();
		var $block = $('[data-field-name="POSTAL_CODE"]').closest('.panel-body');
		checkBlockInputs($block);
		$.each($("div.form-group"), function(index, div) {
			checkFormGroup($(div));
		});
		return false;
	}

	function copyPersonAddressActualFromRegistration() {
		$('#FCOUNTRY_CL select').val($('#COUNTRY_CL select').val()).change().blur();
		if (actualAddressRu) {
			setKladrValueWithCheck($('[data-field-name="FREGION_RL"]'), $('[data-field-name="REGION_RL"]').val(), function () {
				setKladrValueWithCheck($('[data-field-name="FAREA_AL"]'), $('[data-field-name="AREA_AL"]').val(), function () {
					setKladrValueWithCheck($('[data-field-name="FCITY_TL"]'), $('[data-field-name="CITY_TL"]').val(), function () {
						setKladrValueWithCheck($('[data-field-name="FSTREET"]'), $('[data-field-name="STREET"]').val(), function () {
							setKladrValueWithCheck($('[data-field-name="FHOUSE"]'), $('[data-field-name="HOUSE"]').val(), function () {
								setKladrValueWithCheck($('[data-field-name="FSUBHOUSE"]'), $('[data-field-name="SUBHOUSE"]').val(), function () {

								});
							});
						});
					});
				});
			});
		}
		else {
			$('[data-field-name="FREGION_RL"]').val($('[data-field-name="REGION_RL"]').val()).change().blur();
			$('[data-field-name="FAREA_AL"]').val($('[data-field-name="AREA_AL"]').val()).change().blur();
			$('[data-field-name="FCITY_TL"]').val($('[data-field-name="CITY_TL"]').val()).change().blur();
			$('[data-field-name="FSTREET"]').val($('[data-field-name="STREET"]').val()).change().blur();
			$('[data-field-name="FHOUSE"]').val($('[data-field-name="HOUSE"]').val()).change().blur();
			$('[data-field-name="FSUBHOUSE"]').val($('[data-field-name="SUBHOUSE"]').val()).change().blur();
		}
		//$('[data-field-name="FCITY_TL"]').val($('[data-field-name="CITY_TL"]').val()).change();
		//$('[data-field-name="FHOUSE"]').val($('[data-field-name="HOUSE"]').val()).change();
		//$('[data-field-name="FSUBHOUSE"]').val($('[data-field-name="SUBHOUSE"]').val()).change();
		$('[data-field-name="FROOM"]').val($('[data-field-name="ROOM"]').val()).change().blur();
		$('[data-field-name="FPOSTAL_CODE"]').val($('[data-field-name="POSTAL_CODE"]').val()).change().blur();

        var $block = $('[data-field-name="FPOSTAL_CODE"]').closest('.panel-body');

        checkBlockInputs($block);

        $.each($("div.form-group"), function(index, div) {
			checkFormGroup($(div));
		});

        return false;
	}
	//установка значения с асинхронной проверкой
	function setKladrValueWithCheck($input,value,callback){
		if ((!value) || (value === "")) {
			$input.val(value).change();
			callback();
		}
		else {
			$input.on('kladr_check', function () {
						$input.off('kladr_check');
						if (callback)
							callback();
					}
			);
			$input.val(value).change();
		}

	}

	function initDateInputs() {
		$.each($("input[data-field-type=DATE]"), function(index, input){
			var $input = $(input);
			if($input.attr("readonly") != "readonly") {
				$input.radomDateInput({
					startView : 2
				});
			}
		});
	}

	function updateFieldDataValue(){
		var $div = $('.panel-body'),
				$input = $div.find('input'),
				$select = $div.find('select');

		if (${sharer.id == profile.id}){
			$.each($select, function (key, select) {
				$(select).attr('data-field-value', $(select, "option:selected").val());
			});

			$.each($input, function (key, input) {
				$(input).attr('data-field-value', $(input).val());
			});
		}
		$('.block-save-link').remove();
		$('#save-link').hide();
	}

	var profileId = "${profile.id}";
	var sharerId = "${sharer.id}";

	function save() {
		// апдейтим все поля адреса перед сохранением.
		/*for(var groupNameI in updateGeoPositionFunctions) {
		 updateGeoPositionFunctions[groupNameI]();
		 }*/

		$("a#save-link").attr("disabled", "disabled");
		$("a#save-and-set-verified-link").attr("disabled", "disabled");
		$("a#set-verified-link").attr("disabled", "disabled");

		$.ajax({
			type : "post",
			dataType : "json",
			data : $("form#fields-form").serialize(),
			url : "/sharer/save.json",
			success : function(response) {
				if (response.result == "error") {
					bootbox.alert(response.message ? response.message : "Ошибка при сохранении профиля");
				} else {
					if (sharerId == profileId) {
						refreshProfileFilling();
						if (parseInt(response.profileFilling.percent) < parseInt(response.profileFilling.treshold)) {
							var msg =
									"Профиль успешно сохранён. Профиль заполнен на " + response.profileFilling.percent + "%. " +
									"<span style='color: blue; font-weight: bold;'>Необходимо заполнить поля профиля на " + response.profileFilling.treshold +
									"%, иначе он будет удалён.</span> <span style='color: red; font-weight: bold;'>Обратите внимание! Если не установлено фото - то профиль считается заполненным на 0%!</span>";
							bootbox.alert(msg);
						} else {
							bootbox.alert("Профиль успешно сохранен");
						}
					} else {
						bootbox.alert("Профиль успешно сохранен");
					}
					if (response.isAllowSave) {
						$("a#save-link").show();
					} else {
						$("a#save-link").hide();
					}
					if (response.isAllowSaveAndSetVerified) {
						$("a#save-and-set-verified-link").show();
					} else {
						$("a#save-and-set-verified-link").hide();
					}
					if (response.isAllowSetVerified) {
						$("a#set-verified-link").show();
					} else {
						$("a#set-verified-link").hide();
					}
					$("form#fields-form").changesChecker("refresh");
					updateFieldDataValue();
				}
			},
			error : function() {
				console.log("ajax error");
			},
			complete : function() {
				$("a#save-link").removeAttr("disabled");
				$("a#save-and-set-verified-link").removeAttr("disabled");
				$("a#set-verified-link").removeAttr("disabled");
			}
		});
		return false;
	}

	function setVerified() {
		$("a#save-link").attr("disabled", "disabled");
		$("a#save-and-set-verified-link").attr("disabled", "disabled");
		$("a#set-verified-link").attr("disabled", "disabled");

		$.ajax({
			type : "post",
			dataType : "json",
			data : $("form#fields-form").serialize(),
			url : "/sharer/set_verified.json",
			success : function(response) {
				if (response.result == "error") {
					bootbox.alert(response.message ? response.message : "Ошибка при идентификации профиля");
				} else {
					bootbox.alert("Профиль успешно идентифицирован");
					if (response.isAllowSave) {
						$("a#save-link").show();
					} else {
						$("a#save-link").hide();
					}
					if (response.isAllowSaveAndSetVerified) {
						$("a#save-and-set-verified-link").show();
					} else {
						$("a#save-and-set-verified-link").hide();
					}
					if (response.isAllowSetVerified) {
						$("a#set-verified-link").show();
					} else {
						$("a#set-verified-link").hide();
					}
				}
			},
			error : function() {
				console.log("ajax error");
			},
			complete : function() {
				$("a#save-link").removeAttr("disabled");
				$("a#save-and-set-verified-link").removeAttr("disabled");
				$("a#set-verified-link").removeAttr("disabled");
			}
		});
		return false;
	}

	function updateFieldMarkup($item, currentHidden) {
		var $link = $item.find("a.hidden-field-eye");
		$link.removeClass().addClass("hidden-field-eye").addClass("glyphicon").addClass(currentHidden ? "glyphicon-eye-close" : "glyphicon-eye-open");
		$link.attr("data-hidden", currentHidden);
		$link.attr('data-original-title', currentHidden ? "Сейчас это поле скрыто" : "Сейчас это поле видно всем");
		var $tooltip = $("#" + $link.attr("aria-describedby"));
		$tooltip.find("div.tooltip-inner").html(currentHidden ? "Сейчас это поле скрыто" : "Сейчас это поле видно всем");
		var $input = $item.find("input[type=text]");
		if (currentHidden) {
			$input.addClass("hidden-field");
		} else {
			$input.removeClass("hidden-field");
		}
	}

	function slideBlock($link) {
		$panel = $link.closest(".panel-heading").next(".panel-collapse");
		if($link.hasClass("glyphicon-arrow-up")) {
			$panel.slideUp();
			$link.removeClass("glyphicon-arrow-up").addClass("glyphicon-arrow-down");
		} else if ($link.hasClass("glyphicon-arrow-down")) {
			$panel.slideDown();
			$link.removeClass("glyphicon-arrow-down").addClass("glyphicon-arrow-up");
		}

		return false;
	}

	function changeGroupHidden($link, groupId, currentHidden) {
		$.ajax({
			type: "post",
			dataType: "json",
			url: "/sharer/fieldsgroup/" + groupId + "/hidden/" + currentHidden,
			success: function (response) {
                response = JSON.parse(response);

				if (response.result == "success") {
					$.each($link.parents("div.panel").find("div.form-group.hideable"), function (index, div) {
						updateFieldMarkup($(div).closest(".row"), currentHidden);
					});
				} else {
					console.log("group hidden change error");
				}
			},
			error: function () {
				console.log("ajax error");
			}
		});
		return false;
	}

	function changeHidden($link) {
		var currentHidden = $link.attr("data-hidden") == "false" ? false : true;
		var fieldId = $link.attr("data-field-id");
		currentHidden = !currentHidden;
		$.ajax({
			type: "post",
			dataType: "json",
			url: "/sharer/field/" + fieldId + "/hidden/" + currentHidden,
			success: function (response) {
                response = JSON.parse(response);

				if (response.result == "success") {
					updateFieldMarkup($link.parent(), currentHidden);
				} else {
					console.log("hidden change error");
				}
			},
			error: function () {
				console.log("ajax error");
			}
		});
		return false;
	}
	function initAddOrRemoveToGroup(){
		$("a.add-group-select-link").off();
		$("a.add-group-select-link").click(function () {
			$(this).parents(".dropdown.open").removeClass("open");
			var data = {};
			data.sharer_id = ${sharer.id};
			var groupName = $(this).attr("data-group-name");
			data.other_id = ${profile.id};
			data.group_id = $(this).attr("data-group-id");
			if (data.group_id == -1) {
				bootbox.alert("Список не выбран");
			} else {
				$.getJSON("/contacts/add.json", data,function(response){
					renewInListMarkup(response);
					if (data.group_id > 0) {
						$("li [data-group-id=" + data.group_id + "]").html(groupName + "<i class='fa fa-check'></i>");
						$("li [data-group-id=" + data.group_id + "]").removeClass("add-group-select-link");
						$("li [data-group-id=" + data.group_id + "]").addClass("delete-group-select-link");
						initAddOrRemoveToGroup();
					}
					$("li [data-group-id=" + 0 + "]").remove();
				});
			}
			return false;
		});
		$("a.delete-group-select-link").off();
		$("a.delete-group-select-link").click(function () {
			$(this).parents(".dropdown.open").removeClass("open");
			var data = {};
			data.sharer_id = ${sharer.id};
			data.other_id = ${profile.id};
			data.group_id = $(this).attr("data-group-id");
			var groupName = $(this).attr("data-group-name");
			if (data.group_id == -1) {
				bootbox.alert("Список не выбран");
			} else {
				$.getJSON("/contacts/deleteGroup.json", data, function(response){
					renewInListMarkup(response);
					$("li [data-group-id="+data.group_id+"]").html(groupName);
					$("li [data-group-id="+data.group_id+"]").removeClass("delete-group-select-link");
					$("li [data-group-id="+data.group_id+"]").addClass("add-group-select-link");
					initAddOrRemoveToGroup();
				});
			}
			return false;
		});
	}
	function renewInListMarkup(response) {
		var sharerStatus =  response.sharerStatus;
		var otherStatus =  response.otherStatus;
		$("div.edit-contact").hide();
		$("div#edit-contact-" + sharerStatus.toLowerCase() + "-" + otherStatus.toLowerCase()).show();
		/*if (response.contactsGroups) {
			$("span.group-color-example").removeClass().addClass("group-color-example").addClass("group-color-example-" + response.contactsGroup.color).html(response.contactsGroup.name);
		} else {
			$("span.group-color-example").removeClass().addClass("group-color-example").addClass("group-color-example-0").html("Список по умолчанию");
		}*/
		var groupsHtml = "";
		for (var i in response.contactGroups) {
			var group = response.contactGroups[i];
		    groupsHtml += "<span class='group-color-example group-color-example-"+group.color+"'>"+group.name+ "</span>";
		}
		if (response.contactGroups.length == 0) {
			groupsHtml += "<span class='group-color-example group-color-example-0'>Список по умолчанию</span>";
		}
		$("#contactGroups").html(groupsHtml);
		$("#contactGroupsSent").html(groupsHtml);
		$("#contactGroupsGot").html(groupsHtml);
	}

	$(document).ready(function () {
		$("a#profile-filling-info-close-link").click(function() {
			$.radomJsonPost("/sharer/setting/set.json", {
				key : "profile.filling-info-closed",
				value : "true"
			}, function() {
				$('div#profile-filling-info-panel').slideUp();
			});
			return false;
		});

		$("a[data-field-internal-name=WWW]").radomTooltip({
			title: function() {
				return "<b>" + $(this).attr("data-field-value") + "</b> - Сайт участника, нажмите для перехода на сайт"
			},
			placement : "right",
			container : "body",
			html : true
		});

		$("input[data-field-name=WWW]").keyup( function(e) {
			if (/^[-а-яa-zёЁцушщхъфырэчстью0-9_\\\/.\-:~%&?=]*?$/.test(this.value))
				this.defaultValue = this.value;
			else
				this.value = this.defaultValue;
		});

		$("input[data-field-name=REGISTRATOR_WEBSITE]").keyup( function(e) {
			if (/^[-а-яa-zёЁцушщхъфырэчстью0-9_\\\/.\-:~%&?=]*?$/.test(this.value))
				this.defaultValue = this.value;
			else
				this.value = this.defaultValue;
		});

		$("input[data-field-name=SKYPE]").keyup( function(e) {
			if (/^[a-zA-Z0-9,_\\\/.\-:"()]*?$/.test(this.value))
				this.defaultValue = this.value;
			else
				this.value = this.defaultValue;
		});

		$("input[data-field-name=REGISTRATOR_SKYPE]").keyup( function(e) {
			if (/^[a-zA-Z0-9,_\\\/.\-:"()]*?$/.test(this.value))
				this.defaultValue = this.value;
			else
				this.value = this.defaultValue;
		});

		$("a[data-field-internal-name=SKYPE]").radomTooltip({
			title: function() {
				return "<b>" + $(this).attr("data-field-value") + "</b> - Skype участника, если у Вас установлен Skype - нажмите для звонка"
			},
			placement : "right",
			container : "body",
			html : true
		});

		$("a[data-field-internal-name=HOME_TEL]").radomTooltip({
			title: function() {
				return "<b>" + $(this).attr("data-field-value") + "</b> - Домашний телефон участника, если у Вас установлен Skype - нажмите для звонка"
			},
			placement : "right",
			container : "body",
			html : true
		});

		$("a[data-field-internal-name=MOB_TEL]").radomTooltip({
			title: function() {
				return "<b>" + $(this).attr("data-field-value") + "</b> - Мобильный телефон участника, если у Вас установлен Skype - нажмите для звонка"
			},
			placement : "right",
			container : "body",
			html : true
		});

		if ($("a[data-field-internal-name=HOME_TEL]").length > 0) {
			$("a[data-field-internal-name=HOME_TEL]").attr("href", $("a[data-field-internal-name=HOME_TEL]").attr("href").replace("(", "").replace(")", "").replace(new RegExp(" ", "g"), ""));
		}
		if ($("a[data-field-internal-name=MOB_TEL]").length > 0) {
			$("a[data-field-internal-name=MOB_TEL]").attr("href", $("a[data-field-internal-name=MOB_TEL]").attr("href").replace("(", "").replace(")", "").replace(new RegExp(" ", "g"), ""));
		}

		$("input[data-field-name=PROFILE]").okvedInput({
			title : "Выбор основного профиля организации"
		});

		$('a.hidden-field-eye').radomTooltip();
		$('a.hidden-group-eye').radomTooltip();
		initAddOrRemoveToGroup();
		$("a#deleteContactLink").click(function () {
			var data = {};
			data.sharer_id = ${sharer.id};
			data.other_id = ${profile.id};
			$.getJSON("/contacts/delete.json", data, function(){
				location.reload();
			});
			return false;
		});
		$("#profileLink").val(location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '') + "${profile.link}");
		$("#profileLink").click(function () {
			$("#profileLink").select();
		});

		$("#profile-accordion .form-control[readonly]").click(function () {
			$(this).select();
		});
	});


	$(document).ready(function(){
		//$("[data-field-name=PASSPORT_SERIAL]").mask("99 99", {placeholder:"_"} ).attr("placeholder", "__ __");
		//$("[data-field-name=PASSPORT_NUMBER]").mask("999999", {placeholder:"_"} ).attr("placeholder", "______");
		//$("[data-field-name=PASSPORT_DIVISION]").mask("999-999", {placeholder:"_"} ).attr("placeholder", "___-___");
		//$("[data-field-name=PERSON_INN]").mask("999999999999", {placeholder:"_"} ).attr("placeholder", "____________");
		//$("[data-field-name=SNILS]").mask("999-999-999 99", {placeholder:"_"} ).attr("placeholder", "___-___-___ __");

		$("[data-field-name=CEOPASSPORT_SERIAL]").mask("99 99", {placeholder:"_"} ).attr("placeholder", "__ __");
		$("[data-field-name=CEOPASSPORT_NUMBER]").mask("999999", {placeholder:"_"} ).attr("placeholder", "______");
		$("[data-field-name=CEOPASSPORT_DIVISION]").mask("999-999", {placeholder:"_"} ).attr("placeholder", "___-___");

		$("[data-field-name=INN]").mask("9999999999", {placeholder:"_"} ).attr("placeholder", "__________");
		$("[data-field-name=KPP]").mask("999999999", {placeholder:"_"} ).attr("placeholder", "_________");

		$("[data-field-name=RACCOUNT]").mask("99999999999999999999", {placeholder:"_"} ).attr("placeholder", "____________________");
		$("[data-field-name=CORACCOUNT]").mask("99999999999999999999", {placeholder:"_"} ).attr("placeholder", "____________________");
		$("[data-field-name=BIK]").mask("999999999", {placeholder:"_"} ).attr("placeholder", "_________");

		//$("[data-field-name=HOME_TEL]").mask("+7 (999) 999 9999", {placeholder:"_"} ).attr("placeholder", "+7 (___) ___ ____");
		//$("[data-field-name=MOB_TEL]").mask("+7 (999) 999 9999", {placeholder:"_"} ).attr("placeholder", "+7 (___) ___ ____");
		$("input[data-field-type=MOBILE_PHONE]").intlTelInput({
			autoFormat: true,
			defaultCountry: 'ru'
		});
		$("input[data-field-type=LANDLINE_PHONE]").intlTelInput({
			autoFormat: true,
			defaultCountry: 'ru'
		});
		if(${sharer.id != profile.id}) {
			//$("input[data-field-type=MOBILE_PHONE]").attr("disabled", "disabled");
			//$("input[data-field-type=LANDLINE_PHONE]").attr("disabled", "disabled");
		}
		$("[data-field-name=OWORK_TEL]").mask("+7 (999) 999 9999", {placeholder:"_"} ).attr("placeholder", "+7 (___) ___ ____");

		$("[data-field-name=LASTNAME]").capitalizeInput();
		$("[data-field-name=FIRSTNAME]").capitalizeInput();
		$("[data-field-name=SECONDNAME]").capitalizeInput();

		$("[data-field-name=OLASTNAME]").capitalizeInput();
		$("[data-field-name=OFIRSTNAME]").capitalizeInput();
		$("[data-field-name=OSECONDNAME]").capitalizeInput();

		/*$( 'input[data-field-type="REGION"]' ).attr("data-kladr-object-type", "region");
		 $( 'input[data-field-type="DISTRICT"]' ).attr("data-kladr-object-type", "district");
		 $( 'input[data-field-type="CITY"]' ).attr("data-kladr-object-type", "city");
		 $( 'input[data-field-type="STREET"]' ).attr("data-kladr-object-type", "street");
		 $( 'input[data-field-type="BUILDING"]' ).attr("data-kladr-object-type", "building");*/

		$("[data-field-name=BIRTHPLACE]").keyup(function () {
			var $this = $(this);
			var newText = $this.val();
			if (newText.length > 0) {
				var oldText = $this.attr("data-old-text");
				if (oldText != newText) {
					$this.attr("data-old-text", newText);
					var indexOfSpace = newText.indexOf(" ");
					if (indexOfSpace != -1) {
						var part1 = newText.substr(0, indexOfSpace + 1);
						var part2 = newText.substr(indexOfSpace + 1, 1).toUpperCase();
						var part3 = newText.substr(indexOfSpace + 2).toLowerCase();
						newText =  part1 + part2 + part3;
						var caret = $this.getCursorPosition();
						$this.val(newText);
						$this.setCursorPosition(caret);
					}
				}
			}
		}).blur(function() {
			var $this = $(this);
			var newText = $this.val();
			if (newText.indexOf(" ") == -1) {
				$this.val("");
			}
		});

		/*initAddressBlock($("[data-group-name=PERSON_REGISTRATION_ADDRESS]"), $("[data-field-name=POSTAL_CODE]"));
		 initAddressBlock($("[data-group-name=PERSON_ACTUAL_ADDRESS]"), $("[data-field-name=FPOSTAL_CODE]"));
		 initAddressBlock($("[data-group-name=REGISTRATOR_OFFICE_ADDRESS]"), $("[data-field-name=REGISTRATOR_OFFICE_POSTAL_CODE]"));*/

		initDateInputs();

		var settings = ${radom:getPassportCitizenshipSettings()};

		// Возвращает настройку отображения полей паспортных данных для выбранного гражданства
		// или настройку по умолчанию если ля выбранного гражданства не задана специфическая настройка
		function getPassportCitizenshipFieldsSettings(code) {
			var result = settings.defaultSetting;
			for(var i=0; i<settings.settings.length; i++) {
				if(settings.settings[i].countryComCode === code) {
					result = settings.settings[i];
					break;
				}
			}
			return result;
		}

		function getRowParent($element) {
			var $parent = $element.parent();
			while($parent && $parent.length>0 && !$parent.hasClass("row")) {
				$parent = $parent.parent();
			}
			return $parent;
		}

		// Отображение необходимых полей и формата для страны с code
		function showCitizenshipFields(code) {
			var setting = getPassportCitizenshipFieldsSettings(code);

			var $personInn = $("[data-field-internal-name=PERSON_INN]"); //$("[data-field-name=PERSON_INN]");
			var $personInnBlock = getRowParent($personInn);
			if(setting.showPersonInn) {
				$personInnBlock.show();
				$personInn.mask(setting.maskPersonInn, {placeholder:setting.holder} ).attr("placeholder", setting.holderPersonInn);
			} else {
				$personInnBlock.hide();
			}

			var $snils = $("[data-field-internal-name=SNILS]");// $("[data-field-name=SNILS]");
			var $snilsBlock = getRowParent($snils);
			if(setting.showSnils) {
				$snilsBlock.show();
				$snils.mask(setting.maskSnils, {placeholder:setting.holder} ).attr("placeholder", setting.holderSnils);
			} else {
				$snilsBlock.hide();
			}

			var $byIdentificationNumber = $("[data-field-internal-name=BY_IDENTIFICATION_NUMBER]");
			var $byIdentificationNumberBlock = getRowParent($byIdentificationNumber);
			if(setting.showByIdentificationNumber) {
				$byIdentificationNumberBlock.show();
				$byIdentificationNumber.mask(setting.maskByIdentificationNumber, {placeholder:setting.holder} ).attr("placeholder", setting.holderByIdentificationNumber);
				$byIdentificationNumber.change(function() {
					this.value = this.value.toLocaleUpperCase();
				});
			} else {
				$byIdentificationNumberBlock.hide();
			}

			var $kzIndividualIdentificationNumber = $("[data-field-internal-name=KZ_INDIVIDUAL_IDENTIFICATION_NUMBER]");
			var $kzIndividualIdentificationNumberBlock = getRowParent($kzIndividualIdentificationNumber);
			if(setting.showKzIndividualIdentificationNumber) {
				$kzIndividualIdentificationNumberBlock.show();
				$kzIndividualIdentificationNumber.mask(setting.maskKzIndividualIdentificationNumber, {placeholder:setting.holder} ).attr("placeholder", setting.holderKzIndividualIdentificationNumber);
				$kzIndividualIdentificationNumber.change(function() {
					this.value = this.value.toLocaleUpperCase();
				});
			} else {
				$kzIndividualIdentificationNumberBlock.hide();
			}

			var $passportSerial = $("[data-field-internal-name=PASSPORT_SERIAL]"); //  $("[data-field-name=PASSPORT_SERIAL]");
			var $passportSerialBlock = getRowParent($passportSerial);
			if(setting.showPassportSerial) {
				$passportSerialBlock.show();
				$passportSerial.mask(setting.maskPassportSerial, {placeholder:setting.holder} ).attr("placeholder", setting.holderPassportSerial);
				$passportSerial.change(function() {
					this.value = this.value.toLocaleUpperCase();
				});
			} else {
				$passportSerialBlock.hide();
			}

			var $passportNumber = $("[data-field-internal-name=PASSPORT_NUMBER]"); // $("[data-field-name=PASSPORT_NUMBER]");
			var $passportNumberBlock = getRowParent($passportNumber);
			if(setting.showPassportNumber) {
				$passportNumberBlock.show();
				$passportNumber.mask(setting.maskPassportNumber, {placeholder:setting.holder} ).attr("placeholder", setting.holderPassportNumber);
			} else {
				$passportNumberBlock.hide();
			}

			var $passportDivision = $("[data-field-internal-name=PASSPORT_DIVISION]"); // $("[data-field-name=PASSPORT_DIVISION]");
			var $passportDivisionBlock = getRowParent($passportDivision);
			if(setting.showPassportDivision)  {
				$passportDivisionBlock.show();
				$passportDivision.mask(setting.maskPassportDivision, {placeholder:setting.holder} ).attr("placeholder", setting.holderPassportDivision);
			} else {
				$passportDivisionBlock.hide();
			}

			var $passportExpirationDate = $("[data-field-internal-name=PASSPORT_EXPIRATION_DATE]");
			var $passportExpirationDateBlock = getRowParent($passportExpirationDate);
			if(setting.showPassportExpirationDate)  {
				$passportExpirationDateBlock.show();
			} else {
				$passportExpirationDateBlock.hide();
			}

			var $passportDealer = $("[data-field-internal-name=PASSPORT_DEALER]");
			var $passportDealerBlock = getRowParent($passportDealer);
			if(setting.showPassportDealer)  {
				$passportDealerBlock.show();
			} else {
				$passportDealerBlock.hide();
			}

			var $passportExpiredDate = $("[data-field-internal-name=EXPIRED_PASSPORT_DATE]");
			var $showPassportExpiredDateBlock = getRowParent($passportExpiredDate);
			if(setting.showPassportExpiredDate)  {
				$showPassportExpiredDateBlock.show();
			} else {
				$showPassportExpiredDateBlock.hide();
			}

		}

		// Колбек для списка выбора гражданства
		function personCitizenshipRameraListEditorCallback(event, data) {
			if (event == RameraListEditorEvents.CREATED) {
				// Если значение гражданства ещё не задано, то выставляем Россию по умолчанию
				var field_value = $("[data-field-internal-name='PERSON_CITIZENSHIP']").val();
				var select_value = $("#PERSON_CITIZENSHIP select").val();
				if(!field_value && !select_value) {
					$("#PERSON_CITIZENSHIP select").val($("#PERSON_CITIZENSHIP select [com_code="+settings.defaultSelectedCitizenship+"]").attr("value")).change()
				}
			}
			if (event == RameraListEditorEvents.VALUE_CHANGED) {
				// Выставляем поля и форматы паспортных данных для выбранного гражданства
				showCitizenshipFields(data.code);
			}
		}

		function onChangeCountryField(listEditorItemData) {
			var $div = listEditorItemData.domNode.closest("div.form-group");
			var $block = $div.closest('.panel-body');
			var $input = $div.find("select");
			checkFormGroup($div, $input, listEditorItemData.text);
			if($block.length > 0) {
				checkBlockInputs($block);
			}
		}
		function onChangeCountryFieldReg(listEditorItemData) {
			if (listEditorItemData.code === "ru") {
				regAddressRu = true;
			}
			else {
				regAddressRu = false;
			}
			var $div = listEditorItemData.domNode.closest("div.form-group");
			var $block = $div.closest('.panel-body');
			var $input = $div.find("select");
			checkFormGroup($div, $input, listEditorItemData.text);
			if($block.length > 0) {
				checkBlockInputs($block);
			}
		}
		function onChangeCountryFieldActual(listEditorItemData) {
			if (listEditorItemData.code === "ru") {
				actualAddressRu = true;
			}
			else {
				actualAddressRu = false;
			}
			var $div = listEditorItemData.domNode.closest("div.form-group");
			var $block = $div.closest('.panel-body');
			var $input = $div.find("select");
			checkFormGroup($div, $input, listEditorItemData.text);
			if($block.length > 0) {
				checkBlockInputs($block);
			}
		}

		// инициализация компонентов универсальных списков (страна)
		initCitizenshipCountryField("PERSON_PASSPORT", "PERSON_CITIZENSHIP", personCitizenshipRameraListEditorCallback);

		initCountryField(
				"PERSON_REGISTRATION_ADDRESS",
				"COUNTRY_CL", "POSTAL_CODE",
				onChangeCountryFieldReg, regCountryListItem,
				RoomTypes.ROOM, "ROOM",
				"REGION_CODE", "STREET_DESCRIPTION_SHORT",
				"DISTRICT_DESCRIPTION_SHORT", "CITY_DESCRIPTION_SHORT"
		);
		initCountryField(
				"PERSON_ACTUAL_ADDRESS",
				"FCOUNTRY_CL", "FPOSTAL_CODE",
				onChangeCountryFieldActual, factCountryListItem,
				RoomTypes.ROOM, "FROOM",
				"FREGION_CODE", "FSTREET_DESCRIPTION_SHORT",
				"FDISTRICT_DESCRIPTION_SHORT", "FCITY_DESCRIPTION_SHORT"
		);
		initCountryField(
				"REGISTRATOR_OFFICE_ADDRESS",
				"REGISTRATOR_OFFICE_COUNTRY", "REGISTRATOR_OFFICE_POSTAL_CODE",
				onChangeCountryField, registratorCountryListItem,
				RoomTypes.ROOM, "REGISTRATOR_OFFICE_ROOM",
				"REGISTRATOR_OFFICE_REGION_CODE", "REGISTRATOR_OFFICE_STREET_DESCRIPTION_SHORT",
				"REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION_SHORT", "REGISTRATOR_OFFICE_CITY_DESCRIPTION_SHORT"
		);

		// Правильное отображение полей для случая когда редактировать ничего уже нельзя(например после сертификации).
		// Для таких профилей вместо списка страны отображается инпут и у него есть аттрибут com_code.
		// Если аттрибут не задан, то страна гражданства отображается в виде списка и обрабатывается в другом месте
		var com_code = $("[data-field-internal-name='PERSON_CITIZENSHIP']").attr("com_code");
		if(com_code) {
			showCitizenshipFields(com_code);
		}

		<c:if test="${sharer.id != profile.id}">
		<c:if test="${showRegisteredAt == false || profile.verified == true}">
		<c:if test="${radom:hasRole('ROLE_SUPERADMIN') == false}">
		window.onload = function() {
			$("#COUNTRY_CL :input").prop("disabled", true);
			$("#FCOUNTRY_CL :input").prop("disabled", true);
		};
		</c:if>
		</c:if>
		</c:if>
	});
</script>

<script type="text/javascript">
	var profileId = ${profile.id};
	$(document).ready(function() {
		$(radomEventsManager).bind("news.create", function(event, data) {
			if (data.authorType == "SHARER" && data.author.id == profileId) {
				showNewsMarkup(data, true);
			}
		});
		$(radomEventsManager).bind("news.delete", function(event, data) {
			if (data.authorType == "SHARER" && data.author.id == profileId) {
				deleteNewsMarkup(data);
			}
		});
	});
</script>

<script type="text/javascript">
	$(document).ready(function(){
		$.each($("a.show-all-sharer-communities"), function(index, link) {
			var $link = $(link);
			var $container = $("div#" + $link.attr("data-container-id"));
			if ($container[0].scrollHeight <= 95) {
				$link.remove();
			}
		});
		$("a.show-all-sharer-communities").click(function() {
			var $link = $(this);
			var $container = $("div#" + $link.attr("data-container-id"));
			$container.css("height", "90px");
			$container.css("max-height", "none");
			$container.animateAuto("height", 500);
			$link.fadeOut();
			return false;
		});
	});
</script>

<script type="text/javascript">
	var actualAddressRu = false;
	var regAddressRu = false;
	function checkBlockInputs($div) {
		var $input = $div.find("input, select");
		var $cell = $div.find("td.cell-timetable");
		var changedInput = false;
		var button = '<a class="btn btn-primary btn-sm block-save-link" href="#" onclick="return save();">Сохранить профиль</a>';

		$.each( $input, function( key, input ) {
			if($(input).attr('data-field-value') != undefined &&
					$(input).attr('data-field-value') !== $(input).val() &&
					!$(input).attr('data-changes-checker-ignore') &&
					($(input).attr('readonly') != 'readonly')) {
				if($(input).attr('placeholder') == '') {
					changedInput = true;
				}
				if($(input).attr('placeholder') === $(input).val() && $(input).attr('data-field-value') != '' ||
						$(input).val() == '' ||
						$(input).val() != '' &&
						$(input).attr('placeholder') != $(input).val()) {
					changedInput = true;
				}
			}
		});
		//checking time-table
		$.each($cell, function(index, cell) {
			var $cell = $(cell);
			if($cell.attr('default-value') !== $cell.attr('data-selected')) {
				changedInput = true;
			}
		});
		if(changedInput) {
			$('#save-link').show();
			if($div.find('.block-save-link').length == 0){
				$div.append(button);
			}
		} else {
			$div.find('.block-save-link').remove();
			if($('.block-save-link').length == 0) {
				$('#save-link').hide();
			}
		}
	}

	function checkFormGroup($div, $input, inputValue) {
		$div.find("span.glyphicon").remove();
		$div.removeClass("has-error").removeClass("has-warning").removeClass("has-feedback");
		if ($input == null) {
			$input = $div.find("input, select");
			inputValue = $input.val();
		}
		if ($div.attr("data-required") == "true") {
			if (!inputValue) {
				$div.append("<span class='glyphicon glyphicon-remove form-control-feedback' aria-hidden='true'></span>");
				$div.addClass("has-error").addClass("has-feedback");
				$div.find("span.glyphicon").radomTooltip({
					title : "Данное поле обязательно к заполнению. Если оставить это поле пустым, процент заполнения профиля будет равен нулю.",
					placement : "top",
					container : "body"
				});
			} else {
				//$div.append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
				//$div.addClass("has-success").addClass("has-feedback");
			}
		} else if ($div.attr("data-has-points") == "true") {
			if (!inputValue) {
				$div.append("<span class='glyphicon glyphicon-warning-sign form-control-feedback' aria-hidden='true'></span>");
				$div.addClass("has-warning").addClass("has-feedback");
				$div.find("span.glyphicon").radomTooltip({
					title : "Данное поле следует заполнить, так как оно учитывается при расчете процента заполнения Вашего профиля.",
					placement : "top",
					container : "body"
				});
			} else {
				//$div.append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
				//$div.addClass("has-success").addClass("has-feedback");
			}
		} else {
			if($input.attr('data-field-name') == "WWW" || $input.attr('data-field-name') == "REGISTRATOR_WEBSITE") {
				if (/(https?:\/\/)?(www\.)?([-а-яa-zёЁцушщхъфырэчстью0-9_\.]{2,}\.)(рф|[a-z]{2,6})((\/[-а-яёЁцушщхъфырэчстьюa-z0-9_]{1,})?\/?([a-z0-9_-]{2,}\.[a-z]{2,6})?(\?[a-z0-9_]{2,}=[-0-9]{1,})?((\&[a-z0-9_]{2,}=[-0-9]{1,}){1,})?)/i.test(inputValue)) {
					$div.find("span.glyphicon").remove();
					$div.removeClass("has-error").removeClass("has-warning").removeClass("has-feedback");
				} else if (inputValue != '') {
					$div.append("<span class='glyphicon glyphicon-warning-sign form-control-feedback' aria-hidden='true'></span>");
					$div.addClass("has-warning").addClass("has-feedback");
					$div.find("span.glyphicon").radomTooltip({
						title: "Данные введённые в поле не являются адресом сайта.",
						placement: "top",
						container: "body"
					});
				}
			}
		}

	}

	$(document).ready(function(){
		$.each($("div.form-group"), function(index, div) {
			var $div = $(div);
			var $block = $div.closest('.panel-body');
			checkFormGroup($div);
			var $input = $div.find("input, select");
			$input.keyup(function() {
				checkFormGroup($div);
				if($div.closest('.panel-body').length > 0)
					checkBlockInputs($block);
			});
			$input.click(function() {
				checkFormGroup($div);
				if($div.closest('.panel-body').length > 0)
					checkBlockInputs($block);
			});
			$input.change(function() {
				checkFormGroup($div);
				if($div.closest('.panel-body').length > 0)
					checkBlockInputs($block);
			});
			$input.blur(function() {
				checkFormGroup($div);
				if($div.closest('.panel-body').length > 0)
					checkBlockInputs($block);
			});
		})
	});
</script>

<script type="text/javascript">

	var isSelfProfile = "${sharer.id == profile.id}" === "true";

	$(document).ready(function(){
		var sharer = {};
		sharer.id = "${profile.id}";
		sharer.fullName = "${profile.fullName}";
		$('.do-accounts-move-link').accountsMoveDialog(sharer);

		$("form#fields-form").changesChecker();
	});

</script>

<script type="text/javascript">
	function cancelRequest(requestId){
		bootbox.confirm("Вы действительно хотите отменить заявку на идентификацию",
				function(result){
					if(result){
						$.radomJsonPost("/registrator/deleteRequest", {
							requestId: requestId
						}, function(data){
							if(data.result == 'success'){
								$("div#registration-request-block").remove();
								$("div#registrator-select-block").show();
								bootbox.alert("Заявка на идентификацию успешно отменена");
								$(radomEventsManager).trigger("registrationRequest.deleteRequest");
							}
						});
					}
				}
		);
	}

	function updateRegistratorSelectBlock(profileFilling){
		var verified = ${profile.verified};
		if(verified) return;
		var $registratorSelectBlockOk = $('#registrator-select-block-ok');
		var $registratorSelectBlockNot = $('#registrator-select-block-not');
		if(profileFilling.percent == 100) {
			$registratorSelectBlockOk.show();
			$registratorSelectBlockNot.hide();
		} else {
			$registratorSelectBlockOk.hide();
			$registratorSelectBlockNot.show();
		}
	}

	function refreshProfileFilling(){
		$.radomJsonGet("/sharer/profile_filling.json", {}, function(profileFilling) {
            ProfileFillingPanel.refresh(profileFilling);
			updateRegistratorSelectBlock(profileFilling);
		});
	}

	$(document).ready(function() {
		$(radomEventsManager).bind("registrationRequest.updateRequest", function(event, data) {
			$.radomJsonPost("/registrator/myRequest.json", {
			}, function(data){
				if(!data.hasRequest){
					$("div#registration-request-block").remove();
					$("div#registrator-select-block").show();
				}
			});
		});
		$(".image-bottom-links-wrapper .go-to-chat-link").radomTooltip({
			position : "top",
			container : "body",
			title : "Написать сообщение"
		});
		$(".image-bottom-links-wrapper .do-accounts-move-link").radomTooltip({
			position : "top",
			container : "body",
			title : "Перевести средства"
		});
	});
</script>
	</c:if>
<c:if test="${!profileExists}">
	<style>
		.panel-heading a:after {
			font-family:'Glyphicons Halflings';
			content:"\e114";
			float: right;
			color: grey;
		}
		.panel-heading a.collapsed:after {
			content:"\e080";
		}
	</style>
	<div class="panel panel-default">
		<div class="panel-heading">Пользователь не найден!</div>
	</div>
</c:if>
<!-- Модальное окно выбора-->
<div class="modal fade" role="dialog" id="sendContactModal"
	 aria-hidden="true">
	<div class="modal-dialog modal-md">
		<div class="modal-content">
			<div class="modal-header">
				<h4 style="text-align: center;" class="modal-title">Отправить контакт</h4>
			</div>
			<div class="modal-body"  style="text-align: center;">
				<div class="row">
					<label id="search-input-label">Выберите кому отправить контакт</label>
					<div class="form-group" id="search-input-block">
						<input style="display: inline-block;width: 75%;margin-top: 5px" type="text" class="form-control" id="search-input" placeholder="Начните вводить имя или название организации" />
					</div>
				</div>
			  <div id="contacts">
			  </div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>
<iframe id="downloadIframe" style="display:none;"></iframe>
