var community = {
    schema: { },
    shape:   {}
};

var connectionTypes = [];
//{
//    id : null,
//    name : null,
//    color: null
//}

//patch for bootstrap modal. Do not remove the class modal-open from body if there are other open modals.
(function(Modal) {
    var show = Modal.prototype.show;

    Modal.prototype.show = function() {
        this.modalOpen = !this.$body.hasClass('modal-open');
        show.apply(this, arguments);
    };

    Modal.prototype.hideModal = function() {
        var that = this;
        this.$element.hide();
        this.backdrop(function() {
            if (that.modalOpen) {
                that.$body.removeClass('modal-open');
            }
            that.resetScrollbar();
            that.$element.trigger('hidden.bs.modal');
        });
    };
})($.fn.modal.Constructor);

community.schema.RightMouseDownPolicy = draw2d.policy.canvas.CanvasPolicy.extend(
    {
        init : function(schema)
        {
            this.schema = schema;
        },

        onRightMouseDown: function(figure, x, y, shiftKey, ctrlKey) {
            if (this.schema.view.getBestFigure(x, y) != null)
                return;
            $.contextMenu({
                selector: '.community-schema-modal-body',
                events: {
                    hide: function () {
                        $.contextMenu('destroy');
                    }
                },
                callback: $.proxy(function (key, options) {
                    switch (key) {
                        case "new":
                            schema.addNewUnit(x, y);
                            break;
                        case "changebg":
                            schema.changeBgForm();
                            break;
                        case "clearbg":
                            schema.clearBg();
                            break;
                        default:
                            break;
                    }

                }, this),
                x: x,
                y: y,
                items: {
                    "new": {name: "Добавить структурную единицу"},
                    "changebg": {name: "Сменить фон"},
                    "clearbg": {name: "Очистить фон"}
                }
            });
        }

    }
)

community.schema.CommandChnageBgImage = draw2d.command.Command.extend({

    init: function(schema, newBgImageUrl)
    {
        this._super("Change BG color");
        this.schema   = schema;
        this.newBgImageUrl = newBgImageUrl;
        this.oldBgImageUrl = schema.bgImageUrl;
    },

    execute:function()
    {
        this.redo();
    },

    undo:function()
    {
        this.schema.setBgImageUrl(this.oldBgImageUrl);
    },

    redo:function()
    {
        this.schema.setBgImageUrl(this.newBgImageUrl);
    }
});

