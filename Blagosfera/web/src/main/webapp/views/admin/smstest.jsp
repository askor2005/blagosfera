<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>


<div class="panel panel-default">
    <div class="panel-heading">
        Тест отправки СМС
    </div>

    <div class="panel-body">
        <div class="input-group">
            <span class="input-group-addon">Способ отправки</span>
            <select class="form-control" id="type">
                <option value="SMS">СМС</option>
                <option value="VIBER">Viber</option>
                <option value="WHATSAPP">WhatsApp</option>
                <option value="TELEGRAM">Telegram</option>
            </select>
        </div>
        <br>
        <div class="input-group">
            <span class="input-group-addon">Номер / id получателя</span>
            <input type="text" class="form-control" id="tel"/>
        </div>
        <br>
        <div class="input-group">
            <span class="input-group-addon">Сообщение</span>
            <input type="text" class="form-control" id="text"/>
            <span class="input-group-btn">
                <button class="btn btn-warning" type="button" onclick="sendMessage()">Отправить</button>
            </span>
        </div>
    </div>
</div>

<script>
    function sendMessage() {
        var type = $('#type').val();
        var tel = $('#tel').val();
        var text = $('#text').val();

        if (type && tel && text) {
            $.radomJsonPostWithWaiter("/admin/smstest/sendsms.json", {type: type, tel: tel, text: text}, function(response) {
                if (response === 'OK')
                    alert('Сообщение отправлено');
                else if (response === 'ERROR')
                    alert('Ошибка отправки сообщения');
                else if (response === 'DISABLED')
                    alert('Отправка сообщений отключена');
                else if (response === 'sms_service.wrong_url')
                    alert('Адрес службы отправки СМС не настроен');
                else if (response === 'sms_service.wrong_api_key')
                    alert('API-ключ службы отправки СМС не настроен');
                else
                    alert(response);
            });
        }
    }
</script>