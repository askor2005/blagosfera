'use strict';

define([
    'app'
], function (app) {

    function getQueryParams(query) {
        //query.order: 'creationDate',
        var dest = false;
        var columnIndex = 0;
        if (query.order.substr(0,1) != "-") {
            dest = true;
            columnIndex = query.order;
        } else {
            columnIndex = query.order.substr(1,1);
        }

        return 'page=' + query.page +'&per_page=' + query.limit + '&sort_column=' + columnIndex + '&sort_direction=' + dest;
    }

    app.factory('invitesPageService', function (httpService) {
        var invitesPageService = {};

        invitesPageService.getPageData = function() {
            return httpService.get('/invite.json', {headers: {'Cache-Control': 'no-cache'}})
        };

        invitesPageService.loadInvites = function (query, filter) {
            var dest = false;
            var columnIndex = 0;
            if (query.order.substr(0,1) != "-") {
                dest = true;
                columnIndex = query.order;
            } else {
                columnIndex = query.order.substr(1,1);
            }

            var verified = (filter.verified != null && filter.verified != "") ? (filter.verified == "true" || filter.verified == true  ? 1 : 0) : null;
            var guarantee = (filter.guarantee != null && filter.guarantee != "") ? (filter.guarantee == "true" || filter.guarantee == true  ? 1 : 0) : null;
            var sexFilter = (filter.gender != null && filter.gender != "") ? (filter.gender == "true" || filter.gender == true  ? 1 : 0) : null;
            var request = {
                page : query.page,
                perPage : query.limit,
                sortColumnIndex : columnIndex,
                sortDirection : dest,
                email : (filter.emailFilter != null && filter.emailFilter.length > 2) ? filter.emailFilter : null,
                inviteStatus : filter.status,
                fromDate : filter.inviteDateStart != null ? filter.inviteDateStart.getTime() : null,
                toDate : filter.inviteDateEnd != null ? filter.inviteDateEnd.getTime() : null,
                registerFromDate : filter.registrationDateStart != null ? filter.registrationDateStart.getTime() : null,
                registerToDate : filter.registrationDateEnd != null ? filter.registrationDateEnd.getTime() : null,
                verifiedFilter : verified,
                verifiedFromDate : filter.verifiedDateStart != null ? filter.verifiedDateStart.getTime() : null,
                verifiedToDate : filter.verifiedDateEnd != null ? filter.verifiedDateEnd.getTime() : null,

                fromSharersCount : filter.countUsersStart,
                toSharersCount : filter.countUsersEnd,
                fromOrganizationsCount : filter.countCommunitiesStart,
                toOrganizationsCount : filter.countCommunitiesEnd,

                verifierName : (filter.verifierName != null && filter.verifierName.length > 2) ? filter.verifierName : null,
                guaranteeFilter : guarantee,
                sexFilter : sexFilter,
                fromInvitesCount : filter.countInvitesStart,
                toInvitesCount : filter.countInvitesEnd,
                registratorLevel : filter.registratorLevel
            };


            return httpService.post('/invites/invites.json', request, {headers: {'Cache-Control': 'no-cache', 'Content-Type': 'application/json'}})
        };

        invitesPageService.createInvite = function (createInvite) {
            return httpService.post('/invite/create.json', createInvite, {headers: {'Cache-Control': 'no-cache', 'Content-Type': 'application/json'}, showError : false});
        };

        invitesPageService.validateEmail = function (email) {
            return httpService.post('/invite/validateEmail.json', {'email' : email}, {headers: {'Cache-Control': 'no-cache'}, showError : false});
        };

        invitesPageService.sendToEmail = function (id) {
            return httpService.post('/invite/sendToEmail.json', {'inviteId' : id}, {headers: {'Cache-Control': 'no-cache'}, showResult : true, resultMessage : "Письмо отправлено"});
        };

        return invitesPageService;
    });

    return app;
});