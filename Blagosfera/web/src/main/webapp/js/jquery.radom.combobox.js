(function ($) {
    $.widget("radom.radomCombobox", {
        options: {
            inputPlaceholder: "",
            buttonTooltip: "Show all items",
            itemRenderFunction: false,
            onSelect: false,
            clearOnSelect: false
        },

        _create: function () {
            this.wrapper = $("<span>")
                .addClass("radom-combobox")
                .insertAfter(this.element);

            this.element.hide();
            this._createAutocomplete();
            this._createShowAllButton();
        },

        _createAutocomplete: function () {
            var selected = this.element.children(":selected");
            var value = selected.val() ? selected.text() : "";

            var autoCompleteOptions = {
                delay: 0,
                minLength: 0,
                source: $.proxy(this, "_source")
            };

            var self = this;

            autoCompleteOptions.select = function (event, ui) {
                if (self.options.onSelect) self.options.onSelect(event, ui);
                if (self.options.clearOnSelect) self.clear();
                self._sourceData = [];
            };
            if (self.options.inputWidth) {
                this.input = $("<input style='width: " + self.options.inputWidth + "px;' placeholder='" + this.options.inputPlaceholder + "'>")
                    .appendTo(this.wrapper)
                    .val(value)
                    .attr("title", "")
                    .addClass("radom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left")
                    .tooltip({
                        tooltipClass: "ui-state-highlight"
                    });
            }
            else {
                this.input = $("<input placeholder='" + this.options.inputPlaceholder + "'>")
                    .appendTo(this.wrapper)
                    .val(value)
                    .attr("title", "")
                    .addClass("radom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left")
                    .tooltip({
                        tooltipClass: "ui-state-highlight"
                    });
            }
            var autocomplete = this.input.autocomplete(autoCompleteOptions);
            if (this.options.itemRenderFunction) autocomplete.data("ui-autocomplete")._renderItem = this.options.itemRenderFunction;

            this._on(this.input, {
                autocompleteselect: function (event, ui) {
                    ui.item.option.selected = true;
                    this._trigger("select", event, {
                        item: ui.item.option
                    });
                },

                autocompletechange: "_removeIfInvalid"
            });
        },

        _createShowAllButton: function () {
            var input = this.input;
            var wasOpen = false;

            $("<a>")
                .attr("tabIndex", -1)
                .attr("title", this.options.buttonTooltip)
                .tooltip()
                .appendTo(this.wrapper)
                .button({
                    icons: {
                        primary: "ui-icon-triangle-1-s"
                    },
                    text: false
                })
                .removeClass("ui-corner-all")
                .addClass("radom-combobox-toggle ui-corner-right")
                .mousedown(function () {
                    wasOpen = input.autocomplete("widget").is(":visible");
                })
                .click(function () {
                    input.focus();

                    // Close if already visible
                    if (wasOpen) {
                        return;
                    }

                    // Pass empty string as value to search for, displaying all results
                    input.autocomplete("search", "");
                });
        },

        _sourceData: [],

        _source: function (request, response) {
            if (!request.term) this._sourceData = [];

            if (this._sourceData.length == 0) {
                this._sourceData = this.element.children("option").map(function () {
                    var text = $(this).text();

                    return {
                        label: text,
                        value: text,
                        option: this
                    };
                });
            }

            var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");

            var result = $.map(this._sourceData, function (item) {
                if (!request.term || matcher.test(item.value)) {
                    return item;
                }
            });

            response(result);
        },

        _removeIfInvalid: function (event, ui) {
            // Selected an item, nothing to do
            if (ui.item) {
                return;
            }

            // Search for a match (case-insensitive)
            var value = this.input.val();
            var valueLowerCase = value.toLowerCase();
            var valid = false;

            this.element.children("option").each(function () {
                if ($(this).text().toLowerCase() === valueLowerCase) {
                    this.selected = valid = true;
                    return false;
                }
            });

            // Found a match, nothing to do
            if (valid) {
                return;
            }

            // Remove invalid value
            this.clear();

            this._sourceData = [];
        },

        _destroy: function () {
            this.wrapper.remove();
            this.element.show();
        },

        clear: function () {
            this.input.val("");
            this.input.autocomplete("instance").term = "";
        }
    });
})(jQuery);