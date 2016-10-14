<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script type="text/javascript">

</script>

<h1>Управление голосованием</h1>

<hr />

<dl>
	<dt>Тематика голосования</dt>
	<dd>${voting.topic.name}</dd>
</dl>
<dl>
	<dt>Вопрос, поставленный на голосование </dt>
	<dd>${voting.question}</dd>
</dl>
<dl>
	<dt>Предмет голосования</dt>
	<dd>
		<c:if test="${voting.answerIsSharer}">Выборы</c:if>
		<c:if test="${not voting.answerIsSharer}">Принятие решения</c:if>
	</dd>
</dl>
<c:if test="${voting.answerIsSharer}">
	<dl>
		<dt>Самовыдвижение</dt>
		<dd>
			<c:if test="${voting.allowSelfPromotion}">Разрешено</c:if>
			<c:if test="${not voting.allowSelfPromotion}">Запрещено</c:if>
		</dd>
	</dl>
</c:if>
<dl>
	<dt>Система голосования</dt>
	<dd>
		${voting.system.name}
		<br/>
		${voting.system.description}
	</dd>
</dl>
<dl>
	<dt>Дата начала</dt>
	<dd>${voting.startDate}</dd>
</dl>
<dl>
	<dt>Дата завершения голосования</dt>
	<dd>${voting.deadlineDate}</dd>
</dl>
<dl>
	<dt>Текущий этап</dt>
	<dd>${phaseName}</dd>
</dl>

<c:if test="${voting.phase == 'VOTES'}">
	<div class="alert alert-info">Идет прием голосов от участников</div>
	
	<h4>Последние проголосовавшие</h4>
	
	<ul id="last-bulletins">
		<c:forEach items="${lastBulletins}" var="b">
			<li>
				<fmt:formatDate pattern="dd.MM.yyyy HH:mm:ss" value="${b.date}" /> ${b.sharer.fullName} - 
				<c:if test="${b.rejected}"> отказался</c:if>
				<c:if test="${not b.rejected}"> проголосовал</c:if>
			</li>
		</c:forEach>
	</ul>
	
	<script type="text/javascript">
		$(document).ready(function() {
			radomStompClient.subscribeToUserQueue("new_vote", function(messageBody){
				$("ul#last-bulletins").prepend("<li>" + messageBody.date + " " + messageBody.sharer.fullName + " - " + (messageBody.rejected ? "отказался" : "проголосовал") + "</li>");
				if ($("ul#last-bulletins li").length > 10) {
					$("ul#last-bulletins li").last().remove();
				}
			});
		});	
	</script>
</c:if>

<c:if test="${voting.phase == 'RESULTS'}">

	<c:if test="${voting.success}">
		<div class="alert alert-success">Голосование признано состоявшимся</div>
	</c:if>
	
	<c:if test="${not voting.success}">
		<div class="alert alert-danger">Голосование признано не состоявшимся</div>
	</c:if>	
	<hr />
	<dl>
		<dt>Варианты ответа</dt>
		<dd>
			<c:forEach items="${voting.answers}" var="a">
				${a.text}<c:if test="${not empty a.value}"> - ${a.value} </c:if> <c:if test="${a.accepted}"><span class="glyphicon glyphicon-ok"></span></c:if>
				<br/>
			</c:forEach>
		</dd>
	</dl>
	<hr/>
	<dl>
		<dt>Участники</dt>
		<dd id="participants-list">
			<c:forEach items="${voting.bulletins}" var="b">
				${b.sharer.fullName} -
				<c:if test="${not b.done}">Не голосовал</c:if>
				<c:if test="${b.done}">
					<c:if test="${b.rejected}">Отказался от голосования</c:if>
					<c:if test="${not b.rejected}">Проголосовал</c:if>
				</c:if>
				<br/>
			</c:forEach>
		</dd>
	</dl>
	<hr/>

</c:if>