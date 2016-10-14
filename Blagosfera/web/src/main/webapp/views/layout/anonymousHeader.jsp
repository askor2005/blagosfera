<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="navbar navbar-default navbar-fixed-top" role="navigation">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="/sharer"> <img src="<c:if test="${buildBranch eq 'develop'}">/i/logo-50-dev.png</c:if><c:if test="${buildBranch ne 'develop'}">/i/logo-50.png</c:if>"/></a>
		</div>

		<ul class="nav navbar-nav" id="ra-menu"></ul>

		<ul class="nav navbar-nav navbar-right">
			<li>
				<a href="/login">Войти</a>
			</li>
			<li>
				<a href="/register">Зарегистрироваться</a>
			</li>
		</ul>

	</div>
</div>