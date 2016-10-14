'use strict';

define([
    'app',
    'sockjs-client',
    'stomp-websocket'
], function (app, SockJS, Stomp) {

    app.factory('stompService', function (broadcastService) {
        var stompService = {};

        var socket;
        var stompClient;
        var connected = false;
        var debug = false;

        var subscriptions = new Map();
        var messageQueue = [];

        var getSubscription = function (destination) {
            var subscription = subscriptions[destination];

            if (!subscription) {
                subscription = {"id": null, "destination": destination, "callbacks": [], "type": null};
                saveSubscription(subscription);
            }

            return subscription;
        };

        var saveSubscription = function (subscription) {
            subscriptions[subscription.destination] = subscription;
        };

        var subscribe = function (subscription, prefix) {
            return stompClient.subscribe("" + prefix + subscription.destination, function (message) {
                var data = JSON.parse(message.body);

                subscription.callbacks.forEach(function (callback) {
                    callback(data);
                });

                broadcastService.send('stomp/' + subscription.destination, data);

                if (debug) broadcastService.sendWithTitle('debug', subscription.destination, data);
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

        stompService.connect = function () {
            if (!connected) {
                socket = new SockJS('/stomp_endpoint');
                stompClient = Stomp.over(socket);
                stompClient.heartbeat.outgoing = 30000;
                stompClient.heartbeat.incoming = 30000;
                if (!debug) stompClient.debug = null;

                stompClient.connect({}, function (frame) {
                    connected = true;

                    subscriptions.forEach(function (subscription) {
                        if (subscription.type === "queue") {
                            subscribeToQueue(subscription);
                        } else if (subscription.type === "userQueue") {
                            subscribeToUserQueue(subscription);
                        } else if (subscription.type === "topic") {
                            subscribeToTopic(subscription);
                        } else {
                            if (debug) broadcastService.sendWithTitle('debug', 'stompSubscribeFail', subscription);
                        }
                    });

                    for (var i = 0; i < messageQueue.length; i++) {
                        stompService.send(messageQueue[i].destination, messageQueue[i].data);
                    }

                    broadcastService.sendWithTitle('stomp', 'connect', frame);
                    if (debug) broadcastService.sendWithTitle('debug', 'stompConnect', frame);
                }, function (errorFrame) {
                    connected = false;
                    broadcastService.sendWithTitle('stomp', 'disconnect', errorFrame);
                    if (debug) broadcastService.sendWithTitle('debug', 'stompDisconnect', errorFrame);
                })
            }
        };

        stompService.disconnect = function () {
            if (connected) {
                stompClient.disconnect(function (frame) {
                    connected = false;
                    if (debug) broadcastService.sendWithTitle('debug', 'stompDisconnect', frame);
                });
            }
        };

        stompService.send = function (destination, data) {
            if (connected) {
                stompClient.send("/websocket/" + destination, {}, JSON.stringify(data));
            } else {
                messageQueue.push({"destination": destination, "data": data});
            }
        };

        stompService.subscribeToQueue = function (destination, callback) {
            var subscription = getSubscription(destination);
            if (callback) subscription.callbacks.push(callback);
            subscription.type = "queue";

            if (connected && (!subscription.id)) {
                subscribeToQueue(subscription);
            }

            saveSubscription(subscription);
        };

        stompService.subscribeToUserQueue = function (destination, callback) {
            var subscription = getSubscription(destination);
            if (callback) subscription.callbacks.push(callback);
            subscription.type = "userQueue";

            if (connected && (!subscription.id)) {
                subscribeToUserQueue(subscription);
            }

            saveSubscription(subscription);
        };

        stompService.subscribeToTopic = function (destination, callback) {
            var subscription = getSubscription(destination);
            if (callback) subscription.callbacks.push(callback);
            subscription.type = "topic";

            if (connected && (!subscription.id)) {
                subscribeToTopic(subscription);
            }

            saveSubscription(subscription);
        };

        stompService.unsubscribe = function (destination) {
            var subscription = subscriptions[destination];

            if (subscription) {
                subscription.id.unsubscribe();
            }
        };

        return stompService;
    });

    return app;
});