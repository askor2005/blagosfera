<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<div id="contacts-group-target">
</div>
<script id="contacts-groups-template" type="x-tmpl-mustache">
<style type="text/css">

.x-grid-td {
	vertical-align : middle;
}

.x-grid-item-focused .x-grid-cell-inner:before {
	border: none;
}

a.glyphicon {
	text-decoration: none;
}
</style>


<h2>
	Организационные списки <a href="/contacts/lists/edit"
		class="btn btn-primary pull-right">Создать список</a>
</h2>

<hr />

{{^groupsExists}}
	<div class="alert alert-info" role="alert">Нет организационных списков</div>
{{/groupsExists}}

{{#groupsExists}}

	<div id="table-wrapper"></div>

	<table id="table" style="width: 98%; position: absolute; top: -9999px;">
		<thead>
			<tr>
				<th>Название</th>
				<th width="100">Цвет</th>
				<th width="50"></th>
			</tr>
		</thead>
		<tbody>
			{{#groups}}
				<tr>
					<td><a href="/contacts?group_id={{id}}">{{name}} ({{contactsCount}})</a></td>
					<td>
						<div class="group-color-example group-color-example-{{color}}">Цвет {{color}}</div>
					</td>
					<td><a href="/contacts/lists/edit?id={{id}}"
						class="glyphicon glyphicon-pencil"> </a> <a
						href="/contacts/lists/delete.json?id={{id}}"
						onclick="return confirmDelete($(this));"
						class="glyphicon glyphicon-remove"> </a></td>
				</tr>
			{{/groups}}
		</tbody>
	</table>

	<hr/>

	<c:choose>
	    <c:when test='${referer == "/contacts"}'>
			<a href="${referer}" class="btn btn-default pull-right">Вернуться к моим контактам</a>
	    </c:when>
	    <c:when test='${referer == "/contacts/search"}'>
			<a href="${referer}" class="btn btn-default pull-right">Вернуться кпоиску людей</a>
	    </c:when>
	    <c:otherwise>
	        <a href="${referer}" class="btn btn-default pull-right">Назад</a>
	    </c:otherwise>
	</c:choose>

{{/groupsExists}}
</script>
	<script type="text/javascript">
		$(document).ready(function() {
			$.radomJsonGet("/contacts/contacts/lists.json", {}, function(response) {
				var groups = response;
				var groupsExists = groups.length > 0;
				var template = $('#contacts-groups-template').html();
				Mustache.parse(template);
				var rendered = Mustache.render(template, {groups: groups,groupsExists : groupsExists});
				$('#contacts-group-target').html(rendered);
				if (groupsExists) {
					$("table#table").fixMe();
					Ext.onReady(function() {

						grid = Ext.create('Ext.ux.grid.TransformGrid', 'table', {

						});
						grid.render('table-wrapper');
					});

					function confirmDelete($link) {
						bootbox.confirm("Вы уверены?", function(result){
							if (result) {
								window.location = $link.attr("href");
							}
						});
						return false;
					}
				}
			});
		});

	</script>


