-- // fix documents templates document sign
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

-- Правим двоеточие после слова "документ" для всех уже существующих уведомлений
do $$
declare pos int;
declare n record;
declare fixed_short_text text;
begin

  UPDATE documents_templates SET content='<p><span class="mceNonEditable" data-placeholder="" data-span-type="participant-custom-text" data-span-id="1442329631212" data-participant-id="503" data-participant-name="Получатель" data-custom-text-male="Уважаемый" data-custom-text-female="Уважаемая">[Получатель:Уважаемый]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1442329646528" data-is-meta-field="false" data-participant-id="503" data-field-id="83" data-internal-name="FIRSTNAME" data-case-id="CASE_I">[Получатель:Имя:Именительный]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1442329657337" data-is-meta-field="false" data-participant-id="503" data-field-id="84" data-internal-name="SECONDNAME" data-case-id="CASE_I">[Получатель:Отчество:Именительный]</span>,&nbsp;Вам необходимо подписать документ:&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-custom-fields" data-span-id="1443473978148" data-participant-id="503" data-participant-name="Получатель" data-custom-field-type-name="document" data-custom-field-name="Наименование документа" data-custom-field-description="Наименование документа" data-position="-1" data-document-view-type="shortName">[Получатель:Наименование документа:Наименование документа]</span></p>' WHERE code='document.sign';

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


