"use strict";

var RadomStompClient = function () {
    var socket = new SockJS('/stomp_endpoint');
    var stompClient = Stomp.over(socket);
    var idle = true;
    var connected = false;
    var subscriptions = {};
    var messageQueue = [];
    var debug = false;

    stompClient.heartbeat.outgoing = 20000;
    stompClient.heartbeat.incoming = 20000;

    if (!debug) stompClient.debug = null;

    var getSubscription = function (destination) {
        var subscription = subscriptions[destination];

        if (!subscription) {
            subscription = {"id" : null, "destination" : destination, "callbacks" : [], "type" : null};
            saveSubscription(subscription);
        }

        return subscription;
    };

    var saveSubscription = function (subscription) {
        subscriptions[subscription.destination] = subscription;
    };

    var subscribe = function (subscription, prefix) {
        return stompClient.subscribe("" + prefix + subscription.destination, function (message) {
            if (debug) console.log('Received: ' + message);

            subscription.callbacks.forEach(function (callback) {
                callback(JSON.parse(message.body));
            });
        });
    };

    var subscribeToQueue = function (subscription) {
        subscription.id = subscribe(subscription, "/queue/");
    };

    var subscribeToUserQueue = function (subscription) {
        subscription.id = subscribe(subscription, "/user/queue/");
    };

    var subscribeToTopic = function (subscription) {
        subscription.id = subscribe(subscription, "/topic/");
    };

    this.send = function (destination, data) {
        if (connected) {
            stompClient.send("/websocket/" + destination, {}, JSON.stringify(data));
        } else {
            messageQueue.push({"destination" : destination, "data" : data});
        }
    };

    this.subscribeToQueue = function (destination, callback) {
        var subscription = getSubscription(destination);
        subscription.callbacks.push(callback);
        subscription.type = "queue";

        if (connected && (!subscription.id)) {
            subscribeToQueue(subscription);
        }

        saveSubscription(subscription);
    };

    this.subscribeToUserQueue = function (destination, callback) {
        var subscription = getSubscription(destination);
        subscription.callbacks.push(callback);
        subscription.type = "userQueue";

        if (connected && (!subscription.id)) {
            subscribeToUserQueue(subscription);
        }

        saveSubscription(subscription);
    };

    this.subscribeToTopic = function (destination, callback) {
        var subscription = getSubscription(destination);
        subscription.callbacks.push(callback);
        subscription.type = "topic";

        if (connected && (!subscription.id)) {
            subscribeToTopic(subscription);
        }

        saveSubscription(subscription);
    };

    this.connect = function () {
        if (!connected) {
            var self = this;
            stompClient.connect({}, function (frame) {
                if (debug) console.log('Connected: ' + frame);
                //self.send("keep_alive", {"idle": false});
                connected = true;

                $.each(subscriptions, function (index, subscription) {
                    if (subscription.type === "queue") {
                        subscribeToQueue(subscription);
                    } else if (subscription.type === "userQueue") {
                        subscribeToUserQueue(subscription);
                    } else if (subscription.type === "topic") {
                        subscribeToTopic(subscription);
                    } else {
                        if (debug) console.log('Can not subscribe: ' + subscription);
                    }
                });

                for (var i = 0; i < messageQueue.length; i++) {
                    self.send(messageQueue[i].destination, messageQueue[i].data);
                }

                messageQueue = [];
            })
        }
    };

    this.setIdle = function (newIdle) {
        if (idle != newIdle) {
            idle = newIdle;
            if (debug) console.log("idle set to " + idle);
            //this.send("keep_alive", {"idle": false});
        }
    };

    this.getIdle = function () {
        return idle;
    };
};

var radomStompClient;

