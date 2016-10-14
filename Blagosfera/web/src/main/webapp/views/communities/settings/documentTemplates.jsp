<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">

</style>

<script id="template-row-template" type="x-tmpl-mustache">
<tr data-template-id="{{template.id}}">
<td>{{template.name}}</td>
<td>
	<a href="#" class="glyphicon glyphicon-pencil template-edit-link" data-template-id="{{template.id}}"></a>
	<a href="#" class="glyphicon glyphicon-remove template-delete-link" data-template-id="{{template.id}}"></a>
</td>
</tr>
</script>

<script type="text/javascript">

	DocumentTemplates = {
			
			communityId : "${community.id}",
			
			init : function() {
				var $modal = $("div#edit-document-template-modal");
				var $html = $modal.find("textarea#html");
				$html.css("height", Math.max($(window).height() - 450, 200) + "px").radomTinyMCE({
					useRadomPlaceholder : true
				});
			},
			
			fillInputs : function(template) {
				var $modal = $("div#edit-document-template-modal");
				var $id = $modal.find("input#id");
				var $name = $modal.find("input#name");
				var $html = $modal.find("textarea#html");
				var $title = $modal.find("h4.modal-title");
				var $type = $modal.find("select#type-id");
				if (template) {
					$id.val(template.id);
					$name.val(template.name);
					$html.val(template.html);
					$title.html("Редактирование шаблона документа");
					$type.val(template.type.id);
				} else {
					$id.val("");
					$name.val("");
					$html.val("");
					$title.html("Создание шаблона документа");
				}
			},
			
			showCreateDialog : function() {
				DocumentTemplates.fillInputs();
				$("div#edit-document-template-modal").modal("show");	
			},
			
			showEditDialog : function(templateId) {
				$.radomJsonGet("/communities/get_document_template.json", {
					community_id : DocumentTemplates.communityId,
					template_id : templateId
				}, function(response) {
					DocumentTemplates.fillInputs(response);
					$("div#edit-document-template-modal").modal("show");
				});
			},
			
			saveTemplate : function(invalidateDocuments) {
				var $modal = $("div#edit-document-template-modal");
				var id = $modal.find("input#id").val();
				var name = $modal.find("input#name").val();
				var html = $modal.find("textarea#html").val();
				var typeId = $modal.find("select#type-id").val();
				if (!name) {
					bootbox.alert("Название документа не задано");
				} else if (!html) {
					bootbox.alert("Шаблон документа не задан");
				} else {
					$.radomJsonPost("/communities/save_document_template.json", {
						community_id : DocumentTemplates.communityId,
						template_id : id,
						name : name,
						html : html,
						type_id : typeId,
						invalidate_documents : invalidateDocuments
					}, function(response) {
						DocumentTemplates.showTemplateRow(response);
						$("div#edit-document-template-modal").modal("hide");
					});
				}
			},
			
			deleteTemplate : function(templateId) {
				bootbox.confirm("Подтвердите удаление шаблона документа", function(result) {
					if (result) {
						$.radomJsonPost("/communities/delete_document_template.json", {
							community_id : DocumentTemplates.communityId,
							template_id : templateId
						}, function(response) {
							$("tr[data-template-id=" + response.id + "]").remove();
							bootbox.alert("Шаблон документа удален");
						});
					}
				});
			},
			
			showTemplateRow : function(template) {
				var $markup = Mustache.render($("#template-row-template").html(), {
					template : template
				});
				var $old = $("tr[data-template-id=" + template.id + "]");
				if ($old.length == 0) {
					$("table#document-templates-table tbody").append($markup);
				} else {
					$old.replaceWith($markup);
				}
			}
			
	};

	$(document).ready(function() {
		
		DocumentTemplates.init();

		$("table#document-templates-table").fixMe();
		
		$("a.template-create-link").click(function() {
			DocumentTemplates.showCreateDialog();
			return false;
		});
		
		$("table#document-templates-table").on("click", "a.template-edit-link", function() {
			var templateId = $(this).attr("data-template-id");
			DocumentTemplates.showEditDialog(templateId);
			return false;
		});
		
		$("table#document-templates-table").on("click", "a.template-delete-link", function() {
			var templateId = $(this).attr("data-template-id");
			DocumentTemplates.deleteTemplate(templateId);
			return false;
		});
		
		$("div#edit-document-template-modal button#apply-button").click(function() {
			DocumentTemplates.saveTemplate(false);
		});

		$("div#edit-document-template-modal button#apply-and-invalidate-button").click(function() {
			DocumentTemplates.saveTemplate(true);
		});
		
	});
	
</script>

<t:insertAttribute name="communityHeader" />
<hr/>
<t:insertAttribute name="menu" />

<h1>Настройка документов</h1>
<hr/>
<p class="text-muted">На данной странице производится настройка шаблонов документов объединения. Каждый из представленных ниже документов должен быть принят участником объединения для членства в этом объединении.</p>
<hr/>
<a href="#" class="btn btn-primary template-create-link">Создать новый шаблон документа</a>
<hr/>
<table class="table" id="document-templates-table">
	<thead>
		<tr>
			<th>Название</th>
			<th>Действия</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${templates}" var="t">
			<tr data-template-id="${t.id}">
				<td>${t.name}</td>
				<td>
					<a href="#" class="glyphicon glyphicon-pencil template-edit-link" data-template-id="${t.id}"></a>
					<a href="#" class="glyphicon glyphicon-remove template-delete-link" data-template-id="${t.id}"></a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<hr/>

<div class="modal fade" id="edit-document-template-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
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
      				<label>Название документа</label>
      				<input type="text" placeholder="Название документа" class="form-control" id="name" />
      			</div>
      			<div class="form-group">
      				<label>Тип документа</label>
      				<select class="form-control" id="type-id">
      					<c:forEach items="${documentTemplateTypes}" var="t">
      						<option value="${t.id}">${t.name}</option>
      					</c:forEach>
      				</select>
      			</div>      			
      			<div class="form-group">
      				<label>Шаблон документа</label>
   					<textarea class="form-control" id="html"></textarea>
   				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="apply-button">Сохранить</button>
				<button type="button" class="btn btn-primary" id="apply-and-invalidate-button">Сохранить и запросить повторное принятие</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>				
			</div>
		</div>
	</div>
</div>