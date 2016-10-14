community.shape.Connection = draw2d.shape.state.Connection.extend({

    NAME : "community.shape.Connection",
    CONNECTIONS_DISTANCE : 10,

    init : function() {
        this._super();
        this.setSelectable(true);
        this.setLabel(null);

        this.connectionType = {};
        this.connectionIndex = -1;

        this.corona = this.CONNECTIONS_DISTANCE / 4;

        // set label locator
        var _this = this;
        this.children.each(function(i, e){
            if (e.figure === _this.label)
                e.locator = new community.shape.ManhattanMidpointLocator();
        });
    },

    setConnectionType : function(connectionType) {
        this.connectionType = connectionType;
        this.setColor(connectionType.color);
    },

    getConnectionType : function() {
        return this.connectionType;
    },

    onContextMenu:function(x,y){
        $.contextMenu({
            selector: '.community-schema-modal-body',
            events:
            {
                hide:function(){ $.contextMenu( 'destroy' ); }
            },
            callback: $.proxy(function(key, options)
            {
                switch(key){
                    case "delete":
                        var cmd = new draw2d.command.CommandDelete(this);
                        this.getCanvas().getCommandStack().execute(cmd);
                    default:
                        break;
                }

            },this),
            x:x,
            y:y,
            items:
            {
                "delete": {name: "Удалить", icon: "delete"}
            }
        });
    },

    onMouseEnter:function()
    {
        this.setLabel(this.connectionType.name);
    },


    onMouseLeave:function()
    {
        this.setLabel(null);
    },

    getStartPoint:function( refPoint)
    {
        if(this.isMoving===false && this.getStartEndPoint(this.sourcePort, this.targetPort, 1) !== null){
            return this.getStartEndPoint(this.sourcePort, this.targetPort, 1);
        }
        return this._super();
    },

    getEndPoint:function(refPoint)
    {
        if(this.isMoving===false && this.getStartEndPoint(this.targetPort, this.sourcePort, -1)){
            return this.getStartEndPoint(this.targetPort, this.sourcePort, -1);
        }
        return this._super();
    },


    getStartEndPoint : function(port1, port2, direction) {
        var sourceBox = port1.getParent().getBoundingBox();
        var targetBox = port2.getParent().getBoundingBox();
        sourceBox.translate(-1, -1);
        sourceBox.resize(1, 1);
        targetBox.translate(-1, -1);
        targetBox.resize(1, 1);

        var usedConnectionIndexes = this.getUsedConnectionIndexes();
        //console.log(usedConnectionIndexes);

        var bestPoint = {
            point : null,
            distance : null,
            index : null
        }

        var step = 1;
        while(step >= 0.125) {
            bestPoint = this.calculateBestInterceptionPoint(sourceBox, targetBox, direction, bestPoint, usedConnectionIndexes, step);

            if (bestPoint.point == null || bestPoint.point.x < sourceBox.x && bestPoint.point.x > sourceBox.x + sourceBox.width ||
                bestPoint.point.y < sourceBox.y && bestPoint.point.y > sourceBox.y + sourceBox.height) {
                step /= 2;
            }
            else
                break;
        }

        this.connectionIndex = bestPoint.index;

        //console.log("index: " + this.connectionIndex);
        return bestPoint.point;
    },

    calculateBestInterceptionPoint : function(sourceBox, targetBox, direction, bestPoint, usedConnectionIndexes, step) {
        var minUsedConnectionIndex = 1;
        var maxUsedConnectionIndex = -1;
        if (usedConnectionIndexes.length > 0) {
            minUsedConnectionIndex = Math.min.apply(null, usedConnectionIndexes);
            maxUsedConnectionIndex = Math.max.apply(null, usedConnectionIndexes);
        }

        for (var i = 0; i <= maxUsedConnectionIndex + step; i += step)
            if (usedConnectionIndexes.indexOf(i) == -1)
                bestPoint = this.calculateInterceptionPoint(sourceBox, targetBox, direction, i, bestPoint);

        for (var i = -step; i >= minUsedConnectionIndex - step; i -= step)
            if (usedConnectionIndexes.indexOf(i) == -1)
                bestPoint = this.calculateInterceptionPoint(sourceBox, targetBox, direction, i, bestPoint);

        return bestPoint;
    },


    calculateInterceptionPoint : function(sourceBox, targetBox, direction, index, bestPoint)
    {
        var centerOffset = this.getCenterOffset(sourceBox, targetBox, index);
        centerOffset.x *= direction;
        centerOffset.y *= direction;

        var sourcePoint = sourceBox.getCenter();
        var targetPoint = targetBox.getCenter();
        sourcePoint.translate(centerOffset.x, centerOffset.y);
        targetPoint.translate(centerOffset.x, centerOffset.y);

        var sourceInterceptions = sourceBox.intersectionWithLine(sourcePoint, targetPoint);
        var targetInterceptions = targetBox.intersectionWithLine(sourcePoint, targetPoint);

        for (var i = 0; i < sourceInterceptions.getSize(); i++) {
            for (var j = 0; j < targetInterceptions.getSize(); j++){
                var distance = sourceInterceptions.get(i).getDistance(targetInterceptions.get(j));
                if (bestPoint.distance == null || distance < bestPoint.distance){
                    bestPoint.distance = distance;
                    bestPoint.index = index;
                    bestPoint.point = sourceInterceptions.get(i);
                }
            }
        }

        return bestPoint;
    },

    getCenterOffset : function(sourceBox, targetBox, index) {
        var sourceCenter = sourceBox.getCenter();
        var targetCenter = targetBox.getCenter();

        var dx = targetCenter.x - sourceCenter.x;
        var dy = targetCenter.y - sourceCenter.y;
        var s = sourceCenter.getDistance(targetCenter);

        var distance = this.CONNECTIONS_DISTANCE * index;
        return new draw2d.geo.Point(distance*dy/s, -Math.sign(dy) * distance*dx/s);
    },

    getUsedConnectionIndexes : function() {
        var usedConnectionIndexes = [];
        var _this = this;
        this.sourcePort.connections.each(function(i, connection){
            if (connection.connectionIndex != null && connection.getId() != _this.getId()) {
                if (connection.sourcePort.getId() == _this.sourcePort.getId() && connection.targetPort.getId() == _this.targetPort.getId())
                    usedConnectionIndexes.push(connection.connectionIndex);
                else if (connection.targetPort.getId() == _this.sourcePort.getId() && connection.sourcePort.getId() == _this.targetPort.getId())
                    usedConnectionIndexes.push(-connection.connectionIndex);
            }
        });

        return usedConnectionIndexes;
    },

    onDisplayProperties : function(id) {
        $("#" + id).append('<label for="connectionName">Связь</label>');
        $("#" + id).append('<input type="text" class="form-control" rows="3" id="connectionName" readonly/>');
        $("#connectionName").val(this.connectionType.name);
    },

    saveToObject : function()
    {
        var sourceId;
        var targetId;
        if (this.sourcePort != null && this.sourcePort.parent != null)
            sourceId = this.sourcePort.parent.getId();
        if (this.targetPort != null && this.targetPort.parent != null)
            targetId = this.targetPort.parent.getId();

        var memento = {
            draw2dId   : this.getId(),
            type       : this.connectionType,
            sourceDraw2dId : sourceId,
            targetDraw2dId : targetId
        }
        return memento;
    },

    loadFromObject : function(memento)
    {
        this.connectionType = memento.type;
        this.setConnectionType(this.connectionType);
    }
});

