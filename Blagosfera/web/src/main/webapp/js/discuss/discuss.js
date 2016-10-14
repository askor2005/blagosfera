/**
 * Created by ahivin on 07.11.2014.
 */

window.discuss = (function () {

    function Discuss() {

    }

    var discuss = {
        /**
         * Если от сервера приходит пустой массив при подгрузке очередной порции комметариев - значит больше загружать не нужно
         */
        dataOnScroll: true,
        topic: "",
        replySelector: ".btn-reply",
        discussion: null,
        container: null,
        loaderAnchor: $("<div id='listScrollAnchor' class='scrollAnchor'>&nbsp;</div>"),
        inEditMode: false,
        currentCommentFormParent: null,
        oldHtml: "",
        commentsQueue: [],

        init: function (data) {
            this.discussion = data["discussion"];
            this.container = data["container"];
            this.topic = data["topic"];
            this.currentUser = data["currentUser"];
            this.subscribe();
            console.log("discussion: " + this.discussion);
            this.container.append(this.loaderAnchor);
            __this = this;
            if (this.loaderAnchor.visible()){
                this.stopLoading();
            }

            $(document).scroll(function () {
                console.log("scroll");
                if ($("#listScrollAnchor").visible()) {
                    console.log("load next portion of data");
                    __this.loadNextPortion();
                }
            })

        },

        bindReplyButtons: function () {
            __this = this;

            $(__this.replySelector).each(function (index, btn) {
                __this.bindReplyButton(btn);
                return true;
            });
        },

        bindVoters: function() {
        	var self = this;
        	$('.panel .panel-footer .pull-right a').each(function(index, item) {
        		console.log("bind");
        		self.bindVoter(this);
        	});
        },
        
        bindEditButtons: function() {
        	var self = this;
        	$('.btn-edit').each(function(index, btn) {
        		self.bindEditButton(btn);
        	});
        },
        
        bindReplyButton: function (btn) {
            var b = $(btn);
            b.click({"comment-id": b.attr('comment-id')}, function (event) {
                discuss.showNewCommentForm(event.data["comment-id"]);
            })
        },
        
        bindVoter: function(anchor) {
        	$(anchor).click(function() {
        		var $this = $(this),
        		    id = $this.data("id"),
        		    action = $this.data("vote");
        		$.ajax({
        			url: "/discuss/comment/"+id+"/vote",
        			data: {action: action},
        			dataType: "json"
        		}).done(function(data) {
        			var addClass = "rating ";
        			if(data === 0) addClass += "text-muted";
        			else if(data > 0) addClass += "text-success";
        			else addClass += "text-danger";
        			$this
        			  .siblings(".rating")
        			  .text(" " + data + " ")
        			  .removeClass()
        			  .addClass(addClass);
        		});
        			console.log("voted");
        		
        	});
        },
        
        bindEditButton: function(btn) {
        	var self = this;
        	$(btn).click(function() {
        		console.log("edit comment");
        		$('#comment-text_'+$(this).data("id")).trigger('ramera:comment');
        	});
        },
        
        bindEditables: function() {
        	var self = this;
        	$('div.editable').each(function(index, item) {
        		self.bindEditable(item);
        	});
        },
        
        bindEditable: function(field) {
        	$(field).editable('/discuss/comment/edit.json', {
                name: 'comment',
        		type      : 'textarea',
                cancel    : 'Отмена',
                submit    : 'OK',
                indicator : '<img src="/img/loading.gif">',
                event: 'ramera:comment',
                rows: 4
                	
            });
        },
        
        addComment: function (data) {
            $.ajax({
                dataType: "json",
                url: "/discuss/comment/new",
                method: "post",
                data: data,
                success: function (data) {
                    window.location.reload();
                }
            }).fail(
                function () {
                    alert('fail')
                }
            );
        },
        showNewCommentForm: function (commentId) {
            var __this = this;
            if(__this.currentCommentFormParent) {
            	__this.currentCommentFormParent.hide();
            	__this.currentCommentFormParent.html(__this.oldHtml);
            	__this.currentCommentFormParent.fadeIn();
            	__this.currentCommentFormParent.find("button").each(function(){
            		__this.bindReplyButton($(this));
            		return true;
            	});
            	__this.currentCommentFormParent = null;
            	__this.oldHtml = "";
            }
            e = $("#footer_" + commentId);
            oldHtml = e.html();
            e.hide();
            e.html($("#messageFormTemplate").html());
            e.fadeIn();
            button = e.find("button[type=submit]");
            button.prop('disabled', true);
            button.attr("comment-id", commentId);

            textField = e.find("#messageField");
            __this.currentCommentFormParent = e;
            __this.oldHtml = oldHtml;
            textField.focus();
            
            button.click({
                "comment-id": commentId,
                "text-field": textField,
                "old-html": oldHtml,
                "container": e
            }, function (event) {
                var container = event.data["container"];
                container.html(oldHtml);
                __this.currentCommentFormParent = null;
                __this.oldHtml = "";
                
                container.find(".btn-reply").each(function (index, btn) {
                    __this.bindReplyButton(btn);
                    return true;
                });
                
                container.find(".btn-edit").each(function (index, btn) {
                    __this.bindEditButton(btn);
                    return true;
                });
                
                container.find('.pull-right a').each(function(index, item) {
            		__this.bindVoter(item);
            		return true;
            	});
                
                __this.sendNewComment(
                    {
                        "comment": event.data["text-field"].val(),
                        "parent": event.data["comment-id"]
                    }
                );

            });
            textField.bind("change paste keyup", {"button": button}, function (event) {
                /*нелья отправлять пустые сообщения*/
                event.data["button"].prop('disabled', $(this).val().length == 0);
            });
        },
        subscribe: function () {
            __this = this;
            radomStompClient.subscribeToTopic(this.topic, function (messageBody) {
                console.log("message received:" + messageBody);
                __this.commentsQueue.push(messageBody);
                if(!__this.currentCommentFormParent) {
                	while(true) {
                		var mb = __this.commentsQueue.shift();
                		if(!mb) break;
                		__this.showNewComment(mb, true);
                	}
                }
            });
        },
        sendNewComment: function (comment) {
            radomStompClient.send("discuss_" + this.discussion, comment);
        },
        showNewComment: function (comment, scrollTo) {
            console.log("new comment " + comment.id + " insertAfter: " + comment.insertAfter);
            var newCommentBlockId = "#comment_" + comment.id;
            if ($(newCommentBlockId).size() > 0) {
                // this comment is already shown
                return;
            }
            var parentCommentBlockId = "#comment_" + comment.parent;
            var prevCommentBlockId = (comment.insertAfter) ? "#comment_" + comment.insertAfter : comment.parent;
            var depth = parseInt($(parentCommentBlockId).attr("depth"));
            comment.depth = depth + 1;
            comment.margin = 20 * (comment.depth-1);
            comment.ownerAvatar = Images.getResizeUrl(comment.ownerAvatar, "c90");
            comment.isOwn = (comment.ownerId == this.currentUser);
            var template = $("#commentTpl").html();
            
            html = Mustache.to_html(template, comment);

            var e = $(html);
            e.hide();
            e.insertAfter(prevCommentBlockId);
            e.fadeIn();
            var __this = this;
            e.find(".btn-reply").each(function (index, btn) {
                __this.bindReplyButton(btn);
                return true;
            });
            
            e.find(".btn-edit").each(function (index, btn) {
                __this.bindEditButton(btn);
                return true;
            });
            
            e.find('.pull-right a').each(function(index, item) {
        		__this.bindVoter(item);
        		return true;
        	});
            
            e.find('.editable').editable('/discuss/comment/edit.json', {
                name: 'comment',
        		type      : 'textarea',
                cancel    : 'Отмена',
                submit    : 'OK',
                indicator : '<img src="/img/loading.gif">',
                event: 'ramera:comment',
                rows: 4
                	
            });;
            renderRatingControl();
        },
        insertComments: function (data) {
            console.log("comments loaded:" + data.length);
            data.sort(function (a, b) {
                return (a.insertAfter < b.insertAfter) ? 1 : -1;
            })
            __this = this;
            data.forEach(function (e, index) {
                __this.showNewComment(e, false);
            });
        },
        stopLoading: function () {
            this.dataOnScroll = false;
            this.loaderAnchor.hide();
        },
        loadNextPortion: function () {
            if (!this.dataOnScroll) {
                return;
            }
            var elementsLoaded = $(".comment").size();
            console.log("already loaded: " + elementsLoaded);
            var __this = this;

            $.ajax({
                dataType: "json",
                url: "/discuss/comment/list/" + __this.discussion,
                data: {
                    "discussion": this.discussion,
                    "start": elementsLoaded,
                    "limit": 10
                },
                success: function (data) {
                    if (data.length == 0) {
                        __this.stopLoading();
                    }
                    __this.insertComments(data)
                }
            }).fail(
                function () {
                    alert('fail')
                }
            );
        }
    };

    return discuss;
}());