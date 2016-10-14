<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
<script type="text/javascript" src="/js/certification.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		// инициализация компонента универсальных списков (страна)
		var selectedItems = [];
		selectedItems.push($("[data-field-internal-name='COUNTRY_CL']").val());
		RameraListEditorModule.init(
				$("#COUNTRY_CL"),
				{
					labelClasses : ["checkbox-inline"],
					labelStyle : "margin-left: 10px;",
					selectedItems: selectedItems,
					selectClasses: ["form-control"]
				},
				function(event, data) {
					if (event == RameraListEditorEvents.VALUE_CHANGED) {
						$("[data-field-internal-name='COUNTRY_CL']").val(data.value);
					}
				}
		);
		selectedItems = [];
		selectedItems.push($("[data-field-internal-name='FCOUNTRY_CL']").val());
		RameraListEditorModule.init(
				$("#FCOUNTRY_CL"),
				{
					labelClasses : ["checkbox-inline"],
					labelStyle : "margin-left: 10px;",
					selectedItems: selectedItems,
					selectClasses: ["form-control"]
				},
				function(event, data) {
					if (event == RameraListEditorEvents.VALUE_CHANGED) {
						$("[data-field-internal-name='FCOUNTRY_CL']").val(data.value);
					}
				}
		);
	});
</script>

<h1>
	Идентификация участника системы [${profile}]
</h1>
<hr />

<div class="row">
	<div class="col-xs-3">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title" name="panel-title">Проверка персональных данных</h3>
			</div>
		</div>
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title" name="panel-title">Подписание соглашения</h3>
			</div>
		</div>
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title" name="panel-title">Создание биометрического образа</h3>
			</div>
		</div>
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title" name="panel-title">Создание электронного паспорта</h3>
			</div>
		</div>
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title" name="panel-title">Завершение идентификации</h3>
			</div>
		</div>
	</div>
	<div class="col-xs-9">
		<div id="personal-data-block">
			<div class="alert alert-info" role="alert">
				В блоках ниже представленные персональные данные идентифицируемого участника.
				Регистратору следует проверить соответствие этих данных паспорту регистрируемого.
				В случае обнаружения несоответствия регистратор должен внести необходимые исправления.
				После проверки каждого отдельного поля регистратор должен отметить его галочкой как проверенное. 
			</div>
			<c:forEach items="${fieldsGroups}" var="fg">
				<div class="panel panel-default" data-group-name="${fg.internalName}">
					<div class="panel-heading">
						<h3 class="panel-title">${fg.name}</h3>
					</div>
					<div class="panel-body">
						<div class="row">
							<c:forEach items="${fg.fields}" var="f">
								<c:if test="${f.type != 'GEO_POSITION' and f.type != 'GEO_LOCATION' and f.internalName != 'NATIONALITY' and f.internalName != 'LANGUAGE'}">
									<div class="col-xs-4">
										<div class="form-group" style="position : relative; padding-right : 32px;">
											<label>${f.name}</label>
											<c:choose>
												<c:when test="${(f.type == 'COUNTRY') and (not profile.verified)}">
													<input type="hidden" class="form-control"
														   data-field-type='${f.type}'
														   data-field-id='${f.id}'
														   data-field-internal-name="${f.internalName}"
														   readonly="readonly"
														   value='${profile.getFieldValue(f).stringValue}' />
													<c:choose>
														<c:when test="${f.internalName == 'COUNTRY_CL'}">
															<div id="COUNTRY_CL" rameraListEditorName="country_id"></div>
														</c:when>
														<c:when test="${f.internalName == 'FCOUNTRY_CL'}">
															<div id="FCOUNTRY_CL" rameraListEditorName="country_id"></div>
														</c:when>
													</c:choose>
												</c:when>
												<c:when test="${(f.type == 'SELECT') and (not profile.verified)}">
								      				<select class="form-control" name='f:${f.id}'>
								      					<option></option>
														<c:forEach items="${f.possibleValues}" var="p">
															<option <c:if test="${p.stringValue.equals(profile.getFieldValue(f).stringValue)}">selected='selected'</c:if>  value="${p.stringValue}">${p.stringValue}</option>
														</c:forEach>
													</select>
												</c:when>
												<c:otherwise>
													<c:choose>
														<c:when test="${f.type == 'COUNTRY'}">
															<input type="hidden"
																   class="form-control"
																   value='${profile.getFieldValue(f).stringValue}'
																   name='f:${f.id}' data-field-name='${f.internalName}' data-field-internal-name='${f.internalName}'
																   data-field-type='${f.type}' data-field-id='${f.id}'
																   placeholder='${f.example}'/>
															<c:choose>
																<c:when test="${f.internalName == 'COUNTRY_CL'}">
																	<div id="COUNTRY_CL" rameraListEditorName="country_id"></div>
																</c:when>
																<c:when test="${f.internalName == 'FCOUNTRY_CL'}">
																	<div id="FCOUNTRY_CL" rameraListEditorName="country_id"></div>
																</c:when>
															</c:choose>
														</c:when>
														<c:otherwise>
															<input type="text"
																   class="form-control"
																   value='${profile.getFieldValue(f).stringValue}'
																   name='f:${f.id}' data-field-name='${f.internalName}' data-field-internal-name='${f.internalName}'
																   data-field-type='${f.type}' data-field-id='${f.id}'
																   placeholder='${f.example}'/>
														</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
											<input type="checkbox" style="position : absolute; top : 32px; right : 10px;" />
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>
					</div>
				</div>
			</c:forEach>
			<div class="alert alert-info" role="alert">
				Ниже представлены фотография из профиля идентифицируемого участника и поле для вставки фотографии с веб-камеры.
				Для продолжения регистратору необходимо загрузить фотографию регистрируемого со своей веб-камеры, а также определить, следует ли заменить фотографию в профиле на фотографию полученную с веб-камеры.  
			</div>
			<div class="row">
				<div class="col-xs-6 text-center">
					<img class="img-thumbnail" src="${radom:resizeImage(profile.avatar, 'c250')}" style="width : 250px; height : 250px;" />
					<div class="form-group">
						<div class="radio">
							<label>
								<input type="radio" name="photo-radio" checked="checked" />
								Использовать в профиле данную фотографию
							</label>
						</div>
					</div>
				</div>
				<div class="col-xs-6 text-center">
					<img class="img-thumbnail" style="width : 250px; height : 250px;" />
					<div class="form-group">
						<div class="radio">
							<label>
								<input type="radio" name="photo-radio" />
								Использовать в профиле фотографию с веб-камеры
							</label>
						</div>
					</div>
				</div>
			</div>
			<div class="text-center">
				<button class="btn btn-primary">Подтвердить</button>
			</div>
			<hr/>
		</div>
		<div class="navigation-block">
			<div class="row">
				<div class="col-xs-6 text-left">
					<button class="btn btn-default">Вернуться</button>
				</div>
				<div class="col-xs-6 text-right">
					<button class="btn btn-default">Продолжить</button>
				</div>
			</div>
		</div>
	</div>
</div>