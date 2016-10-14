<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			Статистика системы
			<i class="pull-right glyphicon glyphicon-info-sign"></i>	
		</h3>
	</div>
	<ul class="list-group">
		<li class="list-group-item">
			Участников: ${sharersTotalCount}
		</li>
		<li class="list-group-item">
			Объединений: ${rootCommunitiesTotalCount}				
		</li>
		<li class="list-group-item text-center">
			<a href="/register" class="btn btn-primary btn-xs">Присоединиться</a>
		</li>				
	</ul>	
</div>
