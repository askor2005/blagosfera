<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">
    th {
        text-align: center;
        vertical-align: middle;
    }
</style>

<script id="transaction-row-template" type="x-tmpl-mustache">
	<tr class="{{trClass}}">
		<td>
			<a href="/account/transaction/{{transaction.id}}">{{transaction.id}}</a
		</td>
		<td>
			{{transaction.account.type.name}}
		</td>
		<td>
			{{transaction.date}}
		</td>
		<td>
			{{{comment}}}
		</td>
		<td>
			<span style="white-space: nowrap">{{transaction.amount}} Ра</span>
		</td>
		<td>
			{{{additional}}}
		</td>
	</tr>
</script>

<script type="text/javascript">
    var AccountsPage = {

        rowTemplate: $('#transaction-row-template').html(),
        rowTemplateParsed: false,

        getRowTemplate: function () {
            if (!AccountsPage.rowTemplateParsed) {
                Mustache.parse(AccountsPage.rowTemplate);
                AccountsPage.rowTemplateParsed = true;
            }

            return AccountsPage.rowTemplate;
        },

        getRowMarkup: function (transaction) {
            var model = {};
            model.transaction = transaction;
            if (transaction.state == "HOLD") {
                model.trClass = "warning";
            } else if (transaction.state == "REJECT") {
                model.trClass = "danger";
            } else if (transaction.state == "POST") {
                if (transaction.amount > 0) {
                    model.trClass = "success";
                } else {
                    model.trClass = "info";
                }
            }

            var additional = "";

            if (transaction.payment) {
                additional += "Платежная система: " + transaction.payment.system;
                if (transaction.payment.sender) {
                    additional += "<br/>Кошелек отправителя: " + transaction.payment.sender;
                }
                additional += "<br/>Кошелек получателя: " + transaction.payment.receiver;
                additional += "<br/>Сумма в рублях: " + transaction.payment.rurAmount;
                additional += "<br/>Комиссия: " + transaction.payment.rameraComission + "%";
                additional += "<br/>Сумма комиссии: " + transaction.payment.rameraComissionAmount;
            }
            if (transaction.senderComment) {
                additional += "Назначение платежа: " + transaction.senderComment;
            }

            model.additional = additional;


            if (transaction.otherAccountOwner) {
                var link = "<a href='" + transaction.otherAccountOwner.link + "' class='tooltiped-avatar' data-sharer-ikp='" + transaction.otherAccountOwner.ikp + "'>" + transaction.otherAccountOwner.fullName + "</a>"
                if (parseFloat(transaction.amount > 0)) {
                    model.comment = "Перевод средств от участника [" + link + "]";
                } else {
                    model.comment = "Перевод средств участнику [" + link + "]";
                }

            } else {
                model.comment = transaction.comment;
            }

            var markup = Mustache.render(AccountsPage.getRowTemplate(), model);
            var $markup = $(markup);
            return $markup;
        },

        showRowMarkup: function (transaction) {
            $("table#transactions").append(AccountsPage.getRowMarkup(transaction));
        },

        initScrollListener: function () {
            $("table#transactions tbody").empty();

            ScrollListener.init("/account/transactions.json", "get", function () {
                var params = {};

                var accountTypeId = $("select[name=account_type]").val();

                if (accountTypeId) {
                    params.account_type_id = accountTypeId;
                }

                var fromDate = $("input[name=from_date]").val();

                if (fromDate) {
                    params.from_date = fromDate;
                }

                var toDate = $("input[name=to_date]").val();

                if (toDate) {
                    params.to_date = toDate;
                }
                return params;
            }, function () {
                $("div.list-loader-animation").show();
            }, function (entries, page) {
                $.each(entries, function (index, entry) {
                    AccountsPage.showRowMarkup(entry);
                });

                $("div.list-loader-animation").hide();
            });
        },

        refreshAccounts: function () {
            $.radomJsonGet("/account/list.json", {}, function (response) {
                var accountsDiv = $("div#accounts");
                var accountsSelect = $("select[name=account_type]");

                accountsDiv.empty();
                accountsSelect.empty();
                accountsSelect.append('<option value="">Все</option>');

                $.each(response.accounts, function (index, account) {
                    accountsDiv.append("<h3>" + account.type.name + ": <strong>" + account.balance + "</strong> Ра</h3>");
                    accountsSelect.append('<option value="' + account.type.id + '">' + account.type.name + '</option>');
                });

                $("h2#total-balance b").html(response.total + " Ра");
            });
        }
    };

    $(document).ready(function () {
        $("table#transactions").fixMe();

        Ext.onReady(function () {
            Ext.create('Ext.form.field.Date', {
                renderTo: 'from-date',
                xtype: 'datefield',
                name: 'from_date',
                format: 'd.m.Y',
                width: '100%',
                value: '<fmt:formatDate pattern="dd.MM.yyyy" value="${fromDate}" />',
                listeners: {

                    change: function (t, n, o) {

                    },
                    select: function (t, n, o) {
                        AccountsPage.initScrollListener();
                    }

                }
            });

            Ext.create('Ext.form.field.Date', {
                renderTo: 'to-date',
                xtype: 'datefield',
                name: 'to_date',
                format: 'd.m.Y',
                width: '100%',
                value: '<fmt:formatDate pattern="dd.MM.yyyy" value="${toDate}" />',
                listeners: {

                    change: function (t, n, o) {

                    },
                    select: function (t, n, o) {
                        AccountsPage.initScrollListener();
                    }

                }
            });

            $("input[type=hidden][name=from_date]").remove();
            $("input[type=hidden][name=to_date]").remove();
        });

        AccountsPage.initScrollListener();

        $("select[name=account_type]").click(function () {
            AccountsPage.initScrollListener();
            return false;
        });

        $("a#refresh-button").click(function () {
            AccountsPage.initScrollListener();
            AccountsPage.refreshAccounts();
            return false;
        });

        $(radomEventsManager).bind("accounts.refresh", function (event, data) {
            $("h2#total-balance b").html(data.total + " Ра");
            $.each(data.accounts, function (index, account) {
                $("h3[data-account-type-id=" + account.type.id + "] strong").html(account.balance + " Ра");
            });
            AccountsPage.initScrollListener();
        });

        // Загрузка баланса
        $.radomJsonGet(
                "/sharer/balance.json",
                {},
                function (response) {
                    $("#sharerBalance").text(response.balance);
                },
                function () {
                    $("#sharerBalanceBlock").html("Ошибка получения баланса");
                }
        );

        AccountsPage.refreshAccounts();
    });
