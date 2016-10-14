$(document).ready(function () {
    CommunityToBookAccountsMoveDialog.init();
});

var CommunityToBookAccountsMoveDialog = new function(){

    this.init = function () {

        $(radomEventsManager).bind("accounts.refresh", function (event, data) {
            $.each(data.accounts, function (index, account) {
                $("div#community-to-book-accounts-move-modal option[data-account-type-id=" + account.type.id + "]").html(account.type.name + " (" + account.balance + " Ра)");
            });
        });

        var $amount = $("div#community-to-book-accounts-move-modal input#amount");
        $amount.moneyInput();

        var $apply = $("div#community-to-book-accounts-move-modal button#apply-button");
        $apply.click(function () {
            $("div#community-to-book-accounts-move-modal").modal("hide");
            $.radomJsonPost("/account/community_book_move.json", {
                from_community_id: $("div#community-to-book-accounts-move-modal input#from-community-id").val(),
                to_sharer_id: $("div#community-to-book-accounts-move-modal select#to-sharer-id").val(),
                amount: $("div#community-to-book-accounts-move-modal input#amount").val(),
                sender_comment: $("div#community-to-book-accounts-move-modal textarea#sender-comment").val()
            }, function () {
                Accounts.refresh();
                bootbox.alert("Перевод успешно выполнен.");
            });
        });
    }

    function communityMembersCallback(members) {
        var $select = $("div#community-to-book-accounts-move-modal select#to-sharer-id");
        $select.empty();
        for(var i=0; i<members.length; i++) {
            var $opt = $(document.createElement("option"));
            $opt.val(members[i].id);
            $opt.html(members[i].fullName);
            $select.append($opt);
        }
    }

    this.show = function () {
        var communityId = $("div#community-to-book-accounts-move-modal input#from-community-id").val();
        getCommunityMembers(communityId, communityMembersCallback)

        $("div#community-to-book-accounts-move-modal option").removeAttr("selected");
        $("div#community-to-book-accounts-move-modal input#amount").val("0.00");
        $("div#community-to-book-accounts-move-modal textarea#sender-comment").val("");
        $("div#community-to-book-accounts-move-modal").modal("show");
    }
};