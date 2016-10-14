<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<script type="text/javascript">

	var applicationId = "${application.id}";
	var applicationName = "${application.name}";
	var applicationCost = "${radom:formatMoney(application.cost)}";
	
	$(document).ready(function() {

		$("a.install-link").click(function() {
			$.radomJsonPost("/apps/install.json", {
				application_id : applicationId
			}, function() {
				$("a.install-link").hide();
				$("a.uninstall-link").show();
				$("a.start-link").show();
				$("a.hide-in-menu-link").show();
			});
			return false;
		});
		
		$("a.uninstall-link").click(function() {
			$.radomJsonPost("/apps/uninstall.json", {
				application_id : applicationId
			}, function() {
				$("a.uninstall-link").hide();
				$("a.install-link").show();
				$("a.start-link").hide();
				$("a.hide-in-menu-link").hide();
				$("a.show-in-menu-link").hide();
			});
			return false;
		});

		$("a.download-link").click(function() {
			$.radomJsonPost("/apps/download.json", {
				application_id : applicationId
			}, function() {
				$("a.download-link").hide();
				$("a.install-link").show();
			});
			return false;
		});

		$("a.buy-link").click(function() {
 			DebitDialog.show("Покупка приложения [" + applicationName + "]", applicationCost, "Купить", "Отмена", function(accountTypeId) {
 				DebitDialog.hide();
 	 			$.radomJsonPost("/apps/buy.json", {
 	 				application_id : applicationId,
 	 				account_type_id : accountTypeId
 	 			}, function(application) {
 					Accounts.refresh();
 					$("a.buy-link").hide();
 					$("a.install-link").show();
 	 			});
 			});
 			return false;
		});
		
		$("a.show-in-menu-link").click(function() {
			$.radomJsonPost("/apps/show_in_menu.json", {
				application_id : applicationId
			}, function() {
				$("a.show-in-menu-link").hide();
				$("a.hide-in-menu-link").show();
			});
			return false;
		});
	
		$("a.hide-in-menu-link").click(function() {
			$.radomJsonPost("/apps/hide_in_menu.json", {
				application_id : applicationId
			}, function() {
				$("a.hide-in-menu-link").hide();
				$("a.show-in-menu-link").show();
			});
			return false;
		});		
		
	});
	
</script>

<h2>${application.name}</h2>
<hr />
<a <c:if test="${not (not empty sharerApplication and sharerApplication.installed)}">style="display : none;"</c:if> class="btn btn-success start-link" href="${application.startLink}"> <i class="glyphicon glyphicon-play"></i> Запустить приложение</a>
<a <c:if test="${not (not empty sharerApplication and sharerApplication.installed)}">style="display : none;"</c:if> class="btn btn-danger uninstall-link" href="#"> <i class="glyphicon glyphicon-remove"></i> Удалить приложение</a>

<a <c:if test="${not (not empty sharerApplication and not sharerApplication.installed)}">style="display : none;"</c:if> class="btn btn-info install-link" href="#"> <i class="glyphicon glyphicon-ok"></i> Установить приложение</a>
<c:if test="${application.free}">
	<a <c:if test="${not empty sharerApplication}">style="display : none;"</c:if> class="btn btn-primary download-link" href="#"> <i class="glyphicon glyphicon-save"></i> Загрузить приложение</a>
</c:if>
<c:if test="${not application.free}">
	<a <c:if test="${not empty sharerApplication}">style="display : none;"</c:if> class="btn btn-primary buy-link" href="#"> <i class="glyphicon glyphicon-tag"></i> Купить приложение за ${application.cost} Ра</a>
</c:if>

<c:if test="${not empty applicationSection}">
	<a <c:if test="${not (not empty sharerApplication and sharerApplication.installed and not sharerApplication.showInMenu)}">style="display : none;"</c:if> class="btn btn-default show-in-menu-link" href="#"> <i class="glyphicon glyphicon-eye-open"></i> Показывать в меню</a>
	<a <c:if test="${not (not empty sharerApplication and sharerApplication.installed and sharerApplication.showInMenu)}">style="display : none;"</c:if> class="btn btn-default hide-in-menu-link" href="#"> <i class="glyphicon glyphicon-eye-close"></i> Не показывать в меню</a>
</c:if>

<hr/>
${application.description}
<hr/>

<div class="modal fade" id="buy-modal" role="dialog" aria-labelledby="buy-modal-label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="buy-modal-label">Покупка приложения</h4>
      </div>
      <div class="modal-body">

			<input type="hidden" id="dialog-buy-application-id" value="" />

      		<div class="form-group">
      			<label>Выберите счёт для оплаты</label>
				<select id="dialog-buy-account-type-id" class="form-control">
					<c:forEach items="${accountTypes}" var="at">
						<option value="${at.id}">${at.name} <c:if test="${not empty accountsMap[at]}">(${radom:formatMoney(accountsMap[at].balance)} Ра)</c:if> </option>
					</c:forEach>
				</select>
			</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
        <button type="button" class="btn btn-primary" id="dialog-buy-button">Купить</button>
      </div>
    </div>
  </div>
</div>