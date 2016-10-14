<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
function getOptionMarkup(topic, level) {
	var indent = "";
	for (i = 0; i < level; i++) {
		indent += "&nbsp;&nbsp;&nbsp;";
	}
	var markup = "<option value='" + topic.id + "' " + ($.query.get("topic_id") == topic.id ? "selected='selected'" : "") + ">" + indent + topic.name + "</option>";
	if (topic.children.length > 0) {
		$.each(topic.children, function(index, child){
			markup += getOptionMarkup(child, level + 1);
		});
	}
	return markup;
}

$(document).ready(function(){
	$.ajax({
		type : "get",
		dataType : "json",
		url : "/voting/topic/list.json",
		success : function(response) {
			$.each(response, function(index, topic){
				$("select#topic").append(getOptionMarkup(topic, 0));
			});
		},
		error : function() {
			console.log("ajax error");
		}
	});
});

function doFilter() {
	window.location = "/voting/participation/list?topic_id=" + $("select#topic").val();
}
</script>

<h1>Голосования <small>в которых Вы участвуете</small></h1>

<hr />

<form role="form">
	<div class="form-group">
		<label for="topic">Фильтр по тематике</label>
		<select class="form-control" id="topic" name="topic_id" onchange="doFilter();">
			<option value="">Выберите тематику для фильтрации из списка</option>
		</select>
	</div>
</form>

<hr/>


<c:forEach items="${votings}" var="v">
	<dl>
		<dt>Тематика</dt>
		<dd>${v.topic.name}</dd>
	</dl>
	<dl>
		<dt>Вопрос</dt>
		<dd>${v.question}</dd>
	</dl>
	<c:if test="${v.startDate != null}">
		<dl>
			<dt>Дата начала</dt>
			<dd>${v.startDate}</dd>
		</dl>
	</c:if>
	<c:if test="${v.finishDate != null}">
		<dl>
			<dt>Дата завершения</dt>
			<dd>${v.finishDate}</dd>
		</dl>
	</c:if>
	<a href="/voting/participation/${v.id}">Подробнее</a>
	<hr />
</c:forEach>