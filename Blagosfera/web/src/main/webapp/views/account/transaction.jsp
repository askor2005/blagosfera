<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">

	.dl-horizontal dt {
		width : 180px;
	}
	
	.dl-horizontal dd {
		margin-left : 200px;
	}

	dt, dd {
		margin-bottom : 5px;
		font-size : 15px;
	}

</style>

<h1>
	Транзакция ${transaction.id}
</h1>
<hr/>

<dl class="dl-horizontal">
	<dt>Тип</dt>
	<dd>
		<c:if test="${transactionClass == 'PaymentTransaction' and transaction.amount > 0}">Пополнение счёта</c:if>
		<c:if test="${transactionClass == 'PaymentTransaction' and transaction.amount < 0}">Вывод со счёта</c:if>
		<c:if test="${transactionClass != 'PaymentTransaction'}">Внутренняя операция</c:if>
	</dd>
	<dt>Дата, время</dt>
	<dd><fmt:formatDate pattern="dd.MM.yyyy HH:mm:ss" value="${transaction.date}" /></dd>
	<dt>Сумма, Ра</dt>
	<dd>${radom:formatMoney(transaction.amount)}</dd>
	<dt>Счет</dt>
	<dd>${transaction.account.type.name}</dd>
	<dt>Комментарий</dt>
	<dd>
		<c:if test="${transactionClass != 'MoveTransaction'}">${transaction.comment}</c:if>
		<c:if test="${transactionClass == 'MoveTransaction'}">
			<c:if test="${transaction.amount > 0}">
				Перевод средств от участника [<a href="${transaction.otherAccount.owner.link}" data-sharer-ikp="${transaction.otherAccount.owner.ikp}" class="tooltiped-avatar">${transaction.otherAccount.owner.fullName}</a>]
			</c:if>
			<c:if test="${transaction.amount < 0}">
				Перевод средств участнику [<a href="${transaction.otherAccount.owner.link}" data-sharer-ikp="${transaction.otherAccount.owner.ikp}" class="tooltiped-avatar">${transaction.otherAccount.owner.fullName}</a>]			
			</c:if>
		</c:if>
	</dd>
	<c:if test="${transactionClass == 'MoveTransaction' and not empty transaction.senderComment}">
		<dt>Назначение платежа</dt>
		<dd>${transaction.senderComment}</dd>
	</c:if>
	
	<c:if test="${transactionClass == 'PaymentTransaction'}">
		<dt>Платежная система</dt>
		<dd>${transaction.payment.system.name}</dd>
		<dt>Кошелек отправителя</dt>
		<dd>${transaction.payment.sender}</dd>
		<dt>Кошелек получателя</dt>
		<dd>${transaction.payment.receiver}</dd>
		<dt>Сумма в рублях</dt>
		<dd>${radom:formatMoney(transaction.payment.rurAmount)}</dd>
		<dt>Комиссия в процентах</dt>
		<dd>${transaction.payment.rameraComission}</dd>
		<dt>Комиссия в рублях</dt>
		<dd>${radom:formatMoney(transaction.payment.rameraComission)}</dd>								
	</c:if>
	
</dl>

<hr/>

<div class="text-center">
	<a href="/account" class="btn btn-primary">Вернуться к списку транзакций <i class="glyphicon glyphicon-share-alt"></i></a>
</div>

<hr/>