/**
 * Created by aotts on 21.10.2015.
 * Основа для редакторов сложных полей, использующих модальные окна
 */
define([
    "../AbstractPropertyEditor",
    "jquery"
], function (AbstractPropertyEditor, $) {
    return AbstractPropertyEditor.extend({
        /**
         * Класс модального диалога, который будет использоваться для редактирования
         */
        modalClass: null,
        /**
         * Экземпляр класса {@link #modalClass}
         */
        modalInstance: null,

        remove: function () {
            if(this.modalInstance != null) {
                this.modalInstance.remove();
            }
            AbstractPropertyEditor.prototype.remove.call(this);
        },
        
        showModal: function () {
            if(this.modalInstance == null) {
                this.modalInstance = new this.modalClass({model: this.model});
                this.modalInstance.render().$el.appendTo($("body"));
            }
            this.modalInstance.$el.modal("show");
        },

        hideModal: function () {
            if(this.modalInstance != null) {
                this.modalInstance.$el.modal("hide");
            }
        }
    });
});