<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<script id="documents-template" type="x-tmpl-mustache">
	<h5>Документы, обязательные к принятию ({{documentsCount}})</h5>
	<table class="table" id="documents-table">
		<thead>
			<tr>
				<th>Название документа</th>
				<th>Ссылка на текст</th>
				<th class="text-center">Принять</th>
			</tr>
		</thead>
		<tbody>
			{{#documents}}
				<tr>
					<td>{{title}}</td>
					<td><a data-document-id="{{id}}" href="#" class="show-document-link" >Открыть для принятия</td>
					<td class="text-center">
						<input type="checkbox" disabled="disabled" data-document-id="{{id}}" />
					</td>
				</tr>
			{{/documents}}
		</tbody>
	</table>
</script>

<div class="modal fade" id="community-required-conditions-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false" data-backdrop="static">
	<div class="modal-dialog modal-xl">
		<div class="modal-content">
			<div class="modal-header">
        		<h4 class="modal-title">Условия вступления в объединение</h4>
      		</div>
      		<div class="modal-body">
      			
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" id="cancel-button">Отказаться</button>
				<button type="button" class="btn btn-primary" id="apply-button">Принять все условия и продолжить</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="community-required-document-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false" data-backdrop="static">
	<div class="modal-dialog modal-xl modal-full-height">
		<div class="modal-content">
			<div class="modal-header">
        		<h4 class="modal-title"></h4>
      		</div>
      		<div class="modal-body"></div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" id="cancel-button">Отказаться</button>
				<button type="button" class="btn btn-primary" id="apply-button">Принять</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function(){
		$("table#documents-table").fixMe();
	});
	
	var CommunityRequiredConditionsDialog = {

			loadConditions : function(params) {
				$.radomJsonGet("/communities/get_conditions.json", {
					community_id : params.communityId
				}, function(response) {
					if (response.documents.length == 0) {
						params.callback(params.communityId);
					} else {
						CommunityRequiredConditionsDialog.showConditions(response, params);
					}
				});
			},
	
			showConditions : function(conditions, params) {
				var $modal = $("div#community-required-conditions-modal");
				var $body = $modal.find("div.modal-body");
				$body.empty();
				if (conditions.documents.length > 0) {
					var model = {};
					model.documents = conditions.documents;
					model.documentsCount = conditions.documents.length;
					
					var documentsMap = {};
					$.each(conditions.documents, function(index, document) {
						documentsMap[document.id] = document;
					});
					
					var $documents = $(Mustache.render($("#documents-template").html(), model));
					$documents.find("a.show-document-link").click(function() {
						var documentId = $(this).attr("data-document-id");
						var document = documentsMap[documentId]; 
						var $documentModal = $("#community-required-document-modal");
						$documentModal.find(".modal-title").html(document.title);
						$documentModal.find(".modal-body").html(document.text);
						$documentModal.find("#apply-button").off("click").on("click", function() {
							$modal.find("input[type=checkbox][data-document-id=" + documentId + "]").prop('checked', true);
							$documentModal.modal("hide");
						});
						$documentModal.find("#cancel-button").off("click").on("click", function() {
							$modal.find("input[type=checkbox][data-document-id=" + documentId + "]").prop('checked', false);
							$documentModal.modal("hide");
						});						
						$documentModal.modal("show");
						return false;
					});
					$body.append($documents);
				}
				$modal.find("button#apply-button").off("click");
				$modal.find("button#apply-button").on("click", function() {
					if (CommunityRequiredConditionsDialog.acceptConditions(conditions, params)) {
						$modal.modal("hide");	
					}
				});
				
				$modal.find(".modal-title").html(params.dialogTitle ? params.dialogTitle : "Условия вступления в объединение")
				
				$modal.find("button#cancel-button").html(params.rejectButtonText ? params.rejectButtonText : "Отказаться"); 
				
				$modal.find("button#cancel-button").off("click");
				$modal.find("button#cancel-button").on("click", function() {
					$modal.modal("hide");	
					if (params.rejectCallback) {
						params.rejectCallback(communityId);
					}
				});
				
				$modal.modal("show");
			},
			
			acceptConditions : function(conditions, params) {
				if ($("div#community-required-conditions-modal table#documents-table input[type=checkbox]:not(:checked)").length > 0) {
					bootbox.alert("Для продолжения необходимо подтвердить принятие всех документов. Для этого следует открыть каждый документ при помощи ссылки 'Открыть для принятия', ознакомиться с его текстом и нажать кнопку принять в диалоговом окне.");
				} else {
					var postParams = [];
					postParams.push({
						name : "community_id",
						value : params.communityId
					});
					$.each(conditions.documents, function(index, document) {
						postParams.push({
							name : "document_id",
							value : document.id
						});
					});
					var $modal = $("div#community-required-conditions-modal");
					$.radomFingerJsonAjax({
						url : "/communities/accept_conditions.json",
						type : "post",
						data : postParams,
						deviceServiceUrl : "https://localhost:36123",
						timeout : 15,
						successMessage : "Дейстиие выполнено успешно",
						errorMessage : "Ошибка выполнения действия",
						successCallback : function(response) {
							params.callback(params.communityId);
							$modal.modal("hide");
						},
						errorCallback : function(response) {
							console.log("error finger test action");
							if (response) {
								console.log(response);
							}
						}
					});
				}
				return false;
			}
			
	};
	
</script>