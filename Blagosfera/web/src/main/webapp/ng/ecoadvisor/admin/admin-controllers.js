'use strict';

define([
    'app'
], function (app) {

    app.controller('AdminCtrl', function (broadcastService, ecoAdvisorAdminSettingsService) {
        var scope = this;

        scope.ecoAdvisor = {
            settings: {
                generalRunningCosts: 0,    // общехозяйственные расходы (ОХР), %
                wage: 0,                   // зарплата вместе со ВСЕМИ зарплатными налогами составляет по отношению к ОХР, не более, %
                vat: 0,                    // ставка налога на добавленную стоимость, %
                taxOnProfits: 0,           // ставка налога на прибыль, %
                incomeTax: 0,              // ставка подоходного налога (НДФЛ), %
                proprietorshipInterest: 0, // доля собственника в уставном капитале, %
                taxOnDividends: 0,         // ставка обложения выплачиваемых дивидендов для доли собственника физического лица, %
                companyProfit: 0,          // доля чистой прибыли, остающаяся в расп. компании, %
                margin: 0,                 // НАЦЕНКА при купле-продаже, %
                shareValue: 0,             // оценка товара как паевого взноса, % от разницы между минимальной и максимальной оценкой
                departmentPart: 0          // доля средств, которую собственник оставляет в куч, %
            },
            saveSettings: function () {
                broadcastService.send('dialog', {
                    type: 'yesno',
                    title: 'Требуется подтверждение',
                    question: 'Настройки будут перезаписаны. Продолжить?',
                    showYes: true,
                    showNo: true,
                    showCancel: false,
                    onYes: function () {
                        ecoAdvisorAdminSettingsService.saveAdvisorSettings(scope.ecoAdvisor.settings).then(function (response) {
                            scope.ecoAdvisor.loadSettings();
                        });
                    }
                });
            },
            loadSettings: function () {
                ecoAdvisorAdminSettingsService.getAdvisorSettings().then(function (response) {
                    scope.ecoAdvisor.settings.generalRunningCosts = response.data.generalRunningCosts;
                    scope.ecoAdvisor.settings.wage = response.data.wage;
                    scope.ecoAdvisor.settings.vat = response.data.vat;
                    scope.ecoAdvisor.settings.taxOnProfits = response.data.taxOnProfits;
                    scope.ecoAdvisor.settings.incomeTax = response.data.incomeTax;
                    scope.ecoAdvisor.settings.proprietorshipInterest = response.data.proprietorshipInterest;
                    scope.ecoAdvisor.settings.taxOnDividends = response.data.taxOnDividends;
                    scope.ecoAdvisor.settings.companyProfit = response.data.companyProfit;
                    scope.ecoAdvisor.settings.margin = response.data.margin;
                    scope.ecoAdvisor.settings.shareValue = response.data.shareValue;
                    scope.ecoAdvisor.settings.departmentPart = response.data.departmentPart;
                });
            },
            reloadSettings: function () {
                broadcastService.send('dialog', {
                    type: 'yesno',
                    title: 'Требуется подтверждение',
                    question: 'Изменения не будут сохранены. Продолжить?',
                    showYes: true,
                    showNo: true,
                    showCancel: false,
                    onYes: function () {
                        scope.ecoAdvisor.loadSettings();
                    }
                });
            }
        };

        scope.marginSliderMin = 0;
        scope.marginSliderMax = 100;
        scope.marginSliderStep = 1;

        scope.onMarginSliderChange = function (margin) {
            var k = (margin / 100 | 0);
            scope.marginSliderMax = k === 0 ? 100 : k * 100 + k * 100;
        };

        scope.ecoAdvisor.loadSettings();
    });

    return app;
});