<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<script id="sessions-table-row-template" type="x-tmpl-mustache">
	<tr class="{{trClass}}">
		<td>
			{{entry.username}}
			{{#showName}}
				<br/>{{entry.name}}
			{{/showName}}
		</td>
		<td>{{entry.loginDate}}</td>
		<td>{{entry.logoutDate}}</td>
		<td>
			Обшая: {{totalDuration}} {{totalDurationLabel}}
			<br/>
			Активной работы: {{activeDuration}} {{activeDurationLabel}}
			<br/>
			Простоя: {{idleDuration}} {{idleDurationLabel}}

			{{#showProgressbar}}

			<br/>
			<br/>
			<div class="progress" style="height : 10px;">
  				<div class="progress-bar progress-bar-info" style="width: {{activePercent}}%"></div>
				<div class="progress-bar progress-bar-warning" style="width: {{idlePercent}}%">
			</div>

			{{/showProgressbar}}

		</td>
		<td>{{entry.ip}}</td>
		<td>{{device}}<br/>{{os}}<br/>{{browser}}</td>
	</tr>
</script>

<script type="text/javascript">

	var SessionsList = {
			
		rowTemplate : $('#sessions-table-row-template').html(),
		rowTemplateParsed : false,
		
		getRowTemplate : function() {
			if (!SessionsList.rowTemplateParsed) {
				Mustache.parse(SessionsList.rowTemplate);
				SessionsList.rowTemplateParsed = true;
			}
			return SessionsList.rowTemplate;
		},
	
		getRowMarkup : function(entry) {
			var model = {};
			model.entry = entry;
			model.showName = (entry.username != entry.name);
			
			var parser = new UAParser();
			parser.setUA(entry.useragent);
			
			var browserName = parser.getBrowser().name;
			
			model.browser = (browserName ? browserName : "");
			
			var osName = parser.getOS().name;
			var osVersion = parser.getOS().version; 
			
			model.os = (osName ? osName + " " : "") + ((osVersion && osVersion != browserName) ? osVersion : "");
			
			model.device = parser.getDevice().type;
			if (!model.device) {
				model.device = "Персональный компьютер";
			}
			var deviceVendor = parser.getDevice().vendor;
			var deviceModel = parser.getDevice().model;
			if (deviceVendor) {
				model.device += " " + deviceVendor;
			}
			if (deviceModel) {
				model.device += " " + deviceModel;
			}
			
			if (entry.success == false) {
				model.trClass = "danger";
			} else {
				if (entry.closed) {
					model.trClass = "success";
				} else {
					if (entry.idle) {
						model.trClass = "warning";
					} else {
						model.trClass = "info";
					}
				}
			}
			
			var totalDuration = entry.duration;
			var idleDuration = entry.idleKeepalivesCount;
			var activeDuration = totalDuration - idleDuration;

			model.totalDuration = totalDuration;
			model.totalDurationLabel = RadomUtils.getDeclension(totalDuration, "минута", "минуты", "минут");
			
			model.idleDuration = idleDuration;
			model.idleDurationLabel = RadomUtils.getDeclension(idleDuration, "минута", "минуты", "минут");
			
			model.activeDuration = activeDuration;
			model.activeDurationLabel = RadomUtils.getDeclension(activeDuration, "минута", "минуты", "минут");

			if (totalDuration == 0) {
				model.showProgressbar = false;
			} else {
				model.showProgressbar = true;
				model.activePercent = activeDuration * 100 / totalDuration;
				model.idlePercent = 100 - model.activePercent;
			}
			
			var markup = Mustache.render(SessionsList.getRowTemplate(), model);
			var $markup = $(markup);
			return $markup;
		},
		
		showRowMarkup : function(entry, prepend) {
			var $markup = SessionsList.getRowMarkup(entry);
			if (prepend) {
				$("table#sessions-table").prepend(SessionsList.getRowMarkup(entry));
			} else {
				$("table#sessions-table").append(SessionsList.getRowMarkup(entry));
			}
		},
			
		addSharer : function(sharerId, sharerName) {
			if ($("ul#sharers-list li a[data-sharer-id=" + sharerId + "]").length == 0) {
				$("ul#sharers-list").append("<li>" + sharerName + " <a data-sharer-name='" + sharerName + "' data-sharer-id='" + sharerId + "' class='sharer-delete-link glyphicon glyphicon-remove' href='#'></a></li>");
			} else {
				bootbox.alert("Участник уже в списке");
			}
			$("input#sharer-add-input").val("");
			SessionsList.initScrollListener();
		},
		
		deleteSharer : function(sharerId) {
			$("ul#sharers-list li a[data-sharer-id=" + sharerId + "]").parents("li").remove();
			SessionsList.initScrollListener();
		},
			
		lastLoadedId : null,
		
		initScrollListener : function() {
			$("table#sessions-table tbody").empty();
			SessionsList.lastLoadedId = null;
			ScrollListener.init("/system/sessions.json", "get", function() {
				var params = {};
				if (SessionsList.lastLoadedId) {
					params.last_loaded_id = SessionsList.lastLoadedId;
				}
				var filter = $("select[name=filter]").val();

				switch (filter) {
					case "closed":
						params.success = true;
						params.closed = true;
					break;
					case "active":
						params.success = true;
						params.closed = false;
						params.idle = false;
					break;
					case "idle":
						params.success = true;
						params.closed = false;
						params.idle = true;
					break;
					case "error":
						params.success = false;
					break;					
					default:
				
					break;
				}

				
				
				var query = $("input[name=email]").val();
				if (query) {
					params.query = query;
				}
				var fromDate = $("input[name=from_date]").val();
				if (fromDate) {
					params.from_date = fromDate;
				}
				
				var toDate = $("input[name=to_date]").val();
				if (toDate) {
					params.to_date = toDate;
				}
				
				return params;
			}, function() {
				$("div.list-loader-animation").show();
			}, function(entries, page) {
				var $tbody = $("table#sessions-table tbody");
				$.each(entries, function(index, entry) {
					SessionsList.showRowMarkup(entry);
					if (!SessionsList.lastLoadedId || SessionsList.lastLoadedId > entry.id) {
						SessionsList.lastLoadedId = entry.id;
					}
				});
				$("div.list-loader-animation").hide();
			});
		}
		
	}; 
	
	$(document).ready(function() {
		$("table#sessions-table").fixMe();

		Ext.onReady(function() {
			Ext.create('Ext.form.field.Date', {
				renderTo : 'from-date',
				xtype : 'datefield',
				name : 'from_date',
				format : 'd.m.Y',
				width : '100%',
				value : '<fmt:formatDate pattern="dd.MM.yyyy" value="${fromDate}" />',
				listeners : {
					
					change: function (t,n,o) {

					},
					select: function (t,n,o) {
						SessionsList.initScrollListener();
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
					
					change: function (t,n,o) {

					},
					select: function (t,n,o) {
						SessionsList.initScrollListener();
					}
					
				}
			});
			
			$("input[type=hidden][name=from_date]").remove();
			$("input[type=hidden][name=to_date]").remove();
		});	
		
		SessionsList.initScrollListener();
		$("select[name=filter]").change(function() {
			SessionsList.initScrollListener();	
		});
		$("input[name=email]").callbackInput(100, 3, function() {
			SessionsList.initScrollListener();
		});
		$("a#refresh-button").click(function() {
			SessionsList.initScrollListener();
			return false;
		});
		
	});
	
	

</script>

<h1>Список сессий</h1>

<hr/>

<div class="row">

	<div class="col-xs-4">
		<div class="form-group">
			<label>Фильтр</label>
			<select class="form-control" name="filter">
				 <option value="all">Все</option>
				 <option value="closed">Завершенные</option>
				 <option value="active">Активные</option>
				 <option value="idle">Простаивающие</option>
				 <option value="error">Неудачный вход</option>
			</select>
		</div>
	</div>
	
	<div class="col-xs-4">
	
		<div class="form-group">
			<label>С</label>
			<div class="form-control" id="from-date"></div>
			<input type="hidden" name="from_date" value='<fmt:formatDate pattern="dd.MM.yyyy" value="${fromDate}" />' />
		</div>
	
	</div>
	
	<div class="col-xs-4">
		<div class="form-group">
			<label>По</label>
			<div class="form-control" id="to-date"></div>
			<input type="hidden" name="to_date" value='<fmt:formatDate pattern="dd.MM.yyyy" value="${toDate}" />' />
		</div>
	</div>			

	<div class="col-xs-9">
		<div class="form-group">
			<label>Фильтр по e-mail</label>
			<input type="text" autocomplete="off" class="form-control" name="email" placeholder="c 3 символов" />
		</div>	
	</div>

	<div class="col-xs-3">
		<div class="form-group">
			<label>&nbsp;</label>
			<a href="#" class="btn btn-default btn-block" id="refresh-button">Обновить</a>
		</div>	
	</div>

</div>

<hr/>

<table class="table" id="sessions-table">
	<thead>
		<tr>
			<th>Участник</th>
			<th>Вход</th>
			<th>Выход</th>
			<th>Продолжительность</th>
			<th>ip-адрес</th>
			<th>useragent</th>
		</tr>
	</thead>
	<tbody style="font-size : 12px;">
		
	</tbody>
</table>

<div class="row list-loader-animation"></div>