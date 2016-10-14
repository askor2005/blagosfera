'use strict';

define([
    'app'
], function (app) {
    app.factory('timeTableService', function () {
        return {
            convertToOldDbFieldFormat: function (val) {
                return '{"days":'+angular.toJson(val)+'}';
            },
            convertFromOldDbFieldFormat: function (val) {
                var res = val.match("\\[\\[.*\\]\\]");
                if ((res) && (res.length)){
                    return angular.fromJson(res[0]);
                }
            }
        }
    });

    return app;
});