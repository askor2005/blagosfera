<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript">
	$(document).ready(function() {
		$("a#refresh-button").click(function() {
			storeThesaurus.load();
			return false;
		});
	});
</script>

<%@include file="cyberbrainSections.jsp" %>

<h1>Мои термины</h1>

<hr/>

<form role="form" method="post" enctype="multipart/form-data">
	<div class="row">
		<div class="col-xs-9">
			<div class="form-group">
				<label>Фильтр по тегу</label>
				<input id="tag-filter" type="text" autocomplete="off" class="form-control" />
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

	<%@include file="thesaurusGrid.jsp" %>

	<hr/>
</form>