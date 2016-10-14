/**
 * Компонент для задания категории новости.
 *

/**
 * Конструктор
 * @constructor
 * @param $parentContainer родительский контейнер
 */
function CategoryEditor($parentContainer, id) {

    var self = this;

    var $container = $('<div style = "padding-bottom: 5px;"></div>');

    $container.append('<label>Категория новости</label>');
    $container.append($('<div id="newsCategoriesForm" rameraListEditorName="news_categories"></div>'));
    $container.append($('<input type="hidden" class="form-control" id="NEWS_CATEGORY" name="news_categories_form_id"/>'));
    $parentContainer.append($container);

    $("input#NEWS_CATEGORY").attr('data-field-value', $("input#NEWS_CATEGORY").val());


    var categories = [];

    if (id) {
        $("input#NEWS_CATEGORY").val(id);
        categories.push($("input#NEWS_CATEGORY").val());
    }


    RameraListEditorModule.init(
        $container.find("#newsCategoriesForm"),
        {
            labelClasses: ["checkbox-inline"],
            labelStyle: "margin-left: 10px;",
            selectedItems: categories,
            selectClasses: ["form-control"]
        },
        function(event, data) {
            if (event == RameraListEditorEvents.VALUE_CHANGED) {
                $("input#NEWS_CATEGORY").val(data.value);
            }
        }
    );


    this.getCategoryId = function () {
        return $("input#NEWS_CATEGORY").val();
    };

    this.validate = function() {
        if (!self.getCategoryId()) {
            bootbox.alert("Укажите категорию новости!");
            return false;
        }

        return true;
    };

    this.close = function() {
        $("body").trigger('click');
    };

    this.destroy = function() {
        $container.remove();
    };

}