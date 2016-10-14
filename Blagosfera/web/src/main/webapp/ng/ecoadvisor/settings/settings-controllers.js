'use strict';

define([
    'app'
], function (app) {

    app.controller('SettingsCtrl', function ($routeParams, communitiesService, ecoAdvisorSettingsService,
                                             broadcastService, FileSaver, Blob) {
        var scope = this;
        scope.community = {};
        scope.community.id = $routeParams.communityId ? parseInt($routeParams.communityId) : 0;

        var calcPercent = function (number, percent) {
            return Number(number * percent / 100);
        };

        // число сумма которого с заданным процентом от самого себя равна аргументу
        var calcNumberWithoutPercent = function (argument, percent) {
            return Number(argument / (1 + percent / 100));
        };

        var roundToTwo = function (number) {
            return +(Math.round(number + "e+2") + "e-2");
        };

        function isNumber(number) {
            return !isNaN(parseFloat(number)) && isFinite(number);
            //return Number(parseFloat(number)) == number;
        }

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
            data: {
                directCosts: 0, // стоимость приобретения товара магазином (так называемые прямые затраты)
                finalCosts: 0   // цена товара при его продаже в магазине (В ТОМ ЧИСЛЕ НДС)
            },
            charts: {
                chart1: {
                    labels: ['Прибыль Компании', 'Прирост Средств'],
                    series: [''],
                    data: [
                        [0, 0]
                    ],
                    options: {
                        scaleLabel: "<%=value%>%",
                        tooltipTemplate: "<%=value%>%"
                    }
                },
                chart2: {
                    labels: ["0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"],
                    series: ['&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Доля чистой прибыли, остающаяся в компании'],
                    data: [
                        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
                    ],
                    options: {
                        bezierCurve: false,
                        scaleShowLabels: true,
                        scaleLabel: "<%=value%>%",
                        yAxisLabel: "Прирост средств",
                        tooltipTemplate: "<%=value%>%",
                        legendTemplate: "<div class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><div><span style=\"background-color:<%=datasets[i].strokeColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></div><%}%></div>"
                    }
                }
            },
            reports: {
                report1: {
                    directCosts: 0,         // прямые затраты
                    finalCosts: 0,          // цена товара при его продаже в магазине (В ТОМ ЧИСЛЕ НДС)
                    generalRunningCosts: 0, // ОХР
                    vatBase: 0,             // база НДС
                    vat: 0,                 // НДС
                    wage: 0,                // зарплата
                    totalCosts: 0,          // сумма затрат
                    taxOnProfitsBase: 0,    // база налога на прибыль
                    taxOnProfits: 0,        // налог на прибыль
                    taxes: 0,               // налоговое обременение
                    netProfit: 0,           // чистая прибыль
                    companyProfit: 0,       // сумма остающаяся в компании
                    dividends: 0,           // дивиденды собственника
                    taxOnDividends: 0,      // подоходный налог на дивиденды
                    proprietorProfit: 0,    // сумма остающаяся у собственника
                    total: 0,               // сумма средств, остающаяся в распоряжении компании и собственника

                    calculate: function (overrideCompanyProfit) {
                        var report1 = scope.ecoAdvisor.reports.report1;
                        var settings = scope.ecoAdvisor.settings;
                        var data = scope.ecoAdvisor.data;

                        report1.directCosts = data.directCosts;
                        //report1.directCosts = 2722.5;

                        report1.finalCosts = report1.directCosts + calcPercent(report1.directCosts, settings.margin);
                        //report1.finalCosts = 4682.7;

                        report1.generalRunningCosts = calcPercent(report1.finalCosts, settings.generalRunningCosts);
                        report1.vatBase = roundToTwo(report1.finalCosts - report1.directCosts - report1.generalRunningCosts);
                        report1.vat = calcNumberWithoutPercent(calcPercent(report1.vatBase, settings.vat), settings.vat);

                        report1.wage = calcPercent(report1.generalRunningCosts, settings.wage);
                        report1.totalCosts = report1.directCosts + report1.generalRunningCosts + report1.wage;
                        report1.taxOnProfitsBase = report1.finalCosts - report1.totalCosts - report1.vat;
                        report1.taxOnProfits = calcPercent(report1.taxOnProfitsBase, settings.taxOnProfits);

                        report1.taxes = report1.vat + report1.taxOnProfits;
                        report1.netProfit = report1.finalCosts - report1.totalCosts - report1.vat - report1.taxOnProfits;

                        report1.companyProfit = calcPercent(report1.netProfit, isNumber(overrideCompanyProfit) ? overrideCompanyProfit : settings.companyProfit);
                        report1.dividends = report1.netProfit - report1.companyProfit;

                        report1.taxOnDividends = calcPercent(report1.dividends, settings.taxOnDividends);
                        report1.proprietorProfit = report1.dividends - report1.taxOnDividends;

                        report1.total = report1.companyProfit + report1.proprietorProfit;
                    }
                },
                report2: {
                    vat: 0,                             // НДС
                    minShareValue: 0,                   // минимальная оценка пая
                    maxShareValue: 0,                   // максимальная оценка пая
                    shareValue: 0,                      // оценка пая
                    companyProfit: 0,                   // сумма средств, оставшаяся в распоряжении компании
                    taxOnProfitsBase: 0,                // база налога на прибыль
                    taxOnProfits: 0,                    // налог на прибыль
                    cooperativeAmount: 0,               // сумма в ПО
                    taxOnProprietorIncome: 0,           // подоходный налог
                    effect: 0,                          // эффект от перехода к пае
                    proprietorPartInCooperative: 0,     // сумма собственника в куч
                    taxOnCompanyProfits: 0,             // отложенный подоходный налог
                    consumerBonus: 0,                   // выгода потребителя
                    cooperativeBonus: 0,                // выгода ПО
                    proprietorAndCompanyBonus: 0,       // выгода собственника и компании
                    finalPriceForConsumer: 0,           // цена товара для потребителя
                    totalProprietorAndCompanyBonus: 0,  // итоговая выгода собственника и компании
                    differenceInPercents: 0,            // выгода в процентах от купли продажи
                    differenceInPercentsForConsumer: 0, // выгода для пайщика в процентах

                    calculate: function (overrideCompanyProfit) {
                        var report1 = scope.ecoAdvisor.reports.report1;
                        var report2 = scope.ecoAdvisor.reports.report2;
                        var settings = scope.ecoAdvisor.settings;

                        report1.calculate(overrideCompanyProfit);

                        report2.vat = calcPercent(report1.wage, settings.vat);
                        report2.vat = calcNumberWithoutPercent(report2.vat, settings.vat);

                        report2.minShareValue = report1.totalCosts + calcNumberWithoutPercent(calcPercent(report1.wage, settings.vat), settings.vat);
                        report2.maxShareValue = report2.minShareValue + report1.taxOnProfitsBase - report2.vat;

                        report2.shareValue = report2.minShareValue + calcPercent(report2.maxShareValue - report2.minShareValue, settings.shareValue);

                        report2.companyProfit = report2.shareValue - report2.minShareValue;
                        report2.companyProfit = report2.companyProfit - calcPercent(report2.companyProfit, settings.vat);

                        report2.taxOnProfitsBase = report2.shareValue - report2.minShareValue;
                        report2.taxOnProfits = calcPercent(report2.taxOnProfitsBase, settings.taxOnProfits);

                        report2.cooperativeAmount = report1.finalCosts - report2.shareValue + report2.vat;
                        report2.proprietorPartInCooperative = calcPercent(report2.cooperativeAmount, settings.departmentPart);

                        report2.effect = report2.proprietorPartInCooperative + report2.companyProfit - report1.total;

                        report2.taxOnProprietorIncome = calcPercent((report2.cooperativeAmount - report2.proprietorPartInCooperative), settings.incomeTax);

                        /*report2.consumerBonus = report2.effect / 3;
                         report2.cooperativeBonus = report2.effect / 3;
                         report2.proprietorAndCompanyBonus = report2.effect / 3;*/

                        report2.consumerBonus = calcPercent(report2.effect, scope.bonus.allocationItems[0].allocationPercent);
                        report2.cooperativeBonus = calcPercent(report2.effect, scope.bonus.allocationItems[1].allocationPercent);
                        report2.proprietorAndCompanyBonus = report2.effect - report2.consumerBonus - report2.cooperativeBonus;

                        report2.taxOnCompanyProfits = calcPercent(report2.proprietorPartInCooperative, settings.incomeTax);
                        report2.finalPriceForConsumer = report1.finalCosts - report2.consumerBonus;
                        report2.totalProprietorAndCompanyBonus = report1.total + report2.proprietorAndCompanyBonus;

                        report2.differenceInPercents = roundToTwo(((report2.totalProprietorAndCompanyBonus - report1.total) / report1.total) * 100);
                        report2.differenceInPercentsForConsumer = roundToTwo((report2.consumerBonus / report1.finalCosts) * 100);
                    }
                },
                calculate: function () {
                    var report2 = scope.ecoAdvisor.reports.report2;
                    var settings = scope.ecoAdvisor.settings;
                    var charts = scope.ecoAdvisor.charts;

                    for (var i = 0; i <= 10; i++) {
                        report2.calculate(i * 10);
                        charts.chart2.data[0][i] = report2.differenceInPercents;
                    }

                    report2.calculate();

                    charts.chart1.data[0][0] = settings.companyProfit;
                    charts.chart1.data[0][1] = report2.differenceInPercents;
                }
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
                        ecoAdvisorSettingsService.saveAdvisorSettings(scope.community.id, scope.ecoAdvisor.settings, scope.productGroups.selected).then(function (response) {
                            scope.ecoAdvisor.loadSettings();
                        });
                    }
                });
            },
            loadSettings: function () {
                ecoAdvisorSettingsService.getAdvisorSettings(scope.community.id, scope.productGroups.selected).then(function (response) {
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

                    scope.ecoAdvisor.reports.calculate();
                    scope.onMarginSliderChange(scope.ecoAdvisor.settings.margin);
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

        scope.products = {
            data: [],
            count: 0,
            query: {
                sortColumn: null,
                sortDirection: null,
                pageSize: 5,
                page: 1
            }
        };

        function getProducts() {
            var sortColumn = scope.products.query.sortColumn;
            scope.products.query.sortDirection = sortColumn && sortColumn.startsWith('-') ? 'desc' : 'asc';
            scope.products.query.sortColumn = sortColumn && sortColumn.startsWith('-') ? sortColumn.substring(1) : sortColumn;

            scope.promise = communitiesService.getProductsFromStore(scope.community.id,
                scope.products.query.page - 1, scope.products.query.pageSize,
                scope.products.query.sortDirection, scope.products.query.sortColumn,
                scope.productGroups.selected).then(
                function (response) {
                    scope.products.data = response.data.data;
                    scope.products.count = response.data.total;

                    scope.ecoAdvisor.data.directCosts = response.data.directCosts;
                    scope.ecoAdvisor.data.finalCosts = response.data.finalCosts;

                    scope.ecoAdvisor.loadSettings();
                }
            );
        }

        scope.onPaginate = function (page, pageSize) {
            scope.products.query.page = page;
            scope.products.query.pageSize = pageSize;
            getProducts();
        };

        scope.onReorder = function (sortColumn) {
            scope.products.query.sortColumn = sortColumn;
            getProducts();
        };

        scope.openProductDetailsDialog = function ($event, product) {
            $event.stopPropagation();

            broadcastService.send('ecoAdvisorDialog', {
                type: 'productDetails',
                event: $event,
                product: product
            });
        };

        scope.export2excel = function () {
            ecoAdvisorSettingsService.exportAdvisorReport2Excel(scope.ecoAdvisor).then(function (response) {
                var blob = new Blob([response.data], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
                FileSaver.saveAs(blob, 'Очет ЭКС.xlsx');
            });
        };

        scope.marginSliderMin = 0;
        scope.marginSliderMax = 100;
        scope.marginSliderStep = 1;

        scope.onMarginSliderChange = function (margin) {
            var k = (margin / 100 | 0);
            scope.marginSliderMax = k === 0 ? 100 : k * 100 + k * 100;
        };

        // product groups

        scope.productGroups = {
            data: [],
            selected: null
        };

        var getProductGroups = function () {
            ecoAdvisorSettingsService.getProductGroups(scope.community.id).then(
                function (response) {
                    scope.productGroups.data = response.data;
                }
            );
        };

        function createFilterFor(query) {
            var lowercaseQuery = angular.lowercase(query);

            return function filterFn(group) {
                return (group.name.indexOf(lowercaseQuery) === 0);
            };
        }

        scope.productsGroupsSearchText = '';

        scope.productsGroupsSearch = function (query) {
            return query ? scope.productGroups.data.filter(createFilterFor(query)) : scope.productGroups.data;
        };

        scope.productGroupSelected = function (item) {
            getProducts();
        };

        // BONUS ALLOCATIONS

        var allocationsChanged = false;

        scope.receiverTypes = [
            {title: 'Паевая книжка Потребителя (физ.лица)', value: 'CONSUMER_SHAREBOOK'},
            {title: 'Фонды Системы БЛАГОСФЕРА', value: 'SYSTEM'},
            {title: 'Личный счёт Собственника в Системе БЛАГОСФЕРА', value: 'USER_ACCOUNT'},
            {title: 'Паевая книжка Собственника (физ.лица)', value: 'USER_SHAREBOOK'},
            {title: 'Счет Компании в Системе БЛАГОСФЕРА', value: 'COMMUNITY_ACCOUNT'},
            {title: 'Паевая книжка Компании (юр.лица)', value: 'COMMUNITY_SHAREBOOK'}
        ];

        scope.bonus = {
            chart: {
                labels: ['Остаток в КУч'],
                data: [100],
                options: {
                    animationEasing: "easeInOutQuart",
                    //tooltipEvents: [],
                    //showTooltips: true,
                    //tooltipCaretSize: 0,
                    //onAnimationComplete: function () {
                    //    this.showTooltip(this.segments, true);
                    //}
                    legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<segments.length; i++){%><li><span style=\"background-color:<%=segments[i].fillColor%>\"></span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>",
                    tooltipTemplate: "<%if (label){%><%=label%>: <%}%><%= value + '%' %>"
                }
            },
            allocationItems: []
        };

        for (var i = 0; i < scope.receiverTypes.length; i++) {
            scope.bonus.chart.labels.push(scope.receiverTypes[i].title);
            scope.bonus.chart.data.push(0);

            scope.bonus.allocationItems.push({
                id: null,
                receiverType: scope.receiverTypes[i],
                allocationPercent: 0,
                sliderOptions: {
                    min: 0,
                    max: 100,
                    step: 1
                }
            });
        }

        scope.refreshSlidersOptions = function () {
            for (var i = 0; i < scope.bonus.allocationItems.length; i++) {
                scope.bonus.allocationItems[i].sliderOptions.max = scope.bonus.chart.data[0] + scope.bonus.chart.data[i + 1];
            }
        };

        scope.refreshChartData = function () {
            scope.bonus.chart.data[0] = 100;

            for (var i = 0; i < scope.bonus.allocationItems.length; i++) {
                scope.bonus.chart.data[i + 1] = scope.bonus.allocationItems[i].allocationPercent;
                scope.bonus.chart.data[0] -= scope.bonus.chart.data[i + 1];
            }
        };

        scope.pushAllocationItem = function (item) {
            for (var i = 0; i < scope.bonus.allocationItems.length; i++) {
                if (scope.bonus.allocationItems[i].receiverType.value === item.receiverType.value) {
                    scope.bonus.allocationItems[i].id = item.id;
                    scope.bonus.allocationItems[i].allocationPercent = item.allocationPercent;
                    break;
                }
            }
        };

        scope.canSaveAllocationItems = function () {
            return allocationsChanged;
        };

        scope.saveAllocationItems = function ($event) {
            broadcastService.send('dialog', {
                type: 'yesno',
                title: 'Требуется подтверждение',
                question: 'Настройки будут перезаписаны. Продолжить?',
                showYes: true,
                showNo: true,
                showCancel: false,
                event: $event,
                onYes: function () {
                    ecoAdvisorSettingsService.saveBonusAllocations(scope.community.id, scope.bonus.allocationItems).then(
                        function (response) {
                            scope.loadAllocationItems();
                        }
                    );
                }
            });
        };

        scope.loadAllocationItems = function () {
            ecoAdvisorSettingsService.getBonusAllocations(scope.community.id).then(
                function (response) {
                    for (var i = 0; i < response.data.length; i++) {
                        var item = response.data[i];

                        for (var j = 0; j < scope.receiverTypes.length; j++) {
                            if (scope.receiverTypes[j].value === item.receiverType) {
                                item.receiverType = scope.receiverTypes[j];
                                break;
                            }
                        }

                        scope.pushAllocationItem(item);
                    }

                    scope.refreshChartData();
                    scope.refreshSlidersOptions();
                }
            );

            allocationsChanged = false;
        };

        scope.reloadAllocationItems = function ($event) {
            broadcastService.send('dialog', {
                type: 'yesno',
                title: 'Требуется подтверждение',
                question: 'Изменения не будут сохранены. Продолжить?',
                showYes: true,
                showNo: true,
                showCancel: false,
                event: $event,
                onYes: function () {
                    scope.loadAllocationItems();
                }
            });
        };

        scope.sliderChanged = function () {
            scope.refreshChartData();
            scope.refreshSlidersOptions();
            allocationsChanged = true;
        };

        // init

        communitiesService.getCommunity(scope.community.id).then(function (response) {
            scope.community.name = response.data.name;
            scope.ecoAdvisor.loadSettings();
            scope.loadAllocationItems();
            getProducts();
            getProductGroups();
        });
    });

    return app;
});