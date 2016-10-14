<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${payment.status == 'PROCESSING'}">
	<h1>Платеж в обработке</h1>
</c:if>
<c:if test="${payment.status == 'SUCCESS'}">
	<h1>Платеж завершен</h1>
</c:if>
<c:if test="${payment.status == 'FAIL'}">
	<h1>Ошибка платежа</h1>
</c:if>

<div style="font-size : 14px;">
	<c:if test="${payment.status == 'PROCESSING'}">
		<p>В настоящий момент Ваш платеж обрабатывается. Для уточнения его статуса, обновите страницу немного позже.</p>
	</c:if>
	<c:if test="${payment.status == 'SUCCESS'}">
		<p>Платеж успешно завершен, средства зачислены на Вас счёт [${payment.account.type.name}] в системе R@MERA</p>
	</c:if>
	<c:if test="${payment.status == 'FAIL'}">
		<c:if test="${not empty payment.error}">
			<label>Сообщение об ошибке, полученное от платежной системы</label>
			<p>${payment.error}</p>
			<hr/>
		</c:if>
		<p>Попробуйте повторить платеж позже или обратитесь в службу технической поддержки</p>
	</c:if>
</div>
<hr/>
<c:if test="${payment.status == 'PROCESSING'}">
	<a class="btn btn-primary" href="#" onclick="window.location.reload(); return false;">Обновить страницу</a>
</c:if>
<a class="btn btn-default" href="/">Вернуться на главную страницу</a>