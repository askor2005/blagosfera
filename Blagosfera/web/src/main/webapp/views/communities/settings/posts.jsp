<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">

</style>

<script type="text/javascript">

	var communityId = "${communityId}";

	var sourceNamesForInputMembers = null;

	function loadAnyPageData(communityId, callBack) {
		$.radomJsonPost(
				"/communities/any_page_data.json",
				{
					community_id : communityId
				},
				callBack
		);
	}

	Posts = {
			
		communityId : communityId,
		posts : null,
		permissions : null,
		postRowTemplate : null,
		permissionsTemplate : null,

		init : function() {
			var self = this;
			this.loadPostsPageData(function (postsPageData) {
				self.posts = postsPageData.posts;
				self.permissions = postsPageData.permissions;
				self.postRowTemplate = $("#post-row-template").html();
				Mustache.parse(self.postRowTemplate);
				self.permissionsTemplate = $("#permissionsTemplate").html();
				Mustache.parse(self.permissionsTemplate);

				self.initGui();
			});
		},

		initGui : function() {
			var self = this;
			$("table#posts-table").fixMe();
			if (this.posts != null) {
				for (var i in this.posts) {
					$("#communityPosts").append(this.getPostRowMarkup(this.posts[i]));
				}
			}
			$("#communityPermissions").append(Mustache.render(this.permissionsTemplate, { permissions : this.permissions }));

			$("a.post-create-link").click(function() {
				self.showCreateDialog();
				return false;
			});

			$("table#posts-table").on("click", "a.post-edit-link", function() {
				self.showEditDialog($(this).attr("data-post-id"));
				return false;
			});

			$("table#posts-table").on("click", "a.post-copy-link", function() {
				self.copyPost($(this).attr("data-post-id"));
				return false;
			});

			$("table#posts-table").on("click", "a.post-delete-link", function() {
				self.deletePost($(this).attr("data-post-id"));
				return false;
			});

			/*$("div#edit-post-modal button#apply-button").click(function() {
				self.savePost();
			});*/

			$("div#edit-post-modal a#select-all").click(function() {
				self.selectAll();
				return false;
			});

			$("div#edit-post-modal a#deselect-all").click(function() {
				self.deselectAll();
				return false;
			});

			$("div#edit-post-modal label.permission-label").radomTooltip({
				placement : "top",
				container : "body"
			});

			$.each($("table#posts-table tbody tr"), function(index, tr) {
				Posts.initTooltips($(tr));
			})


			/*$(".appointBehavior").click(function(){
				var behaviorValue = $(this).val();

				switch(behaviorValue) {
					case "defaultBehavior":
						$("#documentBlock").hide();
						break;
					case "documentBehavior":
						$("#documentBlock").show();
						break;
				}
			});
			this.initDocumentTemplatesInput();*/

		},

		selectAll : function() {
			$("div#edit-post-modal").find("input#permission[type=checkbox]").prop("checked", true);
		},

		deselectAll : function() {
			$("div#edit-post-modal").find("input#permission[type=checkbox]").prop("checked", false);
		},

		fillInputs : function(post) {
			var self = this;
			var $modal = $("div#edit-post-modal");
			var $id = $modal.find("input#id");
			var $name = $modal.find("input#name");
			var $position = $modal.find("input#position");
			var $vacanciesCount = $modal.find("input#vacancies-count");
			var $title = $modal.find("h4.modal-title");
			var $allButtons = $modal.find("div#all-buttons");
			var $schemaUnit = $modal.find("select#schema-unit");
			//var $selectBehavior = $modal.find(".appointBehavior");

			$modal.find("input[type=checkbox]").prop("checked", false);

			//$selectBehavior.val("defaultBehavior");
			$("#defaultBehavior").prop("checked", true);
			//post.appointBehavior
			if (post) {
				$id.val(post.id);
				$name.val(post.name);
				$position.val(post.position);
				$vacanciesCount.val(post.vacanciesCount);
				$title.html("Редактирование должности");
				//$selectBehavior.val(post.appointBehavior);
				$("#" + post.appointBehavior).prop("checked", true);
				if (post.ceo) {
					$modal.find("input[type=checkbox][data-permission-id]").prop("checked", true).attr("disabled", "disabled");
					$position.parents(".form-group").hide();
					$allButtons.hide();
					$schemaUnit.val("");
					$schemaUnit.parents(".form-group").hide();
				} else {
					$modal.find("input[type=checkbox][data-permission-id]").removeAttr("disabled");
					$.each(post.permissions, function(index, permission) {
						$modal.find("input[type=checkbox][data-permission-id=" + permission.id + "]").prop("checked", true);
					});
					$position.parents(".form-group").show();
					$allButtons.show();
					$schemaUnit.parents(".form-group").show();
				}

				initDocumentTemplateSettings(
						$("#documentBlock"),
						post.documentTemplateSettings == null ? [] : post.documentTemplateSettings,
						searchMembersForTemplate,
						function(documentTemplateSetting){self.savePost(documentTemplateSetting)},//saveDocumentTemplateSetting,
						sourceNamesForInputMembers
				);
			} else {
				$id.val("");
				$name.val("");
				$position.val("0");
				$vacanciesCount.val("0");
				$title.html("Создание должности");
				$modal.find("input[type=checkbox][data-permission-id]").removeAttr("disabled");
				$position.parents(".form-group").show();
				$allButtons.show();
				$schemaUnit.val("");
				$schemaUnit.parents(".form-group").show();

				initDocumentTemplateSettings(
						$("#documentBlock"),
						[],
						searchMembersForTemplate,
						function(documentTemplateSetting){self.savePost(documentTemplateSetting)},
						sourceNamesForInputMembers
				);
			}

			$.radomJsonGet("/group/" + this.communityId + "/get_schema_units.json", {}, function(response) {
				$schemaUnit.empty();
				$schemaUnit.append("<option value=''></option>")
				$.each(response, function(index, unit) {
					$schemaUnit.append("<option data-schema-unit-id='" + unit.id + "' value='" + unit.id + "'>" + unit.name + "</option>")
				});
				if (post && post.schemaUnit) {
					$schemaUnit.find("option[data-schema-unit-id=" + post.schemaUnit.id + "]").attr("selected", "selected");
				}
			});

		},

		showCreateDialog : function() {
			this.fillInputs();
			$("div#edit-post-modal").modal("show");
		},

		showEditDialog : function(postId) {
			var self = this;
			$.radomJsonGet("/group/" + this.communityId + "/get_post.json", {
				post_id : postId
			}, function(response) {
				self.fillInputs(response);
				$("div#edit-post-modal").modal("show");
			});
		},

		savePost : function(documentTemplateSettings) {
			var $modal = $("div#edit-post-modal");
			var id = $modal.find("input#id").val();
			var name = $modal.find("input#name").val();
			var position = $modal.find("input#position").val();
			var vacanciesCount = $modal.find("input#vacancies-count").val();
			var schemaUnitId = $modal.find("select#schema-unit").val();
			var appointBehavior = null;
			if ($(".appointBehavior").filter(":checked").length > 0) {
				appointBehavior = $(".appointBehavior").filter(":checked").attr("id");
			}
			//var $selectBehavior = $modal.find("#appointBehavior");
			/*var $documentTemplate = $modal.find("#documentTemplate");
			var templateId = $documentTemplate.attr("template-id");*/
			if (!name) {
				bootbox.alert("Название должности не задано");
			} else if (!position) {
				bootbox.alert("Номер по порядку не задан");
			} else if (!vacanciesCount) {
				bootbox.alert("Не задано количество мест");
			} else if (appointBehavior == null) {
				bootbox.alert("Не выбран способ принятия на должность. С документами или без.");
			} else {
				var permissionIds = [];
				$.each($modal.find("input[type=checkbox][data-permission-id]:checked"), function(index, checkbox) {
					permissionIds.push($(checkbox).attr("data-permission-id"));
				});

				var params = {
					id : id,
					name : name,
					position : position,
					vacanciesCount : vacanciesCount,
					communityId : this.communityId,
					permissionIds : permissionIds,
					isCeo : false,
					schemaUnitId : schemaUnitId,
					appointBehavior : appointBehavior,
					documentTemplateSettings : documentTemplateSettings
				};

				$.radomJsonPost(
						"/group/" + this.communityId + "/save_post.json",
						JSON.stringify(params),
						function(response) {
							Posts.showPostRow(response);
							$("div#edit-post-modal").modal("hide");
						},
						null,
						{
							contentType : 'application/json'
						}
				);
			}
		},

		copyPost : function(postId) {
			var self = this;
			$.radomJsonPost("/group/" + this.communityId + "/copy_post.json", {post_id : postId}, function(response) {
				self.showPostRow(response);
			});
		},

		deletePost : function(postId) {
			var self = this;
			bootbox.confirm("Подтвердите удаление должности", function(result) {
				if (result) {
					$.radomJsonPost("/group/" + self.communityId + "/delete_post.json", {
						post_id : postId
					}, function(response) {
						$("tr[data-post-id=" + response.id + "]").remove();
						bootbox.alert("Должность удалена");
					});
				}
			});
		},

		getPostRowMarkup : function(post) {
			var $markup = $(Mustache.render(this.postRowTemplate, { post : post }));
			this.initTooltips($markup);
			return $markup;
		},

		showPostRow : function(post) {
			var $markup = this.getPostRowMarkup(post);
			var $old = $("tr[data-post-id=" + post.id + "]");
			if ($old.length == 0) {
				$("table#posts-table tbody").append($markup);
			} else {
				$old.replaceWith($markup);
			}
			this.sortRows();
		},

		initTooltips : function($tr) {
			var $editLink = $tr.find("a.post-edit-link");
			if ($editLink.length > 0) {
				$editLink.radomTooltip({
					title : "Редактировать должность",
					placement : "top",
					container : "body"
				});
			}
			var $copyLink = $tr.find("a.post-copy-link");
			if ($copyLink.length > 0) {
				$copyLink.radomTooltip({
					title : "Создать должность на основе существующей",
					placement : "top",
					container : "body"
				});
			}
			$tr.find("a.post-delete-link").radomTooltip({
				title : "Удалить должность",
				placement : "top",
				container : "body"
			});
		},

		sortRows : function() {
			var $tbody = $("table#posts-table tbody");
			var $trs = $tbody.children("tr");
			$trs.sort(function(a, b) {
				var aPosition = parseInt($(a).attr("data-post-position"));
				var bPosition = parseInt($(b).attr("data-post-position"));

				var aName = $(a).attr("data-post-name");
				var bName = $(b).attr("data-post-name");

				if (aPosition > bPosition) {
					return 1;
				} else if (aPosition < bPosition) {
					return -1
				} else {
					if (aName > bName) {
						return 1;
					} else if (aName < bName) {
						return -1;
					} else {
						return 0;
					}
				}

			});
			$trs.detach().appendTo($tbody);
		},
		loadPostsPageData: function(callBack) {
			$.radomJsonPost("/group/" + this.communityId + "/posts_page_data.json", {}, callBack);
		},

		/*initDocumentTemplatesInput : function() {
			var $modal = $("div#edit-post-modal");
			var $documentTemplate = $modal.find("#documentTemplate");
			var link = "/group/" + this.communityId + "/settings/posts/documentTemplates.json";
			$documentTemplate.typeahead({
				delay: 500,
				matcher: function () { return true; },
				updater: function(item) {
					$documentTemplate.attr("template-id", item.id);
					return item;
				},
				source:  function (query, process) {
					var data = {
						search_string : query
					};
					return $.ajax({
						type: "post",
						dataType: "json",
						url: link,
						data: data,
						success: function (data) {
							return process(data);
						},
						error: function () {
							console.log("ajax error");
							return process(false);
						}
					});
				}
			});
		}*/

	};

	var searchMembersForTemplate = function(searchString, participantType, callBack) {
		if (participantType == "INDIVIDUAL" ||
				participantType == "REGISTRATOR") {
			searchUser(searchString, callBack);
		} else if (participantType == "COMMUNITY_WITH_ORGANIZATION" ||
				participantType == "COMMUNITY_WITHOUT_ORGANIZATION") {
			searchCommunity(searchString, callBack);
		}
	};

	function searchUser(searchString, callBack) {
		$.radomJsonPost(
				"/sharer/searchActive.json",
				{
					query : searchString
				},
				function(response) {
					if (response != null && response.length > 0) {
						var result = [];
						for (var index in response) {
							var user = response[index];
							result.push({
								id : user.id,
								name : user.fullName
							});
						}
						callBack(result);
					} else {
						callBack([]);
					}
				}
		);
	}

	function searchCommunity(searchString, callBack) {
		$.radomJsonPost(
				"/communities/search.json",
				{
					query : searchString
				},
				function(response) {
					if (response != null && response.list != null && response.list.length > 0) {
						var result = [];
						for (var index in response.list) {
							var community = response.list[index];
							result.push({
								id : community.id,
								name : community.name
							});
						}
						callBack(result);
					} else {
						callBack([]);
					}
				}
		);
	}

	var sourceNamesForInputMembersForOrganization = {
		"INDIVIDUAL": [{sourceCode : "user", name : "Кандидат на должность"}],
		"COMMUNITY_WITH_ORGANIZATION": [{sourceCode : "community", name : "Объединение"}]
	};
	var sourceNamesForInputMembersForGroup = {
		"INDIVIDUAL": [{sourceCode : "user", name : "Кандидат на должность"}],
		"COMMUNITY_WITHOUT_ORGANIZATION": [{sourceCode : "community", name : "Объединение"}]
	};

	function getCommunityInputMember(community) {
		var sourceNamesForInputMembers = null;
		if (community.type == "COMMUNITY_WITH_ORGANIZATION") {
			sourceNamesForInputMembers = sourceNamesForInputMembersForOrganization;
		} else if (community.type == "COMMUNITY_WITHOUT_ORGANIZATION") {
			sourceNamesForInputMembers = sourceNamesForInputMembersForGroup;
		}
		return sourceNamesForInputMembers;
	}

	$(document).ready(function() {
		loadAnyPageData(communityId, function(communityAnyPageData) {
			initCommunityHead(communityAnyPageData.community);
			initCommunityMenu(communityAnyPageData.community);
			sourceNamesForInputMembers = getCommunityInputMember(communityAnyPageData.community);
			Posts.init();
		});
	});

