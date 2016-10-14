//Подготавливаем модальное окно перевода средств
$(document).ready(function() {
    $(radomEventsManager).bind("accounts.refresh", function(event, data) {
        $.each(data.accounts, function(index, account) {
            $("div#accounts-move-modal option[data-account-type-id=" + account.type.id + "]").html(account.type.name + " (" + account.balance + " Ра)");
        });
    });

    var $amount = $("div#accounts-move-modal input#amount");
    $amount.moneyInput();
    $("div#accounts-move-modal button#apply-button").click(function() {
        var amount = parseFloat($("div#accounts-move-modal input#amount").val());
        if (amount == 0) {
            bootbox.alert("Необходимо задать сумму перевода");
        } else {
            $.radomFingerJsonAjax({
                url : "/account/move.json",
                type : "post",
                contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                data : {
                    from_account_type_id : $("div#accounts-move-modal select#from-account-type-id").val(),
                    to_sharer_id : $("div#accounts-move-modal input#to-sharer-id").val(),
                    amount : amount,
                    sender_comment : $("div#accounts-move-modal textarea#sender-comment").val()
                },
                successRequestMessage : "Перевод успешно выполнен",
                errorMessage : "Ошибка выполнения перевода",
                successCallback : function(response) {
                    Accounts.refresh();
                    $("div#accounts-move-modal option").removeAttr("selected");
                    $("div#accounts-move-modal input#amount").val("0.00");
                    $("div#accounts-move-modal").modal("hide");
                },
                errorCallback : function(response) {

                }
            });
        }
    });
});(function ( $, window, document, undefined ) {
    // определяем необходимые параметры по умолчанию
    var pluginName = 'accountsMoveDialog',
        defaults = {
            propertyName: "value"
        };

    // конструктор плагина
    function accountsMoveDialog( element, options ) {
        this.element = element;
        this.options = $.extend( {}, defaults, options);

        this._defaults = defaults;
        this._name = pluginName;
        this.init();
    }

    accountsMoveDialog.prototype.init = function () {
        var self = this;
        if (this.options.noClick) {
            $("div#accounts-move-modal option").removeAttr("selected");
            $("div#accounts-move-modal input#amount").val("0.00");
            $("div#accounts-move-modal textarea#sender-comment").val("");
            $("div#accounts-move-modal input#to-sharer-id").val(self.options.id);
            $("div#accounts-move-modal input#to-sharer-full-name").val(self.options.fullName);
            $("div#accounts-move-modal").modal("show");
        } else {
            $(this.element).click(function() {
                $("div#accounts-move-modal option").removeAttr("selected");
                $("div#accounts-move-modal input#amount").val("0.00");
                $("div#accounts-move-modal textarea#sender-comment").val("");
                $("div#accounts-move-modal input#to-sharer-id").val(self.options.id);
                $("div#accounts-move-modal input#to-sharer-full-name").val(self.options.fullName);
                $("div#accounts-move-modal").modal("show");
            });
        }
    };

    // Простой декоратор конструктора,
    // предотвращающий дублирование плагинов
    $.fn[pluginName] = function ( options ) {
        return this.each(function () {
            if (!$.data(this, 'plugin_' + pluginName)) {
                $.data(this, 'plugin_' + pluginName,
                    new accountsMoveDialog( this, options ));
            }
        });
    }

})( jQuery, window, document );
