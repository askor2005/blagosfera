/**
 * Created by aotts on 24.11.2015.
 * Редактор свойств типа список
 */
define([
    "Backbone.CollectionBinder",
    "./tableRow/TableRowView",
    "./SingleTableClassFactory"
], function (CollectionBinder, TableRowView, SingleTableClassFactory) {
    return SingleTableClassFactory({
        title: function() {
            return this.model.get("title");
        },
        header: ["", ""],
        dialogWidth: "30%",
        rowObjectFactory: function () {
            return {value: ""};
        },
        rowFactoryProvider: function () {
            return new CollectionBinder.ViewManagerFactory(function (model) {
                return new TableRowView({
                    model: model,
                    testOnChange: false,
                    rowConfig: [
                        TableRowView.templates.input("value"),
                        TableRowView.templates.minusBtn()
                    ]
                });
            });
        },
        fromModelValueConverter: function (value) {
            return {value: value};
        },
        setter: function (model, value) {
            var values = _.chain(value)
                .map(function (model) { return model.get("value"); })
                .filter(function (value) { return value && value.length; })
                .uniq()
                .value();

            model.set("value", values.length === 0 ? null : values);
        }
    });
});