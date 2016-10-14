-- // fix sharer books
-- Migration SQL that makes the change goes here.

-- Фикс паевых счетов.
-- Данный фикс устанавливает правильный owner_id для все счетов с типом SHARER_BOOK.
-- Необходимо для правильного отображения общего баланса пользователя.
do $$
DECLARE r RECORD;
begin

  FOR r IN SELECT * FROM book_accounts
    LOOP
    UPDATE accounts SET owner_id=r.id,owner_type='SHARER_BOOK' WHERE id = r.account_id;
    END LOOP;

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


