$(document).ready(function () {
    BookToSharerAccountsMoveDialog.init();
});

var BookToSharerAccountsMoveDialog = {

    init: function () {

        $(radomEventsManager).bind("accounts.refresh", function (event, data) {
            $.each(data.accounts, function (index, account) {
                $("div#book-to-sharer-accounts-move-modal option[data-account-type-id=" + account.type.id + "]").html(account.type.name + " (" + account.balance + " Ра)");
            });
        });

        var $amount = $("div#book-to-sharer-accounts-move-modal input#amount");
        $amount.moneyInput();

        var $apply = $("div#book-to-sharer-accounts-move-modal button#apply-button");
        $apply.click(function () {
            $("div#book-to-sharer-accounts-move-modal").modal("hide");
            $.radomJsonPost("/account/book_sharer_move.json", {
                from_community_id: $("div#book-to-sharer-accounts-move-modal input#from-community-id").val(),
                amount: $("div#book-to-sharer-accounts-move-modal input#amount").val(),
                sender_comment: $("div#book-to-sharer-accounts-move-modal textarea#sender-comment").val()
            }, function () {
                Accounts.refresh();
                bootbox.alert("Перевод успешно выполнен.");
            });
        });
    },

    show: function () {
        $("div#book-to-sharer-accounts-move-modal option").removeAttr("selected");
        $("div#book-to-sharer-accounts-move-modal input#amount").val("0.00");
        $("div#book-to-sharer-accounts-move-modal textarea#sender-comment").val("");
        $("div#book-to-sharer-accounts-move-modal").modal("show");
    }
};