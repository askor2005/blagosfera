<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
	$(document).ready(function() {
		Ext.onReady(function () {
			fixTimeKvantBegin = Ext.create('Ext.form.field.Date', {
				renderTo: 'fix-time-kvant-begin',
				xtype: 'datefield',
				name: 'fix-time-kvant-begin',
				format: 'Y-m-d H:i:s',
				width: '100%',
				listeners: {
					change: function (t, n, o) {
					},
					select: function (t, n, o) {
					}
				}
			});

			fixTimeKvantEnd = Ext.create('Ext.form.field.Date', {
				renderTo: 'fix-time-kvant-end',
				xtype: 'datefield',
				name: 'fix-time-kvant-end',
				format: 'Y-m-d H:i:s',
				width: '100%',
				listeners: {
					change: function (t, n, o) {
					},
					select: function (t, n, o) {
					}
				}
			});
		});

		$("a#refresh-button").click(function() {
			storeJournalAttention.load();
			return false;
		});
	});
</script>

<%@include file="cyberbrainSections.jsp" %>

<h1>Мое внимание</h1>

<hr/>

<form role="form" method="post" enctype="multipart/form-data">
	<%@include file="journalAttentionAddNewRecord.jsp" %>

	<hr/>

	<div class="row">
		<div class="col-xs-4">
			<div class="form-group">
				<label>Фильтр по тегу</label>
				<input id="tag-kvant-filter" type="text" autocomplete="off" class="form-control" />
			</div>
		</div>

		<div class="col-xs-3">
			<div class="form-group">
				<label>Дата фиксации с</label>
				<div class="form-control" id="fix-time-kvant-begin"></div>
			</div>
		</div>

		<div class="col-xs-3">
			<div class="form-group">
				<label>Дата фиксации по</label>
				<div class="form-control" id="fix-time-kvant-end"></div>
			</div>
		</div>

		<div class="col-xs-2">
			<div class="form-group">
				<label>&nbsp;</label>
				<a href="#" class="btn btn-default btn-block" id="refresh-button">Обновить</a>
			</div>
		</div>
	</div>

	<hr/>

	<%@include file="journalAttentionGrid.jsp" %>

	<hr/>
</form>