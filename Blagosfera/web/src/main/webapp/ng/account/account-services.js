'use strict';

define([
    'app'
], function (app) {

    app.factory('accService', function (httpService, $q) {
        return {
            loadTransactions: function (accountTypeId, fromDate, toDate, page, perPage, type, state) {
                var data = {
                    accountTypeId: accountTypeId ? accountTypeId : null,
                    fromDate: fromDate ? fromDate.getTime() : null,
                    toDate: toDate ? toDate.getTime() : null,
                    page: page,
                    perPage: perPage,
                    type: type ? type : null,
                    state: state ? state : null
                };

                var q = $q.defer();

                httpService.post('/api/accounts/transactions.json', data, {}).then(
                    function (response) {
                        q.resolve(response.data)
                    }, function (response) {
                        q.reject(response);
                    }
                );

                return q.promise;
            }
        }
    });

    return app;
});