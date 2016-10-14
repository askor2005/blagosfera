<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<t:insertAttribute name="item" />

<script type="text/javascript">
	
	var userId = null;
	
	function initScrollListener() {
		$("div#communities-list").empty();

        ScrollListener.init("/communities/list.json", "post", function() {
			var params = {};
			params.sharer_id = userId;
			params.status = "REQUEST";
			return params;
		}, function() {
			
		}, function(response) {
			$("div.list-not-found").remove();
			$.each(response.list, function(index, item){
				CommunitiesListItem.append(item, $("div#communities-list"));
			});
			if ($("div#communities-list").find(".community-item").length == 0) {
				$("div#communities-list").append("<div style='display : block;' class='row list-not-found'><div class='panel panel-default'><div class='panel-body'>Список пуст</div></div></div>");
			}
		});		
	}

	$(document).ready(function() {
		$(eventManager).bind("inited", function (event, user) {
			userId = user.id;
			initMyRequests();
		});
	});
		
	function initMyRequests(){
		initScrollListener();
		$("input#query").callbackInput(500, 4, function() {
			initScrollListener();
		});
		$("input#query").radomTooltip();
		$("input#creator").change(function() {
			initScrollListener();
		});

		$(radomEventsManager).bind("community-member.event", function(event, data) {
			if (data.member.user.id == userId) {
				switch(data.eventType) {
					case "accept_request":
					case "reject_request":
					case "cancel_request":
						CommunitiesListItem.remove(data.community, data.member);
						break;
					case "request":
						CommunitiesListItem.prepend(data.community, data.member);
						break;
				}
			}
		});

		/*$(radomEventsManager).bind("community-member.accept-request", function(event, data) {
			if (data.member.user.id == userId) {
				CommunitiesListItem.remove(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.reject-request", function(event, data) {
			if (data.member.user.id == userId) {
				CommunitiesListItem.remove(data.community, data.member);
			}
		});
		
		$(radomEventsManager).bind("community-member.request", function(event, data) {
			if (data.member.user.id == userId) {
				CommunitiesListItem.prepend(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.cancel-request", function(event, data) {
			if (data.member.user.id == userId) {
				CommunitiesListItem.remove(data.community, data.member);
			}
		});*/
		
	}
</script>

<h1>Мои запросы</h1>
<hr/>

<div id="communities-list">

</div>