</script>

<h2 id="total-balance">
    Текущий баланс: <b id="sharerBalanceBlock"><span id="sharerBalance"></span> Ра</b>
</h2>

<div id="accounts"></div>

<hr/>

<div layout="row">
    <div layout="row">
        <div layout="column">
            <label>Счет</label>

            <select class="form-control" name="account_type">
                <option value="">Все</option>
            </select>
        </div>
    </div>
    <div class="col-xs-4">
        <label>С</label>

        <div class="form-group">
            <div id="from-date" class="form-control"></div>
            <input type="hidden" name="from_date" value='<fmt:formatDate pattern="dd.MM.yyyy" value="${fromDate}" />'/>
        </div>
    </div>
    <div class="col-xs-4">
        <label>По</label>

        <div class="form-group">
            <div id="to-date" class="form-control"></div>
            <input type="hidden" name="to_date" value='<fmt:formatDate pattern="dd.MM.yyyy" value="${toDate}" />'/>
        </div>
    </div>
    <div class="col-xs-4">
        <label>&nbsp;</label>

        <a href="#" id="refresh-button" class="btn btn-primary btn-block">
            <span class="glyphicon glyphicon-refresh"></span> Обновить
        </a>
    </div>
</div>

<hr/>

<div class="text-center">
    <div class="label label-success">Успешное пополнение</div>
    <div class="label label-info">Успешный вывод средств</div>
    <div class="label label-warning">Средства заблокированы</div>
    <div class="label label-danger">Операция отменена</div>
</div>

<hr/>

<table id="transactions" class="table" style="font-size : 11px;">
    <thead>
    <tr>
        <th>#</th>
        <th>Счет</th>
        <th style="width:70px;">Дата <br/> Время</th>
        <th style="width:160px;">Комментарий</th>
        <th>Сумма</th>
        <th>Дополнительная информация</th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>

<div class="row list-loader-animation"></div>

<hr/>