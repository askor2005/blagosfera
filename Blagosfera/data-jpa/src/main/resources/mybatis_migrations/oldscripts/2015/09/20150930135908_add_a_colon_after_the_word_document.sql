-- // add a colon after the word document
-- Migration SQL that makes the change goes here.

-- Правим двоеточие после слова "документ" для всех уже существующих уведомлений
do $$
declare pos int;
declare n record;
declare fixed_short_text text;
begin

  -- Цикл по всем уведомлениям о подписании документов
  FOR n IN SELECT * FROM notifications WHERE short_text  like '%Вам необходимо подписать документ%'
  LOOP

    -- Вычисляем позицию где должно находиться двоеточие(после слова "документ")
    -- И, если там ещё нет двоеточия, то добавляем его в это место.
    pos := position('Вам необходимо подписать документ' in n.short_text) + char_length('Вам необходимо подписать документ');
    if substring(n.short_text from pos for 1) != ':'
    then
      fixed_short_text := substring(n.short_text from 0 for pos) || ':' || substring(n.short_text from pos);
      UPDATE notifications SET short_text=fixed_short_text WHERE id=n.id;
    end if;

  END LOOP;

end $$;

-- Правим двоеточие после слова "документ" для шаблона уведомления
do $$
declare pos int;
declare n record;
declare fixed_short_text text;
begin

  -- Считываем шаблон уведомления о подписании документа
  select * into n from notification_templates where mnemo='document.sign';

  if n.shorttext like '%Вам необходимо подписать документ%'
  then

    -- Вычисляем позицию где должно находиться двоеточие(после слова "документ")
    -- И, если там ещё нет двоеточия, то добавляем его в это место.
    pos := position('Вам необходимо подписать документ' in n.shorttext) + char_length('Вам необходимо подписать документ');
    if substring(n.shorttext from pos for 1) != ':'
    then
      fixed_short_text := substring(n.shorttext from 0 for pos) || ':' || substring(n.shorttext from pos);
      UPDATE notification_templates SET shorttext=fixed_short_text WHERE id=n.id;
    end if;

  end if;

end $$;

-- Правим двоеточие после слова "документ" для шаблона email
do $$
declare pos int;
declare n record;
declare fixed_short_text text;
begin

  select * into n from email_templates where title='notify.email.document.sign';

  -- Правим двоеточие для тело письма
  if n.body like '%Вам необходимо подписать документ%'
  then

    -- Вычисляем позицию где должно находиться двоеточие(после слова "документ")
    -- И, если там ещё нет двоеточия, то добавляем его в это место.
    pos := position('Вам необходимо подписать документ' in n.body) + char_length('Вам необходимо подписать документ');
    if substring(n.body from pos for 1) != ':'
    then
      fixed_short_text := substring(n.body from 0 for pos) || ':' || substring(n.body from pos);
      UPDATE email_templates SET body=fixed_short_text WHERE id=n.id;
    end if;

  end if;

  -- Правим двоеточие для subject письма
  if n.subject like '%Вам необходимо подписать документ%'
  then

    -- Вычисляем позицию где должно находиться двоеточие(после слова "документ")
    -- И, если там ещё нет двоеточия, то добавляем его в это место.
    pos := position('Вам необходимо подписать документ' in n.subject) + char_length('Вам необходимо подписать документ');
    if substring(n.subject from pos for 1) != ':'
    then
      fixed_short_text := substring(n.subject from 0 for pos) || ':' || substring(n.subject from pos);
      UPDATE email_templates SET subject=fixed_short_text WHERE id=n.id;
    end if;

  end if;

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


