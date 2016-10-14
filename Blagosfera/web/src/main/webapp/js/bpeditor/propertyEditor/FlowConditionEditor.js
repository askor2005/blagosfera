/**
 * Created by aotts on 21.10.2015.
 * Редактор для условия прохождения по "стрелке"
 */
define([
    "Backbone.ModelBinder",
    "./MultilineTextEditor"
], function (ModelBinder, MultilineTextEditor) {
    return MultilineTextEditor.extend({
        bindings: {
            "value": {
                selector: ".value-keeper",
                converter: function (direction, value) {
                    if(ModelBinder.Constants.ModelToView === direction) {
                        return value || "";
                    } else {
                        if(value) {
                            return value;
                        }
                        return null;
                    }
                }
            }
        }
    });
});