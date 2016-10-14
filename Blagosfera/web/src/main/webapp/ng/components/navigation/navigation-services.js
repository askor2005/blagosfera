'use strict';

define([
    'app'
], function (app) {

    app.factory('navigationService', function (appService, httpService, $q, $rootScope, translateService) {
        var navigationService = {};

        var ecoAdvisorDeps = [
            'ecoadvisor/ecoadvisor',
            'ecoadvisor/ecoadvisor-controllers',
            'ecoadvisor/ecoadvisor-services'
        ];

        var ecoAdvisorCommunitiesDeps = ecoAdvisorDeps.concat([
            'ecoadvisor/communities/communities',
            'ecoadvisor/communities/communities-services',
            'ecoadvisor/communities/communities-controllers'
        ]);

        var ecoAdvisorSessionsDeps = ecoAdvisorCommunitiesDeps.concat([
            'ecoadvisor/sessions/sessions',
            'ecoadvisor/sessions/sessions-services',
            'ecoadvisor/sessions/sessions-controllers'
        ]);

        var ecoAdvisorSettingsDeps = ecoAdvisorCommunitiesDeps.concat([
            'ecoadvisor/settings/settings',
            'ecoadvisor/settings/settings-services',
            'ecoadvisor/settings/settings-controllers'
        ]);

        var ecoAdvisorStoreDeps = ecoAdvisorCommunitiesDeps.concat([
            'ecoadvisor/store/store',
            'ecoadvisor/store/store-services',
            'ecoadvisor/store/store-controllers'
        ]);

        var ecoAdvisorAdminDeps = ecoAdvisorDeps.concat([
            'ecoadvisor/admin/admin',
            'ecoadvisor/admin/admin-services',
            'ecoadvisor/admin/admin-controllers'
        ]);

        var invitesDeps = [
            'invites/invites',
            'invites/invites-controllers',
            'invites/invites-filters',
            'invites/invites-services',
            'invites/invites-directives'
        ];

        var userProfileDeps = [
            'user/profile/profile',
            'user/profile/profile-controllers',
            'user/profile/profile-services'
        ];
        var userDeps = [
            'user/user',
            'user/user-controllers',
            'user/user-services'
        ];
        var userSettingDeps = userDeps.concat([
            'user/settings/settings',
            'user/settings/settings-services',
            'user/settings/settings-controllers'
        ]);

        var invitationAcceptDeps = [
            'invitation_accept/invitation-accept',
            'invitation_accept/invitation-accept-controllers',
            'invitation_accept/invitation-accept-directives',
            'invitation_accept/invitation-accept-services'
        ];
        var accountDeps = [
            'account/account',
            'account/account-controllers',
            'account/account-services'
        ];
        var adminDeps = [
            'admin/admin',
            'admin/admin-controllers',
            'admin/admin-services'
        ];
        var adminSupportDeps = adminDeps.concat ([
            'admin/support/support',
            'admin/support/support-controllers',
            'admin/support/support-services'
        ]);
        var adminSupportRequestsDeps = adminSupportDeps.concat ([
            'admin/support/requests/requests',
            'admin/support/requests/requests-controllers',
            'admin/support/requests/requests-services'
        ]);
        navigationService.appAreas = {
            areas: [
                {
                    urlPattern: '/p/:page',
                    deps: [],
                    params: [],
                    translations: [],
                    controller: {
                        name: 'StaticPageCtrl',
                        as: 'page'
                    },
                    templateUrl: 'views/page.html',
                    reloadOnSearch: false,
                    areas: []
                },
                {
                    name: 'userSettings',
                    accessLevel: 'user',
                    urlPattern: '/user/settings',
                    deps: userSettingDeps,
                    params: [{name: 'settings', url: '/api/user/settings/settings.json'}],
                    translations: ['user'],
                    controller: {
                        name: 'UserSettingsCtrl',
                        as: 'userSettings'
                    },
                    templateUrl: '/ng/user/settings/settings.html',
                    reloadOnSearch: false,
                    areas: []
                },
                {
                    name: 'account',
                    accessLevel: 'user',
                    urlPattern: '/account',
                    deps: accountDeps,
                    controller: {
                        name: 'AccountCtrl',
                        as: 'account'
                    },
                    templateUrl: '/ng/account/account.html',
                    reloadOnSearch: false,
                    translations: ['account'],
                    params : [{name : 'acc',url :'/api/accounts/account.json'}],
                    areas: []
                },
                {
                    name: 'supportRequests',
                    accessLevel: 'admin',
                    urlPattern: '/admin/support/requests',
                    deps: adminSupportRequestsDeps,
                    controller: {
                        name: 'SupportRequestsCtrl',
                        as: 'requests'
                    },
                    templateUrl: '/ng/admin/support/requests/requests.html',
                    reloadOnSearch: false,
                    translations: [],
                    params: [{name: 'info', url: '/api/support/requests/admin/info.json'}],
                    areas: []
                },
                {
                    urlPattern: '/ecoadvisor/communities',
                    deps: ecoAdvisorCommunitiesDeps,
                    params: [],
                    translations: ['ecoadvisor'],
                    controller: {
                        name: 'CommunitiesCtrl',
                        as: 'communities'
                    },
                    templateUrl: '/ng/ecoadvisor/communities/communities.html',
                    reloadOnSearch: false,
                    areas: [
                        {
                            urlPattern: '/ecoadvisor/sessions/:communityId',
                            deps: ecoAdvisorSessionsDeps,
                            params: [],
                            translations: ['ecoadvisor'],
                            controller: {
                                name: 'SessionsCtrl',
                                as: 'sessions'
                            },
                            templateUrl: '/ng/ecoadvisor/sessions/sessions.html',
                            reloadOnSearch: false,
                            areas: []
                        }, {
                            urlPattern: '/ecoadvisor/settings/:communityId',
                            deps: ecoAdvisorSettingsDeps,
                            params: [],
                            translations: ['ecoadvisor'],
                            controller: {
                                name: 'SettingsCtrl',
                                as: 'settings'
                            },
                            templateUrl: '/ng/ecoadvisor/settings/settings.html',
                            reloadOnSearch: false,
                            areas: []
                        }, {
                            urlPattern: '/ecoadvisor/store/:communityId',
                            deps: ecoAdvisorStoreDeps,
                            params: [],
                            translations: ['ecoadvisor'],
                            controller: {
                                name: 'StoreCtrl',
                                as: 'store'
                            },
                            templateUrl: '/ng/ecoadvisor/store/store.html',
                            reloadOnSearch: false,
                            areas: []
                        }
                    ]
                }, {
                    urlPattern: '/invites',
                    deps: invitesDeps,
                    params: [],
                    translations: ['invitations'],
                    controller: {
                        name: 'InvitesPageCtrl',
                        as: 'invites'
                    },
                    templateUrl: '/ng/invites/views/invites.html',
                    reloadOnSearch: false,
                    areas: []
                }, {
                    urlPattern: '/user/profile/:ikp',
                    deps: userProfileDeps,
                    params: [{name: 'countryCodesMapping', url: '/api/user/country_codes_mapping.json'}],
                    translations: ['user/profile', 'address','choosephoto'],
                    controller: {
                        name: 'ProfileCtrl',
                        as: 'profile'
                    },
                    templateUrl: '/ng/user/profile/profile.html',
                    reloadOnSearch: false,
                    areas: []
                }, {
                    urlPattern: '/invitationaccept/:hash',
                    deps: invitationAcceptDeps,
                    params: [],
                    translations: ['invitations','choosephoto'],
                    controller: {
                        name: 'InvitationAcceptCtrl',
                        as: 'invitationAccept'
                    },
                    templateUrl: '/ng/invitation_accept/views/invitationAccept.html',
                    reloadOnSearch: false,
                    areas: []
                }
            ]
        };

        function initRouter(areas) {
            for (var i = 0; i < areas.length; i++) {
                var area = areas[i];
                var resolve = {};

                if (area.deps.length) {
                    resolve.deps = appService.loadDeps(area.deps);
                }

                if (area.params.length) {
                    for (var j = 0; j < area.params.length; ++j) {
                        resolve[area.params[j].name] = appService.loadCtrlParam(area.params[j].url);
                    }
                }

                if (area.translations.length) {
                    resolve.translations = translateService.loadTranslations(area.translations);
                }

                app.when(area.urlPattern, {
                    templateUrl: area.templateUrl,
                    controller: area.controller.name,
                    controllerAs: area.controller.as,
                    reloadOnSearch: area.reloadOnSearch,
                    resolve: resolve,
                    area: area
                });

                initRouter(area.areas);
            }
        }

        initRouter(navigationService.appAreas.areas);
        app.otherwise({redirectTo: '/p/welcome'});

        navigationService.navigationMenu = {
            id: 0,
            parentId: null,
            title: null,
            icon: null,
            path: null,
            expandable: false,
            collapsed: false,
            switchMenu: false,
            lazyLoad: false,
            items: []
        };

        navigationService.userMenu = {
            items: []
        };

        function findMenuItem(items, itemId) {
            for (var i = 0; i < items.length; i++) {
                var item = items[i];

                if (item.id === itemId) {
                    return item;
                } else {
                    var item2 = findMenuItem(item.items, itemId);
                    if (item2) return item2;
                }
            }

            return null;
        }

        navigationService.loadMenu = function () {
            httpService.get('/api/navigation/navigationMenu.json', {params: {type: 'MAIN'}}).then(function (response) {
                navigationService.navigationMenu.items = response.data.items;
            });
        };

        navigationService.loadUserMenu = function () {
            httpService.get('/api/navigation/navigationMenu.json', {params: {type: 'USER'}}).then(function (response) {
                navigationService.userMenu.items = response.data.items;
            });
        };

        navigationService.loadLazyMenu = function (menuItemId) {
            var menuItem = findMenuItem(navigationService.navigationMenu.items, menuItemId);

            if (menuItem) {
                httpService.get('/api/navigation/navigationMenu.json', {params: {menuItemId: menuItemId}}).then(function (response) {
                    menuItem.items = response.data.items;
                });
            }
        };

        navigationService.clearMenu = function () {
            navigationService.navigationMenu.id = 0;
            navigationService.navigationMenu.parentId = null;
            navigationService.navigationMenu.title = null;
            navigationService.navigationMenu.icon = null;
            navigationService.navigationMenu.path = null;
            navigationService.navigationMenu.expandable = false;
            navigationService.navigationMenu.collapsed = false;
            navigationService.navigationMenu.switchMenu = false;
            navigationService.navigationMenu.lazyLoad = false;
            navigationService.navigationMenu.items.length = 0;

            navigationService.userMenu.items.length = 0;
        };

        navigationService.findMenuItem = function (itemId) {
            return findMenuItem(navigationService.navigationMenu.items, itemId);
        };

        return navigationService;
    });

    return app;
});