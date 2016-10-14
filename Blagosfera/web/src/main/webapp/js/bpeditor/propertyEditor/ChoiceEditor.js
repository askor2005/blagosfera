/**
 * Created by aotts on 27.10.2015.
 * Редактор для выбора нужной опции
 */
define([
    "./AbstractPropertyEditor",
    "text!./template/ChoiceEditor.html"
], function (AbstractPropertyEditor, template) {
    return AbstractPropertyEditor.extend({
        template: _.template(template, {variable: "data"}),
        bindings: {
            "value": "select"
        },

        getOptions: function () {
            var prop = this.model.get("property");
            return prop._jsonProp.items || [];
        },

        getTemplateParams: function () {
            return _.extend({_options: this.getOptions(), cid: this.cid}, this.model.omit("property", "shape"));
        }
    });
});