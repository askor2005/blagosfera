/**
 * Created by aotts on 23.10.2015.
 * Редактор входных сигналов процесса
 */
define([
    "utils/utils",
    "./ModalBasedEditor",
    "text!./template/MulticomplexValueModalEditor.html"
], function (utils, ModalBasedEditor, template) {
    return ModalBasedEditor.extend({
        template: _.template(template, {variable: "data"}),

        events: {
            "click .edit": utils.clickEventWrapper("showModal")
        }
    });
});