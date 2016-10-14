(function($) {

    var mustacheTemplate =
    '<div class="sharer_container"><a href="/sharer/{{sharer.ikp}}">{{sharer.mediumName}}</a> {{dateTime}}</div>'+
    '<div class="sharer_container" style="float: left">'+
    '    <a href="/sharer/{{sharer.ikp}}"><img data-src="holder.js/40x/40" alt="40x40";'+
    '        src="{{sharer.avatar40}}" data-holder-rendered="true"'+
    '        class="media-object img-thumbnail tooltiped-avatar"'+
    '        data-sharer-ikp="{{sharer.ikp}}" data-placement="left" /></a>'+
    '</div>';

    $.noty.themes.radomTheme= {
        name: 'radomTheme',
        modal: {
            css: {
                position: 'fixed',
                width: '100%',
                height: '100%',
                backgroundColor: '#000',
                zIndex: 10000,
                opacity: 0.6,
                display: 'none',
                left: 0,
                top: 0
            }
        },
        style: function () {

            var containerSelector = this.options.layout.container.selector;
            $(containerSelector).addClass('list-group');

            this.$closeButton.append('<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>');
            this.$closeButton.addClass('close');

            this.$bar.addClass("list-group-item").css('padding', '0px');

            switch (this.options.type) {
                case 'alert':
                case 'notification':
                    this.$bar.addClass("list-group-item-info");
                    break;
                case 'warning':
                    this.$bar.addClass("list-group-item-warning");
                    break;
                case 'error':
                    this.$bar.addClass("list-group-item-danger");
                    break;
                case 'information':
                    this.$bar.addClass("list-group-item-info");
                    break;
                case 'success':
                    this.$bar.addClass("list-group-item-success");
                    break;
            }

            this.$message.css({
                fontSize: '13px',
                lineHeight: '16px',
                textAlign: 'left',
                padding: '8px 10px 9px',
                width: 'auto',
                position: 'relative'
            });

            this.$message.on("click", function (e) {
                    var $e = $(e.target);
                    if ($e.is(".sharer_container") || $e.parents(".sharer_container").length !== 0){
                        e.stopImmediatePropagation();
                    }
            });
            if(!this._sharerRendered) {
                var data = this.options.data;
                if (data && data.sharer) {
                    var messageMinHeight = 65;
                    if (!data.sharer.avatar40 && Images) {
                        data.sharer.avatar40 = Images.getResizeUrl(data.sharer.avatar, "c40")
                    }
                    if (!data.dateTime && data.date) {
                        data.dateTime = data.date.split(" ")[1];
                    } else {
                        data.dateTime = ""
                    }
                    if(data.sharer.mediumName && data.sharer.mediumName.length !== 0) {
                        messageMinHeight += 15;
                    }
                    this.$message.css("min-height", messageMinHeight + "px");
                    if (Mustache != null) {
                        var $place = this.$bar.find(".sharer_after");
                        if ($place.length === 1) {
                            $place.after(Mustache.render(mustacheTemplate, data));
                        }
                    }
                }
                if (this.options.callback && this.options.callback.onContentClick) {
                    var self = this;
                    var cb = this.options.callback.onContentClick;
                    this.$message.on('click', function (e) {
                        var $e = $(e.target);
                        if ($e.is(".noty_message") || $e.is(".noty_text")) {
                            cb.call(self, e);
                        }
                    });
                }
            }
            this._sharerRendered = true;

        },
        callback: {
            onShow: function () {

            },
            onClose: function () {
            }
        }
    };

})(jQuery);