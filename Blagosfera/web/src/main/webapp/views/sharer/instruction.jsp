<%@ page language="java" contentType="text/html; charset=utf-8"
		 pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>


<style type="text/css">

</style>

<script>
	function showUserGuide() {
		var h = 50; // высота в процентах
		var w = 50; // ширина в процентах
		if (window.screen) {
			h = window.screen.availHeight * h / 100; // высота в пикслах
			w = window.screen.availWidth * w / 100; // ширина в пикслах
		} else {
			// если вдруг отсутствует window.screen, то выставляем относительно величины экрана 1024х768
			h = 768 * h / 100; // высота в пикслах
			w = 1024 * w / 100; // ширина в пикслах
		}
		var win = window.open("/section/popup/Благосфера/Руководство Пользователя", "blagosfera_help_window", "scrollbars=1,height="+h+",width="+w);
		win.focus();
	}

</script>

<h1 style="text-align: center;">Добро пожаловать в Систему БЛАГОСФЕРА!</h1>

<hr/>

<div>
	${instructionText}
</div>

<hr/>
<form method="post">
	<div class="form-group text-center">
		<input id="show-user-guide" type="button" class="btn btn-lg btn-info" style="font-size: 14px;" value="Руководство пользователя Системы" onclick="showUserGuide();return false;" />
		&nbsp;
		<input type="submit" class="btn btn-lg btn-info" style="font-size: 14px;" value="Перейти к заполнению профиля" />
	</div>
	<div class="form-group text-center">
		<label>
			<input type="checkbox" name="instruction_showed" value="true" /> Больше не показывать инструкцию
		</label>
	</div>
</form>