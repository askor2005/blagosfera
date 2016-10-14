-- // parallel_session_allowed
-- Migration SQL that makes the change goes here.

-- Разрешаем всем параллельные сессии
update sharers set allow_multiple_sessions = true;

-- //@UNDO
-- SQL to undo the change goes here.


