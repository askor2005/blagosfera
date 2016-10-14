community.shape.AbstractUnit = draw2d.shape.layout.VerticalLayout.extend({
    NAME: "community.shape.AbstractUnit",

    init : function(type)
    {
        this._super();
        this.type = type;

        this.data = {
            id         : null,
            managerIkp : null
        };

        var _this = this;

        this.port = this.createPort("hybrid", new draw2d.layout.locator.BottomLocator(this));
        this.port.setConnectionAnchor(new draw2d.layout.anchor.ChopboxConnectionAnchor(this.port));
        //this.port.setConnectionAnchor(new draw2d.layout.anchor.FanConnectionAnchor(this.port));

        this.installEditPolicy(new draw2d.policy.figure.RectangleSelectionFeedbackPolicy);

        this.setBackgroundColor("#f3f3f3");
        this.setResizeable(true);

        this.setStroke(1);
        this.setGap(0);
        this.setColor("#e0e0e0");
        this.setRadius(5);

        this.resizeListener = function(figure){

        };
        //this.locator =
        //{
        //    translate: function(figure, diff){
        //        figure.setPosition(figure.x+diff.x,figure.y+diff.y);
        //    },
        //
        //    relocate:function(index, target)
        //    {
        //        var stroke = _this.getStroke();
        //        var yPos =stroke; // respect the border of the shape
        //
        //        for (var i=0;i<index;i++){
        //            var child = _this.children.get(i).figure;
        //            if(child.isVisible())
        //                yPos=yPos+child.getHeight()+_this.gap;
        //        }
        //
        //        // fix for redo operation
        //        target.cachedWidth=null;
        //        target.cachedMinWidth=null;
        //        target.repaint();
        //
        //        var boundingBox = target.getParent().getBoundingBox();
        //        var targetBoundingBox = target.getBoundingBox();
        //        target.setPosition(boundingBox.w/2-targetBoundingBox.w/2 + stroke/2,yPos);
        //    }
        //};

        // name label
        this.nameLabel = new draw2d.shape.basic.Text({
            text        : "unit label",
            stroke      : 0,
            radius      : this.getRadius(),
            bgColor     : null,
            padding     : 5,
            color       : this.bgColor.darker(0.2),
            bold        : true,
            parentUnit  : this
        });
        this.nameLabel.installEditor(new community.shape.LabelInPlaceTextAreaEditor());

        //
        this.centerLine =  new draw2d.shape.basic.Rectangle();
        this.centerLine.getHeight= function(){return 1;};
        this.centerLine.setMinWidth(90);
        this.centerLine.setColor("#e0e0e0");

        // manager label
        this.managerNameLabel = new community.shape.LableNoDoubleClick({
            text:       "",
            stroke      : 0,
            radius      : this.getRadius(),
            bgColor     : null,
            padding     : 5,
            color       : this.bgColor.darker(0.2),
            bold        : false,
            parentUnit  : this
        });

        this.members = [];

        // finally compose the shape with top/middle/bottom in VerticalLayout
        this.add(this.nameLabel);
        //this.add(this.centerLine);
        this.add(this.managerNameLabel);

        // editing and removing
        this.initUnitContextMenuFor(this.nameLabel);
        this.initUnitContextMenuFor(this);
        this.initUnitContextMenuFor(this.managerNameLabel);


        // TODO: remove
        //this.setMembers(
        //    [
        //        {
        //            fullName : "member1",
        //            ikp : "123"
        //        },
        //        {
        //            fullName : "member2",
        //            ikp : "124"
        //        },
        //    ]
        //);
    },

    initUnitContextMenuFor : function(element) {
        var _this = this;
        element.on("contextmenu", function (emitter, event) {
            $.contextMenu({
                selector: '.community-schema-modal-body',
                events: {
                    hide: function () {
                        $.contextMenu('destroy');
                    }
                },
                callback: $.proxy(function (key, options) {
                    switch (key) {
                        case "rename":
                            _this.nameLabel.editor.start(_this.nameLabel);
                            break;
                        case "delete":
                            var cmd = new draw2d.command.CommandDelete(_this);
                            emitter.getCanvas().getCommandStack().execute(cmd);
                            break;
                        case "changecolor":
                            new community.shape.ColorPiker().start(_this, options);
                            break;
                        case "changemanager":
                            _this.onChangeUnitManager();
                            break;
                        case "changemembers":
                            _this.onChangeMembers();
                            break;
                        default:
                            break;
                    }

                }, this),
                x: event.x,
                y: event.y,
                items: {
                    "rename"        : {name: "Переименовать"},
                    "changecolor"   : {name: "Изменить цвет"},
                    "changemanager" : {name: "Назначить руководителя", disabled: isDirector()},
                    "changemembers" : {name: "Назначить сотрудников", disabled: isDirector()},
                    "sep1"          : "---------",
                    "delete"        : {name: "Удалить", disabled: isDirector()}
                }
            });
        });

        var isDirector = function()
        {
            return _this.type == 'DIRECTOR';
        }
    },

    setUnitName: function (name)
    {
        if (name != null)
            this.nameLabel.setText(name);
        return this;
    },

    getUnitName: function ()
    {
        return this.nameLabel.getText();
    },

    setManagerIkp: function (managerIkp)
    {
        this.data.managerIkp = managerIkp;
    },

    getManagerIkp: function ()
    {
        return this.data.managerIkp;
    },

    setManagerFullName: function (managerFullName)
    {
        if (managerFullName != 'undefined' && managerFullName != null)
            this.managerNameLabel.setText(managerFullName);
    },

    getManagerFullName: function ()
    {
        return this.managerNameLabel.getText();
    },

    getManagerInfo : function()
    {
        return {managerFullName : this.getManagerFullName(), managerIkp : this.getManagerIkp()};
    },

    setPersistentId: function (id)
    {
        this.data.id = id;
    },

    onDoubleClick:function()
    {
    },

    validate: function(){
    },

    setMembers : function (members){
        this.members = [];
        while(this.children.getSize() > 2)
            this.remove(this.children.get(this.children.getSize() - 1).figure);
        if (members != null && members.length > 0)
            this.add(this.centerLine);

        for (var i = 0; i < members.length; i++){
            var memberLabel = new community.shape.LableNoDoubleClick({
                text:       members[i].fullName,
                stroke      : 0,
                radius      : this.getRadius(),
                bgColor     : null,
                padding     : {left: 5},
                color       : this.bgColor.darker(0.2),
                bold        : false,
                parentUnit  : this,
                member      : members[i]
            });
            this.initUnitContextMenuFor(memberLabel);
            this.add(memberLabel);
        }
        this.members = members;

        this.repaint();
    },

    getMembers : function() {
        return this.members;
    },

    onChangeMembers : function() {
        var _this = this;
        CommunitySchemaPeopleSelector.selectMembers(this, function(members){
            if (members != null) {
                _this.setMembers(members);
                //_this.managerNameLabel.on("change:text", function(){
                //    $("#managerName").val(_this.getManagerFullName());
                //});
                //_this.getCanvas().getCommandStack().execute(new community.shape.CommandSetMembers(_this, members));
            }
        });
    },

    // fix for remove/redo operation
    setCanvas: function( canvas ) {
        this._super(canvas);

        if (canvas == null) {
            this.lastAppliedAttributes = {};
        }
    },

    // hack for resize text label
    setDimension:function( w, h)
    {
        //this._super(w,h);
        this.setDimensionFigure(w, h);

        var width=this.width-(2*this.stroke);
        this.children.each(function(i,e){
            e.figure.setDimension(width,e.figure.getHeight());
        });

        return this;
    },

    setDimensionFigure:function( w, h)
    {
        this.portRelayoutRequired=true; // from draw2d.shape.node.Node

        // from draw2d.Figure
        var _this = this;
        if (w >= this.width)
            w = Math.max(this.getWidth(), w);
        else
            w = Math.max(this.getMinWidth(), w);
        h = Math.max(this.getMinHeight(), h);

        if(this.width === w && this.height ===h){
            // required if an inherit figure changed the w/h to a given constraint.
            // In this case the Resize handles must be informed that the shape didn't resized.
            // because the minWidth/minHeight did have a higher prio.
            this.editPolicy.each(function(i,e){
                if(e instanceof draw2d.policy.figure.DragDropEditPolicy){
                    e.moved(_this.canvas, _this);
                }
            });
            return this;
        }

        // apply all EditPolicy to adjust/modify the new dimension
        //
        this.editPolicy.each(function(i,e){
            if(e instanceof draw2d.policy.figure.DragDropEditPolicy){
                var newDim = e.adjustDimension(_this,w,h);
                w = newDim.w;
                h = newDim.h;
            }
        });

        // respect the aspect ratio if required
        //
        if(this.keepAspectRatio===true){
            if (w >= this.getMinWidth()) {
                // scale the height to the given ratio
                h = this.getHeight() * (w/this.getWidth());
                // and apply the new dimension only if the values are in range of the given constraints
                if(h>=this.getMinHeight()){
                    this.width = w;
                    this.height= h;
                }
            }
        }
        else{
            this.width = Math.max(this.getMinWidth(),w);
            this.height= Math.max(this.getMinHeight(),h);
        }


        this.repaint();

        this.fireEvent("resize");
        this.fireEvent("change:dimension");

        // Update the resize handles if the user change the position of the element via an API call.
        //
        this.editPolicy.each(function(i,e){
            if(e instanceof draw2d.policy.figure.DragDropEditPolicy){
                e.moved(_this.canvas, _this);
            }
        });

        return this;
    },

    getMinWidth:function()
    {
        var width=10;
        this.children.each(function(i,e){
            if(e.figure.isVisible())
                width = Math.max(width, e.figure.getMinWidth());
        });
        return width+(this.stroke*2);
    },

    setBgColorInCommand : function (initialColor, newColor) {
        var cmd = new community.shape.CommandChangeUnitBgColor(this, initialColor, newColor);
        this.getCanvas().getCommandStack().execute(cmd);
    },

    onChangeUnitManager : function() {
        var _this = this;
        CommunitySchemaPeopleSelector.selectManager(this, function(selectedManager){
            if (selectedManager != null) {
                _this.managerNameLabel.on("change:text", function(){
                    $("#managerName").val(_this.getManagerFullName());
                });
                _this.getCanvas().getCommandStack().execute(new community.shape.CommandSetManager(_this, selectedManager));
            }
        });
    },

    onDisplayProperties : function(id)
    {
        var _this = this;

        // unit name
        $("#" + id).append('<label for="unitName">Структурная единица</label>');
        $("#" + id).append('<textarea class="form-control" rows="3" id="unitName" placeholder="Введите имя структурной единицы">');
        $("#unitName").val(this.getUnitName());

        $("#unitName").on("blur", function(){
            var cmd = new community.shape.CommandChnageLabelText(_this.nameLabel, $("#unitName").val().trim());
            _this.getCanvas().getCommandStack().execute(cmd);
            if(_this.getWidth() < _this.nameLabel.getWidth())
                _this.setWidth(_this.nameLabel.getWidth());
            if(_this.getHeight() < _this.nameLabel.getHeight())
                _this.setHeight(_this.nameLabel.getHeight());
        });
        this.nameLabel.on("change:text", function(){
            $("#unitName").val(_this.getUnitName());
        });

        // unit manager
        $("#" + id).append('<label for="unitName">Руководитель</label>');
        $("#" + id).append('<input class="form-control" id="managerName" readonly placeholder="Руководитель не назначен">');
        $("#managerName").val(this.getManagerFullName());
        if (this.type == 'DIRECTOR')
            $("#managerName").attr('readonly','readonly');
        else
            $("#managerName").on("click", function(){_this.onChangeUnitManager()});

        // unit color
        $("#" + id).append('<label for="unitPropertiesColorPicker">Цвет</label>');
        var initialColor = this.getBackgroundColor();
        $('#' + id).append( '<div class="input-group unitPropertiesColorPicker">' +
        ' <span class="input-group-addon"><i></i></span>' +
        ' <input type="text" id="unitPropertiesColorPickerInput" value="' + initialColor.hashString + '" class="form-control" />' +
        '</div>')
        $('.unitPropertiesColorPicker').colorpicker({color : initialColor.hashString});
        $('.unitPropertiesColorPicker').colorpicker().on('showPicker', function(ev){
            initialColor = _this.getBackgroundColor();
            $('.unitPropertiesColorPicker').colorpicker().colorpicker("setValue", initialColor.hashString);
        });
        $('.unitPropertiesColorPicker').colorpicker().on('changeColor', function(ev){
            _this.setBackgroundColor(ev.color.toHex());
        });
        $('.unitPropertiesColorPicker').colorpicker().on('hidePicker', function(ev){
            _this.setBgColorInCommand(initialColor, ev.color.toHex());
        });
        this.on("change:bgColor", function(){
            $('.unitPropertiesColorPicker').colorpicker().colorpicker("setValue", _this.getBackgroundColor().hashString);
        });
    },

    offDisplayProperties : function(id) {
        var cmd = new community.shape.CommandChnageLabelText(this.nameLabel, $("#unitName").val().trim());
        this.getCanvas().getCommandStack().execute(cmd);
        if(this.getWidth() < this.nameLabel.getWidth())
            this.setWidth(this.nameLabel.getWidth());
        if(this.getHeight() < this.nameLabel.getHeight())
            this.setHeight(this.nameLabel.getHeight());
        this.nameLabel.off("change:text");
        this.managerNameLabel.off("change:text");
        this.off("change:bgColor");
    },


    saveToObject : function()
    {
        var unit = {
            draw2dId   : this.getId(),
            type       : this.type,
            x          : this.x,
            y          : this.y,
            width      : this.width,
            height     : this.height,
            bgColor    : this.getBackgroundColor().hashString,
            id         : this.data.id,
            name       : this.getUnitName(),
            managerFullName : this.getManagerFullName(),
            managerIkp : this.data.managerIkp,
            members    : this.members,
            connections: []
        }

        if (this.getHybridPort(0).connections != null) {
            this.getHybridPort(0).connections.each(function (i, c) {
                if (c.sourcePort != null && c.sourcePort.parent != null && c.sourcePort.parent.getId() == unit.draw2dId)
                    unit.connections.push(c.saveToObject());
            });
        }

        return unit;
    },

    loadFromObject : function(unit)
    {
        this.data.id = unit.id;
        this.data.managerIkp = unit.managerIkp;
        this.setUnitName(unit.name);
        this.setManagerFullName(unit.managerFullName);

        this.x = unit.x;
        this.y = unit.y;

        if (unit.width != null)
            this.setWidth(unit.width);
        if (unit.height != null)
            this.setHeight(unit.height);

        if (unit.bgColor != null)
            this.setBackgroundColor(unit.bgColor);

        this.setMembers(unit.members);
    }
});

