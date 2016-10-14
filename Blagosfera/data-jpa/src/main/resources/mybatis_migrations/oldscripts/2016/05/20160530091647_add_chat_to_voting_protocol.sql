-- // add_chat_to_voting_protocol
-- Migration SQL that makes the change goes here.

update documents_templates set content='<p style="text-align: center;"><span class="mceNonEditable" data-placeholder="" data-mce-contenteditable="false" data-span-type="radom-participant-filter" data-span-id="1464373555718" data-is-meta-field="undefined" data-participant-id="766" data-group-internal-name="" data-field-id="201" data-internal-name="COMMUNITY_NAME" data-case-id="CASE_I" data-chars-type="NORMAL">[Объединение:Полное название на русском языке:Именительный]</span>&nbsp;</p>
<p style="text-align: center;">Протокол собрания.</p>
<p style="text-align: center;"><span class="mceNonEditable" data-placeholder="" data-mce-contenteditable="false" data-span-type="radom-participant-filter" data-span-id="1464373643849" data-is-meta-field="undefined" data-participant-id="766" data-group-internal-name="" data-field-id="216" data-internal-name="COMMUNITY_LEGAL_LOCALITY" data-case-id="CASE_I" data-chars-type="NORMAL">[Объединение:Населенный пункт:Именительный]</span> -</p>
<p>Список проголосовавших:</p>
<p><span class="mceNonEditable groupFieldStart" data-placeholder="">[[</span><span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1444854976640" data-is-meta-field="undefined" data-participant-id="765" data-field-id="284" data-internal-name="PERSON_FULL_NAME" data-case-id="CASE_I" data-chars-type="NORMAL">[Список участников собрания:ФИО:Именительный]</span><span class="mceNonEditable groupFieldEnd" data-placeholder="">]]</span>&nbsp;</p>
<p>Результат голосования:</p>
<p><span class="mceNonEditable" data-placeholder="" data-mce-contenteditable="false" data-span-type="radom-participant-custom-fields" data-span-id="1464281275957" data-participant-id="765" data-participant-name="Список участников собрания" data-custom-field-type-name="string" data-custom-field-name="Протокол" data-custom-field-description="Протокол" data-position="0" data-string-mask="" data-string-case="CASE_I">[Список участников собрания:Протокол:Протокол]</span>&nbsp;</p>
<p>Объединение:&nbsp;<span class="mceNonEditable" data-placeholder="" data-mce-contenteditable="false" data-span-type="radom-participant-filter" data-span-id="1464244129206" data-is-meta-field="undefined" data-participant-id="766" data-group-internal-name="" data-field-id="201" data-internal-name="COMMUNITY_NAME" data-case-id="CASE_I" data-chars-type="NORMAL">[Объединение:Полное название на русском языке:Именительный]</span></p>
<p><span class="mceNonEditable" data-placeholder="" data-mce-contenteditable="false" data-span-type="radom-participant-custom-fields" data-span-id="1464593021448" data-participant-id="765" data-participant-name="Список участников собрания" data-custom-field-type-name="string" data-custom-field-name="Чат" data-custom-field-description="Чат" data-position="0" data-string-mask="" data-string-case="CASE_I">[Список участников собрания:Чат:Чат]</span></p>
<p>&nbsp;</p>
<p>&nbsp;</p>' where code='batch_voting_protocol_constructor';

-- //@UNDO
-- SQL to undo the change goes here.


