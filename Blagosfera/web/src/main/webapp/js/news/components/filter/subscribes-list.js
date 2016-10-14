/**
 * Список с контактами для выбора автора, по которому производится фильтрация
 */

/**
 * Конструктор списка
 * @param options - объект с дополнительными настройками списка
 * @constructor
 */
function NewsFilterSubscribesList($parent, authorId, url, communityId,selectWidth) {

    var self = this;
    var $container = createContainer();
    var $select = createSelect();

    var $allAuthorsLi = null;
    var selectedId = null;



    if (authorId) {
        selectedId = authorId;
    }

    this.getSelectedAuthorId = function () {

        if (!selectedId) {
            return null;
        }

        return selectedId;
    };

    this.setSelectedAuthorId = function(id) {
        selectedId = id;
    };

    $parent.append($container);
    $container.append($('<label style="display: block;">Выбор автора</label>'));
    $container.append($select);

    var data = null;

    if (communityId) {
        data = {
          communityId: communityId
        };
    }

    //Загружаем подписки на авторов
    $.radomJsonGet(
        //url
        url,
        //data
        data,
        //callback
        function (authors) {
            //Заполняем select
            for (var i = 0; i < authors.length; ++i) {
                var author = authors[i];

                var $option = $('<option>' + author.shortName + '</option>');
                $option.data("avatarSrc", Images.getResizeUrl(author.avatarSrc, "c254"));
                $option.data("shortName", author.shortName);
                $option.data("sharerId", author.id);

                if (author.id == authorId) {
                    $option.prop("selected", true);
                }

                $select.append($option);
            }

            //Превращаем в combobox
            $select.radomCombobox({
                inputPlaceholder: "Все авторы",
                buttonTooltip: "Все авторы",
                clearOnSelect: true,
                inputWidth : selectWidth,
                itemRenderFunction: function (ul, item) {
                    var avatarSrc = $(item.option).data("avatarSrc");
                    var shortName = $(item.option).data("shortName");

                    var li = $('<li></li>');

                    if (!avatarSrc && !shortName) {
                        li.attr("data-value", 'Все авторы');
                        var span = $('<span>Все авторы</span>');
                        li.append(span);
                        $allAuthorsLi = li;
                    } else {

                        li.attr("data-value", shortName);

                        var img = $('<img style="width: 40px;height : 40px; overlow : auto" class="img-thumbnail" src="' + avatarSrc + '" alt="' + name + '"></img>');
                        var span = $('<span>' + shortName + '</span>');
                        li.append(img);
                        li.append(span);
                    }

                    return li.appendTo(ul);
                },
                onSelect: function(event, ui) {
                    if ($(ui.item.option).data("sharerId")) {
                        self.setSelectedAuthorId($(ui.item.option).data("sharerId"));
                    } else {
                        self.setSelectedAuthorId(null);
                    }
                }
            });
        }
    );

    this.clear = function() {
        if ($allAuthorsLi) {
            $allAuthorsLi.click();
        } else {
            self.setSelectedAuthorId(null);
            $container.find('.radom-combobox input').val('');
        }
    };

    this.destroy = function () {
        self = null;
        $container.remove();
    };

    function createContainer() {
        return $('<div id="news_filter_subscribes_list_container"></div>');
    };

    function createSelect() {
        return $('<select id="new-dialog-contacts-select" style="display: none;"><option></option></select>');
    };

};
