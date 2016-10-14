<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<t:insertAttribute name="favicon" />

<title>БЛАГОСФЕРА</title>


<style type="text/css">
	.form-control-feedback {
		top : 5px;
	}
	.form-group {
		margin-bottom : 0px !important;
		z-index : 2;
	}
</style>

<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/signin.css" rel="stylesheet">

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.cookie.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap.js"></script>

<script type="text/javascript">
	function login() { 
		//if ($("#biometric:checked").length == 0) {
			$("form.form-signin").submit();			
		//} else {
		//	bootbox.alert("Функция в разработке, используйте обычный вход");
		//}
		return false;
	}
	
	function setBiometric() {
		if ($("input#biometric").length > 0 && $("input#biometric")[0].checked) {
			//$("form .text-input").val("");
			//$("form .text-input").attr("readonly", "readonly");
			$.cookie("biometric", "true");
			setTimeout(function() {
				$("button[type=submit]").focus();
			});
		} else {
			$("form .text-input").removeAttr("readonly");
			$.removeCookie("biometric");
			//$("input.text-input[type=text]").focus();
		    setTimeout(function() {
		    	$("input.text-input[type=text]").focus();
		    }, 0);
		}
	}

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
	
	$(document).ready(function() {
		if ($.cookie("biometric")) {
			$("input#biometric").attr("checked", "checked");
		}
		$("input#biometric").change(setBiometric);
		setBiometric();
		
		var $container = $("div.container");
		var windowHeight = $(window).height();
		var containerHeight = $container.height();
		
		if (containerHeight < windowHeight) {
			$container.css("margin-top", ((windowHeight - containerHeight) / 2));
		}
		$container.fadeIn();

		var usernameInput = $("#username");
		var passwordInput = $("#password");

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
		
	});
</script>

</head>

<body>

	<div class="container">
		<form class="form-signin" role="form" method="post" action="security/login">
			<img id="logo" src="${pageContext.request.contextPath}/i/logo.png" alt="Логотип системы Благосфера"/>
			<h2 class="form-signin-heading">Аккаунт активирован</h2>
			<div class="alert alert-success" role="alert">
				Ваш аккаунт активирован, теперь Вы можете войти в кабинет, используя e-mail и пароль, указанные при регистрации
			</div>
			<div class="form-group has-feedback">
				<input type="text" class="form-control text-input login-input" placeholder="Логин или E-mail" required autofocus id="username" name="u" />
			</div>
			<input type="password" class="form-control text-input" placeholder="Пароль" required id="password" name="p" />
			<input type="hidden" value="${header['referer']}" name="referer">
        	<br/>
			<button class="btn btn-lg btn-primary" type="submit" onclick="return login();">Вход</button>        	
        	<hr />
			<a href="${pageContext.request.contextPath}/register">Зарегистрироваться</a> | <a href="#" onclick="bootbox.alert('Функция в разработке'); return false;">Восстановить доступ</a>

			<hr/>
		</form>
		<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". | <a href="${pageContext.request.contextPath}/Благосфера/наши партнеры">Наши партнеры</a></p>
	</div>
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