$(document).ready(function () {
    radomStompClient = new RadomStompClient();
    radomStompClient.connect();

    /*setInterval(function () {
        radomStompClient.send("keep_alive", {idle: radomStompClient.getIdle()});
        radomStompClient.setIdle(true);
    }, 20000);*/

    $(document).mousemove(function () {
        radomStompClient.setIdle(false);
    });

    $(document).click(function () {
        radomStompClient.setIdle(false);
    });

    $(document).keydown(function () {
        radomStompClient.setIdle(false);
    });

    radomStompClient.subscribeToUserQueue("contact_online", function (messageBody) {
        $(radomEventsManager).trigger("contact.online", messageBody);
    });

    radomStompClient.subscribeToUserQueue("contact_offline", function (messageBody) {
        $(radomEventsManager).trigger("contact.offline", messageBody);
    });

    radomStompClient.subscribeToUserQueue("contact_add", function (messageBody) {
        $(radomEventsManager).trigger("contact.add", messageBody);
    });

    radomStompClient.subscribeToUserQueue("contact_delete", function (messageBody) {
        $(radomEventsManager).trigger("contact.delete", messageBody);
    });

    radomStompClient.subscribeToUserQueue("contact_accepted", function (messageBody) {
        $(radomEventsManager).trigger("contact.accepted", messageBody);
    });

    radomStompClient.subscribeToUserQueue("news_create", function (messageBody) {
        $(radomEventsManager).trigger("news.create", messageBody);
    });

    radomStompClient.subscribeToUserQueue("news_edit", function (messageBody) {
        $(radomEventsManager).trigger("news.edit", messageBody);
    });

    radomStompClient.subscribeToUserQueue("news_delete", function (messageBody) {
        $(radomEventsManager).trigger("news.delete", messageBody);
    });

    /*radomStompClient.subscribeToUserQueue("community_request", function (messageBody) {
        $(radomEventsManager).trigger("community-member.request", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_cancel_request", function (messageBody) {
        $(radomEventsManager).trigger("community-member.cancel-request", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_accept_request", function (messageBody) {
        $(radomEventsManager).trigger("community-member.accept-request", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_reject_request", function (messageBody) {
        $(radomEventsManager).trigger("community-member.reject-request", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_accept_invite", function (messageBody) {
        $(radomEventsManager).trigger("community-member.accept-invite", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_reject_invite", function (messageBody) {
        $(radomEventsManager).trigger("community-member.reject-invite", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_leave", function (messageBody) {
        $(radomEventsManager).trigger("community-member.leave", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_cancel_request_leave", function (messageBody) {
        $(radomEventsManager).trigger("community-member.cancel-request-leave", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_request_to_leave", function (messageBody) {
        $(radomEventsManager).trigger("community-member.request-to-leave", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_join", function (messageBody) {
        $(radomEventsManager).trigger("community-member.join", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_exclude", function (messageBody) {
        $(radomEventsManager).trigger("community-member.exclude", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_invite", function (messageBody) {
        $(radomEventsManager).trigger("community-member.invite", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_cancel_invite", function (messageBody) {
        $(radomEventsManager).trigger("community-member.cancel-invite", messageBody);
    });*/

    radomStompClient.subscribeToUserQueue("show_user_message", function (messageBody) {
        $(radomEventsManager).trigger("show_user_message", messageBody);
    });

    radomStompClient.subscribeToUserQueue("community_member_event", function (messageBody) {
        $(radomEventsManager).trigger("community-member.event", messageBody);
    });

    radomStompClient.subscribeToUserQueue("profile_archived", function (messageBody) {
        $(radomEventsManager).trigger("profile.archived", messageBody);
    });

    radomStompClient.subscribeToUserQueue("profile_deleted", function (messageBody) {
        $(radomEventsManager).trigger("profile.deleted", messageBody);
    });

    radomStompClient.subscribeToUserQueue("mark_as_read_notification", function (messageBody) {
        $(radomEventsManager).trigger("notification.mark-as-read", messageBody);
    });

    radomStompClient.subscribeToUserQueue("delete_notification", function (messageBody) {
        $(radomEventsManager).trigger("notification.delete", messageBody);
    });

    radomStompClient.subscribeToTopic("rating_update", function (messageBody) {
        $(radomEventsManager).trigger("rating.update", messageBody);
    });

    radomStompClient.subscribeToTopic("registration_request_update", function (messageBody) {
        $(radomEventsManager).trigger("registrationRequest.updateRequest", messageBody);
    });

    radomStompClient.subscribeToUserQueue("session_alive", function(message){
    });

    radomStompClient.subscribeToUserQueue("session_close", function () {
        $.ajax({
            type: "get",
            dataType: "json",
            url: "/sharer/check.json",
            success: function (response) {
                if (response.result = "success") {
                    console.log("check OK");
                } else {
                    console.log("check not OK");
                    window.location.href = "/?force_closed";
                }
            },
            error: function () {
                console.log("ajax error");
            }
        });
    });
});
