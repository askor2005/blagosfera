<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">
    div#incoming-payment-dialog-modal input[readonly] {
        cursor: pointer;
        color: #428bca;
        text-decoration: underline;
    }
</style>

<div class="modal fade" id="incoming-payment-dialog-modal" tabindex="-1" role="dialog" aria-hidden="true"
     data-keyboard="false">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Закрыть</span>
                </button>
                <h4 class="modal-title">Пополнение счёта</h4>
            </div>
            <div class="modal-body">
                <form role="form" action="/payment/incoming/account/init" method="post">

                    <div class="input-group">
                        <span class="input-group-addon">Счет</span>
                        <select id="account_type_id" class="form-control" name="account_type_id" required>
                        </select>
                    </div>

                    <br>

                    <div class="input-group">
                        <span class="input-group-addon">Платежная система</span>
                        <select id="payment_system_id" class="form-control" name="payment_system_id" required>
                        </select>
                    </div>

                    <span id="incoming-payment-commission" class="help-block"></span>

                    <div id="second-part">
                        <div class="input-group">
                            <span class="input-group-addon">Сумма пополнения в Ра</span>
                            <input type="text" class="form-control" id="replenishment-ra-amount" name="ra_amount"/>
                        </div>

                        <br>

                        <div class="input-group">
                            <span class="input-group-addon">Сумма к оплате в рублях</span>
                            <input type="text" class="form-control" id="payment-rur-amount" name="rur_amount"/>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                <button type="button" class="btn btn-primary" id="incoming-payment-submit">Пополнить счёт</button>
            </div>
        </div>
    </div>
</div>