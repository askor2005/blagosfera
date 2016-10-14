<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script id="activity-journal-row-template" type="x-tmpl-mustache">
	<tr>
		<td>
			{{event.date}}
		</td>
		<td>
			<a href="{{event.userLink}}">{{event.userShortName}}<a>
		</td>
		<td>
			{{event.type}}
		</td>	
		<td>
			{{#event.additionalLink}}
				<a href="{{event.additionalLink}}">{{event.additionalContent}}</a>
			{{/event.additionalLink}}
		</td>	
	</tr>
</script>

<script id="activity-settings-template" type="x-tmpl-mustache">

	<div class="row">

		<div class="col-xs-6">

			<div class="form-group">
				<label>С</label>
				<div class="form-control" id="from-date"></div>
				<input type="hidden" name="from_date" value='{{fromDate}}" />' />
			</div>

		</div>

		<div class="col-xs-6">
			<div class="form-group">
				<label>По</label>
				<div class="form-control" id="to-date"></div>
				<input type="hidden" name="to_date" value='{{toDate}}" />' />
			</div>
		</div>

		<div class="col-xs-6">
			<div class="form-group">
				<label>Тип события</label>
				<select class="form-control" name="type">
					<option value="">Все</option>
					{{#typeGroups}}
						<option disabled>──────────{{name}}──────────</option>
						{{#eventTypes}}
							<option value="{{type}}">{{name}}</option>
						{{/eventTypes}}
					{{/typeGroups}}
				</select>
			</div>
		</div>

		<div class="col-xs-6">
			<div class="form-group">
				<label>&nbsp;</label>
				<a href="#" class="btn btn-default btn-block" id="refresh-button">Обновить</a>
			</div>
		</div>

	</div>

</script>
<script type="text/javascript">

	var communityId = "${communityId}";

	var CommunityActivityJournal = {
			
		communityId : communityId,
		rowTemplate : $('#activity-journal-row-template').html(),
		rowTemplateParsed : false,
		
		getRowTemplate : function() {
			if (!CommunityActivityJournal.rowTemplateParsed) {
				Mustache.parse(CommunityActivityJournal.rowTemplate);
				CommunityActivityJournal.rowTemplateParsed = true;
			}
			return CommunityActivityJournal.rowTemplate;
		},
		
		getRowMarkup : function(event) {
			var model = {};
			model.event = event;
			
			var markup = Mustache.render(CommunityActivityJournal.getRowTemplate(), model);
			return $(markup);
		},
		
		showRowMarkup : function(event, prepend) {
			var $markup = CommunityActivityJournal.getRowMarkup(event);
			if (prepend) {
				$("table#events-table").prepend($markup);
			} else {
				$("table#events-table").append($markup);
			}
		},
		
		initScrollListener : function() {
			$("table#events-table tbody").empty();
			ScrollListener.init("/group/" + CommunityActivityJournal.communityId + "/log_events.json", "get", function() {
				var params = {};
				var type = $("select[name=type]").val();
				if (type) {
					params.type = type;
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
				var $tbody = $("table#events-table");
				if (entries != null) {
					$.each(entries, function (index, entry) {
						CommunityActivityJournal.showRowMarkup(entry);
					});
				}
				$("div.list-loader-animation").hide();
			});
		}
		
			
	};

	function loadJournalActivityPageData(communityId, callBack) {
		$.radomJsonPost(
				"/group/" + communityId + "/activity_journal_page_data.json",
				{},
				callBack
		);
	}
	
	$(document).ready(function() {
		loadJournalActivityPageData(communityId, function (communityJournalActivityPageData) {
			initCommunityHead(communityJournalActivityPageData.community);
			initCommunityMenu(communityJournalActivityPageData.community);
			initActivityJournal(communityJournalActivityPageData);
		});
	});

	function initActivityJournal(communityJournalActivityPageData) {
		var activitySettingsTemplate = $("#activity-settings-template").html();
		Mustache.parse(activitySettingsTemplate);
		var markup = Mustache.render(activitySettingsTemplate, {
			typeGroups : communityJournalActivityPageData.typeGroups
		});
		$("#journalSettings").append(markup);

		$("table#events-table").fixMe();
		
		Ext.onReady(function() {

			Ext.create('Ext.form.field.Date', {
				renderTo : 'from-date',
				xtype : 'datefield',
				name : 'from_date',
				format : 'd.m.Y',
				width : '100%',
				value : communityJournalActivityPageData.fromDate,
				listeners : {
					
					change: function (t,n,o) {

					},
					select: function (t,n,o) {
						CommunityActivityJournal.initScrollListener();
					}
					
				}
			});
			Ext.create('Ext.form.field.Date', {
				renderTo : 'to-date',
				xtype : 'datefield',
				name : 'to_date',
				format : 'd.m.Y',
				width : '100%',
				value : communityJournalActivityPageData.toDate,
				listeners : {
					
					change: function (t,n,o) {

					},
					select: function (t,n,o) {
						CommunityActivityJournal.initScrollListener();
					}
					
				}
			});
		
		
			CommunityActivityJournal.initScrollListener();
			$("select[name=type]").change(function() {
				CommunityActivityJournal.initScrollListener();	
			});
			$("a#refresh-button").click(function() {
				CommunityActivityJournal.initScrollListener();
				return false;
			});
			
		});

	}
	
</script>

<t:insertAttribute name="communityHeader" />
<h2>Журнал активности</h2>
<hr/>


<div id="journalSettings"></div>
<hr/>

<table class="table" id="events-table">
	<thead>
		<tr>
			<th>Дата, время</th>
			<th>Инициатор</th>
			<th>Тип события</th>
			<th>Дополнительная информация</th>
		</tr>
	</thead>
	<tbody style="font-size : 12px;">
		
	</tbody>
</table>

<div class="row list-loader-animation"></div>