<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<script type="text/javascript">

	function selfPromotion() {
		$.radomJsonPost("/voting/participation/self_promotion.json", $("form#self-promotion-form").serialize(), function(response){
			window.location.reload();
		});
		return false;
	}

	function vote() {
		$.radomJsonPost("/voting/participation/vote.json", $("form#vote-form").serialize(), function(response){
			window.location.reload();
		});		
		return false;
	}

	function reject() {
		$.radomJsonPost("/voting/participation/reject.json", $("form#vote-form").serialize(), function(response){
			window.location.reload();
		});		
		return false;
	}
	
</script>

<h1>Участие в голосовании</h1>

<hr />

<c:if test="${voting.protocol != null}">
	<h3>Протокол</h3>
	<a target="_blank" href="/documents/print/${voting.protocol.id}" class="btn btn-default">
		<span class="glyphicon glyphicon-print"></span> Версия для печати
	</a>
	<a target="_blank" href="/documents/download/${voting.protocol.id}/Протокол_голосования_${voting.id}.html" class="btn btn-default">
		<span class="glyphicon glyphicon-download-alt"></span> Загрузить на диск
	</a>
	<hr/>
</c:if>

<dl>
	<dt>Тематика</dt>
	<dd>${voting.topic.name}</dd>
</dl>
<dl>
	<dt>Вопрос</dt>
	<dd>${voting.question}</dd>
</dl>

<dl>
	<dt>Предмет голосования</dt>
	<dd>
		<c:if test="${voting.answerIsSharer}">Кандидатуры участников</c:if>
		<c:if test="${not voting.answerIsSharer}">Варианты решения</c:if>
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

<c:if test="${voting.startDate != null}">
	<dl>
		<dt>Дата начала</dt>
		<dd>${voting.startDate}</dd>
	</dl>
</c:if>

<dl>
	<dt>Дата завершения голосования</dt>
	<dd>${voting.deadlineDate}</dd>
</dl>

<dl>
	<dt>Текущий этап</dt>
	<dd>${phaseName}</dd>
</dl>

<c:if test="${(voting.phase == 'ANSWERS') and (voting.allowSelfPromotion)}">
	<form role="form" id="self-promotion-form">
		<div class="alert alert-info">В данном голосовании разрешено самовыдвижение, поэтомы Вы можете предложить собственную кандидатуру.</div>
		<div class="form-group">
			<a href="#" onclick="return selfPromotion();" class="btn btn-primary">Выдвинуть свою кандидатуру</a>
		</div>
		<input value="${voting.id}" type="hidden" name="voting_id" />
	</form>
</c:if>
<c:if test="${voting.phase == 'VOTES'}">
	<c:if test="${!bulletin.done}">
		<form class="form-horizontal" role="form" id="vote-form">
			<c:forEach items="${voting.answers}" var="a">
				<div class="form-group">
					<label for="answer-${a.id}" class="col-xs-10 control-label" style="text-align : left;">${a.text}</label>
	    			<div class="col-xs-2 text-center">
	    				<c:if test="${voting.system.needTextInput}">
	      					<input type="text" class="form-control" id="answer-${a.id}" name="a:${a.id}" placeholder="" style="text-align : center;" />
	      				</c:if>
	      				<c:if test="${not voting.system.needTextInput}">
	      					<input type="checkbox" class="" id="answer-${a.id}" name="a:${a.id}" style="" />
	      				</c:if>
	    			</div>
	  			</div>
  			</c:forEach>
			<div class="col-xs-12">
				<div class="form-group">	
					<label>Коментарий</label>
					<textarea class="form-control" name="comment"></textarea>				
				</div>
			</div>
  			<div class="col-xs-12">
				<div class="form-group">
					<a href="#" onclick="return vote();" class="btn btn-primary">Проголосовать</a>
					<a href="#" onclick="return reject();" class="btn btn-danger">Не голосую</a>
				</div>
			</div>
			<input value="${voting.id}" type="hidden" name="voting_id" />
		</form>
	</c:if>
	<c:if test="${bulletin.done}">
		<c:if test="${bulletin.rejected}">
			<div class="alert alert-danger">Вы отказались голосовать</div>
		</c:if>	
		<c:if test="${not bulletin.rejected}">
			<div class="alert alert-info">Вы уже проголосовали</div>
		</c:if>	
		
	</c:if>
</c:if>
<c:if test="${voting.phase == 'RESULTS'}">
	<c:if test="${voting.success}">
		<div class="alert alert-success">Голосование признано состоявшимся</div>
	</c:if>
	<c:if test="${not voting.success}">
		<div class="alert alert-danger">Голосование признано не состоявшимся</div>
	</c:if>	
</c:if>
<hr />
<dl>
	<dt>Варианты ответа</dt>
	<dd>
		<c:forEach items="${voting.answers}" var="a">
			${a.text}<c:if test="${not empty a.value}"> - ${a.value} </c:if>
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
