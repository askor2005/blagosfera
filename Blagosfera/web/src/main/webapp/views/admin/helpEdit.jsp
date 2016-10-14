<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<div id="helpEditTarget">
</div>
<script id="helpEditTemplate" type="x-tmpl-mustache">
<style type="text/css">

	.bootstrap-tagsinput {
		width : 100%;
	}

	.bootstrap-tagsinput .label {
		font-size : 100%;
		font-weight : normal;
	}

</style>

<h1>
	Редактирование раздела справки
</h1>
<hr/>
	
<form role="form" id="form">
	<input type="hidden" name="id" value="{{currentHelpSection.id}}" />
	<div class="form-group">
		<label for="name">Имя раздела</label>
		<input name="name" type="text" class="form-control" id="name" placeholder="Метатэг title" value="{{currentHelpSection.name}}" />
		<span id="helpBlock" class="help-block">Это имя будет использоваться в верстке, рекомендуется использовать латиницу</span>
	</div>
	<div class="form-group">
		<label for="title">Заголовок страницы</label>
		<input name="title" type="text" class="form-control" id="title" placeholder="Метатэг title" value="{{page.title}}" />
		<span id="helpBlock" class="help-block">Используется в качестве метатэга title и для отображения пользователям, рекомендуется использовать кирилицу</span>
	</div>
	<div class="form-group">
		<label for="content">Содержимое страницы</label>
		<textarea name="content" id="content" rows="30" style="font-size : 11px; font-family: monospace;">{{page.content}}</textarea>
		<span id="helpBlock" class="help-block"></span>
	</div>	
	<div class="form-group">
		<label for="description">Описание страницы</label>
		<textarea name="description" class="form-control" id="description" rows="5">{{page.description}}</textarea>
		<span id="helpBlock" class="help-block">Используется в качестве метатэга description</span>
	</div>
	<div class="form-group">
		<label for="keywords">Ключевые слова</label>
		<input type="text" name="keywords" class="form-control" id="keywords" data-role="tagsinput" value="{{page.keywords}}"/>
		<span id="helpBlock" class="help-block">Используется в качестве метатэга keywords</span>
	</div>
	<a href="#" id="edit" class="btn btn-primary btn-sm">Сохранить изменения</a>
	{{#hasParentId}}
		<a href="/admin/help?parent_id={{currentHelpSection.parentId}}" id="cancel" class="btn btn-default btn-sm">Отмена</a>
	{{/hasParentId}}
	{{^hasParentId}}
		<a id="cancel" href="/admin/help" class="btn btn-default btn-sm">Отмена</a>
	{{/hasParentId}}
	<a href="/help/{{currentHelpSection.name}}" target="_blank" class="btn btn-default btn-sm" id="open-in-new-tab-link">Открыть в новом окне</a>
</form>

<hr/>
	<label>Выбор родительского раздела</label>
	<div id="tree-div"></div>
<hr/>
</script>

<script type="text/javascript">

	$(document).ready(function() {
		var helpSectionId = "${helpSectionId}";
			$.radomJsonGet(encodeURI("/admin/help/get/"+helpSectionId), {}, function(response) {
				var page = response.page;
				var currentHelpSection = response.currentHelpSection;
				var hasParentId = (currentHelpSection.parentId != null);
				var template = $('#helpEditTemplate').html();
				Mustache.parse(template);
				var rendered = Mustache.render(template, {hasParentId : hasParentId,page : page,currentHelpSection : currentHelpSection});
				$('#helpEditTarget').html(rendered);
				$("input[data-role=tagsinput], select[multiple][data-role=tagsinput]").tagsinput();
				$('.selectpicker').selectpicker();
				$("a#edit").click(function() {
					$.radomJsonPost("/admin/help/edit.json", $("form#form").serialize(), function(response) {
						$("a#open-in-new-tab-link").attr("href", "/help/" + response);
						bootbox.alert("Изменения сохранены");
					});
					return false;
				});

				$("textarea#content").radomTinyMCE();

				Ext.onReady(function() {

							var store = Ext.create('Ext.data.TreeStore', {
								proxy : {
									type : 'ajax',
									url : '/admin/help/tree.json',
									extraParams : {
										current_help_section_id : $("input[type=hidden][name=id]").val()
									}
									//actionMethods : {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'}
								},
								sorters : [ {
									property : 'leaf',
									direction : 'ASC'
								}, {
									property : 'id',
									direction : 'ASC'
								} ]
							});
							var tree = Ext.create('Ext.tree.Panel', {
								store : store,
								rootVisible : false,
								useArrows : true,
								frame : true,
								renderTo : 'tree-div',
								width : 650,
								height : 200,
								listeners: {
									checkchange : function(node, checked, eOpts) {
										if (checked == true) {
											$.each(tree.getChecked(), function(index, currentNode) {
												if (currentNode.id != node.id) {
													currentNode.set("checked", false);
												}
											});
											$.radomJsonPost("/admin/help/set_parent.json", {
												id : $("input[type=hidden][name=id]").val(),
												parent_id : node.id
											}, function() {
												$("#cancel").attr("href","/admin/help?parent_id="+ node.id);
												bootbox.alert("Родительский раздел установлен");
											});
										} else {
											$.radomJsonPost("/admin/help/set_parent.json", {
												id : $("input[type=hidden][name=id]").val()
											}, function() {
												$("#cancel").attr("href","/admin/help");
												bootbox.alert("Текущий раздел назначен корневым");
											});
										}
									}
								}
							});

			}
		);
		});
	});

</script>