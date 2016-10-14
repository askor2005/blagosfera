<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script id="member-item-template" type="x-tmpl-mustache">

<div class="row member-item" data-member-id="{{member.id}}" data-sharer-id="{{member.user.id}}">
	<div class="col-xs-3">
		<div class="row">
			<div class="col-xs-12">
				<a class="sharer-item-avatar-link" href="{{member.user.link}}">
					<img style="display : block; width : 141px; height : 141px;" src="{{member.user.avatar}}" class="img-thumbnail">
					{{#member.user.online}}
						<img src="/i/icon-online.png" class="sharer-item-online-icon">
					{{/member.user.online}}
					{{^member.user.online}}
						<img src="/i/icon-offline.png" class="sharer-item-online-icon">
					{{/member.user.online}}
				</a>
				{{#member.user.online}}
					<span class="sharer-item-online-status">В сети</span>
				{{/member.user.online}}				
				{{^member.user.online}}
					<span class="sharer-item-online-status text-muted">Не в сети</span>
				{{/member.user.online}}
			</div>
			<div class="col-xs-12 text-center">
				<a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{member.user.id}}');" class="btn btn-sm btn-link">Написать сообщение</a>
			</div>
		</div>
	</div>
	<div class="col-xs-9">
		<h3><a href="{{member.user.link}}">{{member.user.fullName}}</a></h3>
		<p class="text-muted">
			{{communityName}}
		</p>
		<hr>

		<p>{{{distance}}}</p>

		<a class="btn btn-primary accept-link" href="#">Одобрить</a>
		<a class="btn btn-primary reject-link" href="#">Отклонить</a>

	</div>

	
</div>
</script>

<script type="text/javascript">

	ScrollListener.firstPage = null;

	var memberItemTemplate = $('#member-item-template').html();	
	Mustache.parse(memberItemTemplate);	

	function getMemberMarkup(member) {
		var model = {};
		member.user.avatar = Images.getResizeUrl(member.user.avatar, "c141");
		model.member = member;
		model.communityName = member.communityName;
		
		model.distance = "Запрос получен <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + member.requestDate + "'>" + member.sendRequestHumanString + " назад</span>";
		
		var markup = Mustache.render(memberItemTemplate, model);
		var $markup = $(markup);
		
		$markup.find("span.request-distance").radomTooltip({
			placement : "top",
			container : "body"
		});
		
		$markup.find("a.accept-link").click(function(){
			var $this = $(this);
			var $item = $this.parents(".member-item");
			var memberId = $item.attr("data-member-id");
			var sharerId = $item.attr("data-sharer-id");
			$.radomJsonPost("/communities/accept_request.json", {
				member_id : memberId,
				check_parent : false
			}, function(response) {
				$item.fadeOut(function(){
					LeftSidebar.changeRequestsCount(-1);
					$item.next("hr").remove();
					$item.remove();
				});
			});
			return false;
		});
		
		$markup.find("a.reject-link").click(function(){
			var $this = $(this);
			var $item = $this.parents(".member-item");
			var memberId = $item.attr("data-member-id");
			$.radomJsonPost("/communities/reject_request.json", {
				member_id : memberId
			}, function(response) {
				LeftSidebar.changeRequestsCount(-1);
				$item.fadeOut(function(){
					$item.next("hr").remove();
					$item.remove();
				});
			});
			return false;
		});
		return $markup;
	}

	function initScrollListener() {
		$("div#members-list").empty();
		ScrollListener.init("/communities/requests.json", "post", null, function() {}, function(response) {
            //response = JSON.parse(response);

			if (response.result == "error") {
				bootbox.alert(response.message);
			} else {
				$.each(response, function(index, member){
					if ($("div[data-member-id=" + member.id + "]").length == 0) {
						$("div#members-list").append(getMemberMarkup(member));
						$("div#members-list").append("<hr/>");
					}
				});
			}
		});		
	}

	$(document).ready(function() {
		initScrollListener();
		radomStompClient.subscribeToUserQueue("community_request", function(messageBody) {
			$("div#members-list").prepend("<hr/>");
			var member = messageBody.member;
			member.community = messageBody.community;
			$("div#members-list").prepend(getMemberMarkup(member));
		});
		
		radomStompClient.subscribeToUserQueue("community_cancel_request", function(messageBody) {
			var $item = $("div[data-sharer-id=" + messageBody.member.user.id + "]");
			$item.fadeOut(function(){
				$item.next("hr").remove();
				$item.remove();
			});
		});
		
	});
</script>

<h1>Список запросов</h1>
<hr/>
<div id="members-list"></div>
