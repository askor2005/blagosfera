
community.View = draw2d.Canvas.extend({

    init : function(id, width, height){
        this._super(id, width, height);

        this.setScrollArea("#"+id);
        this.installEditPolicy(new draw2d.policy.canvas.CoronaDecorationPolicy());
        //this.installEditPolicy(new draw2d.policy.canvas.FadeoutDecorationPolicy());

        // fix for bootstrap modal window
        // after destroy/init canvas top becames null and absolute offset is wrong
        this.paper.canvas.style.left='-0.5px';
        this.paper.canvas.style.top='0px';
    }
})