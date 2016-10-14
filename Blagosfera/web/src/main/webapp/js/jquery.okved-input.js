(function($) {
	
	"use strict";

    function Plugin(option, event) {
        // get the args of the outer function..
        var args = arguments;
        // The arguments of the function are explicitly re-defined from the argument list, because the shift causes them
        // to get lost
        //noinspection JSDuplicatedDeclaration
        var _option = option,
            option = args[0],
            event = args[1];
        [].shift.apply(args);

        var value;
        var chain = this.each(function () {
            var $this = $(this);
            if ($this.is('input')) {
                if (option == "clear") {
                    if ($this.next().hasClass("okved-textarea")) {
                        var $textarea = $this.next();
                        $textarea.empty();
                    }
                } else {
                    var data = $this.data('okvedInput'),
                        options = typeof option == 'object' && option;

                    if (!data) {
                        var config = $.extend({}, OkvedInput.DEFAULTS, $.fn.okvedInput.defaults || {}, $this.data(), options);
                        $this.data('okvedInput', (data = new OkvedInput(this, config, event)));
                        console.log("OKVED", data);
                    } else if (options) {
                        data.options = data.options == null ? {} : data.options;
                        for (var i in options) {
                            if (options.hasOwnProperty(i)) {
                                data.options[i] = options[i];
                            }
                        }
                    }

                    if (typeof option == 'string') {
                        if (data[option] instanceof Function) {
                            value = data[option].apply(data, args);
                        } else {
                            value = data.options[option];
                        }
                    }
                }
            }
        });

        if (typeof value !== 'undefined') {
            //noinspection JSUnusedAssignment
            return value;
        } else {
            return chain;
        }
    }
	
    var OkvedInput = function(element, options, event) {
        this.$element = $(element);
        this.$textarea = null;
        this.$okvedsModal = null;
        this.value = [];
        this.okvedsTree = null;
        
        // methods
        this.val = OkvedInput.prototype.val;
        
        this.init(options);
    };
    
    OkvedInput.DEFAULTS = {
       value: [],
       editTitle: 'Редактировать'
    };
    
    OkvedInput.prototype = {
    		
    	constructor: OkvedInput,
        
        init: function(options) {
        	var array = [],
        	    val = this.$element.val() || "";
        	$.each(val.split(";"), function(index, item) {
        		if(item) array.push(parseInt(item));
        	});
        	this.val(array);
            this.attachModal(options);
            this.render(options);
            this.$element.change(this.onValueChange);
        },
        
        render: function(options) {
        	var $this = this;
            var value = $this.val();
        	var $textarea = null;
            if (options.singleSelect == true) {
                $textarea = $("<div class='form-control okved-textarea' style='height : 34px; overflow-x : hidden; overflow-y : scroll;'></div>");
            } else {
                $textarea = $("<div class='form-control okved-textarea' style='height : 200px; overflow-x : hidden; overflow-y : scroll;'></div>");
            }
        	this.$textarea = $textarea;
        	$.ajax({
    			type : "get",
    			dataType : "json",
    			data: {id: value},
    			url : "/sharer/okveds.json",
    			success : function(response) {
    				console.log("okveds res", response);
    				$.each(response, function(index, okved) {
						$textarea.append($this.getTextareaItemMarkup(okved.id, okved.code, okved.longName));
					});
    			},
    			error : function() {
    				console.log("ajax error");
    			}
    		});

            $this.$element.after($textarea);
            var $edit = $("<a href='#' style='position: relative; top: 5px; float: right; right: 5px;'/>").append(options.editTitle);
            $edit.click(function() {
                $this.$okvedsModal.find("input.okved-query").val("");
                $this.$okvedsModal.modal();
                $this.$okvedsModal.find("h4").html(options.title);
                $this.showTree(options);

                return false;
            });
            $textarea.after($("<div class='clearfix'/>")).after($edit);
            $this.$element.hide();
        },
        
        val: function(value) {
            var self = this;
            if(typeof value === 'undefined') {
            	return self._getValue();
            } else {
                self._setValue(value);
            }
        },
        
        _setValue: function(value) {
            this.value = value;
            this.$element.val(this.value.join(";"));
            this.$element.trigger("change");
        },
        
        _getValue: function() {
            return this.value;
        },
        
        _removeVal: function(item) {
        	var index = this.value.indexOf(item);
        	this.value.splice(index, 1);
        	this.$element.val(this.value.join(";"));
            
            this.$element.trigger("change");
        },
        
        _addValue: function(item) {
        	this.value.push(item);
        	this.$element.val(this.value.join(";"));
        	this.$element.trigger("change");
        },
        
        attachModal: function(options) {
        	var $this = this,
        	    $element = this.$element;

            var modalBlock = $("#" + $element.attr("id") + "Modal");
            if (modalBlock.length > 0) {
                this.$okvedsModal = modalBlock;
            } else {
                this.$okvedsModal = $($('#okved-modal-template').html());
                this.$okvedsModal.attr("id", $element.attr("id") + "Modal");
                $("body").append(this.$okvedsModal);
            }

            this.$okvedsModal.find("input.okved-query").radomTooltip();
            this.$okvedsModal.find("input.okved-query").callbackInput(500, 3, function(){
                $this.showTree(options);
            });

            this.$okvedsModal.find("button.save").off();
            this.$okvedsModal.find("button.save").click(function() {
                var $textarea = $this.$textarea;
                $textarea.empty();
                var records = $this.okvedsTree.getView().getChecked();
                var ids = [];
                Ext.Array.each(records, function(rec) {
                    ids.push(rec.get('id'));
                    $textarea.append($this.getTextareaItemMarkup(rec.get('id'), rec.get('code'), rec.get('text')));
                });
                $this.val(ids);
            });

        },
        
        getTextareaItemMarkup: function (id, code, text) {
            var styles = "display: inline-block; margin-right: 50px; width: 100%; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;";
            return $("<p/>").data("id", id).append("<span title='" + code + " " + text + "' style='" + styles + "'>" + code + " " + text + "</span>").append(this.getItemLink());
        },
        
        getItemLink: function() {
        	var $this = this,
        	    a = $("<a href='#' class='glyphicon glyphicon-remove pull-right'></a>");
        	a.click(function() {
        		var parent = $(this).parent();
                $this._removeVal(parseInt(parent.data("id")));
                parent.remove();                
                return false;
            });
        	return a;
        },
        
        showTree: function(options) {
            var $treeDiv = this.$okvedsModal.find(".tree-div");
            $treeDiv.attr("id", "tree-div-" + this.$element.attr("id"));
            $treeDiv.empty();

            var $this = this;
            var value = this.$element.val();
            var query = this.$okvedsModal.find(".okved-query").val();
            var excludedValuesId = this.$element.attr("excluded_values");
            var excludedValues = $("#" + excludedValuesId).val() == null ? "" : $("#" + excludedValuesId).val();
            var excludedValuesArr;
            if (excludedValues != "") {
                excludedValuesArr = excludedValues.split(";");
            } else {
                excludedValuesArr = [];
            }
            var store = Ext.create('Ext.data.TreeStore', {
                proxy : {
                    type : 'ajax',
                    url : '/sharer/okveds_tree.json',
                    actionMethods : {create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'},
                    extraParams : {query : query, id: $this.val()}
                },
                sorters : [ {
                    property : 'leaf',
                    direction : 'ASC'
                }, {
                    property : 'id',
                    direction : 'ASC'
                } ]
            });
            this.okvedsTree = Ext.create('Ext.tree.Panel', {
                store : store,
                rootVisible : false,
                useArrows : true,
                frame : true,
                renderTo : $treeDiv.attr("id"),
                width : 800,
                height : 400,
                listeners: {
                    afteritemexpand: function(c) {
                        if (query) {
                            $.each($("span.x-tree-node-text"), function(index, span){
                                var $span = $(span);
                                var html = $span.html();
                                if (html.indexOf("<i ") == -1) {
                                    html = html.replace(query, "<i style='background-color:#dddddd;'>" + query + "</i>");
                                    $span.html(html);
                                }
                            });
                        }
                    },
                    checkchange: function(node, checked, eOpts) {
                        var foundExcluded = false;
                        for (var i = 0; i<excludedValuesArr.length; i++) {
                            if (excludedValuesArr[i] == node.data.id) {
                                foundExcluded = true;
                                break;
                            }
                        }

                        // Если Okved код выглядит так: 54.10.54 и количество знаков - не меньше 5 (4 цыфры и точка)
                        if (checked && !foundExcluded && node.data.code.indexOf(".") > -1 && node.data.code.length > 4) {
                            checked = true;
                        } else {
                            checked = false;
                        }
                        if (options.singleSelect == true && checked) { // Если можно выбирать только 1 вариант
                            var records = $this.okvedsTree.getView().getChecked();
                            // Cнимаем остальные выбраные чекбоксы
                            for (var i = 0; i < records.length; i++) {
                                records[i].set('checked', false);
                            }
                        }
                        node.set("checked", checked);
                    }
                }
            });
        }
    };
    
    
    $.fn.okvedInput = Plugin;
	$.fn.okvedInput.Constructor = OkvedInput;
})(jQuery);