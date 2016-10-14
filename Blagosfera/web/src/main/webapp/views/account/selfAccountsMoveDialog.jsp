<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="self-accounts-move-modal" tabindex="-1" role="dialog" aria-labelledby="self-accounts-label"
     aria-hidden="true" data-keyboard="false">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title" id="self-accounts-move-label">Перевод средств между своими счетами</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>Списать со счёта</label>
                    <select id="from-account-type-id" class="form-control"></select>
                </div>
                <div class="form-group">
                    <label>Зачислить на счёт</label>
                    <select id="to-account-type-id" class="form-control"></select>
                </div>
                <div class="form-group">
                    <label>Сумма перевода</label>
                    <input type="text" class="form-control" id="amount" placeholder="Сумма для перевода" name="amount"
                           required="required" value="0.00"/>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                <button type="button" class="btn btn-primary" id="apply-button">Перевести</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        SelfAccountsMoveDialog.init();
    });

    var SelfAccountsMoveDialog = {
        init: function () {
            $(radomEventsManager).bind("accounts.refresh", function (event, data) {
                $.each(data.accounts, function (index, account) {
                    $("div#self-accounts-move-modal option[data-account-type-id=" + account.type.id + "]").html(account.type.name + " (" + account.balance + " Ра)");
                });
            });

            var $amount = $("div#self-accounts-move-modal input#amount");
            $amount.moneyInput();
            var $apply = $("div#self-accounts-move-modal button#apply-button")

            $apply.click(function () {
                $("div#self-accounts-move-modal").modal("hide");

                $.radomFingerJsonAjax({
                    url: "/account/self_move.json",
                    type: "post",
                    contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                    data: {
                        from_account_type_id: $("div#self-accounts-move-modal select#from-account-type-id").val(),
                        to_account_type_id: $("div#self-accounts-move-modal select#to-account-type-id").val(),
                        amount: $("div#self-accounts-move-modal input#amount").val()
                    },
                    successRequestMessage: "Перевод успешно выполнен",
                    errorMessage: "Ошибка выполнения перевода",
                    successCallback: function (response) {
                        Accounts.refresh();
                        $("div#self-accounts-move-modal option").removeAttr("selected");
                        $("div#self-accounts-move-modal input#amount").val("0.00");
                    },
                    errorCallback: function (response) {

                    }
                });
            });
        },

        show: function () {
            $("div#self-accounts-move-modal").modal("show");
        }
    };

</script>