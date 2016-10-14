//TODO!!! После реализации вычленить отладочную информацию

$(document).ready(function () {

    var reg = undefined;

    //Запрашиваем разрешение на отправку уведомлений, после чего пытаемся подписаться на push уведомления
    //Callback вызывается всегда, даже если права уже заданы и confirm не отобразился.
    Notification.requestPermission(function () {
        registerGcm();
    });

    function registerGcm() {
        //Если пользователь не разрешал получение уведомлений, то выходим
        if (Notification.permission !== 'granted') {
            console.warn('The user has not allowed notifications.');
            return;
        }

        //Проверяем поддержку servce-worker'ов браузером
        //Если поддерживаются, то регистрируем
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('push/gcm/pushWorker' , {
                 scope: "/"
                })
                .then(function (serviceWorkerRegistration) {
                    //Проверяем, поддерживают ли service-worker'ы раузера Notification API
                    if (!('showNotification' in ServiceWorkerRegistration.prototype)) {
                        console.wart("Notifications not supported by service-workers");
                        return;
                    }

                    //Если браузер не поддерживает Push API, то выходим
                    if (!('PushManager' in window)) {
                        console.warn('Push messaging isn\'t supported.');
                        return;
                    }

                    reg = serviceWorkerRegistration;

                    //Проверяем наличие подписки
                    reg.pushManager.getSubscription()
                        .then(function (subscription) {

                            if (!subscription) {
                                //Если подписки нет, то оформляем
                                subscribe(reg);
                                return;
                            } else {
                                sendSubscriptionToServer(subscription);
                            }

                        })
                        .catch(function (err) {
                            console.warn('Error during getSubscription()', err);
                        });
                });
        } else {
            console.warn('Service workers aren\'t supported in this browser.');
        }
    }

    //Отправляем зарегистрированый ID на наш сервер
    function sendSubscriptionToServer(subscription) {
        var endpoint = subscription.endpoint;

        console.log(endpoint);

        if (endpoint.startsWith('https://android.googleapis.com/gcm/send')) {
            var endpointParts = endpoint.split('/');
            var registrationId = endpointParts[endpointParts.length - 1];

            $.radomJsonPost("/push/gcm/register.json", {
                "deviceId": registrationId
            }, function (response) {
                if (response.result == 'success') {
                    //Успешно зарегистрировали gcm устройство в системе. Ничего не делаем.
                }
            }, function (response) {
                //При ошибке ничего не выводим. Для этого есть логи сервера.
            });
        }

    };

    /**
     * Подписывает service-worker'a на уведомления
     */
    function subscribe() {
        reg.pushManager.subscribe({
            userVisibleOnly: true
        }).then(function(subscription) {
            //Успешно подписались. Обрабатываем информацию на нашем сервере
            return sendSubscriptionToServer(subscription);
        });
    };

    /**
     * Отписывает service-worker'a от уведомлений
     */
    function unsubscribe() {
        //Получаем текущую подписку
        reg.pushManager.getSubscription().then(
            function (pushSubscription) {
                // Если подписки нет, то выходим
                if (!pushSubscription) {
                    return;
                }

                //Уничтожаем подписку
                pushSubscription.unsubscribe();
            }).catch(function (e) {
                console.error('Error thrown while unsubscribing from push messaging.', e);
            });
    };

});
