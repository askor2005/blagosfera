/**
 * Created by aotts on 21.10.2015.
 * Редактор простого строкового поля
 */
define([
    "./ValidateTextEditor"
], function (ValidateTextEditor) {
    return ValidateTextEditor.extend({
        inputType: "number",

        validateNonEmpty: function (val, property, attrs) {
            val = val.replace(/\s+/, "").replace(",", ".");
            var num = Number(val);
            if(isNaN(num)) {
                return "Неверный числовой формат"
            }
            var type = property.type();
            if(type === "integer" && val.indexOf(".") !== -1) {
                return "Значение должно быть целочисленным"
            }
            var min = property.min();
            if(min > num) {
                return "Значение должно быть не меньше " + min;
            }
            var max = property.max();
            if(max < num) {
                return "Значение должно быть не больше " + min;
            }
        }
    });
});