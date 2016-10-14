<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="debit-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				<span class="sr-only">Закрыть</span></button>
        		<h4 class="modal-title"></h4>
      		</div>
      		<div class="modal-body">
      			<div class="form-group">
      				<label>Списать со счёта</label>
      				<select id="from-account-type-id" class="form-control">
						<c:forEach items="${accountTypes}" var="at">
							<option data-account-type-id="${at.id}" value="${at.id}">${at.name} <c:if test="${not empty accountsMap[at]}">(${radom:formatMoney(accountsMap[at].balance)} Ра)</c:if> </option>
						</c:forEach>
					</select>
      			</div>
				<div class="form-group">
      				<label id="amount-label"></label>
      				<input type="text" class="form-control" id="amount" readonly="readonly" value="0.00" />
      			</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" id="cancel-button" data-dismiss="modal"></button>
				<button type="button" class="btn btn-primary" id="apply-button"></button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function(){
		DebitDialog.init();
	});
	
	var DebitDialog = {
	
			callback : null,
			
			init : function() {
				
				$(radomEventsManager).bind("accounts.refresh", function(event, data) {
					$.each(data.accounts, function(index, account) {
						$("div#debit-modal option[data-account-type-id=" + account.type.id + "]").html(account.type.name + " (" + account.balance + " Ра)");
					});
				});
				
				$("div#debit-modal button#apply-button").click(function() {
					var accountTypeId = $("div#debit-modal select#from-account-type-id").val();
					if (DebitDialog.callback) {
						DebitDialog.callback(accountTypeId);
					}
				});
			},
			
			show : function(title, amount, applyText, cancelText, callback) {
				DebitDialog.callback = callback;
				$("div#debit-modal h4.modal-title").html(title);
				$("div#debit-modal input#amount").val(amount);
				$("div#debit-modal button#apply-button").html(applyText);
				$("div#debit-modal button#cancel-button").html(cancelText);
				$("div#debit-modal").modal("show");
			},
			
			hide : function() {
				$("div#debit-modal").modal("hide");
				$("div#debit-modal option").removeAttr("selected");
				$("div#debit-modal input#amount").val("0.00");				
			}
			
	};
	
</script>