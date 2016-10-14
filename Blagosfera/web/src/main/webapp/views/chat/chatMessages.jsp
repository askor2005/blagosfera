<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>


<div class="row">
	<div class="col-xs-12">
		<ul class="nav nav-tabs" role="tablist">
			<li><a href="#">Списко чатов</a></li>
			<li class="active"><a href="#">${profile.shortName} <i
					class="glyphicon glyphicon-remove"></i>
			</a></li>
		</ul>
	</div>
	<div class="col-xs-12" style="min-height: 450px;">
		<c:choose>
			<c:when test="${empty  messages}">
				<div class="text-muted text-center col-xs-12"
					style="padding-top: 25px;">Здесь будет выводиться история
					переписки</div>
			</c:when>
			<c:otherwise>
				<c:forEach var="message" items="${messages}" varStatus="counter">
					<div class="col-xs-12" style="padding-top: 25px;">
						<div class="col-xs-2">
							<img class="img-thumbnail" src="/photo/${message.from.ikp}" />
						</div>
						<div class="col-xs-10">
							<a href="/sharer/${message.from.ikp}">${message.from.shortName}</a>
							<span class="text-muted"> ${message.posted} </span>
							<p>${message.message}</p>
						</div>
						<hr />
					</div>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="col-xs-12">
		<div class="well">
			<div class="row">
				<div class="col-xs-2">
					<a href="/sharer/${sharer.ikp}"> <img class="img-thumbnail"
						src="/photo/${sharer.ikp}" />
					</a>
				</div>
				<div class="col-xs-8">
					<div class="row">
						<form method="post" action="/chat/addMessage">
							<input type="hidden" name="to" value="${profile.ikp}" />
							<div class="col-xs-12">
								<textarea name="message" style="height: 77px; width: 100%;"></textarea>
							</div>
							<div class="col-xs-12" style="padding-top: 15px;">
								<input type="submit" class="btn btn-info pull-left"
									value="Отправить" /> <a href="#"
									class="btn btn-link pull-right">Прикрепить</a>
							</div>
						</form>
					</div>
				</div>
				<div class="col-xs-2">
					<a href="/sharer/${profile.ikp}"> <img class="img-thumbnail"
						src="/photo/${profile.ikp}" />
					</a>
				</div>
			</div>
		</div>
	</div>
</div>

