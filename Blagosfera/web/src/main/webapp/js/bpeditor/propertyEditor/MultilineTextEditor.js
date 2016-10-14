/**
 * Created by aotts on 21.10.2015.
 * Редактор для многострочного текста
 */
define([
    "./ValidateTextEditor",
    "text!./template/MultilineTextEditor.html"
], function (ValidateTextEditor, template) {
    return ValidateTextEditor.extend({
        template: _.template(template, {variable: "data"})
    });
});