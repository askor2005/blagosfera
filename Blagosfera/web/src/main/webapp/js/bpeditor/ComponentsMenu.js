define([
    "utils/utils",
    "text!./template/ComponentsMenuGroup.html",
    "text!./template/ComponentsMenuToggleGroup.html",
    "backbone"
], function (utils, groupTemplate, toggleGroupTemplate) {
    var $ = jQuery;

    var ComponentsMenu = Backbone.View.extend({
        tagName: "div",
        events: {
            "click li": utils.clickEventWrapper("elementClicked")
        },

        className: "panel-group",

        url: "/admin/bpeditor/stencil/set",

        stencilSet: null,

        template: _.template(groupTemplate),

        toggleTemplate: _.template(toggleGroupTemplate),

        stencilByGroup: null,

        stencilsWithoutGroups: null,

        ignoreStencil: function (item) {
            return item.type === "edge" || item.id === "BPMNDiagram";
        },

        setStencils: function (stencils) {
            this.stencils = stencils;
            var byGroup = this.stencilByGroup = {};
            this.stencilsWithoutGroups = [];
            for (var i = 0; i < stencils.length; i++) {
                var item = stencils[i];
                if(this.ignoreStencil(item)) {
                    continue;
                }
                var groups = item.groups;
                if(typeof groups === "string") {
                    groups = groups.split(",");
                }
                var length = groups.length;
                if(length > 0) {
                    for (var j = 0; j < length; j++) {
                        var group = groups[j].trim();
                        var arr = byGroup[group];
                        if(!arr) {
                            arr = [];
                            byGroup[group] = arr;
                        }
                        arr.push(item);
                    }
                } else {
                    this.stencilsWithoutGroups.push(item);
                }
            }
            return this;
        },

        renderStencils: function () {
            var html = "";
            _.each(this.stencilByGroup, function (items, group) {
                html += this.toggleTemplate({
                    items: items,
                    content: this.template,
                    group: {
                        id: _.uniqueId("stencil-set-group-"),
                        title: group
                    }
                });
            }, this);
            _.each(this.stencilsWithoutGroups, function (item) {
                html += this.template({
                    items: items
                });
            }, this);
            this.$el.html(html);
            this.$(".stencil-item").draggable({
                appendTo: "body",
                helper: "clone"
            });
            return this;
        },

        fetchStencils: function (success, error) {
            $.ajax(this.url, {
                method: "GET",
                context: this,
                dataType: "json",
                success: function (data) {
                    this.setStencils(data && data.stencils || data);
                    if(typeof success === "function") {
                        success.apply(this, [].slice.call(arguments, 0));
                    }
                },
                error: error
            });
        },

        render: function () {
            if(this.stencils) {
                return this.renderStencils();
            }
            this.fetchStencils(function (data) {
                this.renderStencils();
            });
            return this;
        },

        elementClicked: function (e) {
            var $li = $(e.target);
            if(!$li.is("li")) {
                $li = $li.parents("li").first()
            }
            var stencilId = $li.attr("stencil-id");
            var stencil = _.find(this.stencils, function (s) {
                return s.id === stencilId;
            });
            this.trigger("stencil.clicked", stencil);
        }
    });

    return ComponentsMenu;
});
