/**
 * Created by aotts on 12.11.2015.
 * Плагин для подключения стилей
 */
define({
    load: function (name, req, onload, config) {
        name = name.trim();
        if(name.indexOf(".css") !== name.length - 4) {
            name = name + ".css"
        }
        var link = document.createElement('link');
        link.rel = 'stylesheet';
        link.type = 'text/css';
        link.href = name;
        document.getElementsByTagName('head')[0].appendChild(link);
        onload();
    }
});