draw2d.Connection.createConnection=function(sourcePort, targetPort, callback, dropTarget){
    var pos = dropTarget.getAbsolutePosition();
    //pos = dropTarget.canvas.fromCanvasToDocumentCoordinate(pos.x,pos.y);

    var menu = '<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu" style="display:none">';
    for (var i = 0; i < connectionTypes.length; i++) {
        menu += '<li><a data-connector="community.shape.Connection" data-id="' + i + '" tabindex="-1" href="#">' + connectionTypes[i].name + '</a></li>';
    }
    menu += '</ul>';

    var context = $(menu);
    $(".community-schema-modal-body").append(context);

    var removeContext = function() {
        context.remove();
    }

    var removeContextOnEsc = function(e) {
        switch (e.which) {
            case 27:
                removeContext();
                break;
        }
    }

    $(document).bind("click", removeContext);
    $(document).bind("keyup",removeContextOnEsc);

    context.show()
        .css({left:pos.x, top:pos.y})
        .find("a").on("click", function(){
            removeContext();
            $(document).unbind("click", removeContext);
            $(document).unbind("keyup",removeContextOnEsc);
            var connectionType = connectionTypes[$(this).data("id")];
            var connection = null;
            sourcePort.getConnections().each(function(i, c){
                if (c.getConnectionType() != null && c.getConnectionType().id == connectionType.id && c.targetPort == targetPort)
                    connection = new community.shape.FakeConnection(sourcePort.canvas);
            });

            if (connection == null) {
                sourcePort.getConnections().each(function(i, c){
                    if (c.getConnectionType() != null && c.getConnectionType().id == connectionType.id &&
                        c.sourcePort == targetPort &&c.targetPort == sourcePort && !connectionType.reversable)
                        connection = new community.shape.FakeConnection(sourcePort.canvas);
                });
            }

            if (connection == null) {
                connection = eval("new " + $(this).data("connector"));
                connection.setConnectionType(connectionType);
            }
            callback(connection);
        });
};


community.shape.FakeConnection = draw2d.shape.state.Connection.extend({

    init : function(canvas) {
        this.canvas = canvas;
        this.canvas.getCommandStack().undostack.pop();
    },

    setSource: function (port) {
    },

    setTarget: function (port) {
    }
});

community.shape.ManhattanMidpointLocator= draw2d.layout.locator.ConnectionLocator.extend({
    NAME : "community.shape.ManhattanMidpointLocator",

    init: function()
    {
        this._super();
    },

    relocate:function(index, target)
    {
        var conn = target.getParent();
        var points = conn.getVertices();

        var segmentIndex = Math.floor((points.getSize() -2) / 2);
        if (points.getSize() <= segmentIndex+1)
            return;

        var p1 = points.get(segmentIndex);
        var p2 = points.get(segmentIndex + 1);

        var x = ((p2.x - p1.x) / 2 + p1.x - target.getWidth()/2)|0;
        var y = ((p2.y - p1.y) / 2 + p1.y - target.getHeight()/2)|0;

        target.setPosition(x,y - 10);
    }
});
