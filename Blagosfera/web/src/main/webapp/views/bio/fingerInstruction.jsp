<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>


<style type="text/css">

</style>

<script type="text/javascript">
    $(document).ready(function () {
        $("table.table").fixMe();
    });
</script>

<h1>
    Инструкция
    <br/>
    <small>по установке и запуску Сервера авторизации "Благосфера"</small>
</h1>

<hr/>

<p>Для того чтобы иметь возможность подтверждать действия в системе при помощи отпечатков пальцев,
    необходимо скачать и установить Сервер авторизации "БЛАГОСФЕРА".</p>
<p>Ссылка для скачивания: <a href="/ras/download" target="_blank">setup-ras.msi</a></p>

<hr/>

<span class="open" data-toggle="collapse" data-target="#section1" style="cursor: pointer;"><b>Инструкция по установке Сервера авторизации "БЛАГОСФЕРА"</b></span>
<div id="section1" class="collapse in">
    <!--<p>
        1. Нажать на кнопку скачать для того чтобы началось скачивание
    </p>
    <p>
        <img src="/i/finger-instruction/step-1.jpeg"/>
    </p>
    <hr/>
    <p>
        2. После того как архив будет полностью скачан, распаковать его в удобную для Вас папку Вашего компьютера
    </p>
    <p>
        <img src="/i/finger-instruction/step-2.jpeg"/>
    </p>

    <hr/>

    <p>
        3. Запустить Сервер авторизации "Благосфера" дважды кликнув по файлу RAS2.exe и держать его запущенным во время
        работы на сайте ramera.ru, чтобы в любой момент иметь возможность подтвердить какое либо действие при помощи
        отпечатка пальца
    </p>
    <p>
        <img src="/i/finger-instruction/step-3.jpeg"/>
    </p>-->

    <p>
        Скачайте и запустите установочный файл setup-ras.msi. В открывшемся окне нажмите "Далее"
    </p>
    <p>
        <img src="/i/finger-instruction/setup_1.png"/>
    </p>

    <p>
        Выберите путь для установки (или оставьте значение по-умолчанию). Нажмите "Далее"
    </p>
    <p>
        <img src="/i/finger-instruction/setup_2.png"/>
    </p>

    <p>
        Для установки Сервера авторизации "БЛАГОСФЕРА" нажмите кнопку "Установить"
    </p>
    <p>
        <img src="/i/finger-instruction/setup_3.png"/>
    </p>

    <p>
        После завершения мастера установки нажмите кнопку "Готово"
    </p>
    <p>
        <img src="/i/finger-instruction/setup_4.png"/>
    </p>

    <p>
        Ниже находятся инструкции для браузера Firefox и ручной установки SSL-сертификата
    </p>
</div>

<br><br>
<span data-toggle="collapse" data-target="#section2" style="cursor: pointer;"><b>Инструкция для пользователей браузера Mozilla Firefox</b></span>
<div id="section2" class="collapse">
    <p>
        Если Вы используете браузер Mozilla Firefox, то Вам необходимо вручную установить SSL-сертификат.
        Для этого запустите Сервер авторизации "БЛАГОСФЕРА" и перейдите по следующей ссылке:
    </p>
    <p>
        <img src="/i/finger-instruction/ras_url.png"/>
    </p>

    <p>
        В открывшемся окне браузер сообщит о том что соединение является недоверенным.
        Нажмите на пункт "Я понимаю риск"
    </p>
    <p>
        <img src="/i/finger-instruction/ras_url_1.png"/>
    </p>

    <p>
        Теперь нажмите на кнопку "Добавить исключение..."
    </p>
    <p>
        <img src="/i/finger-instruction/ras_url_2.png"/>
    </p>

    <p>
        В открывшемся окне убедитесь что установлен флажок "Постоянно хранить это исключение"
        и нажмите кнопку "Подтвердить исключение безопасности"
    </p>
    <p>
        <img src="/i/finger-instruction/ras_url_3.png"/>
    </p>

    <p>
        Перезагрузите страницу (например клавишей F5). После перезагрузки Вы увидите краткую инструкцию
        по работе с Сервером авторизации "БЛАГОСФЕРА". Это значит что сертификат установлен корректно.
    </p>
    <p>
        <img src="/i/finger-instruction/ras_url_4.png"/>
    </p>
</div>

<br><br>
<span data-toggle="collapse" data-target="#section3" style="cursor: pointer;"><b>Ручная установка SSL-сертификата (для браузеров Internet Explorer и Google Chrome)</b></span>
<div id="section3" class="collapse">
    <p>Если по каким-то причинам Вам необходимо установить SSL-сертификат вручную, выполните следующие шаги:</p>
    <p>
        перейдите в директорию в которую был установлен Сервер аторизации "Благосфера".
        (по-умолчанию это "C:\Program Files (x86)\ASKOR LLC\БЛАГОСФЕРА RAS\").
        Теперь перейдите в поддиректорию "htmldocs" в которой Вы найдете файл "blagosfera.cer".
        Кликните по файлу правой кнопкой мыши и выберите пункт меню "Установить сертификат"
    </p>
    <p>
        <img src="/i/finger-instruction/file.png"/>
    </p>
    <p>
        будет открыто окно "Мастер импорта сертификатов". В этом окне нажмите кнопку "Далее".
    </p>
    <p>
        <img src="/i/finger-instruction/master01.png"/>
    </p>
    <p>
        мастер импорта предложит выбрать <b>хранилище сертификатов</b>. Выберите пункт "Поместить все сертификаты в следующее хранилище" и нажмите кнопку "Обзор..."
    </p>
    <p>
        <img src="/i/finger-instruction/master02.png"/>
    </p>
    <p>
        в открывшемся окне выберите "Доверенные корневые центры сертификации" и нажмите кнопку "ОК"
    </p>
    <p>
        <img src="/i/finger-instruction/master03.png"/>
    </p>
    <p>
        выбранное хранилище отобразится в поле "Хранилище сертификатов:". Нажмите кнопку "Далее"
    </p>
    <p>
        <img src="/i/finger-instruction/master04.png"/>
    </p>
    <p>
        будет отображено сообщение о завершении мастера импорта сертификатов. Нажмите кнопку "Далее"
    </p>
    <p>
        <img src="/i/finger-instruction/master05.png"/>
    </p>
    <p>
        если сертификат устанавливается впервые, будет отображено предупреждение. Нажмите кнопку "Да"
    </p>
    <p>
        <img src="/i/finger-instruction/master07.png"/>
    </p>
    <p>
        в случае успеха мастер импорта выведет следующее сообщение
    </p>
    <p>
        <img src="/i/finger-instruction/master06.png"/>
    </p>
</div>
<br><br><br>