/**
 * Фильтр дат публикаций новостей
 */

/**
 * Конструктор
 * @param $parent родительский контейнер
 * @param options опции (значения initDateFrom и initDateTo)
 * @constructor
 */
function NewsFilterDate($parent, options) {

    var self = this;

    var initDateFrom = null;
    var initDateTo = null;

    if (options && options.initDateFrom) {
        initDateFrom = options.initDateFrom;
    }

    if (options && options.initDateTo) {
        initDateTo = options.initDateTo;
    }

    var $container = $('<div><div style="width: 100%;text-align: center;"><div id="dateBlockToAdd" style="display: inline-block;"> </div></div></div>');
    $parent.append($container);

    $container.prepend($('<label style="display: block;">Дата</label>'));
    var $dateFrom = $('<input type="text" style="width: '+options.width+'px; text-align: center; display: inline-block; margin-right: 10px;" class="form-control" />');
    $container.find("#dateBlockToAdd").append($dateFrom);
    var $dateTo = $('<input type="text" style="width: '+options.width+'px; text-align: center; display: inline-block;" class="form-control" />');
    $container.find("#dateBlockToAdd").append($dateTo);

    //Создаем dateTimeInput'ы
    $dateFrom.radomDateTimeInput({
            format: "DD.MM.YYYY",
            defaultDate: initDateFrom
        });

    $dateTo.radomDateTimeInput({
            format: "DD.MM.YYYY",
            defaultDate: initDateTo
        });


    /**
     * Позволяет получить начальную дату создания новости для фильтра
     * @returns {*}
     */
    this.getDateFrom = function() {

        var dateFrom = $dateFrom.data("DateTimePicker").date();
        //Если дата задана, то возвращаем ее
        if (dateFrom) {
            return dateFrom.toDate().getTime();
        }

        //иначе - null
        return null;
    };

    /**
     * Позволяет получить конечную дату создания новости для фильтра
     * @returns {*}
     */
    this.getDateTo = function() {

        var dateTo = $dateTo.data("DateTimePicker").date();

        //Если дата задана, то возвращаем ее
        if (dateTo) {
            return dateTo.toDate().getTime();
        }

        //иначе - null
        return null;
    };

    this.clear = function() {
        $dateFrom.val("");
        $dateTo.val("");
    };

    this.destroy = function() {
        self = null;
        $container.remove();
    };
};
