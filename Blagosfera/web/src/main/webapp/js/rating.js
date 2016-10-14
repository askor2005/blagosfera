function renderRatingControl(reloadData) {
    $(".rating").rating();
    if(reloadData) {
        var outMap = new Object();
        $('.rating').each(function (idx, el) {
            var type = $(this).attr('data-type');
            if (!(type in outMap)) outMap[type] = [];
            outMap[type].push($(this).attr('data-id'));
        });
        for (var typeKey in outMap) {
            $.radomJsonGet("/rating/list.json", {
                contentIds: outMap[typeKey].join(", "),
                contentType: typeKey
            }, $.proxy(function (type, response) {
                $(radomEventsManager).trigger("rating.updateList." + type, response);
            }, this, typeKey));
        }
    }

}

$.widget("ramera.rating", {

    //inactiveColor: 'grey',
    //upColor: '#3c763d',
    //downColor: '#8a6d3b',
	inactiveClass : "rating-inactive-link",
    upClass : "rating-up-link",
    downClass : "rating-down-link",
    mediumFontSize: '18px',
    inactiveUpTooltip: "Высказаться ЗА",
    inactiveDownTooltip: "Высказаться ПРОТИВ",
    activeUpTooltip: "Вы высказались ЗА",
    activeDownTooltip: "Вы высказались ПРОТИВ",

    _create: function () {
        var that = this;
        if($(this.element).attr("rendered")) return;
        that.contentId = $(this.element).data("id");
        that.contentType = $(this.element).data("type");
        that.contentTitle = $(this.element).data("title");
        that.all = $(this.element).data("all");
        if (!that.all) {
            that.all = "0";
        }

        that.left = $('<a style="cursor: pointer;"><i class="fa fa-thumbs-o-up"></i></a>').appendTo($(this.element));
        that.centerWrapper = $('<a style="cursor: pointer; min-width: 50px; display: inline-block; text-align:center;"></a>').appendTo($(this.element));
        that.center = $('<span class="text-muted" style="margin:7px; font-size: 18px; font-weight: bold;">' + that.all + '</span>').appendTo(that.centerWrapper);
        that.right = $('<a style="cursor: pointer;"><i class="fa fa-thumbs-o-down"></i></a>').appendTo($(this.element));
        that._configureView();
        $(that.left).click($.proxy(that._onPositiveVote, that));
        $(that.right).click($.proxy(that._onNegativeVote, that));
        $(that.center).click($.proxy(that._onShowDetails, that));

        $(radomEventsManager).bind("rating.updateList." + that.contentType, $.proxy(that._onUpdateListReceive, that));
        $(radomEventsManager).bind("rating.update", $.proxy(that._onUpdateReceive, that));

        that.weight = $(this.element).data("weight");
        if(!that.weight){
            that.weight = 0;
        } else {
            that._updateState(that.weight);
        }
        $(this.element).attr("rendered", true);
    },
    _configureView: function(){
        var that = this;
        that.right.css({
            "font-size": that.mediumFontSize
        });
        
        that.right.addClass(that.inactiveClass);
        
        that.right.radomTooltip({
            placement : "right",
            container: "body",
            title: that.inactiveDownTooltip
        });


        that.left.css({
            "font-size": that.mediumFontSize
        });

        that.left.addClass(that.inactiveClass);
        
        that.left.radomTooltip({
            placement : "right",
            container: "body",
            title: that.inactiveUpTooltip
        });
    },
    _onUpdateReceive: function (e, data) {
        var that = $(this)[0];
        var rating = data.rating;
        if(rating && (rating.contentId == that.contentId) && ((rating.contentType == that.contentType))){
            $(this.center).text(data.count);
        }
    },
    _onUpdateListReceive: function (e, data) {
        var that = $(this)[0];
        //console.log(that.contentType + that.contentId);
        //console.log(data);
        var dataVal = data[that.contentType + that.contentId];
        if (dataVal) {
            $(that.center).text(dataVal.count);
            that._updateState(dataVal.me);
        }
    },
    _updateData: function(response){
        var that = $(this)[0];
        $(that.center).text(response.count);
        var rating = response.rating;
        if(rating){
            that._updateState(rating.weight);
        }
    },
    _updateState: function(weight) {
        var that = $(this)[0];
        if(weight){
            that.weight = weight;
            
            that.left.removeClass(that.upClass).removeClass(that.downClass).removeClass(that.inactiveClass);
            that.right.removeClass(that.upClass).removeClass(that.downClass).removeClass(that.inactiveClass);
            
            that.left.addClass((that.weight > 0) ? that.upClass : that.inactiveClass);
            that.right.addClass((that.weight < 0) ? that.downClass : that.inactiveClass);

            that.left.attr('data-original-title', (that.weight > 0) ? that.activeUpTooltip : that.inactiveUpTooltip);
            var $tooltipLeft = $("#" + that.left.attr("aria-describedby"));
            $tooltipLeft.find("div.tooltip-inner").html((that.weight > 0) ? that.activeUpTooltip : that.inactiveUpTooltip);

            that.right.attr('data-original-title', (that.weight < 0) ? that.activeDownTooltip : that.inactiveDownTooltip);
            var $tooltipRight = $("#" + that.right.attr("aria-describedby"));
            $tooltipRight.find("div.tooltip-inner").html((that.weight < 0) ? that.activeDownTooltip : that.inactiveDownTooltip);
        }
    },
    _onNegativeVote: function (e) {
        var that = this;
        if(that.weight < 0) return;
        $.radomJsonPost("/rating/create.json", {
            contentId: that.contentId,
            contentType: that.contentType,
            direction: "-"
        }, $.proxy(that._updateData, that));
    },
    _onPositiveVote: function (e) {
        var that = this;
        if(that.weight > 0) return;
        $.radomJsonPost("/rating/create.json", {
            contentId: that.contentId,
            contentType: that.contentType,
            direction: "+"
        }, $.proxy(that._updateData, that));
    },
    _onShowDetails: function(){
        var that = this;
        if(RatingDetailsDialog) RatingDetailsDialog.show(that.contentId, that.contentType, that.contentTitle);
    }
});