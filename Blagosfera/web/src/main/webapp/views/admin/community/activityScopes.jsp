<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<script id="scope-template" type="x-tmpl-mustache">

	<tr data-scope-id="{{scope.id}}">
		<td>{{scope.name}}</td>
		<td>
			<a href="#" class="edit-link glyphicon glyphicon-pencil"></a>
			<a href="#" class="delete-link glyphicon glyphicon-remove"></a>
		</td>
	</tr>

</script>

<script type="text/javascript">

	function initActivityScopesPage() {

		var $modal = $("div#scope-edit-dialog");
		
		function showEditDialog(scope) {
			$modal.find(".modal-title").html(scope ? "Редактирование": "Создание");
			$modal.find("input[name='id']").val(scope ? scope.id : "");
			$modal.find("input[name='name']").val(scope ? scope.name : "");
			$modal.modal("show");
			$modal.changesChecker("refresh");
		}
		
		function hideEditDialog() {
			$modal.modal("hide");
			$modal.changesChecker("destroy");
		}
		
		function getMarkup(scope) {
			var $markup = $(Mustache.render($("#scope-template").html(), {
				scope : scope
			}));
			
			$markup.data("scope", scope);
			
			$markup.find("a.edit-link").click(function() {
				var scope = $(this).parents("tr").data("scope");
				showEditDialog(scope);				
				return false;
			});
			
			$markup.find("a.delete-link").click(function() {
				var $tr = $(this).parents("tr");
				var scope = $tr.data("scope");
				bootbox.confirm("Подтвердите удаление", function(result) {
					if (result) {
						$.radomJsonPost("/admin/communities/activity_scopes/delete.json", {
							id : scope.id
						}, function() {
							$tr.slideUp(function() {
								$tr.remove();
							});
						});
					}
				});				
				return false;
			});
			
			return $markup;
		}
		
		function loadList() {
			$.radomJsonGet("/admin/communities/activity_scopes/list.json", {}, function(response) {
				var $tbody = $("table#scopes-table tbody");
				$tbody.empty();
				$.each(response, function(index, scope) {
					$tbody.append(getMarkup(scope));
				});
			});
		}
		
		$("a#create-scope-link").click(function() {
			showEditDialog();
			return false;
		});
		
		$modal.find("button#cancel-button").click(function() {
			if ($modal.changesChecker("check")) {
				bootbox.confirm("Имеются несохраненные изменения, продтвердите закрытие", function(result) {
					if (result) {
						hideEditDialog();
					}
				});
			} else {
				hideEditDialog();
			}
		});
		
		$modal.find("button#save-button").click(function() {
			$.radomJsonPost("/admin/communities/activity_scopes/save.json", $modal.find(".modal-body :input").serialize(), function() {
				loadList();
				hideEditDialog();
			});
		});
		
		loadList();
		
	}
	
	$(document).ready(function() {
		$("table#scopes-table").fixMe();

		initActivityScopesPage();
	});

</script>

<h1>
	Сферы деятельности объединений
</h1>
<hr />
	<a href="#" class="btn btn-primary" id="create-scope-link">Создать</a>
<hr/>

<table class="table" id="scopes-table">

	<thead>
		<tr>
			<th>Название</th>
			<th>Действия</th>
		</tr>
	</thead>
	<tbody>
		
	</tbody>

</table>

<hr/>

<div class="modal fade" id="scope-edit-dialog" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<input type="hidden" name="id" value="" />
				<div class="form-group">
					<label>Название</label>
					<input class="form-control" type="text" name="name" value="" placeholder="Введите название" />
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" id="cancel-button">Отмена</button>
				<button type="button" class="btn btn-primary" id="save-button">Сохранить</button>
			</div>
		</div>
	</div>
</div>