$(document).ready(function () {
    IncomingPaymentDialog.init();
});

var IncomingPaymentDialog = new function() {
    var self = this;

    var $dialog;
    var $submit;
    var $form;

    var $accountType;
    var $paymentSystem;
    var $replenishmentRaAmount;
    var $paymentRurAmount;
    var $commission;

    var $secondPart;

    function openRobokassaWindow(inv, crc, amount, login, test, opener) {
        var url = '/ng/robokassa.html?inv=' + inv
            + '&crc=' + crc
            + '&amount=' + amount
            + '&login=' + login
            + '&test=' + test;

        //var robokassaWindow = window.open(url);
        var robokassaWindow = window.open(url, '_blank', 'scrollbars=1,location=0,status=0,width=1024,height=768');

        var timer = window.setInterval(function () {
            if (robokassaWindow.closed) {
                opener.hide();
                window.clearInterval(timer);
            }
        }, 500);
    }

    function onSubmit(e) {
        var $container = $('#incoming-payment-dialog-modal');
        var amountRa = new Number($container.find('#replenishment-ra-amount').val());
        var amount = new Number($container.find('#payment-rur-amount').val());
        var $accountTypeOption = $container.find('#account_type_id option:selected');
        var $paymentSystemOption = $container.find('#payment_system_id option:selected');

        if (amount > 0) {
            if ('robokassa' === $paymentSystemOption.attr('data-bean-name')) {

                $.radomFingerJsonAjax({
                    isSync: false,
                    url : '/payments/robokassa/initpayment.json',
                    type : "post",
                    contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                    data : {
                        account_type_id: $accountTypeOption.val(),
                        payment_system_id: $paymentSystemOption.val(),
                        ra_amount: amountRa,
                        rur_amount: amount,
                        description: 'Пополнение счета'
                    },
                    successRequestMessage : "Транзакция создана успешно",
                    errorMessage : "Ошибка создания транзакции",
                    closeModalOnSuccess: true,
                    successCallback : function (response) {
                        openRobokassaWindow(response.inv, response.crc, amount, response.login, response.test, self);
                    },
                    errorCallback : function(response) {
                    }
                });

                //self.hide();
                e.preventDefault();
                return false;
            }
        } else {
            e.preventDefault();
            return false;
        }
    }

    function onSubmitButtonClick() {
        $form.submit();
    }

    function calculatePaymentAmount() {
        var raAmount = parseFloat($replenishmentRaAmount.val());
        var commission = parseFloat($paymentSystem.find("option:selected").attr("data-ramera-comission"));
        var rurAmount = (raAmount * (100 + commission) / 100).toFixed(2);
        $paymentRurAmount.val(isNaN(rurAmount) ? "0.00" : rurAmount);
    }

    function calculateReplenishmentAmount() {
        var rurAmount = parseFloat($paymentRurAmount.val());
        var commission = parseFloat($paymentSystem.find("option:selected").attr("data-ramera-comission"));
        var raAmount = ((100 * rurAmount) / (100 + commission)).toFixed(2);
        $replenishmentRaAmount.val(isNaN(raAmount) ? "0.00" : raAmount);
    }

    function calculateAmount() {
        if ($paymentRurAmount.is("[readonly]")) {
            calculatePaymentAmount();
        } else {
            calculateReplenishmentAmount();
        }
    }

    function refreshSubmitButton() {
        if($accountType.val() && $paymentSystem.val()&& $replenishmentRaAmount.val()&& $paymentRurAmount.val()) {
            $submit.removeClass("disabled");
        } else {
            $submit.addClass("disabled");
        }
    }

    function refreshSecondPart() {
        if($accountType.val() && $paymentSystem.val()) {
            $secondPart.slideDown();
        } else {
            $secondPart.slideUp();
        }
    }

    function onChangePaymentSystem() {
        if ($paymentSystem.val()) {
            $commission.html("Комиссия " + $paymentSystem.find("option:selected").attr("data-ramera-comission") + "%").slideDown();
        } else {
            $commission.slideUp().html("");
        }
        calculateAmount();
    }

    this.init = function () {
        $dialog = $("div#incoming-payment-dialog-modal");

        $form = $dialog.find("form");
        $form.submit(onSubmit);

        $accountType = $dialog.find("#account_type_id");
        $accountType.change(refreshSecondPart);
        $accountType.change(refreshSubmitButton);

        $paymentSystem = $dialog.find("#payment_system_id");
        $paymentSystem.change(onChangePaymentSystem);
        $paymentSystem.change(refreshSecondPart);
        $paymentSystem.change(refreshSubmitButton);

        $commission = $dialog.find("#incoming-payment-commission");

        $secondPart = $dialog.find("#second-part");

        $replenishmentRaAmount = $dialog.find("#replenishment-ra-amount");
        $replenishmentRaAmount.moneyInput();
        $replenishmentRaAmount.keyup(calculateAmount);
        $replenishmentRaAmount.keyup(refreshSubmitButton);
        $replenishmentRaAmount.click(function() {
            $replenishmentRaAmount.removeAttr("readonly");
            $paymentRurAmount.attr("readonly", "readonly");
        });

        $paymentRurAmount = $dialog.find("#payment-rur-amount");
        $paymentRurAmount.moneyInput();
        $paymentRurAmount.keyup(calculateAmount);
        $paymentRurAmount.keyup(refreshSubmitButton);
        $paymentRurAmount.click(function() {
            $paymentRurAmount.removeAttr("readonly");
            $replenishmentRaAmount.attr("readonly", "readonly");
        });

        $submit = $dialog.find("#incoming-payment-submit");
        $submit.click(onSubmitButtonClick);
    };

    function prepare() {
        $submit.addClass("disabled");
        $secondPart.hide();
        $accountType.val("");
        $paymentSystem.val("");
        $replenishmentRaAmount.val("0.00");
        $replenishmentRaAmount.removeAttr("readonly");
        $paymentRurAmount.val("0.00");
        $paymentRurAmount.removeAttr("readonly");
        $commission.html("");
    }

    this.show = function() {
        prepare();
        $dialog.modal("show");
    };

    this.hide = function() {
        $dialog.modal("hide");
    };
};