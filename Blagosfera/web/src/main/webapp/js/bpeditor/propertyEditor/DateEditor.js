/**
 * Created by vgusev on 10.12.2015.
 * Редактор для поля с датой
 */
define([
    "./ValidateTextEditor"
], function (ValidateTextEditor) {
    return ValidateTextEditor.extend({
        inputType: "date",

        validateNonEmpty: function (val, property, attrs) {
        },

        render: function () {
            ValidateTextEditor.prototype.render.call(this);
            return this;
        },
    });
});