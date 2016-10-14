-- // add_active_column_to_payment_system
-- Migration SQL that makes the change goes here.

ALTER TABLE public.payment_systems ADD COLUMN active boolean;
update payment_systems set active = false where bean_name = 'yandexMoneyPaymentSystemBean';
update payment_systems set active = false where bean_name = 'webmoneyPaymentSystemBean';

-- //@UNDO
-- SQL to undo the change goes here.


