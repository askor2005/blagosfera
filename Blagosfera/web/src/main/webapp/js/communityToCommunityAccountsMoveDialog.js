$(document).ready(function () {
    CommunityToCommunityAccountsMoveDialog.init();
});

var CommunityToCommunityAccountsMoveDialog = new function(){

    this.init = function () {
        var $amount = $("div#community-to-community-accounts-move-modal input#amount");
        $amount.moneyInput();

        var $apply = $("div#community-to-community-accounts-move-modal button#apply-button");
        $apply.click(function () {
            $("div#community-to-community-accounts-move-modal").modal("hide");
            $.radomJsonPost("/account/community_community_move.json", {
                from_community_id: $("div#community-to-community-accounts-move-modal input#from-community-id").val(),
                to_community_id: $("div#community-to-community-accounts-move-modal select#to-community-id").val(),
                amount: $("div#community-to-community-accounts-move-modal input#amount").val(),
                sender_comment: $("div#community-to-community-accounts-move-modal textarea#sender-comment").val()
            }, function () {
                Accounts.refresh();
                bootbox.alert("Средства заморожены. Перевод будет выполнен когда документ будет подписан всеми требуемыми участниками.");
            });
        });
    }

    function communitiesCallback(communities) {
        var $select = $("div#community-to-community-accounts-move-modal select#to-community-id");
        $select.empty();
        for(var i=0; i<communities.length; i++) {
            var $opt = $(document.createElement("option"));
            $opt.val(communities[i].id);
            $opt.html(communities[i].name);
            $select.append($opt);
        }
    }

    this.show = function () {
        getCommunities(communitiesCallback);

        $("div#community-to-community-accounts-move-modal option").removeAttr("selected");
        $("div#community-to-community-accounts-move-modal input#amount").val("0.00");
        $("div#community-to-community-accounts-move-modal textarea#sender-comment").val("")
        $("div#community-to-community-accounts-move-modal").modal("show");
    }

};