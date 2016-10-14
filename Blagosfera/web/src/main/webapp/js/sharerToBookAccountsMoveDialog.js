$(document).ready(function () {
    SharerToBookAccountsMoveDialog.init();
});

var SharerToBookAccountsMoveDialog = {

    init: function () {

        $(radomEventsManager).bind("accounts.refresh", function (event, data) {
            $.each(data.accounts, function (index, account) {
                $("div#sharer-to-book-accounts-move-modal option[data-account-type-id=" + account.type.id + "]").html(account.type.name + " (" + account.balance + " Ра)");
            });
        });

        var $amount = $("div#sharer-to-book-accounts-move-modal input#amount");
        $amount.moneyInput();

        var $apply = $("div#sharer-to-book-accounts-move-modal button#apply-button");
        $apply.click(function () {
            $("div#sharer-to-book-accounts-move-modal").modal("hide");
            $.radomJsonPost("/account/sharer_book_move.json", {
                from_account_type_id: $("div#sharer-to-book-accounts-move-modal select#from-account-type-id").val(),
                to_community_id: $("div#sharer-to-book-accounts-move-modal input#to-community-id").val(),
                amount: $("div#sharer-to-book-accounts-move-modal input#amount").val(),
                sender_comment: $("div#sharer-to-book-accounts-move-modal textarea#sender-comment").val()
            }, function () {
                Accounts.refresh();
                bootbox.alert("Перевод успешно выполнен.");
            });
        });
    },

    show: function () {
        $("div#sharer-to-book-accounts-move-modal option").removeAttr("selected");
        $("div#sharer-to-book-accounts-move-modal input#amount").val("0.00");
        $("div#sharer-to-book-accounts-move-modal textarea#sender-comment").val("");
        $("div#sharer-to-book-accounts-move-modal").modal("show");
    }
};