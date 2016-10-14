//SERVICE WORKER для получения PUSH уведомлений от Google Cloud Messages
//Здесь обрабатываются различные события

'use strict'

var appDomain = '%s';

//Обработчки push'a от GCM
self.addEventListener('push', function (event) {

    console.log('Received a push message', event);

    event.waitUntil(
        clients.matchAll({
            type: "window"
        }).then(function (clientList) {
            for (var i = 0; i < clientList.length; i++) {
                var client = clientList[i];
                //Если хотя бы одна вкладка благосферы в фокусе, то не показываем уведомление
                if (client.focused) {
                    throw new Error();
                }
            }

        }).then(function () {
            return self.registration.pushManager.getSubscription().then(function (subscription) {

                var endpoint = subscription.endpoint;
                if (endpoint.startsWith('https://android.googleapis.com/gcm/send')) {
                    var endpointParts = endpoint.split('/');
                    var deviceId = endpointParts[endpointParts.length - 1];

                    return fetch('/push/gcm/last.json?deviceId=' + deviceId).then(function (response) {

                        return response.json().then(function (data) {
                            if (data.result == 'success') {
                                return self.registration.showNotification(data.title, {
                                    body: data.body,
                                    icon: data.icon,
                                    tag: data.tag,
                                    data: {
                                        link: data.link,
                                    }
                                }).then(function () {
                                    markAsRead(data);
                                });
                            } else if (data.result == 'no_content') {
                                throw new Error();
                            } else if (data.result == 'error') {
                                throw new Error();
                            }
                        })

                    });
                }

            });
        }));

});

self.addEventListener('notificationclick', function (event) {
    console.log('On notification click: ', event.notification.tag);
    // Android doesn't close the notification when you click on it
    // See: http://crbug.com/463146
    //об этом надо почитать
    var url = event.notification.data.link;
    event.notification.close();


    // This looks to see if the current is already open and
    // focuses if it is
    event.waitUntil(
        clients.matchAll({
            type: "window"
        }).then(function (clientList) {
            for (var i = 0; i < clientList.length; i++) {
                var client = clientList[i];
                if (client.url == (appDomain + url) && 'focus' in client)
                    return client.focus();
            }
            if (clients.openWindow) {
                return clients.openWindow(url);
            }
        })
    );
});


function markAsRead(data) {

    if (data.notificationId) {
        setTimeout(function () {
            fetch('/push/gcm/read?notificationId=' + data.notificationId, {
                method: 'POST'
            });
        }, 3000);
    } else if (data.sharerId && data.chatMessageId) {
        setTimeout(function () {
            fetch('/push/gcm/read?chatMessageId=' + data.chatMessageId + '&sharerId=' + data.sharerId, {
                method: 'POST'
            });
        }, 3000);
    }
};