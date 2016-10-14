/**
 * Created by aotts on 21.10.2015.
 * Диалог для преобразования компонента
 */
define([
    "./commands/MorphToCommand",
    "backbone",
    "text!./template/MorphDialog.html"
], function (MorphToCommand, Backbone, template) {
    return Backbone.View.extend({
        tagName: "div",
        className: "modal",
        template: _.template(template, {variable: 'items'}),

        events: {
            "click .accept":    "save",
            "click li":         "choice"
        },

        /**
         * Экземпляр ORYX.Editor, в котором хотим изменить компонент
         */
        editor: null,

        /**
         * Возможные альтернативы. [ORYX.Stencil]
         */
        items: null,

        /**
         * Компонент, который трансформируем
         */
        shape: null,

        /**
         * Выбор, который сделал пользователь
         */
        selectedOption: null,

        render: function () {
            if(this.editor != null) {
                this.$el.html(this.template(_.map(this.items, function (item) {
                    return {
                        id: item.idWithoutNs(),
                        title: item.title(),
                        icon: "/img/bpeditor/" + item.icon()
                    };
                })));
                this.$el.appendTo("body");
            }
            return this;
        },

        show: function (shape, editor) {
            var stencil = shape.getStencil();
            this.editor = editor;

            var stencilSet = this.editor.getStencilSets()[stencil.namespace()];
            var rules = stencilSet.jsonRules().morphingRules;
            var roles = _.map(stencil.roles(), function (role) {
                return role.replace(stencil.namespace(), "");
            });
            var morphRole = null;
            for (var i = 0; i < rules.length; i++) {
                var rule = rules[i];
                if(roles.indexOf(rule.role) !== -1) {
                    morphRole = stencil.namespace() + rule.role;
                    break;
                }
            }
            if(morphRole === null) {
                this.clear();
                return;
            }
            var items = _.filter(stencilSet._stencils.values(), function (item) {
                return item.id() !== stencil.id() && item.roles().indexOf(morphRole) !== -1;
            });

            if(items.length === 0) {
                this.clear();
                return;
            }

            this.items = items;
            this.shape = shape;
            this.render();
            this.$(".accept").prop("disabled", true);
            this.$el.modal("show");
        },

        clear: function () {
            this.editor = null;
            this.items = null;
            this.shape = null;
            this.selectedOption = null;
        },

        save: function () {
            if(this.selectedOption == null) {
                return;
            }

            var stencil = _.find(this.items, function (item) {
                return item.idWithoutNs() === this.selectedOption;
            }, this);

            if(stencil) {
                this.editor.executeCommands([
                    new MorphToCommand(this.shape, stencil, this.editor)
                ]);
            }

            this.clear();
            this.$el.modal("hide");
        },

        choice: function (event) {
            var $current = jQuery(event.currentTarget);
            this.selectedOption = $current.attr("stencil-id");
            this.$("li.active").removeClass("active");
            $current.addClass("active");
            this.$(".accept").prop("disabled", false);
        }
    });
});