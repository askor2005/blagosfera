<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<t:insertAttribute name="favicon" />

<title>Пользовательское соглашение</title>

<link href="/css/bootstrap.css" rel="stylesheet">
<link href="/css/signin.css" rel="stylesheet">

<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery.cookie.js"></script>
<script type="text/javascript" src="/js/bootstrap.js"></script>

</head>
<body>
	<div class="container" style="text-align: center;">
		<img id="logo" src="/i/logo.png" />
		<h2 class="form-signin-heading">Пользовательское соглашение</h2>
		<hr/>
		<p>${userAgreement}</p>
		<hr/>
		<p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". | <a href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
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