/**
 * Фильтр новостных категорий
 */

/**
 * Конструктор
 * @constructor
 * @param $parentContainer родительский контейнер
 */
function NewsFilterCategoryList($parentContainer, id) {

    var self = this;

    var $container = $('<div style = "padding-bottom: 5px;"></div>');

    $container.append($('<label>Выбор категории</label>'));
    $container.append($('<div id="newsCategoriesForm" rameraListEditorName="news_categories"></div>'));
    $container.append($('<input type="hidden" class="form-control" id="NEWS_CATEGORY" name="news_categories_form_id"/>'));
    $parentContainer.append($container);

    $("input#NEWS_CATEGORY").attr('data-field-value', $("input#NEWS_CATEGORY").val());


    var categories = [];

    if (id) {
        $container.find("input#NEWS_CATEGORY").val(id);
        categories.push($container.find("input#NEWS_CATEGORY").val());
    }


    initListEditor();

    this.getCategoryId = function () {
        var result = $container.find("input#NEWS_CATEGORY").val();

        if (!result) {
            result = null;
        }
        return result;
    };


    this.clear = function() {
        $container.find("input#NEWS_CATEGORY").val("");
        $container.find("#newsCategoriesForm").empty();
        categories = [];
        initListEditor();
    };

    this.setCategoryId = function(categoryId) {
        $container.find("input#NEWS_CATEGORY").val(categoryId);
        $container.find("#newsCategoriesForm").empty();

        if (categoryId) {
            categories = [categoryId];
        } else {
            categories = [];
        }

        initListEditor();
    };

    this.destroy = function() {
        self = null;
        $container.remove();
    };

    function initListEditor() {
        RameraListEditorModule.init(
            $container.find("#newsCategoriesForm"),
            {
                labelClasses: ["checkbox-inline"],
                labelStyle: "margin-left: 10px;",
                selectedItems: categories,
                selectClasses: ["form-control"],
                childStyle: "margin-top: 30px"
            },
            function(event, data) {
                if (event == RameraListEditorEvents.VALUE_CHANGED) {
                    $("input#NEWS_CATEGORY").val(data.value);
                }
            }
        );
    };

};