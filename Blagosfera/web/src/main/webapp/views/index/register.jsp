<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<t:insertAttribute name="favicon" />

		<title>БЛАГОСФЕРА</title>

		<link href="/css/bootstrap.css" rel="stylesheet">
		<link href="/css/signin.css" rel="stylesheet">

		<script type="text/javascript" src="/js/jquery.js"></script>
		<script type="text/javascript" src="/js/jquery.cookie.js"></script>
		<script type="text/javascript" src="/js/bootstrap.js"></script>
		<script type="text/javascript" src="/js/bootbox.js"></script>

		<style type="text/css">
			span.alert {
				display : block;
			}
			.form-control-feedback {
				top : 5px;
			}
			.form-group {
				margin-bottom : 0;
				z-index : 2;
			}
		</style>

        <script>
            $(document).ready(function() {
                $("#sharer").submit(function (event) {

                });
            });
        </script>
	</head>
	<body>
		<c:choose>
			<c:when test="${signupIsClosed == 0}">
				<div class="container" style="display : none;">
					<form:form action="register" method="post" cssClass="form-signin" commandName="sharer">
						<img id="logo" src="/i/logo.png" />
						<h2 class="form-signin-heading">Регистрация нового аккаунта</h2>

						<form:errors path="email" cssClass="alert alert-danger"></form:errors>
						<form:errors path="password" cssClass="alert alert-danger"></form:errors>

						<div class="alert alert-info" role="alert">
							Для регистрации заполните поля ниже. Все поля обязательны для заполнения. Необходимо указать свой действующий e-mail, т.к. на него будет выслано письмо с кодом активации аккаунта.
						</div>

						<div class="form-group has-feedback">
							<form:input path="email" cssClass="form-control text-input login-input" cssStyle="border-bottom-left-radius: 0; border-bottom-right-radius: 0; margin-bottom: -1px;" placeholder="Email" required="required" onkeypress="return event.charCode != 32"></form:input>
						</div>
						<form:password path="password" cssClass="form-control text-input" cssStyle="border-radius: 0;margin-bottom: -1px;" placeholder="Пароль" required="required"></form:password>
						<input type="password" name="confirm" class="form-control text-input" style="border-radius: 0; margin-bottom: -1px;" placeholder="Подтверждение пароля" required="required">

                        <br>

						<div class="checkbox">
							<label><input name="isUserAgreementAccepted" type="checkbox" value="">Я принимаю условия <a href="/rules">пользовательского соглашения</a> </label>
						</div>

						<br>

						<form:button class="btn btn-lg btn-primary">Регистрация</form:button>
							<hr />
							<a href="/">Войти</a> | <a href="/recovery/init">Восстановить доступ</a>
						</form:form>
					<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". | <a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
				</div>

				<script type="text/javascript">
					var emailRegexp = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
					function checkEmail(usernameInput) {
						if (!emailRegexp.test(usernameInput.val())) {
							usernameInput.parent().removeClass("has-success").addClass("has-error");
							usernameInput.parent().find(".form-control-feedback").removeClass("glyphicon-ok").addClass("glyphicon-remove");
						} else {
							usernameInput.parent().removeClass("has-error").addClass("has-success");
							usernameInput.parent().find(".form-control-feedback").addClass("glyphicon-ok").removeClass("glyphicon-remove");
						}
					}

					$(document).ready(function () {
						var $container = $("div.container");
						var windowHeight = $(window).height();
						var containerHeight = $container.height();

						if (containerHeight < windowHeight) {
							$container.css("margin-top", ((windowHeight - containerHeight) / 2));
						}
						$container.fadeIn();

						var usernameInput = $("#email");
						var passwordInput = $("input[name=password]");
						var confirmInput = $("input[name=confirm]");

						$("#sharer").submit(function () {
							var $password = passwordInput.val();
							var $confirm = confirmInput.val();
							var isUserAgreementAccepted = $("input[name=isUserAgreementAccepted]").is(":checked");

							if (usernameInput.val().indexOf(" ") != -1) {
								bootbox.alert("E-mail не должен содержать пробелы!");
								return false;
							}

							if ($password !== $confirm) {
								bootbox.alert("Пароль и его подтверждение не совпадает");
								return false;
							}

							if (!isUserAgreementAccepted) {
								bootbox.alert("Необходимо принять условия пользовательского соглашения");
								return false;
							}
						});

						usernameInput.bind("keypress", function(event) {
							return RegExp('[a-zA-Z0-9_.+-@\.]').test(String.fromCharCode(event.charCode));
						});

						usernameInput.on("input", function () {
							checkEmail(usernameInput);
						});
						// Проверка при вставке мыла
						usernameInput.bind('paste', function(e) {
							setTimeout(function(){
								usernameInput.val(usernameInput.val().split(" ").join("").toLowerCase());
								checkEmail(usernameInput);
							}, 100);
						});

						passwordInput.bind("keypress", function(event) {
							return RegExp('^[\\S]{1}$').test(String.fromCharCode(event.charCode));
						});
						// Проверка при вставке пароля
						passwordInput.bind('paste', function(e) {
							setTimeout(function(){
								passwordInput.val(passwordInput.val().split(" ").join(""));
							}, 100);
						});

						confirmInput.bind("keypress", function(event) {
							return RegExp('^[\\S]{1}$').test(String.fromCharCode(event.charCode));
						});
						// Проверка при вставке пароля
						confirmInput.bind('paste', function(e) {
							setTimeout(function(){
								confirmInput.val(confirmInput.val().split(" ").join(""));
							}, 100);
						});
					});
				</script>
			</c:when>
			<c:when test="${signupIsClosed != 0}">
				<div class="container" style="display : none;">
					<a href="/"><img src="/i/logo.png" style="height: 300px;display: block;margin-left: auto;margin-right: auto"/></a>
					<h2 class="form-signin-heading" style="text-align: center">Регистрация нового аккаунта</h2>

					<div class="alert alert-info" role="alert" style="text-align: center">
						В настоящий момент регистрация возможна только по приглашениям от действующих участников системы Благосфера
					</div>

					<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". | <a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
				</div>
				<script type="text/javascript">
					$(document).ready(function () {
						var $container = $("div.container");
						var windowHeight = $(window).height();
						var containerHeight = $container.height();

						if (containerHeight < windowHeight) {
							$container.css("margin-top", ((windowHeight - containerHeight) / 2));
						}
						$container.fadeIn();
					});
				</script>
			</c:when>
		</c:choose>
		<!-- BEGIN JIVOSITE CODE {literal} -->
		<script type='text/javascript'>
			var jivositeKey = "${jivositeKey}";
			function jivo_onLoadCallback() {
				$.radomJsonGet(
						"/jivosite/info.json",
						null,
						function (response) {
							clearJivositeInfo();
							if (response.userInfo) {
								var date = new Date(0);
								jivo_api.setContactInfo(
										{
											name: response.userInfo.name,
											email: response.userInfo.email,
											phone: response.userInfo.phone,
											description : ""

										}
								);
								//jivo_api.setUserToken(response.userToken);
							}
						});
			}
			function clearJivositeInfo() {
				delete_cookie('jv_client_name_'+jivositeKey);
				delete_cookie('jv_email_'+jivositeKey);
				delete_cookie('jv_phone_'+jivositeKey);
			}
			function delete_cookie ( cookie_name )

			{
				var cookie_date = new Date ( );
				cookie_date.setTime ( 0 );
				document.cookie = cookie_name += "=; path=/ ; expires=" +
						cookie_date.toUTCString();

			}

			(function(){ var widget_id = jivositeKey;var d=document;var w=window;function l(){
				var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = '//code.jivosite.com/script/widget/'+widget_id; var ss = document.getElementsByTagName('script')[0]; ss.parentNode.insertBefore(s, ss);}if(d.readyState=='complete'){l();}else{if(w.attachEvent){w.attachEvent('onload',l);}else{w.addEventListener('load',l,false);}}})();
		</script>
		<!-- {/literal} END JIVOSITE CODE -->
	</body>
</html>