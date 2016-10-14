/**
 * Created by aotts on 27.10.2015.
 * Редактор параметров для класса который выполняет задание
 */
define([
    "Backbone.CollectionBinder",
    "./tableRow/TableRowView",
    "./SingleTableClassFactory"
], function (CollectionBinder, TableRowView, SingleTableClassFactory) {
    return SingleTableClassFactory({
        attributeName: "fields",
        title: "Свойтва класса",
        header: ["Название", "Значение", "Тип значения", ""],
        rowObjectFactory: function () {
            return {name: "", value: "", type: "stringValue"}
        },
        rowFactoryProvider: function () {
            return new CollectionBinder.ViewManagerFactory(function (model) {
                return new TableRowView({
                    model: model,
                    rowConfig: [
                        TableRowView.templates.input("name"),
                        function (data) {
                            if(data.type === "string") {
                                return TableRowView.templates.textarea("value");
                            }
                            return TableRowView.templates.input("value");
                        },
                        TableRowView.templates.select("type", [
                            {value: "stringValue", text: "Строка"},
                            {value: "expression", text: "Выражение"},
                            {value: "string", text: "Текст"}
                        ]),
                        TableRowView.templates.minusBtn()
                    ]
                });
            });
        },
        fromModelValueConverter: function (value) {
            var type;
            var val;
            if(val = value.stringValue) {
                type = "stringValue"
            } else if(val = value.string) {
                type = "string";
            } else {
                val = value.expression;
                type = "expression"
            }
            return {
                name: value.field || "",
                type: type,
                value: val || ""
            };
        },
        toModelValueConverter: function (model) {
            var res = {
                field: model.get("name") || "",
                stringValue: "",
                expression: "",
                string: ""
            };
            res[model.get("type")] = model.get("value");
            return res;
        }
    });
});