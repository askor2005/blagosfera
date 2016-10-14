$(document).ready(function () {
    CommunityToSharerAccountsMoveDialog.init();
});

var CommunityToSharerAccountsMoveDialog = new function(){

    this.init = function () {
        var $amount = $("div#community-to-sharer-accounts-move-modal input#amount");
        $amount.moneyInput();

        var $apply = $("div#community-to-sharer-accounts-move-modal button#apply-button");
        $apply.click(function () {
            $("div#community-to-sharer-accounts-move-modal").modal("hide");


            $.radomJsonPost("/account/community_sharer_move.json", {
                from_community_id: $("div#community-to-sharer-accounts-move-modal input#from-community-id").val(),
                to_sharer_id: $("div#community-to-sharer-accounts-move-modal select#to-sharer-id").val(),
                amount: $("div#community-to-sharer-accounts-move-modal input#amount").val(),
                sender_comment: $("div#community-to-sharer-accounts-move-modal textarea#sender-comment").val()
            }, function () {
                Accounts.refresh();
                bootbox.alert("Средства заморожены. Перевод будет выполнен когда документ будет подписан всеми требуемыми участниками.");
            });
        });
    }

    function sharersCallback(sharers) {
        var $select = $("div#community-to-sharer-accounts-move-modal select#to-sharer-id");
        $select.empty();
        for(var i=0; i<sharers.length; i++) {
            var $opt = $(document.createElement("option"));
            $opt.val(sharers[i].id);
            $opt.html(sharers[i].fullName);
            $select.append($opt);
        }
    }

    this.show = function () {
        getSharers(sharersCallback);

        $("div#community-to-sharer-accounts-move-modal option").removeAttr("selected");
        $("div#community-to-sharer-accounts-move-modal input#amount").val("0.00");
        $("div#community-to-sharer-accounts-move-modal textarea#sender-comment").val("");
        $("div#community-to-sharer-accounts-move-modal").modal("show");
    }
};