community.schema.Schema = Class.extend(
    {
        NAME : "community.schema.Schema",

        init : function(id, maxWidth, maxHeight, scrollTop, scrollLeft)
        {
            this.id = id;
            this.autoScrollEnabled = false;
            this.bgUploader = null;
            this.bgImageUrl = null;
            this.previousScrollTop = null;
            this.previousScrollLeft = null;

            if (maxWidth == undefined)
                maxWidth = $("#" + id).width();
            if (maxHeight == undefined)
                maxHeight = $("#" + id).height();

            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this._init(id, maxWidth, maxHeight, scrollTop, scrollLeft);
        },

        _init : function(id, maxWidth, maxHeight, scrollTop, scrollLeft)
        {
            this.id = id;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;

            this.view = new community.View(id, maxWidth, maxHeight);
            this.view.installEditPolicy(new community.schema.RightMouseDownPolicy(this));

            // bg upload form
            {
                var uploadFormHtml =
                    '<form id="bg-upload-form" action="javascript:;">' +
                    '<input id="bg-upload-form-input" type="file" name="image" accept="image"  value="Выбрать фон" onchange="$(this).parents(\'form\').submit(); $(this).val(\'\');">' +
                    '</form>';
                var uploadForm = $(uploadFormHtml);
                $("#bg-upload-form").remove();
                $("#bg-upload-form-input").remove();
                $('body').append(uploadForm);
                $("#bg-upload-form-input").hide();

                var _this = this;
                $("#bg-upload-form").submit(function (event) {
                    _this.onBgUpload(event);
                    return false;
                });
            }

            var _this = this;
            this.view.on("select", function(emitter, figure){
                if (_this.view.getSelection().getSize() == 0) {
                    _this.onAllUnSelected();
                }
                else {
                    _this.onSomethingSelected();
                }
            });

            // fix for infinite loop to top and left
            if (scrollTop == null || scrollTop == 0)
                scrollTop = 1;
            if (scrollLeft == null || scrollTop == 0)
                scrollLeft = 1;
            $("#" + id).scrollTop(scrollTop).scrollLeft(scrollLeft);

            this.previousScrollTop = scrollTop;
            this.previousScrollLeft = scrollLeft;

            this.showBgImage();
            this.initScrollEvent();
        },

        initScrollEvent : function() {
            var _this = this;
            if (this.autoScrollEnabled) {
                $("#" + this.id).scroll(function () {
                    _this.onScroll();
                });
            }
        },

        onScroll : function()
        {
            //console.log('scroll ' + $("#" + this.id).scrollTop() + " - " + $("#" + this.id).height());
            var offset = 50;
            var scrollTop = $("#" + this.id).scrollTop();
            var scrollLeft = $("#" + this.id).scrollLeft();
            var offsetX = 0;
            var offsetY = 0;

            if (scrollTop - this.previousScrollTop > 0 && scrollTop + $("#" + this.id).height() + 30 >= this.maxHeight) {
                offsetY = offset;
            }
            else if (scrollLeft - this.previousScrollLeft && scrollLeft + $("#" + this.id).width() + 30 >= this.maxWidth) {
                offsetX = offset;
            }
            else if (scrollTop - this.previousScrollTop < 0 && scrollTop <= 1)
            {
                scrollTop = 1;
                offsetY = -offset;
            }
            else if (scrollLeft - this.previousScrollLeft < 0 && scrollLeft <= 1)
            {
                scrollLeft = 1;
                offsetX = -offset;
            }

            if (offsetX != 0 || offsetY != 0) {
                this.maxWidth += Math.abs(offsetX);
                this.maxHeight += Math.abs(offsetY);

                var memento = this.saveToObject();
                var zoomFactor = this.view.zoomFactor;
                this.destroy();
                this._init(this.id,this.maxWidth, this.maxHeight, scrollTop, scrollLeft);

                if (offsetX < 0 || offsetY < 0) {
                    for(var i = 0; i < memento.units.length; i++) {
                        memento.units[i].x += Math.abs(offsetX);
                        memento.units[i].y += Math.abs(offsetY);
                    }
                }

                this.loadFromObject(memento);
                this.setUndoRedoStackListener(this.undoRedoStackListener);
                this.view.setZoom(zoomFactor);
            }
            else
                $("#" + this.id).css('background-position', '-' + $("#" + this.id).scrollLeft() + 'px -' + $("#" + this.id).scrollTop() + 'px');

            this.previousScrollTop = scrollTop;
            this.previousScrollLeft = scrollLeft;
        },


        destroy : function(){
            this.view.destroy();
            $("#" + this.id).off();
            $("#" + this.id).unbind();
        },

        enableAutoScroll : function(autoScrollEnabled)
        {
            this.autoScrollEnabled = autoScrollEnabled;
            this.initScrollEvent();
        },

        setConnectionTypes : function(types)
        {
            connectionTypes = types;
        },

        getScrollTop : function()
        {
            return $("#" + this.id).scrollTop();
        },

        getScrollLeft : function()
        {
            return $("#" + this.id).scrollLeft();
        },

        setDirector : function (ikp, fullName)
        {
            var directorUnit = new community.shape.DirectorUnit();
            directorUnit.setManagerIkp(ikp);
            directorUnit.setManagerFullName(fullName);
            this.view.add(directorUnit, this.view.getWidth(), 10);
            directorUnit.setX(this.view.getWidth()/2 - directorUnit.width/2);
        },

        addNewUnit : function(x, y) {
            if (x == null) {
                x = this.getScrollLeft() + $("#" + this.id).width()/2;
                y = this.getScrollTop() + $("#" + this.id).height()/2;
            }

            var unit = new community.shape.DepartmentUnit();
            unit.setUnitName('Новая структурная единица');
            var command = new draw2d.command.CommandAdd(this.view, unit, x, y);
            this.view.getCommandStack().execute(command);
        },
        //to center selected object
        toCenterUnit: function() {
            var x = this.getScrollLeft() + $("#" + this.id).width()/ 2,
                y = this.getScrollTop() + $("#" + this.id).height()/ 2,
                zoomFactor = this.view.zoomFactor;
            if (this.view.getSelection().getSize() == 0)
                return;
            var allFigure = this.view.getSelection().getAll().data;
            $.each(allFigure, function(i, figure) {
                if($('#communitySchemaModalToolbar').hasClass('col-md-2'))
                    figure.setX((x - figure.getWidth() / 2 + $('#communitySchemaModalToolbar').width() / 2)*zoomFactor);
                else
                    figure.setX((x - figure.getWidth() / 2)*zoomFactor);
                figure.setY((y - figure.getHeight()/2)*zoomFactor);
            });
        },

        onAllUnSelected : function(callback) {
            if (callback != null)
                this.onAllUnSelectedCallback = callback;
            else if (this.onAllUnSelectedCallback != null)
                this.onAllUnSelectedCallback();
        },

        onSomethingSelected : function(callback) {
            if (callback != null)
                this.onSomethingSelectedCallback = callback;
            else if (this.onSomethingSelectedCallback != null)
                this.onSomethingSelectedCallback(this.view.getSelection().getAll());
        },

        removeSelected : function() {
            if (this.view.getSelection().getSize() == 0)
                return;

            var cmd = new draw2d.command.CommandDeleteSelection(this.view);
            this.view.getCommandStack().execute(cmd);

            //this.view.getSelection().each(function(e, f){
            //    var cmd = new draw2d.command.CommandDelete(f);
            //    f.getCanvas().getCommandStack().execute(cmd);
            //});
        },

        changeBgForm : function()
        {
            $("#bg-upload-form-input").hide();
            $("#bg-upload-form-input").click();
        },

        clearBg : function()
        {
            schema.onBgUpload(null);
        },

        setBgImageUrl : function(bgImageUrl){
            this.bgImageUrl = bgImageUrl;
            this.showBgImage();
        },

        getBgImageCss : function()
        {
            var css = "<style>";
            if (this.bgImageUrl != null) {
                css += "body {";
                css += "background: url('" + this.bgImageUrl + "') no-repeat";
                css += "}"
            }
            css += "</style>";
            return css;
        },

        setBgImageUrlInCommand : function(bgImageUrl){
            var cmd = new community.schema.CommandChnageBgImage(schema, bgImageUrl);
            this.view.getCommandStack().execute(cmd);
        },

        showBgImage : function() {
            if (this.bgImageUrl != null) {
                $("#" + this.id).css({background: 'url(' + this.bgImageUrl + ') no-repeat'});
                $("#" + this.id).css('background-position', '-' + $("#" + this.id).scrollLeft() + 'px -' + $("#" + this.id).scrollTop() + 'px');
            }
            else
                $("#" + this.id).css('background', 'transparent');
        },

        onBgUpload : function (event) {
            if (event == null) {
                this.setBgImageUrlInCommand(null);
            }
            else if (this.bgUploader == null) {
                console.log("bg uploader is not set");
            }
            else
                this.bgUploader(event);
        },

        setUndoRedoStackListener : function(undoRedoStackListener) {
            this.undoRedoStackListener = undoRedoStackListener;
            if (this.view != null && undoRedoStackListener != null)
                this.view.getCommandStack().addEventListener(undoRedoStackListener);
        },

        undo : function() {
            this.view.getCommandStack().undo();
        },

        redo : function() {
            this.view.getCommandStack().redo();
        },

        zoomIn : function() {
            var oldWidth = this.view.initialWidth;
            var oldHeight = this.view.initialHeight;
            if(this.view.paper._viewBox) {
                oldWidth = this.view.paper._viewBox[2];
                oldHeight = this.view.paper._viewBox[3];
            }
            this.view.setZoom(this.view.getZoom()*0.75,false);
            this.scrollAfterZoom(oldWidth, oldHeight)
        },

        zoomDefault : function() {
            var oldWidth = this.view.initialWidth;
            var oldHeight = this.view.initialHeight;
            if(this.view.paper._viewBox) {
                oldWidth = this.view.paper._viewBox[2];
                oldHeight = this.view.paper._viewBox[3];
            }
            this.view.setZoom(1.0, false);
            this.scrollAfterZoom(oldWidth, oldHeight)
        },

        zoomOut : function() {
            var oldWidth = this.view.initialWidth;
            var oldHeight = this.view.initialHeight;
            if(this.view.paper._viewBox) {
                oldWidth = this.view.paper._viewBox[2];
                oldHeight = this.view.paper._viewBox[3];
            }
            this.view.setZoom(this.view.getZoom()*1.25, false);
            this.scrollAfterZoom(oldWidth, oldHeight)
        },

        scrollAfterZoom: function(oldWidth, oldHeight) {
            var html = this.view.html;
            var newWidth = this.view.paper._viewBox[2];
            var newHeight = this.view.paper._viewBox[3];
            var ratioH = ((newHeight + oldHeight) / 2) / html.height();
            var ratioW = ((newWidth + oldWidth) / 2) / html.width();
            html.scrollTop(html.scrollTop() + ((oldHeight - newHeight) / ratioH));
            html.scrollLeft(html.scrollLeft() + ((oldWidth - newWidth) / ratioW));
        },

        saveToObject : function()
        {
            var units = [];
            if (this.view.figures != null)
            {
                this.view.figures.each(function(i,f){
                    units.push(f.saveToObject());
                });
            }

            var schema = {
                units      : units,
                bgImageUrl : this.bgImageUrl,
                width      : this.maxWidth,
                height     : this.maxHeight,
                scrollLeft : this.getScrollLeft(),
                scrollTop  : this.getScrollTop()
            };

            return schema;
        },

        loadFromObject : function(schema) {
            if (schema.units == undefined)
                return;

            if (schema.width == null)
                schema.width = this.maxWidth;
            if (schema.height == null)
                schema.height = this.maxHeight;
            if (schema.width != this.maxWidth || schema.height != this.maxHeight) {
                this.destroy();
                this._init(this.id, schema.width, schema.height, schema.scrollTop, schema.scrollLeft);
            }
            if (schema.scrollLeft != null && schema.scrollTop != null) {

                // hacks for infinite loop to top and left
                if (schema.scrollLeft == 0)
                    schema.scrollLeft = 1;
                if (schema.scrollTop == 0)
                    schema.scrollTop = 1;
                $("#" + this.id).scrollTop(schema.scrollTop).scrollLeft(schema.scrollLeft);
            }

            this.setBgImageUrl(schema.bgImageUrl);

            var units = {};
            var connections = [];
            for (var i = 0; i < schema.units.length; i++) {
                var m = schema.units[i];
                var unit = null;
                if (m.type == 'DIRECTOR')
                    unit = new community.shape.DirectorUnit();
                else if (m.type == 'DEPARTMENT')
                    unit = new community.shape.DepartmentUnit();
                if(unit != null) {
                    unit.loadFromObject(m);
                    if (m.draw2dId) {
                        unit.id = m.draw2dId;
                    }
                    this.view.add(unit);
                    units[m.id] = unit;

                    Array.prototype.push.apply(connections, m.connections);
                }
            }

            for (var i = 0; i < connections.length; i++) {
                var connection = new community.shape.Connection();
                var connectionData = connections[i];
                var source = units[connectionData.sourceDraw2dId];
                var target = units[connectionData.targetDraw2dId];
                //fix for infinity scroll
                if (source === undefined && target === undefined) {
                    $.each(units, function (i, unit) {
                        if (unit.id == connectionData.sourceDraw2dId) {
                            source = unit;
                        } else if (unit.id == connectionData.targetDraw2dId) {
                            target = unit;
                        }
                    });
                }
                //////////////////////////
                if (source != null && target != null)
                {
                    connection.loadFromObject(connectionData);
                    if (connectionData.draw2dId) {
                        connection.id = connectionData.draw2dId;
                    }
                    connection.setSource(source.getHybridPort(0));
                    connection.setTarget(target.getHybridPort(0));
                    this.view.add(connection);
                }
            }
        },

        saveToJson : function()
        {
            return JSON.stringify(this.saveToObject());
        },

        loadFromJson : function(json)
        {
            try {
                var schema = JSON.parse(json)
                this.loadFromObject(schema);
            }
            catch(ex){
                console.log("failed to load data from json: " + ex);
            }
        },

        saveAsSvg : function(callback)
        {
            var writer = new draw2d.io.svg.Writer();
            writer.marshal(this.view,function(svg, svg64){callback(svg, svg64);});
        },

        saveAsPng : function(callback)
        {
            var writer = new draw2d.io.png.Writer();
            writer.marshal(this.view,function(png){callback(png);});
        },

        saveAsPdf : function(callback)
        {
            this.saveAsSvg(function(svg){
                var pdf = new jsPDF('p', 'pt', 'a4');
                svgElementToPdf(svg, pdf, {
                    scale: 72/96,
                    removeInvalid: true
                });
                callback(pdf.output('dataurlstring'));
            });
        }
    });

