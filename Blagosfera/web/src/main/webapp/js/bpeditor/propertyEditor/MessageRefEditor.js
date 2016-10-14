/**
 * Created by aotts on 27.10.2015.
 * Редактор для выбора сообщения на который надо реагировать
 */
define([
    "Backbone.ModelBinder",
    "./ChoiceEditor"
], function (ModelBinder, ChoiceEditor) {
    return ChoiceEditor.extend({
        getOptions: function () {
            var messageDefinitionsProperty;
            var parent = this.model.get("shape");
            while (parent) {
                if (parent.properties && parent.properties['oryx-messagedefinitions']) {
                    messageDefinitionsProperty = parent.properties['oryx-messagedefinitions'];
                    break;
                } else {
                    parent = parent.parent;
                }
            }
            
            if(typeof messageDefinitionsProperty === "string") {
                try {
                    messageDefinitionsProperty = JSON.parse(messageDefinitionsProperty);
                    if (typeof messageDefinitionsProperty == 'string') {
                        messageDefinitionsProperty = JSON.parse(messageDefinitionsProperty);
                    }
                } catch (err) {
                }
            }

            return _.map(messageDefinitionsProperty, function (signal) {
                return {
                    value: signal.id || signal.name,
                    text: signal.name || signal.id
                }
            });
        }
    });
});