/**
 * Created by aotts on 23.10.2015.
 * Представление с ModelDinder'ом
 */
define([
    "backbone",
    "Backbone.ModelBinder"
], function (Backbone, ModelBinder) {
    return Backbone.View.extend({
        modelBinder: null,
        /**
         * бинды для ModelBinder'а
         */
        bindings: {},

        /**
         * настройки для ModelBinder'а
         */
        bindingOptions: {},

        initialize: function () {
            this.modelBinder = new ModelBinder();
        },

        remove: function () {
            this.modelBinder.unbind();
            Backbone.View.prototype.remove.call(this);
        },

        /**
         * Добавить связывание между моделью и представлением
         */
        applyBindings: function () {
            var binding = _.result(this, "bindings");
            var bindingOptions = _.result(this, "bindingOptions");
            if(typeof binding === "string") {
                binding = ModelBinder.createDefaultBindings(this.el, binding);
            }
            this.modelBinder.bind(this.model, this.el, binding, bindingOptions);
        }
    });
});