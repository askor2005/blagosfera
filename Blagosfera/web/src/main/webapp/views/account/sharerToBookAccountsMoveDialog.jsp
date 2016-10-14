<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="sharer-to-book-accounts-move-modal" tabindex="-1" role="dialog" aria-hidden="true"
     data-keyboard="false">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title">Перевод средств</h4>
            </div>
            <div class="modal-body">
                <input type="hidden" id="to-community-id" value="${communityId}"/>
                <div class="form-group">
                    <label>ПО</label>
                    <input type="text" readonly="readonly" id="to-community-name" class="form-control" value="<c:out value="${community.name}"/>">
                </div>
                <div class="form-group">
                    <label>Получатель</label>
                    <input type="text" readonly="readonly" id="to-sharer-name" class="form-control" value="${sharer.name}">
                </div>
                <div class="form-group">
                    <label>Списать со счёта</label>
                    <select id="from-account-type-id" class="form-control">
                        <!-- TODO -->
                        <c:forEach items="${accountTypes}" var="at">
                            <option data-account-type-id="${at.id}" value="${at.id}">${at.name} <c:if
                                    test="${not empty accountsMap[at]}">(${radom:formatMoney(accountsMap[at].balance)} Ра)</c:if></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label>Сумма перевода</label>
                    <input type="text" class="form-control" id="amount" placeholder="Сумма для перевода" name="amount"
                           required="required" value="0.00"/>
                </div>
                <div class="form-group">
                    <label>Назначение платежа</label>
                    <textarea id="sender-comment" class="form-control"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                <button type="button" class="btn btn-primary" id="apply-button">Перевести</button>
            </div>
        </div>
    </div>
</div>