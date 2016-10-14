<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">

.tooltip-inner {
	max-width: 400px;
}

.news-writers-list {
	padding-left : 0;
}

.news-writers-list-item {
	list-style : none;
	padding : 3px 10px;
}

.news-writers-list-item .glyphicon-remove {
	margin-top : 2px;
	margin-right : 2px;
}

.news-writers-list-item:hover {
	background-color : #eee;
}

.moderators-list {
	padding-left : 0;
}

.moderators-list-item {
	list-style : none;
	padding : 3px 10px;
}

.moderators-list-item .glyphicon-remove {
	margin-top : 2px;
	margin-right : 2px;
}

.moderators-list-item:hover {
	background-color : #eee;
}

</style>

<script type="text/javascript">
	function addNewsWriter(memberId, memberFullName) {
		$.radomJsonPost("/communities/add_news_writer.json", {
			member_id : memberId
		}, function() {
			$(".news-writers-list ul").append("<li class='news-writers-list-item' id='news-writers-list-item-" + memberId + "'>" + memberFullName + "<a href='#' class='delete-news-writer-link pull-right glyphicon glyphicon-remove'></a></li>")
			$("input.add-news-writer-input").val("");
		});
	}
	
	function deleteNewsWriter(memberId) {
		$.radomJsonPost("/communities/delete_news_writer.json", {
			member_id : memberId
		}, function() {
			$("li#news-writers-list-item-" + memberId).remove();	
		});		
	}
	
	function addModerator(memberId, memberFullName) {
		$.radomJsonPost("/communities/add_moderator.json", {
			member_id : memberId
		}, function() {
			$(".moderators-list ul").append("<li class='moderators-list-item' id='moderators-list-item-" + memberId + "'>" + memberFullName + "<a href='#' class='delete-moderator-link pull-right glyphicon glyphicon-remove'></a></li>")
			$("input.add-moderator-input").val("");
		});
	}
	
	function deleteModerator(memberId) {
		$.radomJsonPost("/communities/delete_moderator.json", {
			member_id : memberId
		}, function() {
			$("li#moderators-list-item-" + memberId).remove();	
		});		
	}
	
	$(document).ready(function() {
		
		var communityId = "${community.id}";
		
		$(".news-writers-list ul").on("click", "a.delete-news-writer-link", function() {
			var $link = $(this);
			var $li = $link.parents("li");
			var memberId = $li.attr("id").replace("news-writers-list-item-", "");
			deleteNewsWriter(memberId);
			return false;
		});
		
		$("input.add-news-writer-input").typeahead({
		    onSelect: function(item) {
		    	addNewsWriter(item.value, item.text);			
		    },
		    ajax: {
		        url: "/communities/members.json",
		        timeout: 500,
		        displayField: "fullName",
		        triggerLength: 1,
		        method: "post",
		        loadingClass: "loading-circle",
		        preDispatch: function (query) {
		            return {
		                query : query,
						"status_list[]" : ["MEMBER", "REQUEST_TO_LEAVE", "LEAVE_IN_PROCESS"],
		                community_id : communityId
		            }
		        },
		        preProcess: function (response) {
		            if (response.result == "error") {
		                console.log("ajax error")
		                return false;
		            }
		            $.each(response, function(index, member) {
		            	member.fullName = member.sharer.fullName;
		            });
		            return response;
		        }
		    }
		});
		
		$(".moderators-list ul").on("click", "a.delete-moderator-link", function() {
			var $link = $(this);
			var $li = $link.parents("li");
			var memberId = $li.attr("id").replace("moderators-list-item-", "");
			deleteModerator(memberId);
			return false;
		});
		
		$("input.add-moderator-input").typeahead({
		    onSelect: function(item) {
		    	addModerator(item.value, item.text);			
		    },
		    ajax: {
		        url: "/communities/members.json",
		        timeout: 500,
		        displayField: "fullName",
		        triggerLength: 1,
		        method: "post",
		        loadingClass: "loading-circle",
		        preDispatch: function (query) {
		            return {
		                query : query,
						"status_list[]" : ["MEMBER", "REQUEST_TO_LEAVE", "LEAVE_IN_PROCESS"],
		                community_id : communityId
		            }
		        },
		        preProcess: function (response) {
		            if (response.result == "error") {
		                console.log("ajax error")
		                return false;
		            }
		            $.each(response, function(index, member) {
		            	member.fullName = member.sharer.fullName;
		            });
		            return response;
		        }
		    }
		});
		
		
		$("a#community-delete-link").click(function() {
			bootbox.confirm("Подтвердите удаление объединения. В дальнейшем Вы не сможете отменить данное действие, объединение будет удалено безвозвратно.", function(result){
				if (result) {
					$.radomJsonPost("/communities/delete.json", {
						community_id : communityId
					}, function(response){
						window.location = "/group";
					});
				}
			});
			return false;
		});
		
	});
