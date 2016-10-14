/**
 * Created by aotts on 27.10.2015.
 * Редактор для выбора сигнала на который надо реагировать
 */
define([
    "Backbone.ModelBinder",
    "./ChoiceEditor"
], function (ModelBinder, ChoiceEditor) {
    return ChoiceEditor.extend({
        getOptions: function () {
            var signalDefinitionsProperty;
            var parent = this.model.get("shape");
            while (parent) {
                if (parent.properties && parent.properties['oryx-signaldefinitions']) {
                    signalDefinitionsProperty = parent.properties['oryx-signaldefinitions'];
                    break;
                } else {
                    parent = parent.parent;
                }
            }

            if(typeof signalDefinitionsProperty === "string") {
                try {
                    signalDefinitionsProperty = JSON.parse(signalDefinitionsProperty);
                    if (typeof signalDefinitionsProperty == 'string') {
                        signalDefinitionsProperty = JSON.parse(signalDefinitionsProperty);
                    }
                } catch (err) {
                }
            }

            return _.map(signalDefinitionsProperty, function (signal) {
                return {
                    value: signal.id || signal.name,
                    text: signal.name || signal.id
                }
            });
        }
    });
});