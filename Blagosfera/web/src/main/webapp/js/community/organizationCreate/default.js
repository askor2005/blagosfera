define([], function () {

    var DefaultCreate = {};

    // API метод инициализация модуля
    DefaultCreate.init = function () {
        // do nothing
    };

    // API метод - создание запроса на создание юр лица
    DefaultCreate.createRequest = function(){
        bootbox.alert("Для данной организационно-правовой формы нет реализации создания юр лица!");
    };

    return DefaultCreate;
});