community.shape.LableNoDoubleClick = draw2d.shape.basic.Label.extend( {
    init: function(attr, setter, getter) {
        this._super(attr, setter, getter);
    },

    onDoubleClick : function () {
    }
});

community.shape.CommandChangeUnitBgColor = draw2d.command.Command.extend({

    init: function(unit, oldBgColor, newBgColor)
    {
        this._super("Change unit color");
        this.unit   = unit;
        this.oldBgColor = oldBgColor;
        this.newBgColor = newBgColor;
    },

    execute:function()
    {
        this.redo();
    },

    undo:function()
    {
        this.unit.setBackgroundColor(this.oldBgColor);
    },

    redo:function()
    {
        this.unit.setBackgroundColor(this.newBgColor);
    }
});

community.shape.ColorPiker = Class.extend({

    start: function(unit, options){
        var html = $('<input type="text" class="unitColorPicker" value="#5367ce"  style="overflow:hidden;resize:none"/>');
        $('.community-schema-modal-body').append(html);

        var initialColor = unit.getBackgroundColor();
        $('.unitColorPicker').colorpicker({color : initialColor.hashString}).show();

        $('.unitColorPicker').colorpicker().on('changeColor', function(e){
            unit.setBackgroundColor(e.color.toHex())
        });

        $('.unitColorPicker').colorpicker().on('hidePicker', function(e) {
            unit.setBgColorInCommand(initialColor, html.val().trim());

            html.remove();
            html = null;
            $('.unitColorPicker').colorpicker().colorpicker('disable');
            $('.unitColorPicker').colorpicker().colorpicker('destroy');
        });

        html.css({position:"absolute","top": options.y, "left":options.x});
        $('.unitColorPicker').colorpicker().colorpicker('show');
        html.hide();


        ////colorDiv.css({"top": 200, "left":200}).show();
        //$("#colorDiv").spectrum({
        //    showPalette: true,
        //    flat: true,
        //    palette: [
        //        ['black', 'white', 'blanchedalmond']
        //    ],
        //    appendTo : "#colorDiv"
        //});
    }
});

