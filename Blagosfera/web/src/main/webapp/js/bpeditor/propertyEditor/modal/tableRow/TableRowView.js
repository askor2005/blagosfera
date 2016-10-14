/**
 * Created by aotts on 26.10.2015.
 * Строка в таблице
 */
define([
    "backbone/components/ViewWithModelBinder"
], function (ViewWithModelBinder) {

    var attrPairToString = function (val, key) {
        return key + '="' + _.escape(val) + '"';
    };

    var input = function (name, attrs) {
        attrs = _.extend({type: "text", "class": "form-control", name: name}, attrs);
        return "<input " + _.map(attrs, attrPairToString).join(" ") + "/>";
    };

    var textarea = function (name, attrs) {
        attrs = _.extend({"class": "form-control", name: name, style: "resize: vertical; height: 34px;"}, attrs);
        return "<textarea " + _.map(attrs, attrPairToString).join(" ") + "></textarea>";
    };

    var select = function (name, options, attrs, optionAttrs) {
        attrs = _.extend({"class": "form-control", name: name}, attrs);
        return  "<select " + _.map(attrs, attrPairToString).join(" ") + ">" +
                    _.map(options || [], function (option) {
                        attrs = _.extend({}, optionAttrs, {value: option.value});
                        return "<option " + _.map(attrs, attrPairToString).join(" ") + ">" + option.text + "</option>";
                    }) +
                "</select>";
    };

    var minusBtn = function () {
        return '<a href="#" class="btn btn-danger btn-sm minusBtn">' +
            '       <span class="glyphicon glyphicon-minus"></span>' +
            '   </a>';
    };

    var link = function(className, text) {
        return "<a href='javascript:void(0);' class='" + className + "' >" + text + "</a>";
    };

    var TableRowView = ViewWithModelBinder.extend({
        tagName: "tr",
        bindings: "name",

        rowConfig: [],

        _currentTemplates: null,

        testOnChange: true,

        initialize: function (options) {
            ViewWithModelBinder.prototype.initialize.apply(this, arguments);
            _.extend(this, _.pick(options, "rowConfig", "testOnChange"));

        },

        render: function () {
            var html = (this._currentTemplates || (this._currentTemplates = this.extractTemplates())).join("</td><td>");
            if(html) {
                html = "<td>" + html + "</td>";
            }
            this.$el.html(html);
            if(this.testOnChange) {
                this.stopListening(this.model, "change", this.testAndRenderIfRequired);
            }
            this.applyBindings();
            if(this.testOnChange) {
                this.listenTo(this.model, "change", this.testAndRenderIfRequired);
            }
            return this;
        },

        testAndRenderIfRequired: function () {
            var nowTemplates = this.extractTemplates();

            var correct = _.isEqual(this._currentTemplates, nowTemplates);

            if(!correct) {
                this._currentTemplates = nowTemplates;
                this.render();
            }
            return this;
        },

        extractTemplates: function () {
            var data = this.getTemplateParams();
            return _.map(this.rowConfig || [], function (config) {
                if(typeof config === "function") {
                    return config.call(this, data);
                }
                return config;
            });
        },

        getTemplateParams: function () {
            if(!this.model) {
                return {};
            }
            return this.model.toJSON();
        }


    });

    TableRowView.templates = {
        input: input,
        textarea: textarea,
        select: select,
        minusBtn: minusBtn,
        link: link
    };

    return TableRowView;
});