</script>

<t:insertAttribute name="communityHeader" />
<hr/>
<t:insertAttribute name="menu" />

<div class="news-writers-list">
	
	<h2>Лента новостей</h2>
	
	<p class="text-muted">Ниже настраивается список участников объединения, которым разрешено писать новости в ленте объединения. Чтобы добавить в этот список кого-либо, начните вводить фамилию или имя в поле ввода ниже и выберите нужного участника из выпадающего списка. Чтобы исключить участника из списка тех, кому разрешено писать новости, нажмите на крестик напротив его имени в списке ниже. Организатору объединения нельзя запретить писать новости в ленте объединения.</p>

	<ul class="news-writers-list">
		<c:forEach items="${newsWriters}" var="w">
			<li class="news-writers-list-item" id="news-writers-list-item-${w.id}">
				${w.sharer.fullName}
				<c:if test="${not w.creator}">
					<a href="#" class="delete-news-writer-link pull-right glyphicon glyphicon-remove"></a>
				</c:if>
			</li>
		</c:forEach>
	</ul>
	<br/>
	<div class="form-group">
		<input type="text" autocomplete="off" class="add-news-writer-input form-control" placeholder="Начните вводить фамилию или имя участника" />
	</div>
	
	<hr/>
</div>
<div class="moderators-list">
	<h2>Модераторы</h2>
	
	<p class="text-muted">Ниже настраивается список участников объединения, которым разрешено контролировать информацию, добавляемую участниками объединения (новости, обсуждения и т.д.). Чтобы добавить в этот список кого-либо, начните вводить фамилию или имя в поле ввода ниже и выберите нужного участника из выпадающего списка. Чтобы исключить участника из списка модераторов - нажмите на крестик напротив его имени в списке ниже. Организатор объединения является модератором по умолчанию.</p>

	<ul class="news-writers-list">
		<c:forEach items="${moderators}" var="w">
			<li class="moderators-list-item" id="moderators-list-item-${w.id}">
				${w.sharer.fullName}
				<c:if test="${not w.creator}">
					<a href="#" class="delete-moderator-link pull-right glyphicon glyphicon-remove"></a>
				</c:if>
			</li>
		</c:forEach>
	</ul>
	<br/>
	<div class="form-group">
		<input type="text" autocomplete="off" class="add-moderator-input form-control" placeholder="Начните вводить фамилию или имя участника" />
	</div>
	
	<hr/>
</div>
<div class="delete-community">
	<h2>Удаление объединения</h2>
	
	<p class="text-muted">Организатор объединения имеет возможность удалить ранее созданное объединение. Однака важно понимать, что данное действие необратимо, и после подтверждения уделения, объединение будет удалено безвозвратно без возможности восстановления. Если Вам все же необходимо удалить данное объединение, нажмите кнопку ниже и подтвердите свое решение.</p>
	<div class="form-group text-center">
		<a href="#" class="btn btn-danger btn-lg" id="community-delete-link">Удалить объединение</a>
	</div>
	<hr/>
</div>