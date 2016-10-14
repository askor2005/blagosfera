'use strict';

define([
    'app',
    'petrovich'
], function (app, petrovich) {

    var DAY_TIME = 24 * 60 * 60 * 1000;

    var TWO_DAY_TIME = 2 * DAY_TIME;

    function getStatusWrapperByCode(inviteStatus) {
        var statusWrapper = {
            status : "",
            statusClass : "",
            statusRowClass : "",
        };
        switch (inviteStatus) {
            case 0:
                statusWrapper.status = "Принято";
                break;
            case 1:
                statusWrapper.status = "Ожидание ответа";
                break;
            case 2:
                statusWrapper.status = "Просрочено";
                break;
            case 3:
                statusWrapper.status = "Отклонено";
                break;
            case 4:
                statusWrapper.status = "Профиль перенесён в архив";
                break;
            case 5:
                statusWrapper.status = "Профиль удален";
                break;
        }
        statusWrapper.statusClass = "invite-circle-" + inviteStatus;
        statusWrapper.statusRowClass = "invite-row-" + inviteStatus;
        return statusWrapper;
    }

    app.filter('simpleDateFormat', function ($filter) {
        return function (timeStamp) {
            var result = "";
            try {
                var now = new Date();
                var date = new Date(timeStamp);
                var nowDay = $filter("date")(now.getTime(), "dd");
                var day = $filter("date")(timeStamp, "dd");
                if ((now.getTime() - date.getTime()) < DAY_TIME && nowDay == day) {
                    result = "Сегодня";
                } else if ((now.getTime() - date.getTime()) < TWO_DAY_TIME && (parseInt(nowDay) - parseInt(day)) == 1) {
                    result = "Вчера";
                } else {
                    result = $filter("date")(timeStamp, "dd MMMM, yyyy");
                }
            } catch (e) {
                result = "Ошиба парсинга даты: " + e;
            }
            return result;
        }
    });

    app.filter('invitesStatusFilter', function () {
        return function (inviteStatus) {
            return getStatusWrapperByCode(inviteStatus).status;
        }
    });

    app.filter('invitesStatusClassFilter', function () {
        return function (inviteStatus) {
            return getStatusWrapperByCode(inviteStatus).statusClass;
        }
    });

    app.filter('invitesStatusRowClassFilter', function () {
        return function (inviteStatus) {
            return getStatusWrapperByCode(inviteStatus).statusRowClass;
        }
    });

    app.filter('initialFilter', function () {
        return function (name) {
            return name != null ? name.substr(0,1) + "." : "";
        }
    });

    return app;
});