community.shape.CommandSetManager = draw2d.command.Command.extend({
    init: function(unit, newManagerInfo)
    {
        this._super("Set manager");
        this.unit    = unit;
        this.newManagerInfo = newManagerInfo;
        this.oldManagerInfo = unit.getManagerInfo();

        this.changeFullNameCommand = new community.shape.CommandChnageLabelText(unit.managerNameLabel, newManagerInfo.managerFullName);
    },

    execute:function()
    {
        this.redo();
    },

    undo:function()
    {
        this.unit.setManagerIkp(this.oldManagerInfo.managerIkp);
        this.changeFullNameCommand.undo();
    },

    redo:function()
    {
        this.unit.setManagerIkp(this.newManagerInfo.managerIkp);
        this.changeFullNameCommand.redo();
    }
});


community.shape.CommandChnageLabelText = draw2d.command.Command.extend({

    init: function(label, newText)
    {
        this._super("Change lable text");
        this.label   = label;
        this.newText = newText;
        this.oldText = label.getText();
    },

    execute:function()
    {
        this.redo();
    },

    undo:function()
    {
        this.label.setText(this.oldText);
        this.label.setDimension(1, 1);
        if (this.oldWidth != null && this.oldHeight != null && this.label.parentUnit != null)
            this.label.parentUnit.setDimension(this.oldWidth, this.oldHeight);
    },

    redo:function()
    {
        if (this.label.parentUnit != null) {
            this.oldWidth = this.label.parentUnit.getWidth();
            this.oldHeight = this.label.parentUnit.getHeight();
        }

        this.label.setText(this.newText);
        if (this.label.parentUnit != null) {
            var unit = this.label.parentUnit;
            if (this.label.getMinWidth() > unit.getWidth() - unit.stroke * 2) {
                this.label.parentUnit.setDimension(this.label.getMinWidth() + 2 * unit.stroke, unit.getHeight());
                this.label.parentUnit.repaint();
            }

            var height = unit.stroke*2;
            var _this = this;
            unit.children.each(function(i, e){
                if (e.figure.isVisible() && e.figure !== _this.label){
                    height += e.figure.getHeight() + unit.gap;
                }
            });

            if (this.label.getHeight() > unit.getHeight() - height) {
                this.label.parentUnit.setDimension(unit.getWidth(), height + this.label.getHeight());
                this.label.parentUnit.repaint();
            }

        }
    }
});

