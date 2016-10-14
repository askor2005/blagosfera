-- // change_invite_voting_email_template
-- Migration SQL that makes the change goes here.
update documents_templates set content='<p><span class="mceNonEditable" data-placeholder="" data-span-type="participant-custom-text" data-span-id="1442329631212" data-participant-id="506" data-participant-name="Получатель" data-custom-text-male="Уважаемый" data-custom-text-female="Уважаемая">[Получатель:Уважаемый]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1442329646528" data-is-meta-field="false" data-participant-id="506" data-field-id="83" data-internal-name="FIRSTNAME" data-case-id="CASE_I">[Получатель:Имя:Именительный]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1442329657337" data-is-meta-field="false" data-participant-id="506" data-field-id="84" data-internal-name="SECONDNAME" data-case-id="CASE_I">[Получатель:Отчество:Именительный]</span>,</p>
<p><img style="box-shadow: 0 0 8px rgba(0, 0, 0, 0.8); display: inline-block; border-radius: 50%; border: 0; box-sizing: border-box; font-size: 21px; font-weight: 200; border-width: 0px; margin: 5px;" src="${sender.avatar}" width="64" height="64" /><a href="${applicationUrl}${sender.link}">${sender.fullName}</a>&nbsp;просит Вас &nbsp;пройти голосование по теме:&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-custom-fields" data-span-id="1442436367782" data-participant-id="507" data-participant-name="Отправитель" data-custom-field-type-name="string" data-custom-field-name="Тема голосования" data-custom-field-description="Тема голосования" data-position="-1" data-string-mask="">[Отправитель:Тема голосования:Тема голосования]</span>&nbsp; в рамках собрания:&nbsp;<a href="${applicationUrl}${batchVotingLink}">${batchVotingSubject}</a></p>
<p><img style="box-shadow: 0 0 8px rgba(0, 0, 0, 0.8); display: inline-block; border-radius: 50%; border: 0; box-sizing: border-box; font-size: 21px; font-weight: 200; border-width: 0px; margin: 5px;" src="${community.avatar}" width="64" height="64" />&nbsp;в сообществе:&nbsp;<a title="${community.name}" href="${applicationUrl}${community.link}">${community.name}</a></p>
<p>Ссылка для перехода к участию в голосовании: <a href="${applicationUrl}${votingLink}">${applicationUrl}${votingLink}</a></p>
<p>Подробности голосования:</p>
<p style="background-color:#F8F8F8;">${votingDescription}</p>' where code='email.voting-active';


-- //@UNDO
-- SQL to undo the change goes here.


