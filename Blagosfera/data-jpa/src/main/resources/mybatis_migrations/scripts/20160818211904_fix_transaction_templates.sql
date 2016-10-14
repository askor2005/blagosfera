-- // fix_transaction_templates
-- Migration SQL that makes the change goes here.
BEGIN;
update documents_templates set content = 'Транзакция успешно исполнена. Перевод ${amount} Ра со счёта: ${accountType}  ${senderType} <a href="${senderLink}">${senderName} </a> на счет: ${receiverAccountType}' where code = 'transaction.saved';
	update documents_templates set content = 'Транзакция успешно исполнена. Перевод ${amount} Ра со счёта: ${accountType}  ${senderType} <a href="${senderLink}">${senderName} </a> на счет: ${receiverAccountType}' where code = 'email.transaction.saved';
COMMIT;


-- //@UNDO
-- SQL to undo the change goes here.