</script>

<script id="post-row-template" type="x-tmpl-mustache">
	<tr data-post-id="{{post.id}}" data-post-position="{{post.position}}" data-post-name="{{post.name}}">
		<td>{{^post.ceo}}{{post.position}}{{/post.ceo}}</td>
		<td>{{post.name}}</td>
		<td>{{post.vacanciesCount}}</td>
		<td>
			<a href="#" class="glyphicon glyphicon-pencil post-edit-link" data-post-id="{{post.id}}"></a>
			{{^post.ceo}}
				<a href="#" class="glyphicon glyphicon-share post-copy-link" data-post-id="{{post.id}}"></a>
				<a href="#" class="glyphicon glyphicon-remove post-delete-link" data-post-id="{{post.id}}"></a>
			{{/post.ceo}}
		</td>
	</tr>
</script>

<script id="permissionsTemplate" type="x-tmpl-mustache">
	{{#permissions}}
		<div class="col-xs-4">
			<div class="checkbox">
				<label class="permission-label" data-title="{{description}}">
					<input type="checkbox" id="permission" data-permission-id="{{id}}"> {{title}}
				</label>
			</div>
		</div>
	{{/permissions}}
</script>

<t:insertAttribute name="communityHeader" />
<h2>Штатное расписание</h2>
<hr/>
<a href="#" class="btn btn-primary post-create-link">Создать новую должность</a>
<hr/>
<table class="table" id="posts-table">
	<thead>
	<tr>
		<th>#</th>
		<th>Название</th>
		<th>Мест</th>
		<th style="width : 100px;">Действия</th>
	</tr>
	</thead>
	<tbody id="communityPosts"></tbody>
</table>
<hr/>

<div class="modal fade" id="edit-post-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog modal-xl">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				<span class="sr-only">Закрыть</span></button>
        		<h4 class="modal-title"></h4>
      		</div>
      		<div class="modal-body">
      			<input id="id" value="" type="hidden"/>
      			<div class="form-group">
      				<label>Название должности</label>
      				<input type="text" placeholder="Название должности" class="form-control" id="name" />
      			</div>
      			<div class="form-group">
      				<label>Номер по порядку</label>
      				<input type="number" class="form-control" id="position" />
      			</div>
      			<div class="form-group">
      				<label>Количество мест</label>
      				<input type="number" class="form-control" id="vacancies-count" />
      			</div>
				<div class="form-group">
					<label>Подразделение</label>
					<select class="form-control" id="schema-unit">
						<option value=""></option>
					</select>
				</div>
				<hr/>

				<label>Отметьте возможности, которые сможет использовать в объединении человек, занимающий эту должность.</label>
      			
      			<div id="all-buttons">
      				<hr/>
	      			<a href="#" class="btn btn-info btn-xs" id="select-all">Выбрать все</a>
	      			<a href="#" class="btn btn-danger btn-xs" id="deselect-all">Снять все</a>
	      			<hr/>
      			</div>
      			
      			<div class="row" id="communityPermissions"></div>

				<hr/>
				<div class="form-group">
					<label>
						<input type="radio" class="appointBehavior" name="appointBehavior" id="documentPostAppointBehavior" />
						Принимать участника на должность с подписанием документов
					</label>
					<label>
						<input type="radio" class="appointBehavior" name="appointBehavior" id="defaultPostAppointBehavior" />
						Принимать участника на должность без использования документов
					</label>
				</div>
				<div class="form-group">
					<div id="documentBlock"></div>
				</div>

			</div>
			<div class="modal-footer">
				<!--button type="button" class="btn btn-primary" id="apply-button">Сохранить</button-->
				<button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>				
			</div>
		</div>
	</div>
</div>