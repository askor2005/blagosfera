<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<h4 style="width: 100%;text-align: center">Обратная связь</h4>
<script id="supportFormTemplate" type="x-tmpl-mustache">
<div class="row">
    <div class="col-xs-8 col-xs-offset-2">
    <form role="form">
        <div class="form-group">
            <label for="email">Ваш e-mail</label>
            <input type="email" class="form-control" id="email">
        </div>
        <div class="form-group">
            <label for="">Тема</label>
            <input type="text" class="form-control" id="theme">
        </div>
        <div class="form-group">
            <label for="supportRequestType">Категория</label>
            <select class="form-control" id="supportRequestType">
            {{#supportRequestTypes}}
            <option value="{{id}}">{{name}}<option>
            {{/supportRequestTypes}}
            </select>
        </div>
        <div class="form-group">
            <label for="description">Текст сообщения</label>
            <textarea class="form-control" rows="10" id="description"></textarea>
        </div>
        <div class="form-group" align="center">
        <div class="g-recaptcha"  data-sitekey="6Lf8GCITAAAAAAuST_60fckQPMdOgQIKa0djjONc"></div>
        </div>
        <div class="form-group text-center">
         <button type="button"  class="btn btn-lg btn-primary" id="submitButton">Сохранить</button>
         </div>
    </form>
    </div>
</div>
</script>
<div id="supportFormTarget">
</div>
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<script type="text/javascript">
    $(document).ready(function() {
        var template = $("#supportFormTemplate").html();
        $.radomJsonGet("/feedback.json", {}, function(response) {
            Mustache.parse(template);
            var rendered = Mustache.render(template, {supportRequestTypes : response.supportRequestTypes});
            $('#supportFormTarget').html(rendered);
            $("#submitButton").click(function(){
                var data =  {captcha : grecaptcha.getResponse(),theme: $('#theme').val(),description : $('#description').val(),
                    email : $('#email').val(),supportRequestTypeId : $('#supportRequestType').val()};
                $.radomJsonPost("/feedback/save.json", data, function(response) {
                    grecaptcha.reset();
                    bootbox.alert("Сообщение отправлено. Мы свяжемся с вами в ближайшее время.");
                    $('#email').val("");
                    $('#theme').val("");
                    $('#description').val("");
                },function(response) {
                    if (response.result == "error") {
                        bootbox.alert(response.message);
                        grecaptcha.reset();
                    }
                } );
            });
        });

    });

</script>
