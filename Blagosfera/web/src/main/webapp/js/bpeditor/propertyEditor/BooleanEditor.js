/**
 * Created by aotts on 21.10.2015.
 * Редактор для логического поля
 */
define([
    "./AbstractPropertyEditor",
    "text!./template/BooleanEditor.html"
], function (AbstractPropertyEditor, template) {
    return AbstractPropertyEditor.extend({
        template: _.template(template, {variable: "data"}),

        bindings: {
            "value": "input"
        }
    });
});