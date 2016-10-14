/**
 * Created by aotts on 21.10.2015.
 * Редактор для выбора типа паралельного исполнения задания
 */
define([
    "Backbone.ModelBinder",
    "./ChoiceEditor"
], function (ModelBinder, ChoiceEditor) {
    return ChoiceEditor.extend({
        bindings: {
            "value": {
                selector: "select",
                converter: function (direction, value) {
                    if(ModelBinder.Constants.ModelToView === direction) {
                        return value || "None";
                    } else {
                        return value;
                    }
                }
            }
        },

        getOptions: function () {
            return [
                { value: "None", text: "Отсутсвует" },
                { value: "Parallel", text: "Паралельно" },
                { value: "Sequential", text: "Последовательно" }
            ]
        }
    });
});