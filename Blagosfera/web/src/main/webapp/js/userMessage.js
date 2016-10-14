$(document).ready(function(){
    var userMessageModal =
        '<div class="modal fade" role="dialog" id="userMessageWindow" aria-labelledby="userMessageWindowTextLabel" aria-hidden="true">' +
        '<div class="modal-dialog modal-lg">' +
        '<div class="modal-content">' +
        '<div class="modal-header">' +
        '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
        '<h4 class="modal-title">Информационное сообщение</h4>' +
        '</div>' +
        '<div class="modal-body">' +
        '<div id="userMessageContent">' +

        '</div>' +
        '</div>' +
        '<div class="modal-footer">' +
        '<button type="button" class="btn btn-primary" data-dismiss="modal">Закрыть</button>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>';


    $(radomEventsManager).bind("show_user_message", function(event, data) {
        console.log(data);
        var $content = data.content;
        Mustache.parse($content);
        if ($("#userMessageWindow").length == 0) {
            $("body").append(userMessageModal);
        }
        var markup = $(Mustache.render($content, data.parameters));
        $("#userMessageContent").empty();
        $("#userMessageContent").append(markup);
        $("#userMessageWindow").modal("show");
    });
});