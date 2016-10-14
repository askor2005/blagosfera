(function ($) {
    $(document).ready(function () {
        $(".remove").click(function () {
            if (confirm("Удалить шаблон?")) {
                var $tr = $(this).closest("tr");
                $.ajax("/email/delete/" + $tr.attr("id"), {
                    type: "DELETE"
                }).done(function () {
                    $tr.remove();
                });
            }
        });
        
        $(".test").click(function() {
            if (confirm("Отправить тестовое письмо на Ваш Email?")) {
                $.ajax("/email/test/" + $("form input#id[type=hidden]").val()).done(function() {
                	bootbox.alert("Email сообщение отправлено");
                });
            }
        });
    });
})(jQuery);