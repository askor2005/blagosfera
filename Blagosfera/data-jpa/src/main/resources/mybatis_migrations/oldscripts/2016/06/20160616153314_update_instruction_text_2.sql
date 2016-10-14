-- // update instruction text 2
-- Migration SQL that makes the change goes here.

UPDATE ramera_texts SET text='<p style="text-align: left;">Мы рады приветствовать Вас в вашем Личном Информационном Кабинете (ЛИК). Для первичной регистрации в БЛАГОСФЕРЕ, просим Вас выполнить следующие действия:</p>
<ul style="text-align: justify;">
<li>Загрузите вашу фотографию, нажав в вашем профиле кнопку <strong>&laquo;Загрузить фото&raquo;</strong>. Вы можете загрузить фотографию в виде файла с вашего компьютера (выбрав в открывшемся окне пункт <strong>&laquo;Выбрать файл&raquo;</strong>), либо в виде URL-ссылки (выбрав в открывшемся окне пункт <strong>&laquo;Указать URL&raquo;</strong>). В случае, если у вашего компьютера имеется веб-камера, Вы можете сфотографировать себя, выбрав в открывшемся окне пункт <strong>&laquo;Сделать снимок&raquo;</strong> - после чего следуйте указаниям Системы.</li>
<li>Заполните в вашем профиле все поля данных в блоке <strong>&laquo;Данные для регистрации&raquo;.</strong></li>
</ul>
<p style="text-align: justify;">В случае, если ваш профиль будет заполнен менее, чем на 30% (для уточнения смотрите окно <strong>&laquo;Заполнение профиля&raquo;</strong>, расположенное в правом верхнем углу страницы вашего профиля), на вашу почту будут приходить соответствующие напоминания от Системы БЛАГОСФЕРА.</p>
<p style="text-align: justify;"><ins><strong>Внимание:</strong></ins> в случае, если в течение пяти дней после принятия приглашения в ваш профиль не будет загружена ваша фотография и не будет заполнен блок <strong>&laquo;Данные для регистрации&raquo;</strong>, ваш Личный Информационный Кабинет (ЛИК) будет заблокирован и перенесён в архив. В этом случае Вы можете восстановить его с помощью функции <strong>&laquo;Восстановить доступ&raquo;</strong>.</p>
<p style="text-align: justify;">С того момента, как Вы загрузили вашу фотографию и заполнили блок <strong>&laquo;Данные для регистрации&raquo;</strong> в вашем профиле, Вы становитесь неидентифицированным пользователем Системы БЛАГОСФЕРА и получаете ограниченные пользовательские права. Вы можете ознакомиться с некоторыми разделами и понятиями Системы БЛАГОСФЕРА, а также узнать, какие возможности, инструменты и преимущества может предоставить Вам Система. На данном этапе Вам всё ещё ограничен доступ к большинству инструментов Системы. Для того, чтобы получить полный доступ ко всем возможностям Системы БЛАГОСФЕРА, Вам необходимо получить статус идентифицированного пользователя. Для этого:</p>
<ul style="text-align: justify;">
<li>Активируйте на странице вашего профиля блок данных <strong>&laquo;Данные для идентификации&raquo;</strong> (с помощью нажатия на кнопку <strong>&laquo;Заполнить данные для идентификации&raquo;</strong>), и заполните все поля данных в нём.</li>
<li>После заполнения всех полей данных профиля на 100%, перейдите к подаче заявки на идентификацию с помощью кнопки <strong>&laquo;Перейти к выбору регистратора для идентификации&raquo;</strong> (появляется под вашей фотографией на странице вашего профиля) и следуйте дальнейшим указаниям.</li>
</ul>
<p style="text-align: justify;">Узнать подробнее Вы можете нажав на кнопку <strong>&laquo;Руководство пользователя Системы&raquo;</strong>.</p>'
WHERE code='INSTRUCTION';

-- //@UNDO
-- SQL to undo the change goes here.




