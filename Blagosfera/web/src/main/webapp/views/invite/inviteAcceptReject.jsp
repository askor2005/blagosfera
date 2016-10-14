<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<t:insertAttribute name="favicon"/>

	<title>БЛАГОСФЕРА</title>

	<link href="/css/bootstrap.css" rel="stylesheet">
	<link href="/css/signin.css" rel="stylesheet">

	<script type="text/javascript" src="/js/jquery.js"></script>
	<script type="text/javascript" src="/js/jquery.cookie.js"></script>
	<script type="text/javascript" src="/js/bootstrap.js"></script>
	<script type="text/javascript" src="/js/radom-ajax.js"></script>
	<script type="text/javascript" src="/js/bootbox.js"></script>

	<style type="text/css">
		span.alert {
			display: block;
		}
	</style>
</head>
<body>

<c:choose>
	<c:when test="${not empty errorMessage}">
		<div class="container">
			<a href="/"><img src="/i/logo.png" style="height: 300px;display: block;margin-left: auto;margin-right: auto"/></a>
			<h1 class="form-signin-heading" style="text-align: center;">БЛАГОСФЕРА</h1>
			<h3 class="form-signin-heading" style="text-align: center">${errorMessage}</h3>

			<hr/>
			<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". |
				<a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
		</div>
	</c:when>
	<c:when test="${inviteIsExpire == 0 && inviteIsAccept == 1 && inviteStatus == 0}">
		<div id="offer-container" class="container" style="text-align : center;">
			<script type="text/javascript">
				$(document).ready(function () {
					var $acceptOffer = $("#accept-offer");
					var $signUp = $("#sign-up");

					$acceptOffer.change(function () {
						if ($acceptOffer.prop("checked")) {
							$signUp.removeClass("disabled");
						} else {
							$signUp.addClass("disabled");
						}
					});

					$signUp.click(function () {
						$acceptOffer.prop("disabled", true)
						$acceptOffer.addClass("disabled");
						$signUp.addClass("disabled");
						$.radomJsonPost("/invite/accept.json", {
							hash: $("input#hash").val(),
							aggreement_accepted: "yes"
						}, function (r) {
							$acceptOffer.prop("disabled", false)
							$acceptOffer.removeClass("disabled");
							$acceptOffer.change();

							$("#offer-container").hide();
							$("#hide-offer-container").show();
							$("#inviteEmail").html('Введите адрес вашей электронной почты и проверочный код. ' +
												   'Проверочный код Вы можете найти в сообщении "Данные для ' +
									 			   'авторизации" на вашей электронной почте (' + r.inviteEmail + ')');
						}, function (r) {
							$acceptOffer.prop("disabled", false)
							$acceptOffer.removeClass("disabled");
							$acceptOffer.change();
							if (r && r.message) {
								bootbox.alert(r.message);
							}
						});
					});
				});
			</script>

			<input id="hash" type="hidden" name="hash" value="${hash}"/>
			<a href="/"><img src="/i/logo.png" style="height: 300px;display: block;margin-left: auto;margin-right: auto"/></a>
			<h1 class="form-signin-heading" style="text-align: center;">БЛАГОСФЕРА</h1>
			<br/>

			<div class="panel panel-primary" style="max-width: 640px; float: none; margin-left: auto; margin-right: auto;">
				<div class="panel-heading">Для регистрации в Системе БЛАГОСФЕРА, Вам необходимо прочесть и принять оферту</div>
				<div class="panel-body"	style="max-height: 340px;overflow-y: scroll;text-align: left;">${offerText}</div>
			</div>

			<div class="checkbox">
				<label><input id="accept-offer" type="checkbox" value="">Я принимаю оферту Системы БЛАГОСФЕРА</label>
			</div>

			<button id="sign-up" class="btn btn-lg btn-primary disabled">Зарегистрироваться</button>
			<hr/>
			<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". | <a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
		</div>

		<div id="hide-offer-container" class="container" style="display: none;">
			<form class="form-signin" role="form" method="post" action="/security/login">
				<a href="/"><img src="/i/logo.png"
								 style="height: 300px;display: block;margin-left: auto;margin-right: auto"/></a>

				<h1 class="form-signin-heading" style="text-align: center;">БЛАГОСФЕРА</h1>

				<h3 class="form-signin-heading" style="text-align: center">Приглашение успешно подтверждено</h3>

				<div id="inviteEmail" class="alert alert-info" role="alert"
					 style="text-align: center; background-color: #CCFFFF; margin-top: -8px;">
					Введите адрес вашей электронной почты и проверочный код.
					Проверочный код Вы можете найти в сообщении "Данные для
					авторизации" на вашей электронной почте (${inviteEmail})
				</div>

				<div class="form-group has-feedback">
					<input type="text" class="form-control text-input login-input" placeholder="E-mail" required
						   autofocus name="u" id="username" value="${param.email}"/>
					<span class="glyphicon form-control-feedback"></span>
				</div>

                <input type="password" class="form-control text-input" placeholder="Проверочный код" required name="p"
					   id="password"/>

                <div class="captcha-block">
                    <center>
                        <div class="g-recaptcha" data-sitekey="6Lf8GCITAAAAAAuST_60fckQPMdOgQIKa0djjONc"></div>
                    </center>
                </div>

				<button class="btn btn-lg btn-primary" type="submit" onclick="return login();">Вход</button>
			</form>
			<hr/>
			<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". |
				<a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
		</div>
	</c:when>
	<c:when test="${inviteIsExpire == 0 && inviteStatus == 1}">
		<div class="container">
			<form class="form-signin" role="form" method="post" action="/security/login">
				<a href="/"><img src="/i/logo.png"
								 style="height: 300px;display: block;margin-left: auto;margin-right: auto"/></a>

				<h1 class="form-signin-heading" style="text-align: center;">БЛАГОСФЕРА</h1>

				<h3 class="form-signin-heading" style="text-align: center;">Приглашение успешно подтверждено</h3>

				<div class="alert alert-info" role="alert"
					 style="text-align: center; background-color: #CCFFFF; margin-top: -8px;">
					Введите адрес вашей электронной почты и проверочный код.
					Проверочный код Вы можете найти в сообщении "Данные для
					авторизации" на вашей электронной почте (${inviteEmail})
				</div>

				<div class="form-group has-feedback">
					<input type="text" class="form-control text-input login-input" placeholder="E-mail" required
						   autofocus name="u" id="username" value="${param.email}"/>
					<span class="glyphicon form-control-feedback"></span>
				</div>

				<input type="password" class="form-control text-input" placeholder="Проверочный код" required name="p"
					   id="password"/>

                <div class="captcha-block">
                    <center>
                        <div class="g-recaptcha" data-sitekey="6Lf8GCITAAAAAAuST_60fckQPMdOgQIKa0djjONc"></div>
                    </center>
                </div>

				<button class="btn btn-lg btn-primary" type="submit" onclick="return login();">Вход</button>
			</form>
			<hr/>
			<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". |
				<a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
		</div>
	</c:when>
	<c:when test="${inviteIsExpire == 0 && inviteStatus == 2}">
		<div class="container">
			<a href="/"><img src="/i/logo.png"
							 style="height: 300px;display: block;margin-left: auto;margin-right: auto"/></a>

			<h1 class="form-signin-heading" style="text-align: center;">БЛАГОСФЕРА</h1>

			<h3 class="form-signin-heading" style="text-align: center">Приглашение уже было отклонено</h3>
			<hr/>
			<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". |
				<a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
		</div>
	</c:when>
	<c:when test="${inviteIsExpire == 0 && inviteIsAccept == 0}">
		<div class="container">
			<a href="/"><img src="/i/logo.png"
							 style="height: 300px;display: block;margin-left: auto;margin-right: auto"/></a>

			<h1 class="form-signin-heading" style="text-align: center;">БЛАГОСФЕРА</h1>

			<h3 class="form-signin-heading" style="text-align: center">Приглашение отклонено</h3>
			<hr/>
			<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". |
				<a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
		</div>
	</c:when>
	<c:when test="${inviteIsExpire != 0}">
		<div class="container">
			<a href="/"><img src="/i/logo.png"
							 style="height: 300px;display: block;margin-left: auto;margin-right: auto"/></a>

			<h1 class="form-signin-heading" style="text-align: center;">БЛАГОСФЕРА</h1>

			<h3 class="form-signin-heading" style="text-align: center">Срок действия вашего приглашения истек</h3>

			<hr/>
			<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". |
				<a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
		</div>
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
