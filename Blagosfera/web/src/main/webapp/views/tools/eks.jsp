<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<h1>ЭКС</h1>

<hr/>

<form role="form" method="post" enctype="multipart/form-data">
	<div class="form-group">
		<label for="file-input">Загрузка файла</label>
    	<input type="file" id="file-input" name="file" />
	</div>
	<button type="submit" class="btn btn-default">Загрузить</button>
</form>