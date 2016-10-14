<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<h1>
	<c:if test="${not empty application}">
		Редактирование приложения
	</c:if>
	<c:if test="${empty application}">
		Создание приложения
	</c:if>	
</h1>
<hr />


<form role="form" id="form">
	<c:if test="${not empty application}">
		<input type="hidden" id="id" name="id" value="${application.id}" />
	</c:if>
	<div class="form-group">
		<label for="name">Название</label>
		<input name="name" type="text" class="form-control" id="name" placeholder="Название" value="${application.name}" />
	</div>
	
	<div class="checkbox">
		<label>
			<input type="checkbox" name="for_communities" <c:if test="${application.forCommunities}">checked="checked"</c:if> /> Приложение для объединений
		</label>
	</div>
	
	<div class="form-group" <c:if test="${not application.forCommunities}">style="display : none;"</c:if> id="community-association-forms">
		<label>Привязка к формам объединений</label>
		<br/>
		<c:forEach items="${CommunityAssociationFormsGroups}" var="afg">
			<br/>
			<label>
				${afg.name}
			</label>
			<a href="#" id="deselect-all" class="pull-right" style="margin-left : 20px;" data-group-id="${afg.id}">Выключить все</a>
			<a href="#" id="select-all" class="pull-right" style="margin-left : 20px;" data-group-id="${afg.id}">Включить все</a>
			<div class="row">
				<c:forEach items="${afg.forms}" var="af">
					<div class="col-xs-4">
						<div class="checkbox">
							<label>
								<input data-group-id="${afg.id}" type="checkbox" name="community_association_form_id" value="${af.id}" <c:if test="${application.forCommunities && application.communityAssociationForms.contains(af)}">checked="checked"</c:if> /> ${af.name}
							</label>
						</div>
					</div>				
				</c:forEach>		
			</div>
		</c:forEach>
	</div>
	
	<div class="form-group">
		<label for="description">Описание</label>
		<textarea name="description" class="form-control" id="description" rows="30" style="font-size : 11px; font-family: monospace;">${application.description}</textarea>
	</div>	
	<c:if test="${not empty application}">
		<div class="form-group">
			<label for="logo-url">Логотип</label>
			<br/>
			<img id="logo-img" src='${radom:resizeImage(application.logoUrl, "c200")}' />
			<input name="logo_url" type="hidden" class="form-control" id="logo-url" value="${application.logoUrl}" />
			<br/>
			<br/>
			<a href="#" id="logo-upload-link" class="btn btn-sm btn-primary">Загрузить</a>
		</div>
	</c:if>
	<c:if test="${empty application}">
		<input name="logo_url" type="hidden" value="${defaultLogoUrl}" />
	</c:if>
	<div class="form-group">
		<label for="name">Стоимость</label>
		<input name="cost" type="text" class="form-control" id="cost" placeholder="Стоимость" value="${application.cost}" />
	</div>
	<div class="form-group">
		<label for="featuresLibrary">Библиотека возможностей</label>
		<select name="features_library_section_id" class="form-control" id="featuresLibrary">
			<c:forEach items="${featuresLibrarySections}" var="f">
				<option 
					value="${f.id}"
					<c:if test="${not empty application and application.featuresLibrarySection.id == f.id}">selected="selected"</c:if>
					<c:if test="${not empty featuresLibrarySection and featuresLibrarySection.id == f.id}">selected="selected"</c:if>
				>
					${featuresLibrarySectionsMap[f]}
				</option>
			</c:forEach>
		</select>
	</div>	
	<div class="form-group">
		<label for="iframeUrl">URL IFrame'a</label>
		<input name="iframe_url" type="text" class="form-control" id="iframe-url" placeholder="Iframe URL" value="${application.iframeUrl}" />
	</div>		
	<div class="form-group">
		<label for="redirect-uri">Redirect URI</label>
		<input name="redirect_uri" type="text" class="form-control" id="redirect-uri" placeholder="Redirect URI" value="${application.redirectUri}" />
	</div>	
	<a href="#" id="edit" class="btn btn-primary">Сохранить изменения</a>
	<a
		<c:if test="${not empty application}">href="${application.featuresLibrarySection.link}"</c:if>
		<c:if test="${empty application}">href="/"</c:if>
		class="btn btn-default"
	>
		Отмена
	</a>
</form>

<hr/>
	<div class="form-group">
		<label>Client ID</label>
		<input type="text" class="form-control" value="${application.clientId}" readonly="readonly" id="client-id" style="font-family : monospace;" />
	</div>
	<div class="form-group">
		<label>Client Secret</label>
		<input type="text" class="form-control" value="${application.clientSecret}" readonly="readonly" id="client-secret" style="font-family : monospace;" />
	</div>
	<div class="form-group">
		<a href="#" id="generate-client-id-and-secret-link" class="btn btn-default">Сгенерировать Client ID и Client Secret</a>
	</div>
<hr/>

<script type="text/javascript">
	$(document).ready(function() {
		
		$("input[name=for_communities]").change(function() {
			if ($("input[name=for_communities]:checked").length == 0) {
				$("div#community-association-forms").slideUp();
			} else {
				$("div#community-association-forms").slideDown();
			}
		});

		$("a#select-all").click(function() {
			var groupId = $(this).attr("data-group-id");
			$("input[type=checkbox][data-group-id=" + groupId + "]").prop("checked", true);
			return false;
		});

		$("a#deselect-all").click(function() {
			var groupId = $(this).attr("data-group-id");
			$("input[type=checkbox][data-group-id=" + groupId + "]").prop("checked", false);
			return false;
		});
		
		$("textarea#description").radomTinyMCE();
		
		$("a#logo-upload-link").click(function() {
			$.radomUpload("image", "/images/upload/application/" + $("input[type=hidden][name=id]").val() + ".json", ["jpg", "jpeg", "png", "bmp", "gif"], function(response) {
				$("img#logo-img").attr("src", Images.getResizeUrl(response.image, "c200"));
				$("input[name=logo_url]").val(response.image);
				bootbox.alert("Логотип сохранен");
			});
			return false;
		});
		
		$("a#edit").click(function() {
			$.radomJsonPost("/admin/apps/save.json", $("form#form").serialize(), function(application) {
				if ($("input[name=id]").length == 0) {
					window.location = "/admin/apps/edit/" + application.id;
				} else {
					bootbox.alert("Изменения сохранены");
				}
			});
			return false;
		});
		
		$("input[name=cost]").moneyInput();
		
		$("a#generate-client-id-and-secret-link").click(function() {
			$.radomJsonPost("/admin/apps/generate_client_id_and_secret.json", {
				id : $("input[type=hidden][name=id]").val()
			}, function(response) {
				$("input#client-id").val(response.clientId);
				$("input#client-secret").val(response.clientSecret);
				bootbox.alert("Client ID и Client Secret успешно сгенерированы");
			});
			return false;
		});
		
	});
</script>