/**
 * Фильтр новостей
 */

function NewsFilter($parent, options) {

    var self = this;
    var $container = createContainer();
    var $padding = createPadding();
    var $containerAppendTo = $container.find("#containerAppendTo");
    console.log($containerAppendTo);
    var subscribesList = null;
    var categoryList = null;
    var dateFilter = null;
    var tagFilter = null;
    var $buttonGroup = null;
    var $acceptButton = null;
    var $resetButton = null;
    var $space = null;

    //Кладем в DOM сновной контейнер
    $parent.append($container);

    //Включить фильтрацию по автору, если указано в options
    if (options && options.subscribesList) {

        var authorId = null;
        var authorsSourceUrl = options.subscribesList.url;
        var communityId = options.subscribesList.communityId;

        if (options.data && options.data.authorId) {
            authorId = options.data.authorId;
        }
        $containerAppendTo.append(createPadding());
        subscribesList = new NewsFilterSubscribesList($containerAppendTo, authorId, authorsSourceUrl, communityId,options.authorsSelectWidth);

        this.getSubscribesList = function() {
            return subscribesList;
        }

    };

    //Включить фильтрацию по категориям, если указано в options
    if (options && options.categoryList) {

        var categoryId = null;

        if (options.data && options.data.categoryId) {
            categoryId = options.data.categoryId;
        }
        $containerAppendTo.append(createPadding());
        categoryList = new NewsFilterCategoryList($containerAppendTo, categoryId);

        this.getCategoryList = function() {
            return categoryList;
        };
    }


    //Включить фильтрацию по тегам
    if (options && options.tagFilter) {

        options.tagFilter.options.initialTags = options.data.tags;
        $containerAppendTo.append(createPadding());
        tagFilter = new RadomTagEditor($containerAppendTo, options.tagFilter.options);

        this.getTagFilter = function() {
            return tagFilter;
        };
    }

    //Включить фильтрацию по датам
    if (options && options.dateFilter) {

        var dateFrom = null;

        if (options.data && options.data.dateFrom) {
            dateFrom = options.data.dateFrom;
        }

        var dateTo = null;

        if (options.data && options.data.dateTo) {
            dateTo = options.data.dateTo;
        }
        $containerAppendTo.append(createPadding());
        dateFilter = new NewsFilterDate($containerAppendTo, {
            initDateFrom: dateFrom,
            initDateTo: dateTo,
            width : options.dateWidth
        });

        this.getDateFilter = function() {
            return dateFilter;
        };
    }


    $buttonGroup = createButtonGroup();
    $acceptButton = createAcceptButton();
    $resetButton = createResetButton();
    $space = createButtonSpace();
    $buttonGroup.append($resetButton);
    $buttonGroup.append($space);
    $buttonGroup.append($acceptButton);
    $containerAppendTo.append(createPadding());
    $containerAppendTo.append($buttonGroup);
    $containerAppendTo.append(createPadding());

    this.destroy = function() {
        $container.destroy();
    };

    /**
     * Создает контейнер с шапкой
     * @returns {Mixed|jQuery|HTMLElement}
     */
    function createContainer() {
        return $('<div class="panel panel-info">' +
                    '<div class="panel-heading">' +
                        '<h3 class="panel-title">' +
                            '<span id="sidebar-dialog-contacts-count">Фильтр новостей</span>' +
                        '</h3>' +
                    '</div>' +
            '<div id="containerAppendTo" style="margin-left: 15px;margin-right: 15px">' +
            '</div>' +
                '</div>');
    };
    function createPadding() {
        return $('<div style="height: 10px">' +
            '</div>');
    };


    function createButtonGroup() {
        return $('<div class="btn-group" role="group" style="display: table; margin: 10px auto 5px;"></div>');
    }

    function createAcceptButton() {
        var $result = $('<button class="btn btn-primary">Применить</button>');

        if (options && options.acceptCallback) {
            $result.on("click", function () {
                options.acceptCallback(getData());
            });
        }

        return $result;
    };
    function createButtonSpace() {
        var $result = $('<td  style="width:10px;"></td>');
        return $result;
    };

    /**
     * Позволяет получить все необходимые данные для сохранения фильтра на сервере
     */
    function getData() {
        var result = {};

        if (subscribesList) {
            result.authorId = subscribesList.getSelectedAuthorId();
        }

        if (categoryList) {
            result.categoryId = categoryList.getCategoryId();
        }

        if (dateFilter) {
            result.dateFrom = dateFilter.getDateFrom();
            result.dateTo = dateFilter.getDateTo();
        }

        if (tagFilter) {
            result.tags = tagFilter.getTags();
        }

        return result;
    };


    //Производим подписку на клики по категориям
    $(document).on('clickNewsCategory', function(event, categoryId) {
        categoryList.setCategoryId(categoryId);
        clear({
            exclude: ['categoryList']
        });

        $acceptButton.trigger("click");
    });

    //Производим подписку на клики по тегам
    $(document).on('clickNewsTag', function(event, tagText) {
        clear();
        tagFilter.addTag(tagText);

        $acceptButton.trigger("click");
    });

    /**
     * Очищает фильтр
     */
    function clear(options) {

        //Очищаем поле с выбранным автором
        if (subscribesList) {

            if (options && options.exclude && options.exclude.indexOf('subscribeList') != -1) {

            } else {
                subscribesList.clear();
            }
        }

        //Очищаем поле с выбранной категорией
        if (categoryList) {
            if (options && options.exclude && options.exclude.indexOf('categoryList') != -1) {

            } else {
                categoryList.clear();
            }
        }

        //Очищаем диапазон дат создания новости
        if (dateFilter) {
            if (options && options.exclude && options.exclude.indexOf('dateFilter') != -1) {

            } else {
                dateFilter.clear();
            }
        }

        //Очищаем фильтр тегов
        if (tagFilter) {
            if (options && options.exclude && options.exclude.indexOf('tagFilter') != -1) {

            } else {
                tagFilter.clear();
            }
        }
    };

    /**
     * Создает кнопку сброса
     * @returns {Mixed|jQuery|HTMLElement}
     */
    function createResetButton() {
        var $result = $('<button class="btn btn-danger">Сбросить</button>');

        //При клике очищаем все компоненты один за другим
        $result.on("click", function() {
            clear();
            $acceptButton.trigger("click");
        });

        return $result;
    };
    function createSpaceBetweenButtons() {
        var $result = $('<button class="btn btn-danger">Сбросить</button>');
    };

};
