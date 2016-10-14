/**
 * Created by aotts on 21.10.2015.
 * Редактор строкового поля с валидацией
 */
define([
    "./AbstractPropertyEditor",
    "text!./template/ValidateTextEditor.html"
], function (AbstractPropertyEditor, template) {
    return AbstractPropertyEditor.extend({
        template: _.template(template, {variable: "data"}),
        inputType: "text",

        bindings: {
            "value": ".value-keeper"
        },

        initialize: function() {
            AbstractPropertyEditor.prototype.initialize.call(this);
            var originalValidate = this.model.validate;
            this.model.validate = _.bind(function (attrs, options) {
                var valid = originalValidate ? originalValidate.call(this.model, attrs, options) : true;
                return valid && this.validate(attrs);
            }, this);
            this.listenTo(this.model, "invalid", function(model, error) {
                this.makeInvalid(error);
            });
            this.listenTo(this.model, "change:value", this.makeValid);
        },

        render: function () {
            AbstractPropertyEditor.prototype.render.call(this);
            this.model.isValid();
            return this;
        },

        /**
         * Показать что свойство не валидно
         * @param error
         */
        makeInvalid: function (error) {
            if(!this._$invalidElem) {
                this._$invalidElem = $("<span>", {
                    "class": "glyphicon glyphicon-warning-sign",
                    title: error,
                    "data-toggle": "tooltip",
                    "aria-hidden": true
                });
                this._$invalidElem.tooltip({delay: 350});
                this.$(".extra-info").prepend(this._$invalidElem);
                this.$el.addClass("panel-warning");
            } else {
                if(this._$invalidElem.attr("title") !== error) {
                    this._$invalidElem.tooltip('destroy');
                    this._$invalidElem.attr("title", error);
                    this._$invalidElem.tooltip({delay: 350});
                }
            }
        },

        /**
         * Убрать пометку о не валидности
         */
        makeValid: function () {
            if(this._$invalidElem) {
                this._$invalidElem.tooltip('destroy');
                this._$invalidElem.remove();
                this.$el.removeClass("panel-warning");
                this._$invalidElem = null;
            }
        },

        validate: function (attrs) {
            var property = this.model.get("property");
            var val = attrs.value;
            var len = val && val.length || 0;
            if(!property.optional() && len === 0) {
                return "Значение не может быть пустым";
            }
            if(len !== 0) {
                return this.validateNonEmpty(val, property, attrs);
            }
            return false;
        },

        validateNonEmpty: function (val, property, attrs) {
            var maxLength = property.length();
            if(maxLength < val.length) {
                return "Строка должна быть не длинее " + maxLength + " символов";
            }
        },

        getTemplateParams: function () {
            return _.extend(
                {inputType: this.inputType},
                AbstractPropertyEditor.prototype.getTemplateParams.call(this)
            );
        }
    });
});