community.shape.LabelInPlaceTextAreaEditor = draw2d.ui.LabelInplaceEditor.extend({

    start: function( label){
        this.label = label;
        //check is started editor
        if (this.html !== undefined && this.html !== null) {
            return;
        }

        this.html = $('<textarea id="inplaceeditor" style="overflow:hidden;resize:none">');
        this.html.val(label.getText());
        this.html.select();
        this.html.hide();
        this.html.css('background-color' , this.label.parent.getBackgroundColor().hashString);
        $(".community-schema-modal-body").append(this.html);

        this.html.blur($.proxy(function(){
            if (this.html!=null) {
                this.html.remove();
                this.html = null;
                this.listener.onCommit(this.label.getText());
            }
        },this));

        this.html.bind("keyup",$.proxy(function(e){
            switch (e.which) {
                case 13:
                    if (!e.ctrlKey) {
                        this.commit();
                    } else {
                        var editorLoc = $("#inplaceeditor");
                        e.preventDefault();
                        var s = editorLoc.val();
                        editorLoc.val(s+"\n");
                    }
                    break;
                case 27:
                    this.cancel();
                    break;
            }
        },this));

        $('#inplaceeditor').keyup(function(){
            $(this).height(20);
            $(this).height(this.scrollHeight);
        });

        var _this = this;
        this.onClickCommit = function(event) {
            var box = _this.label.getBoundingBox();
            var pos = _this.label.parent.canvas.fromDocumentToCanvasCoordinate(event.clientX, event.clientY);
            if(!box.contains(new draw2d.geo.Rectangle(pos.x, pos.y, 0, 0)))
                _this.commit()
        };
        $(".community-schema-modal-body").bind("click",this.onClickCommit);
        $(".community-schema-modal-body").bind("contextmenu",this.onClickCommit);

        var canvas = this.label.getCanvas();
        var bb = this.label.getBoundingBox();
        this.allSelectedFigures = canvas.getSelection().getAll().data;
        this.allSelectedFigures[0].draggable = false;
        this.allSelectedFigures[0].resizeable = false;
        $.each(this.allSelectedFigures[0].selectionHandles.data, function(index, data) {
            data.setVisible(false);
        });
        this.html.css({position:"absolute","top": bb.y / canvas.zoomFactor, "left":bb.x / canvas.zoomFactor, "width":bb.w / canvas.zoomFactor, "height":bb.h / canvas.zoomFactor});
        this.html.fadeIn($.proxy(function(){
            this.html.focus();
            this.html.select();
        },this));
    },

    commit: function(){
        $(".community-schema-modal-body").unbind("click",this.onClickCommit);
        $(".community-schema-modal-body").unbind("contextmenu",this.onClickCommit);
        $.each(this.allSelectedFigures, function(index, figure) {
            figure.draggable = true;
            figure.resizeable = true;
        });
        if (this.html!=null) {
            var label = this.html.val().trim();
            var cmd = new community.shape.CommandChnageLabelText(this.label, label);
            this.label.getCanvas().getCommandStack().execute(cmd);

            this.html.fadeOut($.proxy(function () {
                if (this.html != null) {
                    this.html.remove();
                    this.html = null;
                    this.listener.onCommit(this.label.getText());
                }
            }, this));
            //change size of figure after rename
            if(this.label.parent.getWidth() < this.label.getWidth())
                this.label.parent.setWidth(this.label.getWidth());
            if(this.label.parent.getHeight() < this.label.getHeight())
                this.label.parent.setHeight(this.label.getHeight());
        }
    },

    cancel: function(){
        $.each(this.allSelectedFigures, function(index, figure) {
            figure.draggable = true;
            figure.resizeable = true;
        });
        $(".community-schema-modal-body").unbind("click",this.onClickCommit);
        $(".community-schema-modal-body").unbind("contextmenu",this.onClickCommit);
        this.html.fadeOut($.proxy(function(){
            this.html.remove();
            this.html = null;
            this.listener.onCancel();
        },this));
    }
});

