$(document).ready(function() {

    if (typeof(Storage) !== "undefined" && window.Notification) {

        Notification.requestPermission();

        if (Notification.permission != "granted") {
            return;
        }

        function makeId()
        {
            var text = "";
            var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

            for( var i=0; i < 10; i++ )
                text += possible.charAt(Math.floor(Math.random() * possible.length));

            return text;
        }

        //Ключ для хранения идентификатора страницы, которая будет генерировать уведомление
        var pageForActionKey = "PageIdForGeneratingOfNotification";
        //Ключ-флаг находится ли хоть одна страница в фокусе
        var activePageKey = "IsActivePageForGeneratingOfNotification";


        //Идентификатор страницы Благосферы
        var tabId = makeId();

        //Показывает уведомление
        var showNotification = function(options) {
            var notification = new Notification(options.title, {
                body: options.body,
                icon: options.icon,
                tag: 'chat'
            });

            notification.onclick = function(event) {
                notification.close();
                window.focus();
            };
        };

        //функция ожидания
        var wait = function() {
            var delay = 600;
            var start = new Date().getTime();

            while (new Date().getTime() - start < delay) {}
        }

        var userIsHere = undefined;

        $(window).bind("mousemove", function() {
            userIsHere = true;
            radomLocalStorage.setItem(pageForActionKey, tabId);
            radomLocalStorage.setItem(activePageKey, "true");
        });

        $(window).bind("focus", function() {
            userIsHere = true;
            radomLocalStorage.setItem(pageForActionKey, tabId);
            radomLocalStorage.setItem(activePageKey, "true");
        });

        $(window).bind("blur", function() {
            radomLocalStorage.setItem(activePageKey, "false");
            userIsHere = false;
        });

        $(window).bind("unload", function() {
            if (radomLocalStorage.getItem(pageForActionKey) == tabId) {
                radomLocalStorage.removeItem(pageForActionKey);
            }

            if (userIsHere) {
                radomLocalStorage.setItem(activePageKey, "false");
            }
        });

        Chat.subscribeToNewMessage(function(message) {

            //Не генерируем уведомление, если оно пришло отправителю
            if (CurrentSharer.id == message.sender.id) {
                return;
            }

            //Не генерируем уведомление, если хотя бы одна страница в фокусе
            if (radomLocalStorage.getItem(activePageKey) == "true") {
                return;
            }

            //Если нужная страница была закрыта, то выбираем новую
            if (!radomLocalStorage.getItem(pageForActionKey)) {
                radomLocalStorage.setItem(pageForActionKey, tabId);
                wait();
            }

            //Не генерируем уведомление, если не эта страница последней была в фокусе
            if (radomLocalStorage.getItem(pageForActionKey) != tabId) {
                return;
            }

            var options = {
                title : message.sender.fullName,
                body: message.text,
                icon: message.sender.avatar,
                tag: 'chat'
            };

            showNotification(options);

        });
    }

});