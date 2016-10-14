/**
 * Created by aotts on 21.10.2015.
 * Суперкласс для редакторов свойств
 */
define([
    "backbone/components/ViewWithModelBinder"
], function (ViewWithModelBinder) {
    return ViewWithModelBinder.extend({
        tagName: "div",
        className: "panel panel-default",
        model: null,
        template: null,
        modelBinder: null,
        /**
         * бинды для ModelBinder'а
         */
        bindings: {},

        /**
         * настройки для ModelBinder'а
         */
        bindingOptions: {
            modelSetOptions: {validate: true}
        },

        remove: function () {
            this.$('[data-toggle="tooltip"]').tooltip('destroy');
            ViewWithModelBinder.prototype.remove.call(this);
        },


        render: function () {
            this.applyTemplate();
            this.$('[data-toggle="tooltip"]').tooltip({
                delay: 350
            });
            this.applyBindings();
            return this;
        },

        /**
         * Применить шаблон во время отрисовки
         */
        applyTemplate: function () {
            this.$el.html(this.template(this.getTemplateParams()));
        },

        /**
         * Параметры для шаблона
         */
        getTemplateParams: function () {
            return _.extend({cid: this.model.cid}, this.model.omit("property", "shape"));
        }

    });
});