community.shape.LabelInPlaceEditor = draw2d.ui.LabelInplaceEditor.extend({



    start: function( label){
        this.label = label;

        this.commitCallback = $.proxy(this.commit,this);

        // commit the editor if the user clicks anywhere in the document
        $(".community-schema-modal-body").bind("click",this.commitCallback);

        // append the input field to the document and register
        // the ENTER and ESC key to commit /cancel the operation
        this.html = $('<input id="inplaceeditor">');
        this.html.val(label.getText());
        this.html.hide();
        this.html.select();
        $(".community-schema-modal-body").append(this.html);

        this.html.autoResize({animate:false});

        this.html.bind("keyup",$.proxy(function(e){
            switch (e.which) {
                case 13:
                    if (!e.ctrlKey) {
                        this.commit();
                    } else {
                        var editorLoc = $("#inplaceeditor");
                        e.preventDefault();
                        var s = editorLoc.val();
                        editorLoc.val(s+"\n");
                    }
                    break;
                case 27:
                    this.cancel();
                    break;
            }
        },this));

        this.html.bind("blur",this.commitCallback);

        // avoid commit of the operation if we click inside the editor
        this.html.bind("click",function(e){
            e.stopPropagation();
            e.preventDefault();
        });

        // Position the INPUT and init the autoresize of the element
        var canvas = this.label.getCanvas();
        var bb = this.label.getBoundingBox();
        bb.translate(-1,-1);
        bb.resize(2,2);

        this.html.css({position:"absolute","top": bb.y, "left":bb.x, "min-width":bb.w*(1/canvas.getZoom()), "height":Math.max(25,bb.h*(1/canvas.getZoom()))});
        this.html.fadeIn($.proxy(function(){
            this.html.focus();
            this.html.select();
        },this));
    },

    /**
     * @method
     * Transfer the data from the editor into the label.<br>
     * Remove the editor.<br>
     * @private
     */
    commit: function(){
        this.html.unbind("blur",this.commitCallback);
        $(".community-schema-modal-body").unbind("click",this.commitCallback);
        var label = this.html.val();
        this.label.setText(label);
        this.html.fadeOut($.proxy(function(){
            this.html.remove();
            this.html = null;
            this.listener.onCommit(this.label.getText());
        },this));
    },

    /**
     * @method
     * Transfer the data from the editor into the label.<br>
     * Remove the editor.<br>
     * @private
     */
    cancel: function(){
        this.html.unbind("blur",this.commitCallback);
        $(".community-schema-modal-body").unbind("click",this.commitCallback);
        this.html.fadeOut($.proxy(function(){
            this.html.remove();
            this.html = null;
            this.listener.onCancel();
        },this));

    }
});



