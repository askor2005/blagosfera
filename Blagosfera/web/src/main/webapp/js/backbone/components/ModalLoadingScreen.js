/**
 * Created by aotts on 30.10.2015.
 * Представление, которое отображает модальное окно загрузки, но так чтобы не было мерцаний для юзера
 */
define([
    "jquery",
    "backbone",
    "underscore"
], function ($, Backbone) {
    return Backbone.View.extend({
        tagName: "div",
        className: "modal",
        model: {
            text: "Действие в процессе..."
        },

        template: _.template('' +
            '<div class="modal-dialog modal-sm">' +
            '   <div class="modal-content">' +
            '       <div class="modal-body">' +
            '           <%=data.text%>' +
            '       </div>' +
            '   </div>' +
            '</div>',
            {variable: "data"}
        ),

        _timeout: null,
        canBeClosed: false,
        shouldBeClosed: false,

        /**
         * Задержка удобная для восприятия человеком
         */
        humanAcceptableDelay: 750,

        show: function () {
            this.shouldBeClosed = false;
            if(!this._timeout) {
                this.canBeClosed = true;
                this._timeout = _.delay(_.bind(this._showModal, this), this.humanAcceptableDelay);
            }
            return this;
        },

        _showModal: function () {
            this._timeout = null;
            this.canBeClosed = false;
            _.delay(_.bind(function () {
                this.canBeClosed = true;
                if(this.shouldBeClosed) {
                    this._close();
                }
            }, this), this.humanAcceptableDelay);
            this.$el.modal({
                backdrop: "static",
                keyboard: false,
                show: true
            });
        },

        hide: function () {
             if(!this.canBeClosed) {
                this.shouldBeClosed = true;
            } else {
                this._close();
            }
            return this;
        },

        _close: function () {
            this.shouldBeClosed = false;
            this.canBeClosed = false;
            if(this._timeout) {
                clearTimeout(this._timeout);
                this._timeout = null;
            }
            this.$el.modal("hide");
        },

        render: function () {
            var data;
            if(this.model) {
                if(this.model instanceof Backbone.Model) {
                    data = this.model.toJSON();
                } else {
                    data = this.model;
                }
            } else {
                data = {};
            }
            this.$el.html(this.template(data));
            return this;
        },

        remove: function () {
            if(this.shouldBeClosed) {
                _.delay(_.bind(this.remove, this), 10);
            } else {
                this._close();
                Backbone.View.prototype.remove.call(this);
            }
        }
    });
});