draw2d.command.CommandDeleteSelection = draw2d.command.Command.extend({

    init: function(canvas)
    {
        this._super("delete selection");
        this.canvas = canvas;
        this.selection = canvas.getSelection().getAll();
        this.connections = new draw2d.util.ArrayList();

        var directorIndex = -1;
        for (var i = 0; i < this.selection.getSize(); ++i){
            var figure = this.selection.get(i);
            if (figure instanceof draw2d.Connection) {
                if (!this.connections.contains(figure))
                    this.connections.add(figure);
            }
            else if (figure instanceof community.shape.DirectorUnit){
                directorIndex = i;
            }
            else {
                var figureConnections = figure.getConnections()
                for (var j = 0; j < figureConnections.getSize(); j++) {
                    if (!this.connections.contains(figureConnections.get(j)))
                        this.connections.add(figureConnections.get(j));
                }
            }

        }

        if (directorIndex != -1)
            this.selection.removeElementAt(directorIndex);

        for (var i = 0; i < this.connections.getSize(); i++)
            this.selection.remove(this.connections.get(i));
    },

    execute:function()
    {
        this.redo();
    },

    undo:function()
    {
        for (var i = 0; i < this.selection.getSize(); ++i) {
            this.canvas.add(this.selection.get(i));
        }

        for (var i = 0; i < this.connections.getSize(); ++i){
            this.canvas.add(this.connections.get(i));
            this.connections.get(i).reconnect();
        }
    },

    redo:function()
    {
        this.canvas.setCurrentSelection(null);

        for (var i = 0; i < this.connections.getSize(); ++i){
            this.connections.get(i).disconnect();
        }

        for (var i = 0; i < this.selection.getSize(); ++i) {
            this.canvas.remove(this.selection.get(i));
        }

        for (var i = 0; i < this.connections.getSize(); ++i){
            this.canvas.remove(this.connections.get(i));
        }

    }
});