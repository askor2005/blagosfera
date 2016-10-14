<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h1>Список тематик голосований</h1>
<hr />
	<a href="/voting/topic/edit" class="btn btn-primary">Создать новую тематику голосования</a>
<hr />

<ul id="topics-list">

</ul>

<script type="text/javascript">
	
	function getTopicMarkup(topic) {
		var markup = "<li>" + topic.name + " <a href='/voting/topic/edit?id=" + topic.id + "' class='glyphicon glyphicon-pencil'></a> <a href='#' onclick='return deleteTopic(" + topic.id + ", $(this));' class='glyphicon glyphicon-remove'></a></li>";
		if (topic.children.length > 0) {
			markup += "<ul>"
			$.each(topic.children, function(index, child){
				markup += getTopicMarkup(child);
			});
			markup += "</ul>"
		}
		return markup;
	}
	
	$(document).ready(function(){
		$.ajax({
			type : "get",
			dataType : "json",
			url : "/voting/topic/list.json",
			success : function(response) {
				$.each(response, function(index, topic) {
					$("ul#topics-list").append(getTopicMarkup(topic));
				});
			},
			error : function() {
				console.log("ajax error");
			}
		});
	});

	function deleteTopic(id, $link) {
		$.ajax({
			type : "post",
			dataType : "json",
			data : {
				id : id
			},
			url : "/voting/topic/delete.json",
			success : function(response) {
				if (response.result == "success") {
					$link.parents("li").fadeOut();
				} else {
					bootbox.alert(response.error);
				}
			},
			error : function() {
				console.log("ajax error");
			}
		});
		return false;
	}
</script>