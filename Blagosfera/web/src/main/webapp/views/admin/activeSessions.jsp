<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" type="text/css" href="/css/jsgrid.min.css?v=${buildNumber}"/>
<link rel="stylesheet" type="text/css" href="/css/jsgrid-theme.min.css?v=${buildNumber}"/>

<style type="text/css">
    div#jsGrid td {
        overflow-y: hidden;
        overflow-x: auto;
        /*white-space: nowrap;*/
    }
</style>

<div id="jsGrid"></div>

<script type="application/javascript">
    var deviceTypes = [
        {device: ""},
        {device: "Игровая консоль"},
        {device: "Не определено"},
        {device: "Коммуникатор"},
        {device: "Персональный компьютер"},
        {device: "Телевизор"},
        {device: "Смартфон"},
        {device: "Планшет"},
        {device: "Не определено"},
        {device: "Переносной компьютер"}
    ];

    var controller = {
        loadData: function (filter) {
            console.log(filter);

            return $.ajax({
                type: "POST",
                url: "activeSessions/list.json",
                data: JSON.stringify(filter),
                dataType: "json",
                contentType: "application/json"
            });
        },
        insertItem: function (item) {
        },
        updateItem: function (item) {
        },
        deleteItem: function (item) {
            $.ajax({
                type: "POST",
                url: "activeSessions/close.json",
                data: JSON.stringify({sessionId: item.sessionId}),
                dataType: "json",
                contentType: "application/json",
                success: function (response) {
                    setTimeout(function() {$("#jsGrid").jsGrid("search");}, 300);
                }
            });

            return item;
        }
    };

    $("#jsGrid").jsGrid({
        height: "auto",
        width: "100%",

        deleteConfirm: function(item) {
            return "Выбранная сессия пользователя \"" + item.username + "\" будет закрыта. Вы уверены?";
        },

        filtering: true,
        editing: false,
        sorting: true,

        paging: true,
        pageLoading: true,
        pageSize: 10,
        pageIndex: 1,

        autoload: true,
        controller: controller,

        pagerFormat: "Страницы: {first} {prev} {pages} {next} {last} &nbsp;&nbsp; {pageIndex} из {pageCount}",
        pageNextText: "Следующая",
        pagePrevText: "Предыдущая",
        pageFirstText: "Первая",
        pageLastText: "Последняя",
        loadMessage: "",

        fields: [
            {name: "visibleSessionId", type: "text",   title: "SESSION_ID", width: "20%", filtering: false, align:"center"},
            {name: "username",  type: "text",   title: "Email",      width: "20%", align:"center"},
            {name: "device",    type: "select", title: "Устройство", width: "15%", items: deviceTypes, valueField: "device", textField: "device", align:"center"},
            {name: "os",        type: "text",   title: "ОС",         width: "10%", align:"center"},
            {name: "browser",   type: "text",   title: "Браузер",    width: "10%", align:"center"},
            {name: "ip",        type: "text",   title: "IP",         width: "15%", align:"center"},
            {
                type: "control",
                editButton: false,
                deleteButton: true,
                clearFilterButton: true,
                modeSwitchButton: true,
                width: "5%"
            }
        ]
    });
</script>