<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">
  div#outgoing-payment-dialog-modal input[readonly] {
    cursor : pointer;
    color : #428bca;
    text-decoration : underline;
  }
</style>

<div class="modal fade" id="outgoing-payment-dialog-modal" tabindex="-1" role="dialog" aria-hidden="true"
     data-keyboard="false">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Закрыть</span></button>
        <h4 class="modal-title">Вывод средств</h4>
      </div>
        <div class="modal-body">
          <form role="form">

            <div class="form-group">
              <select id="account_type_id" class="form-control" name="account_type_id" required>
                <option value="">Счет</option>
                <c:forEach items="${accountTypes}" var="t">
                  <option value="${t.id}">${t.name}</option>
                </c:forEach>
              </select>
            </div>

            <div class="form-group">
              <select id="payment_system_id" class="form-control" name="payment_system_id" required>
                <option value="">Платежная система</option>
                <c:forEach items="${paymentSystems}" var="s">
                  <option value="${s.id}" data-ramera-comission="${s.rameraIncomingComission}">${s.name}</option>
                </c:forEach>
              </select>
              <span id="outgoing-payment-commission" class="help-block"></span>
            </div>

            <div id="second-part">
              <div class="form-group">
                <label>Номер кошелька</label>
                <input type="text" class="form-control" id="receiver" name="receiver"/>
              </div>

              <div class="form-group">
                <label>Сумма для вывода в Ра</label>
                <input type="text" class="form-control" id="outgoing-ra-amount" name="ra_amount"/>
              </div>

              <div class="form-group">
                <label>Сумма к получению в рублях</label>
                <input type="text" class="form-control" id="obtain-rur-amount" name="rur_amount"/>
              </div>
            </div>

          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
          <button type="button" class="btn btn-primary" id="outgoing-payment-submit">Вывести средства</button>
        </div>
    </div>
  </div>
</div>