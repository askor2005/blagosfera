<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link rel="stylesheet" type="text/css" href="/css/strength-meter.css"/>

<script src="/js/strength-meter.min.js"></script>
<script src="/js/strength-meter-ru.js"></script>

<script type="text/javascript">
	function changeUserPassword(oldPassword, newPassword, callBack) {

		$.radomJsonPostWithWaiter(
				"/sharer/change_password.json",
				{
					old_password: oldPassword,
					new_password: newPassword
				},
				callBack
		)
	}
	function closePasswordDialog(close) {
		if (close) {
			window.location.href = "/security/logout";
		}
	}
	$(document).ready(function() {

		var passwordIsChanged = false;

		$("#changePasswordWindow .modal-body p").css({
			'margin': '0px',
			'line-height': 'normal',
			'color': '#000000'
		});
		$("#changePasswordWindow .modal-body label").css('color', '#000000');




		$('#changePasswordWindow').on('hidden.bs.modal', function() {
			if (!passwordIsChanged) {
				bootbox.alert("Вы не сможете пользоваться услугами системы, пока не создадите ваш пароль.",
				function() {
					closePasswordDialog(true);
				});
			}
		});

		$("#changePasswordWindow").modal({backdrop: 'static', keyboard: false});

		// Смена пароля
		$("#changePasswordButton").click(function() {
			var oldPassword = $("#old_password").val();
			var newPassword = $("#new_password").val();
			var confirmNewPassword = $("#new_password_confirm").val();
			if (newPassword == "") {
				bootbox.alert("Не введён ваш пароль");
				return false;
			}
			if (newPassword != confirmNewPassword) {
				bootbox.alert("Ваш пароль и его подтвеждение не совпадают");
				return false;
			}



			changeUserPassword(oldPassword, newPassword, function() {
				passwordIsChanged = true;
				$("#changePasswordWindow").modal("hide");
				bootbox.alert("Пароль успешно создан");
			});
		});
	});

	$(window).load(function() {
		<c:if test="${not empty generatedPassword}">
		$("#old_password").val('${generatedPassword}');
		$("#new_password").focus();
		</c:if>
	});
</script>
<!--  -->
<div class="modal fade" role="dialog" id="changePasswordWindow" aria-labelledby="changePasswordLabel" aria-hidden="true">
	<div class="modal-dialog" style="width: 798px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="changePasswordLabel" style="text-align: center;"><strong>Создать пароль</strong></h4>
			</div>
			<div class="modal-body">

				<p style="font-size:16px;text-align:center;">
					<c:choose>
						<c:when test="${sharer.sex}">Уважаемый </c:when>
						<c:otherwise>Уважаемая </c:otherwise>
					</c:choose>
					${sharer.fullName}
				</p>
				<p>
					Для Вас создан Личный Информационный Кабинет (<strong>ЛИК</strong>). Для того, чтобы в него войти, Вам необходимо</br>
					создать свой пароль. Для этого введите проверочный код, присланный Вам Системой БЛАГОСФЕРА на</br>
					электронную почту, в поле "<strong>Проверочный код</strong>". Затем придумайте и напишите ваш пароль в поле</br>
					"<strong>Пароль</strong>", после чего повторите его в поле "<strong>Подтверждение вашего пароля</strong>".
				</p>

				<p>&nbsp;</p>
				<p>
					В дальнейшем при необходимости Вы сможете менять пароль в настройках вашего профиля (при нажатии</br>
					на ваше имя в верхнем правом углу экрана выберите в раскрывающемся списке пункт "<strong>Мои настройки</strong>" и</br>
					следуйте написанным там указаниям).
				</p>
				<p>&nbsp;</p>

				<div class="form-group">
					<label>Проверочный код</label>
					<input type="password" class="form-control" id="old_password" placeholder="Проверочный код"/>
				</div>
				<div class="form-group">
					<label>Пароль</label>
					<input type="password" class="form-control strength" id="new_password" placeholder="Создайте свой пароль" data-language="ru" data-toggle-mask="false"/>
				</div>
				<div class="form-group">
					<label>Подтверждение вашего пароля</label>
					<input type="password" class="form-control" id="new_password_confirm" placeholder="Подтвердите ваш пароль"/>
				</div>
			</div>
			<div class="modal-footer" style="text-align: center;">
				<button type="button" class="btn btn-info" id="changePasswordButton" style="width: 240px;">Подтвердить</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->