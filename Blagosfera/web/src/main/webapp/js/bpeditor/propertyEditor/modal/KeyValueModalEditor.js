/**
 * Created by aotts on 27.10.2015.
 * Редактор свойств типа ключ-значение
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
        header: ["", "", ""],
        rowObjectFactory: function () {
            return {key: "", value: ""}
        },
        rowFactoryProvider: function () {
            return new CollectionBinder.ViewManagerFactory(function (model) {
                return new TableRowView({
                    model: model,
                    testOnChange: false,
                    rowConfig: [
                        TableRowView.templates.input("key"),
                        TableRowView.templates.input("value"),
                        TableRowView.templates.minusBtn()
                    ]
                });
            });
        },
        fromModelValueConverter: function (value, key) {
            return {key: key, value: value};
        },
        setter: function (model, value) {
            var res = {};
            for (var i = 0; i < value.length; i++) {
                var v = value[i].toJSON();
                if(v.key) {
                    res[v.key] = v.value || "";
                }
            }
            if(_.size(res) === 0) {
                res = null;
            }
            model.set("value", res);
        }
    });
});