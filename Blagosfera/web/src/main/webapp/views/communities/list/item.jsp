<%@ page language="java" contentType="text/html; charset=utf-8"
		 pageEncoding="utf-8"%>

<script id="community-item-template" type="x-tmpl-mustache">
<div class="row community-item" data-community-id="{{community.id}}" data-member-id="{{community.memberId}}" style="{{rowStyle}}" data-is-creator="{{#creator}}true{{/creator}}{{^creator}}false{{/creator}}">
	<div class="col-xs-3">
		<div class="row">
			<div class="col-xs-12">
				<a class="sharer-item-avatar-link" href="{{link}}">
					<img style="display : block; {{#community.verified}}background-color : rgb(51, 142, 207);{{/community.verified}}" src="{{community.avatar_c250}}" class="img-thumbnail">
				</a>
			</div>
		</div>
		<p class="text-muted text-center" style="margin : 5px 0 0 0;"><i class="fa fa-user"></i> Участников: {{community.membersCount}}</p>
		<p class="text-muted text-center"style="margin : 0 0 0 0;"><i class="fa fa-users"></i> Подгрупп: {{community.subgroupsCount}}</p>
	</div>
	<div class="col-xs-9">
		<div class="row">
			<div class="col-xs-9">
				<h3>
					<a href="{{community.link}}">{{community.name}}</a>
				</h3>
				<p class="text-muted">
					{{#community.visible}}
						<a href="#" class="glyphicon glyphicon-eye-open not-invisible" style="font-size : 18px; text-decoration : none !important;"></a>
					{{/community.visible}}
					{{^community.visible}}
						<a href="#" class="glyphicon glyphicon-eye-close is-invisible" style="font-size : 18px; text-decoration : none !important;"></a>
					{{/community.visible}}
					<a href="#" class="access-type {{accessTypeGlyphicon}}" style="font-size : 18px; text-decoration : none !important; margin-left : 5px;" data-title="Тип доступа: {{accessTypeLabel}}"></a>
					<span class="pull-right">Дата создания: {{community.createdAtFormatted}}</span>
				</p>
				<p>{{{community.announcement}}}</p>
			</div>
			<div class="col-xs-3">
				<div class="row">
					<div class="col-xs-12">
						<a class="sharer-item-avatar-link" href="{{community.creatorLink}}">
							<img style="display : block; width : 84px; height : 84px;" src="{{community.creatorAvatar}}" class="img-thumbnail tooltiped-avatar" data-sharer-ikp="{{community.creatorIkp}}" data-placement="left">
						</a>
					</div>
				</div>
				<p class="text-muted text-left">Основатель</p>
			</div>
			<div class="col-xs-12 text-right" style="margin-top : 10px; padding-right : 30px;">
				<span style="margin-bottom : 10px; display : block;">{{{currentStatus}}}</span>
				{{#allowJoin}}
					<a class="btn btn-success join-link" href="#">Вступить</a>
				{{/allowJoin}}
				{{#allowRequest}}
					<a class="btn btn-primary request-link" href="#">Подать заявку</a>
				{{/allowRequest}}
				{{#allowCancel}}
					<a class="btn btn-warning cancel-link" href="#">Отменить</a>
				{{/allowCancel}}
				{{#allowAccept}}
					<a class="btn btn-success accept-link" href="#">Принять</a>
				{{/allowAccept}}
				{{#allowReject}}
					<a class="btn btn-warning reject-link" href="#">Отклонить</a>
				{{/allowReject}}
				{{#allowLeave}}
					<a class="btn btn-warning leave-link" href="#">Выйти</a>
				{{/allowLeave}}
				{{#allowOpen}}
					<a class="btn btn-info" href="{{community.link}}">Открыть</a>
				{{/allowOpen}}
				{{#allowDelete}}
					<a href="#" class="btn btn-danger delete-link">Удалить</a>
				{{/allowDelete}}
				{{#allowRestore}}
					<a href="#" class="btn btn-info restore-link">Восстановить</a>
				{{/allowRestore}}
			</div>
		</div>
	</div>	
</div>
<hr/>
</script>

<script id="community-item-tree-template" type="x-tmpl-mustache">
	<li>
		<div class="community-item communities-tree-item" data-community-id="{{community.id}}" data-member-id="{{community.memberId}}" data-is-creator="{{#creator}}true{{/creator}}{{^creator}}false{{/creator}}">
			<div class="outer-wrapper">
				<div class="inner-wrapper">
					<img class="photo img-thumbnail" src="{{community.avatar_c68}}" />
					<div class="text row">
						<div class="col-xs-6 left-text-block">
							<a class="name" href="{{community.link}}">{{community.name}}</a>
							<div class="status text-muted">
								{{{currentStatus}}}
							</div>
						</div>
						<div class="col-xs-6 text-right text-muted right-text-block">
							Организатор: <a href="{{community.creator.link}}" data-sharer-ikp="{{community.creatorIkp}}" class="tooltiped-avatar">{{community.creatorShortName}}</a>
							<br/>
							Дата создания: {{community.createdAt}}
						</div>
					</div>
					<div class="row controls">
						<div class="col-xs-2 icons">
							<a href="#" class="access-type {{accessTypeGlyphicon}}" style="font-size : 18px; text-decoration : none !important; margin-left : 5px;" data-title="Тип доступа: {{accessTypeLabel}}"></a>
						</div>
						<div class="col-xs-10 text-right buttons">
							{{#allowJoin}}
								<a class="btn btn-success btn-xs join-link" href="#">Вступить</a>
							{{/allowJoin}}
							{{#allowRequest}}
								<a class="btn btn-primary btn-xs request-link" href="#">Подать заявку</a>
							{{/allowRequest}}
							{{#allowCancel}}
								<a class="btn btn-warning btn-xs cancel-link" href="#">Отменить</a>
							{{/allowCancel}}
							{{#allowAccept}}
								<a class="btn btn-success btn-xs accept-link" href="#">Принять</a>
							{{/allowAccept}}
							{{#allowReject}}
								<a class="btn btn-warning btn-xs reject-link" href="#">Отклонить</a>
							{{/allowReject}}
							{{#allowLeave}}
								<a class="btn btn-warning btn-xs leave-link" href="#">Выйти</a>
							{{/allowLeave}}
							{{#allowOpen}}
								<a class="btn btn-info btn-xs" href="{{community.link}}">Открыть</a>
							{{/allowOpen}}
							{{#allowDelete}}
								<a href="#" class="btn btn-danger btn-xs delete-link">Удалить</a>
							{{/allowDelete}}
							{{#allowRestore}}
								<a href="#" class="btn btn-info btn-xs restore-link">Восстановить</a>
							{{/allowRestore}}
						</div>
					</div>
				</div>
			</div>
		</div>
	</li>
</script>

<script type="text/javascript">

	var CommunitiesListItem = {

		sharerId : null,

		template : $('#community-item-template').html(),
		templateParsed : false,

		treeTemplate : $('#community-item-tree-template').html(),
		treeTemplateParsed : false,

		getTemplate : function() {
			if (!CommunitiesListItem.templateParsed) {
				CommunitiesListItem.templateParsed = true;
				Mustache.parse(CommunitiesListItem.template);
			}
			return CommunitiesListItem.template;
		},

		getTreeTemplate : function() {
			if (!CommunitiesListItem.treeTemplateParsed) {
				CommunitiesListItem.treeTemplateParsed = true;
				Mustache.parse(CommunitiesListItem.treeTemplate);
			}
			return CommunitiesListItem.treeTemplate;
		},

		getCurrentStatusText : function(community) {

			if (community.deleted) {
				return "Объединение удалено";
			} else {
				if (!community.memberId || !community.memberStatus) {
					return "Вы не состоите в " + (community.root ? "объединении" : "группе");
				} else {
					switch (community.memberStatus) {
						case "MEMBER":
							if (self.creator) {
								return "Вы состоите в " + (community.root ? "объединении" : "группе");
							} else {
								return "Вы состоите в " + (community.root ? "объединении" : "группе");
							}
						case "REQUEST":
							return "Вы отправили запрос <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + community.memberRequestDate + "'>" + (community.memberRequestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(community.memberRequestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
						case "INVITE":
							return "Вы получили приглашение <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + community.memberRequestDate + "'>" + (community.memberRequestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(community.memberRequestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
						default:
							return "";
					}
				}
			}
		},

		getTreeMarkup : function(community) {
			var model = CommunitiesListItem.initModel(community);
			var markup = Mustache.render(CommunitiesListItem.getTreeTemplate(), model);
			var $markup = $(markup);
			$markup =  CommunitiesListItem.initMarkup($markup, community);
			return $markup;
		},

		getMarkup : function(community) {
			var model = CommunitiesListItem.initModel(community);
			var markup = Mustache.render(CommunitiesListItem.getTemplate(), model);
			var $markup = $(markup);
			$markup =  CommunitiesListItem.initMarkup($markup, community);
			return $markup;
		},

		initModel : function(community) {
			var model = {};

            var createdAt = new moment(community.createdAt);
            community.createdAtFormatted = createdAt.format("DD.MM.YYYY");

			community.creatorAvatar = Images.getResizeUrl(community.creatorAvatar, "c84");
			community.avatar_c250 = Images.getResizeUrl(community.avatar, "c250");
			community.avatar_c68 = Images.getResizeUrl(community.avatar, "c68");

			model.community = community;
			model.currentStatus = CommunitiesListItem.getCurrentStatusText(community);
			model.allowJoin = !community.deleted && ((!community.memberId || !community.memberStatus) && community.accessType == "OPEN");
			model.allowRequest = !community.deleted &&  ((!community.memberId || !community.memberStatus) && community.accessType != "OPEN");
			model.allowAccept = !community.deleted &&  (community.memberId && community.memberStatus == "INVITE");
			model.allowReject = !community.deleted &&  (community.memberId && community.memberStatus == "INVITE");
			model.allowCancel = !community.deleted &&  (community.memberId && community.memberStatus == "REQUEST");
			model.allowLeave = !community.deleted &&  (community.memberId && !community.memberCreator && community.memberStatus == "MEMBER");
			model.allowOpen =  !community.deleted;
			model.allowDelete = !community.deleted && community.canDelete;
			model.allowRestore = community.deleted && community.canRestore;
			model.creator = CommunitiesListItem.sharerId == community.creatorId;
			switch (community.accessType) {
				case "OPEN":
					model.accessTypeGlyphicon = "glyphicon glyphicon-ok text-success";
					model.accessTypeLabel = community.root ? "открытое объединение" : "открытая группа";
					break;
				case "RESTRICTED":
					model.accessTypeGlyphicon = "fa fa-key text-warning";
					model.accessTypeLabel = (community.root ? "объединение" : "группа") + " с ограниченным доступом";
					break;
				case "CLOSE":
					model.accessTypeGlyphicon = "fa fa-lock text-danger";
					model.accessTypeLabel = community.root ? "закрытое объединение" : "закрытая группа";
					break;
			}

			return model;
		},

		initMarkup : function($markup, community) {
			$markup.find("a.access-type").click(function() {
				return false
			}).radomTooltip({
				placement : "top",
				container : "body"
			});

			$markup.find("a.is-invisible").click(function() {
				return false
			}).radomTooltip({
				title : community.root ? "невидимое объединение" : "невидимая группа",
				placement : "top",
				container : "body"
			});

			$markup.find("a.not-invisible").click(function() {
				return false
			}).radomTooltip({
				title : community.root ? "видимое для всех объединение" : "видимая для всех группа",
				placement : "top",
				container : "body"
			});

			$markup.find("a.delete-link").radomTooltip({
				position : "top",
				container : "body",
				title : community.root ? "Удалить объединение" : "Удалить подгруппу"
			});

			$markup.find("a.delete-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".community-item");
				var isCreator = $item.attr("data-is-creator") == "true";

				function doDelete(comment) {
					$.radomFingerJsonAjax({
						url : "/communities/delete.json",
						data : {
							community_id : community.id,
							comment : comment
						},
						type : "post",
						successRequestMessage : "Удаление завершено",
						errorMessage : "Ошибка удаления",
						successCallback : function(response) {
                            $(radomEventsManager).trigger("community.deleted", response);
						}
					});
				}

				if (isCreator) {
					bootbox.confirm("Подтвердите удаление", function(result) {
						if (result) {
							doDelete();
						}
					});
				} else {
					bootbox.prompt("Укажите причину удаления", function(result) {
						if (result) {
							doDelete(result)
						}
					});
				}

				return false;
			});

			$markup.find("a.restore-link").radomTooltip({
				position : "top",
				container : "body",
				title : "Восстановить объединение"
			});

			$markup.find("a.restore-link").click(function(){
				bootbox.confirm("Подтвердите восстановление", function(result) {
					if (result) {
						$.radomFingerJsonAjax({
							url : "/communities/restore.json",
							data : {
								community_id : community.id
							},
							type : "post",
							successRequestMessage : "Восстановление завершено",
							errorMessage : "Ошибка восстановления",
							successCallback : function(response) {
								$(radomEventsManager).trigger("community.restored", response);
							}
						});
					}
				});
				return false;
			});

			$markup.find("a.join-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".community-item");
				var communityId = $item.attr("data-community-id");
				CommunityFunctions.joinToOpenCommunity(communityId);
				return false;
			});

			$markup.find("a.request-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".community-item");
				var communityId = $item.attr("data-community-id");
				CommunityFunctions.createRequestToJoinInCommunity(communityId);
				return false;
			});

			$markup.find("a.accept-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".community-item");
				var memberId = $item.attr("data-member-id");
				CommunityFunctions.acceptToJoinToCommunity(memberId);
				/*CommunityRequiredConditionsDialog.loadConditions({
				 communityId : communityId,
				 callback : function(communityId) {
				 $.radomJsonPost("/communities/accept_invite.json", {
				 member_id : memberId
				 }, function(response) {
				 $(radomEventsManager).trigger("community-member.accept-invite", response);
				 });
				 }
				 });*/
				return false;
			});

			$markup.find("a.reject-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".community-item");
				var memberId = $item.attr("data-member-id");
				var communityId = $item.attr("community-id");
				$.radomJsonPost("/communities/reject_invite.json", {
					member_id : memberId
				}, function(response) {
					$(radomEventsManager).trigger("community-member.reject-invite", response);
				});
				return false;
			});

			$markup.find("a.cancel-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".community-item");
				var memberId = $item.attr("data-member-id");
				var communityId = $item.attr("data-community-id");
				$.radomJsonPost("/communities/cancel_request.json", {
					member_id : memberId
				}, function(response) {
					$(radomEventsManager).trigger("community-member.cancel-request", response);
				});
				return false;
			});

			$markup.find("a.leave-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".community-item");
				var memberId = $item.attr("data-member-id");
				var communityId = $item.attr("data-community-id");
				CommunityFunctions.leaveFromCommunity(memberId);
				return false;
			});

			$markup.find("span.request-distance").radomTooltip({
				placement : "top",
				container : "body"
			});

			return $markup;
		},

		append : function(community, $list) {
			var $item = $("div.community-item[data-community-id=" + community.id + "]");
			if ($item.length == 0) {
				$list.append(CommunitiesListItem.getMarkup(community));
			}
		},

		prepend : function(community, $list) {
			var $item = $("div.community-item[data-community-id=" + community.id + "]");
			if ($item.length == 0) {
				$list.prepend(CommunitiesListItem.getMarkup(community));
			}
		},

		replace : function(community) {
			var $item = $("div.community-item[data-community-id=" + community.id + "]");
			var level = $item.attr("data-level");
			community.level = level;
			$item.next("hr").remove();
			$item.replaceWith(CommunitiesListItem.getMarkup(community));
		},

		remove : function(community) {
			var $item = $("div.community-item[data-community-id=" + community.id + "]");
			$item.fadeOut(function(){
				$item.next("hr").remove();
				$item.remove();
			});
		},

		appendTree : function(community, $list) {
			var $item = $("div.communities-tree-item[data-community-id=" + community.id + "]");
			if ($item.length == 0) {
				$list.append(CommunitiesListItem.getTreeMarkup(community));
			}
		},

		prependTree : function(community, $list) {
			var $item = $("div.communities-tree-item[data-community-id=" + community.id + "]");
			if ($item.length == 0) {
				$list.prepend(CommunitiesListItem.getTreeMarkup(community));
			}
		},

		replaceTree : function(community) {
			var $item = $("div.communities-tree-item[data-community-id=" + community.id + "]");
			var level = $item.attr("data-level");
			community.level = level;
			$item.next("hr").remove();
			$item.replaceWith(CommunitiesListItem.getTreeMarkup(community));
		},

		removeTree : function(community) {
			var $item = $("div.communities-tree-item[data-community-id=" + community.id + "]");
			$item.fadeOut(function(){
				$item.next("hr").remove();
				$item.remove();
			});
		}

	};

</script>