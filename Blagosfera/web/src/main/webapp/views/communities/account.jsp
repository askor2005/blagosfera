<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">

	th {
		text-align : center;
		vertical-align : middle;
	}

</style>

<script id="transaction-row-template" type="x-tmpl-mustache">
	<tr class="{{trClass}}">
		<td>
			{{transaction.id}}
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

<script id="communityAccountsTemplate" type="x-tmpl-mustache">
	{{#communityAccounts}}
		<h3>
			{{accountTypeName}}
			<strong>{{balance}} Ра</strong>
		</h3>
	{{/communityAccounts}}
</script>

<script type="text/javascript">
	var communityId = "${communityId}";
	var AccountsPage = {
			
		rowTemplate : $('#transaction-row-template').html(),
		rowTemplateParsed : false,
		
		getRowTemplate : function() {
			if (!AccountsPage.rowTemplateParsed) {
				Mustache.parse(AccountsPage.rowTemplate);
				AccountsPage.rowTemplateParsed = true;
			}
			return AccountsPage.rowTemplate;
		},
		
		getRowMarkup : function(transaction) {
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
			
			if (transaction.description) {
				additional += "Назначение платежа: " + transaction.description;
			}
			
			model.additional = additional;

			var link = "";
			var ownerTypeNameCredit = "";
			var ownerTypeNameDebet = "";
			switch (transaction.accountOwnerType) {
				case "SHARER":
					link = "<a href='" + transaction.otherLink + "' class='tooltiped-avatar' data-sharer-ikp='" + transaction.otherIkp + "'>" + transaction.otherName + "</a>";
					ownerTypeNameCredit = "от участника";
					ownerTypeNameDebet = "участнику";
					break;
				case "COMMUNITY":
					link = "<a href='" + transaction.otherLink + "'>" + transaction.otherName + "</a>";
					ownerTypeNameCredit = "от объединения";
					ownerTypeNameDebet = "объединению";
					break;
				case "SHARER_BOOK":
					if (transaction.otherSharebookType == "SHARER") {
						link = "<a href='" + transaction.otherLink + "' class='tooltiped-avatar' data-sharer-ikp='" + transaction.otherIkp + "'>" + transaction.otherName + "</a>";
						ownerTypeNameCredit = "из паевой книжки участника";
						ownerTypeNameDebet = "в паевую книжку участника";
					} else if (transaction.otherSharebookType == "COMMUNITY") {
						link = "<a href='" + transaction.otherLink + "'>" + transaction.otherName + "</a>";
						ownerTypeNameCredit = "из паевой книжки объединения";
						ownerTypeNameDebet = "в паевую книжку объединения";
					}
					break;
				case "SYSTEM_ACCOUNT":
					link = transaction.otherName;
					ownerTypeNameCredit = "от Системы БЛАГОСФЕРА";
					ownerTypeNameDebet = "в Систему БЛАГОСФЕРА";
					break;
			}
			if (transaction.detailType == "CREDIT") {
				model.comment = "Перевод средств " + ownerTypeNameCredit + " [" + link + "]";
			}
			if (transaction.detailType == "DEBIT") {
				model.comment = "Перевод средств " + ownerTypeNameDebet + " [" + link + "]";
			}
			
			var markup = Mustache.render(AccountsPage.getRowTemplate(), model);
			var $markup = $(markup);
			return $markup;
		},
		
		showRowMarkup : function(transaction, prepend) {
			var $markup = AccountsPage.getRowMarkup(transaction);
			if (prepend) {
				$("table#transactions").prepend($markup);
			} else {
				$("table#transactions").append($markup);
			}
		},

		communityId : communityId,
		
		initScrollListener : function() {
			$("table#transactions tbody").empty();
			//AccountsPage.lastLoadedId = null;
			ScrollListener.init("/group/" + AccountsPage.communityId + "/transactions.json", "get", function() {
				var params = {
				};
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
			}, function() {
				$("div.list-loader-animation").show();
			}, function(entries, page) {
				var $tbody = $("table#transactions");
				$.each(entries, function(index, entry) {
					AccountsPage.showRowMarkup(entry);
				});
				$("div.list-loader-animation").hide();
			});
		}
	};

	function loadAccountPageData(communityId, callBack) {
		$.radomJsonPost(
				"/group/" + communityId + "/account_page_data.json",
				{},
				callBack
		);
	}

	$(document).ready(function() {
		loadAccountPageData(communityId, function(accountPageData) {
			initCommunityHead(accountPageData.community);
			initCommunityMenu(accountPageData.community);
			initAccountPage(accountPageData);
		});
	});

	function initAccountPage(accountPageData) {
		var communityAccountsTemplate = $("#communityAccountsTemplate").html();
		Mustache.parse(communityAccountsTemplate);

		var model = accountPageData;
		var markup = Mustache.render(communityAccountsTemplate, model);
		$("#accounts").append(markup);

		$("table#transactions").fixMe();

		AccountsPage.initScrollListener();

		$("a#refresh-button").click(function() {
			AccountsPage.initScrollListener();
			AccountsPage.refreshAccounts();
			return false;
		});
	}
</script>

<t:insertAttribute name="communityHeader" />
<h2>Баланс</h2>
<hr/>

<div id="accounts"></div>
<hr />

<div class="text-center">
	<div class="label label-success">Успешное пополнение</div>
	<div class="label label-info">Успешный вывод средств</div>
	<div class="label label-warning">Средства заблокированы</div>
	<div class="label label-danger">Операция отменена</div>
</div>
<hr />

<table id="transactions" class="table" style="font-size : 11px;">
	<thead>
		<tr>
			<th>#</th>
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