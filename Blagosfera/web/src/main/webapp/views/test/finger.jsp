<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>


<script id="scan-description-template" type="x-tmpl-mustache">
	<div class="row">
		<div class="col-xs-2">
			<img class="img-thumbnail" src="{{avatar}}" />
		</div>
		<div class="col-xs-10">
			<p>Участник [<b>{{shortName}}</b>], просканируйте палец на сканере {{sensor}}, подключенном к {{server}}, для подтверждения выполнения тестового действия при помощи отпечатка пальца</p>
		</div>
	</div>
</script>

<script type="text/javascript">
	
	$(document).ready(function() {
		$("a#do-action-link").click(function() {
				
			$.radomJsonGet("/sharer/me.json", {}, function(response) {
				var me = response;
				
				$.radomFingerJsonAjax({
					url : "/finger_test.json",
					type : "post",
					data : {},
					deviceServiceUrl : $("input#device-service-url").val(),
					successRequestMessage : "Тестовое дейстиие выполнено успешно",
					errorMessage : "Ошибка выполнения тестового действия",
					
					getScanDescription : function(sensor, server) {
						var scanDescription = Mustache.render($("script#scan-description-template").html(), {
							avatar : me.avatar,
							shortName : me.shortName,
							sensor : sensor,
							server : (server == "localhost") ? ("локальному серверу авторизации Благосфера") : ("серверу авторизации Благосфера на " + server)
						});
						return scanDescription;
					},
					
					successCallback : function(response) {
						console.log("success finger test action");
						console.log(response);
					},
					errorCallback : function(response) {
						console.log("error finger test action");
						if (response) {
							console.log(response);
						}
					}
				});
				
			});
			
		});
	});
	
</script>

<h1>Finger Test</h1>
<hr/>

 <div class="form-group">
    <label>Device service URL</label>
    <input type="text" value="https://localhost:36123" id="device-service-url" class="form-control" />
</div>
<div class="form-group">
	<a class="btn btn-success btn-xl" id="do-action-link">Выполнить действие с пальцем</a>
</div>

<hr/>