//community.shape.TextLableNoDoubleClick = draw2d.shape.basic.Label.extend( {
//    init: function(attr, setter, getter) {
//        this._super($.extend({resizeable:false},attr), setter, getter);
//        this.cachedWrappedAttr = null;
//
//        //this._super($.extend({width:300, height:50, resizeable:false},attr), setter, getter);
//        //this.installEditPolicy(new draw2d.policy.figure.WidthSelectionFeedbackPolicy());
//
//    },
//
//    repaint: function(attributes)
//    {
//
//        if(this.repaintBlocked===true || this.shape===null){
//            return;
//        }
//
//        // style the label
//        this.svgNodes.attr($.extend({},this.calculateTextAttr(),this.wrappedTextAttr(this.text, this.getWidth()-this.padding.left-this.padding.right)));
//        //this.svgNodes.attr($.extend({},this.calculateTextAttr(),this.wrappedTextAttr(this.text, this.parent.getWidth()-this.padding.left-this.padding.right)));
//        //this.svgNodes.attr($.extend({},this.calculateTextAttr(),this.text));
//
//        // set of the x/y must be done AFTER the font-size and bold has been set.
//        // Reason: the getHeight method needs the font-size for calculation because
//        //         it redirects the calculation to the SVG element.
//        this.svgNodes.attr({x:this.padding.left, y: this.getHeight()/2});
//
//        // this is an exception call. Don't call the super method (Label) to avoid
//        // the calculation in this method.
//        draw2d.SetFigure.prototype.repaint.call(this,attributes);
//    },
//
//
//    ///**
//    // * @inheritdoc
//    // */
//    setDimension:function( w, h)
//    {
//        this.clearCache();
//        var attr = this.wrappedTextAttr(this.text, w);
//
//        //this._super(Math.min(w,attr.width),attr.height);
//        this._super(w, attr.height);
//        //this._super(w, h);
//        this.fireEvent("change:dimension");
//
//        return this;
//    },
//
//    /**
//    * @method
//    * clear the internal cache for width/height precalculation
//    * @private
//    */
//    clearCache:function()
//    {
//        this._super();
//        this.cachedWrappedAttr = null;
//        this.cachedMinWidth = null;
//        this.cachedWidth = null;
//        return this;
//    },
//
//
//    /**
//    * @inheritdoc
//    */
//    getMinWidth:function()
//    {
//        if (this.shape === null) {
//            return 0;
//        }
//
//        if(this.cachedMinWidth === null){
//            // get the longest word in the text
//            //
//            var longestWord = this.text.split(" ").reduce(function(arg1,arg2){ return arg1.length > arg2.length ? arg1 : arg2; });
//            var svgText = this.canvas.paper
//                .text(0, 0, longestWord)
//                .attr($.extend({},this.calculateTextAttr(),{text:longestWord}));
//            this.cachedMinWidth= svgText.getBBox(true).width+this.padding.left+this.padding.right+2*this.getStroke();
//            svgText.remove();
//        }
//
//        return this.cachedMinWidth;
//    },
//
//    getWidth : function()
//    {
//        if (this.shape === null) {
//            return 0;
//        }
//
//        return Math.max(this.width, this.getMinWidth());
//
//        //if(this.cachedWidth===null){
//        //    if(this.resizeable===true){
//        //        this.cachedWidth = Math.max(this.width, this.getMinWidth());
//        //    }
//        //    else{
//        //        this.cachedWidth = this.getMinWidth();
//        //    }
//        //}
//        //
//        //
//        //return this.cachedWidth;
//    },
//    //
//    ///**
//    // * @method
//    // * calculates the attributes (wrapped text and width, height) with the given parameter
//    // *
//    // * @private
//    // */
//    wrappedTextAttr: function(text, width)
//    {
//        var words = text.split(" ");
//        if(this.canvas ===null || words.length===0){
//            return {text:text, width:width, height:20};
//        }
//
//        if(this.cachedWrappedAttr===null){
//            var abc = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//            var svgText = this.canvas.paper.text(0, 0, "").attr($.extend({},this.calculateTextAttr(),{text:abc}));
//
//            // get a good estimation of a letter width...not correct but this is working for the very first draft implementation
//            var letterWidth = svgText.getBBox(true).width / abc.length;
//
//            var s = [words[0]], x=s[0].length*letterWidth;
//            var w =null;
//            for ( var i = 1; i < words.length; i++) {
//                w= words[i];
//                var l = w.length* letterWidth;
//                if ((x+l) > width) {
//                    s.push("\n");
//                    x = l;
//                }
//                else{
//                    s.push(" ");
//                    x += l;
//                }
//                s.push(w);
//            }
//            var bbox = svgText.getBBox(true);
//            svgText.remove();
//            this.cachedWrappedAttr= {text: s.join(""), width:(bbox.width+this.padding.left+this.padding.right), height: (bbox.height+this.padding.top+this.padding.bottom)};
//        }
//        return this.cachedWrappedAttr;
//    },
//
//    ///**
//    // * @inheritdoc
//    // */
//    //getPersistentAttributes : function()
//    //{
//    //    var memento = this._super();
//    //
//    //
//    //    return memento;
//    //},
//    //
//    ///**
//    // * @inheritdoc
//    // */
//    //setPersistentAttributes : function(memento)
//    //{
//    //    this._super(memento);
//    //
//    //    return this;
//    //},
//
//
//    onDoubleClick : function () {
//    }
//});
//
