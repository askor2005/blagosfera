<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${topic != null && topic.parent != null}">
	<script type="text/javascript">
		var parentId = ${topic.parent.id};
	</script>
</c:if>

<c:if test="${topic == null || topic.parent == null}">
	<script type="text/javascript">
		var parentId = null;
	</script>
</c:if>

<script type="text/javascript">

	function getOptionMarkup(topic, level) {
		var indent = "";
		for (i = 0; i < level; i++) {
			indent += "&nbsp;&nbsp;&nbsp;";
		}
		var markup = "<option value='" + topic.id + "' " + ((topic.id == parentId) ? "selected='selected'" : "") + ">" + indent + topic.name + "</option>";
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
					$("select#parent").append(getOptionMarkup(topic, 0));
				});
			},
			error : function() {
				console.log("ajax error");
			}
		});
	});
</script>

<c:if test="${topic == null}">
	<h1>Создание тематики голосований</h1>
</c:if>

<c:if test="${topic != null}">
	<h1>Редактирование тематики голосований<br/>${topic.name}</h1>
</c:if>

<hr />

<c:if test="${error != null}">
	<div class="alert alert-danger" role="alert">${error}</div>
	<hr />
</c:if>

<form role="form" method="post">
  <div class="form-group">
    <label for="name">Название</label>
    <input type="text" class="form-control" id="name" name="name" placeholder="Название" required value="${topic.name}" />
  </div>
  <div class="form-group">
    <label for="parent">Родительская тематика</label>
    <select class="form-control" id="parent" name="parent_id">
    	<option value=""> - Нет - </option>
    </select>
  </div>
  <button type="submit" class="btn btn-primary">Сохранить</button>
  <a href="/voting/topic/list" class="btn btn-default">К списку тематик</a>
</form>