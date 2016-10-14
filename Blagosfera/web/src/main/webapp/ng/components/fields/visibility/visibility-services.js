'use strict';

define([
    'app'
], function (app) {

    app.factory('visibilityService', function (httpService, $q) {
        return {
            changeBasicInformationFieldVisibility: function (name,value) {
                return httpService.post('/api/user/fields/visibility/basic_information.json', {name:name,value:value},{});
            },
            changeRegistrationAddressFieldVisibility: function (name,value) {
                return httpService.post('/api/user/fields/visibility/registration_address.json', {name:name,value:value},{});
            },
            changeFactAddressFieldVisibility: function (name,value) {
                return httpService.post('/api/user/fields/visibility/fact_address.json', {name:name,value:value},{});
            },
            changeRegistratorOfficeAddressFieldVisibility: function (name,value) {
                return httpService.post('/api/user/fields/visibility/registrator_office_address.json', {name:name,value:value},{});
            },
            changeRegistratorDataFieldVisibility: function (name,value) {
                return httpService.post('/api/user/fields/visibility/registrator_data.json', {name:name,value:value},{});
            }
        }
    });
    return app;
});