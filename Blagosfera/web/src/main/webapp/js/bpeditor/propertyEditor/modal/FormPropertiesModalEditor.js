/**
 * Created by aotts on 26.10.2015.
 * Редактор входных параметров посылаемых в вызов процесса
 */
define([
    "utils/utils",
    "Backbone.CollectionBinder",
    "./tableRow/TableRowView",
    "./SingleTableClassFactory",
    "underscore"
], function (utils, CollectionBinder, TableRowView, SingleTableClassFactory, _) {
    var enumTemplate = _.template('' +
        '<div class="list-group enums" style="min-width: 125px;">' +
            '<% _.each(data, function(row) { %>' +
            '<div class="list-group-item" style="padding: 3px 10px">' +
                '<div class="row">' +
                    '<div class="col-md-9" style="padding-right: 0; padding-left: 10px">' +
                        '<input type="text" class="form-control" value="<%-row.value%>"/>' +
                    '</div>' +
                    '<div class="col-md-3"><a href="#" class="minus-enum"><i class="glyphicon glyphicon-minus"></i></a></div>' +
                '</div>' +
            '</div>' +
            '<% }); %>' +
            '<div class="list-group-item">' +
                '<div class="row">' +
                    '<div class="col-md-9" style="padding-right: 0; padding-left: 10px">' +
                    'Значение' +
                    '</div>' +
                    '<div class="col-md-3"><a href="#" class="plus-enum"><i class="glyphicon glyphicon-plus"></i></a></div>' +
                '</div>' +
            '</div>' +
        '</div>',
        {variable: "data"}
    );

    var FromPropertyRow = TableRowView.extend({
        events: _.extend({
            "click .enums .plus-enum": utils.clickEventWrapper("addEnum"),
            "click .enums .minus-enum": utils.clickEventWrapper("removeEnum"),
            "change .enums input": "updateEnumValue",
            "input .enums input": "updateEnumValue"
        }, TableRowView.prototype.events),

        updateEnumValue: function (e) {
            var $input = this.$(e.target);
            var value = $input.val() || "";
            var index = $input.parentsUntil(".list-group", ".list-group-item").index()
            var values = this.model.get("enumValues");
            values[index].value = value;
        },

        removeEnum: function (e) {
            var index = $(e.target).parentsUntil(".list-group", ".list-group-item").index();
            var values = this.model.get("enumValues");
            this.model.set("enumValues", _.filter(values, function(val, i) {
                return i !== index;
            }));
        },

        addEnum: function () {
            var values = _.clone(this.model.get("enumValues") || []);
            values.push({value: ""});
            this.model.set("enumValues", values);
        }
    });


    return SingleTableClassFactory({
        attributeName: "formProperties",
        title: "Параметры формы",
        header: ["Обязательное", "Редактируемое", "Видимое", "ID", "Название", "Выражение", "Переменная", "Тип значения", "Дополнительно", ""],
        rowObjectFactory: function (i) {
            return {
                required: false,
                writable: true,
                readable: true,
                id: "id" + i,
                name: "",
                expression: "",
                variable: "",
                type: "string",
                datePattern: "MM-dd-yyyy hh:mm",
                enumValues: []
            }
        },
        rowFactoryProvider: function () {
            return new CollectionBinder.ViewManagerFactory(function (model) {
                return new FromPropertyRow({
                    model: model,
                    rowConfig: [
                        TableRowView.templates.input("required", {type: "checkbox", style: "width: 34px"}),
                        TableRowView.templates.input("writable", {type: "checkbox", style: "width: 34px"}),
                        TableRowView.templates.input("readable", {type: "checkbox", style: "width: 34px"}),
                        TableRowView.templates.input("id"),
                        TableRowView.templates.input("name"),
                        TableRowView.templates.input("expression"),
                        TableRowView.templates.input("variable"),
                        TableRowView.templates.select("type", [
                            {value: "string", text: "Строка"},
                            {value: "long", text: "Число"},
                            {value: "boolean", text: "Логическое"},
                            {value: "date", text: "Дата"},
                            {value: "enum", text: "Перечисление"}
                        ]),
                        function (data) {
                            if(data.type === "date") {
                                return TableRowView.templates.input("datePattern");
                            } else if(data.type === "enum") {
                                return enumTemplate(data.enumValues || []);
                            }
                            return "";
                        },
                        TableRowView.templates.minusBtn()
                    ]
                });
            });
        },
        toModelValueConverter: function (model) {
            var type = model.get("type");
            if(type == "date") {
                return model.omit("enumValues");
            } else if(type == "enum") {
                var value = model.omit("datePattern");
                value.enumValues = _.chain(value.enumValues).filter(function (val) {
                    return val && val.value && val.value.length > 0;
                }).uniq(false, function (val) {
                    return val.value;
                }).value();
                return value;
            } else {
                return model.omit("datePattern", "enumValues");
            }
        }
    });
});