<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style type="text/css">
	.form-control .x-field {
		width : 100% !important;
	}
	
	div.checkbox label.label {
		display: block;
		padding-left: 28px;
		line-height: 20px;
		text-align: left;
		font-size: 13px;
	}
</style>

<script id="document-row-template" type="x-tmpl-mustache">
	<tr class="{{trClass}}">
		<td>{{document.category.name}}</td>
		<td>{{document.title}}</td>
		<td>{{info}}</td>
		<td>{{document.date}}</td>
		<td>
			<a href="#" class="show-document-link" data-document-id="{{document.id}}">
				<span class="glyphicon glyphicon-search"></span>
			</a>
			&nbsp;
			<a target="_blank" href="/documents/print/{{document.id}}">
				<span class="glyphicon glyphicon-print"></span>
			</a>
			&nbsp;
			<a target="_blank" href="/documents/download/{{document.id}}/{{document.title}}.html">
				<span class="glyphicon glyphicon-download-alt"></span>
			</a>
		</td>
	</tr>
</script>

<script type="text/javascript">

	var DocumentsList = {
			init : function() {
				Ext.onReady(function() {
					Ext.create('Ext.form.field.Date', {
						renderTo : 'from-date',
						xtype : 'datefield',
						name : 'from_date',
						format : 'd.m.Y',
						width : '100%',
						value : '<fmt:formatDate pattern="dd.MM.yyyy" value="${fromDate}" />',
						listeners : {
							select: function (t,n,o) {
								DocumentsList.initScrollListener();
							}
							
						}
					});
					Ext.create('Ext.form.field.Date', {
						renderTo : 'to-date',
						xtype : 'datefield',
						name : 'to_date',
						format : 'd.m.Y',
						width : '100%',
						value : '<fmt:formatDate pattern="dd.MM.yyyy" value="${toDate}" />',
						listeners : {
							select: function (t,n,o) {
								DocumentsList.initScrollListener();
							}
							
						}
					});
					$("input[type=hidden][name=from_date]").remove();
					$("input[type=hidden][name=to_date]").remove();
				});	
				
				$("form#documents-filter-form input[type=checkbox]").change(function() {
					DocumentsList.initScrollListener();	
				});

				$("form#documents-filter-form select").change(function() {
					DocumentsList.initScrollListener();	
				});
				
				$("table#documents-table tbody").on("click", "a.show-document-link", function() {
					DocumentsList.showDocument($(this).attr("data-document-id"));
					return false;
				});
				
				
				DocumentsList.initScrollListener();
			},
			
			lastLoadedId : null,
			
			initScrollListener : function() {
				$("table#documents-table tbody").empty();
				DocumentsList.lastLoadedId = null;
				ScrollListener.init("/documents/list.json", "get", function() {
					var data = $("form#documents-filter-form").serializeArray();
					var params = {};
					params.status = [];
					$.each(data, function(index, item) {
						if (item.name == "status") {
							params.status.push(item.value);
						} else {
							params[item.name] = item.value;
						}
					});
					if (DocumentsList.lastLoadedId) {
						params.last_loaded_id = DocumentsList.lastLoadedId; 
					}
					return params;
				}, function() {
					$("div.list-loader-animation").show();
				}, function(documents) {
					$.each(documents, function(index, document) {
						DocumentsList.showRowMarkup(document);
						DocumentsList.lastLoadedId = document.id;
					});
					$("div.list-loader-animation").hide();
				});
			},
			
			showRowMarkup : function(document) {
				var model = {};
				switch (document.status) {
				case "NEW" : 
					model.trClass="info";
					break;
				case "VALID" : 
					model.trClass="success";
					break;
				case "INVALID" : 
					model.trClass="danger";
					break;
				}
				switch (document.scopeType) {
				case "COMMUNITY" : 
					model.info = "Объединение: " + document.scope.name;
					break;
				case "MOVE_TRANSACTION" :
					model.info = document.scope.comment + " " + document.scope.senderComment;
					break;
				default :
					model.info = "";
					break;
				}
				model.document = document;
				var $markup = $(Mustache.render($("script#document-row-template").html(), model));
				$("table#documents-table tbody").append($markup);
			},
			
			showDocument : function(documentId) {
				$.radomJsonGet("/documents/get.json", {
					document_id : documentId
				}, function(document) {
					var $documentModal = $("#document-modal");
					$documentModal.find(".modal-title").html(document.title);
					$documentModal.find(".modal-body").html(document.text);
					$documentModal.modal("show");
				});
				return false;
			}
			
	};

	$(document).ready(function() {
		$("table#documents-table").fixMe();
		
		DocumentsList.init();
	});
	
</script>

<h1>Список документов</h1>

<hr/>
	<form id="documents-filter-form">
		<div class="row">
			<div class="col-xs-4 form-group">
				<label>Категория</label>
				<select name="category_id" class="form-control">
					<option value="">Все</option>
					<c:forEach items="${categories}" var="c">
						<option value="${c.id}">${c.name}</option>
					</c:forEach>
				</select>
			</div>
			<div class="col-xs-4">
				<label>С</label>
				<div class="form-group">
					<div id="from-date" class="form-control"></div>
					<input type="hidden" name="from_date" value='<fmt:formatDate pattern="dd.MM.yyyy" value="${fromDate}" />' />
				</div>
			</div>
			<div class="col-xs-4">
				<label>По</label>
				<div class="form-group">
					<div id="to-date" class="form-control"></div>
					<input type="hidden" name="to_date" value='<fmt:formatDate pattern="dd.MM.yyyy" value="${toDate}" />' />
				</div>
			</div>
			<div class="col-xs-12">
				<label>Фильтр по статусам документа</label>
			</div>
			<div class="col-xs-4 form-group">
				<div class="checkbox">
					<label class="label label-info">
		    			<input type="checkbox" name="status" value="NEW" />
		    			Новый документ
		  			</label>
				</div>		
			</div>
			<div class="col-xs-4 form-group">
				<div class="checkbox">
					<label class="label label-success">
		    			<input type="checkbox" name="status" checked="checked" value="VALID" />
		    			Действующий документ
		  			</label>
				</div>		
			</div>
			<div class="col-xs-4 form-group">
				<div class="checkbox">
					<label class="label label-danger">
		    			<input type="checkbox" name="status" value="INVALID" />
		    			Утративший силу документ
		  			</label>
				</div>		
			</div>		
		</div>
	</form>
<hr/>

<table class="table" id="documents-table">
	<thead>
		<tr>
			<th>Категория</th>
			<th>Название</th>
			<th>Информация</th>
			<th>Дата, время</th>
			<th style="width : 100px;"></th>
		</tr>
	</thead>
	<tbody>
	
	</tbody>
</table>
<div class="row list-loader-animation"></div>
<hr/>

<div class="modal fade" id="document-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false" data-backdrop="static">
	<div class="modal-dialog modal-xl modal-full-height">
		<div class="modal-content">
			<div class="modal-header">
        		<h4 class="modal-title"></h4>
      		</div>
      		<div class="modal-body"></div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
			</div>
		</div>
	</div>
</div>