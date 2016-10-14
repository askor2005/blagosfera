$(document).ready(function () {
    OutgoingPaymentDialog.init();
});

var OutgoingPaymentDialog = new function() {
    var self = this;

    var $dialog;
    var $submit;
    var $form;

    var $accountType;
    var $paymentSystem;
    var $receiver;
    var $outgoingRaAmount;
    var $obtainRurAmount;
    var $commission;

    var $secondPart;

    function onSubmit(e) {
        e.preventDefault();

        var data = {};
        data.account_type_id = $accountType.val();
        data.payment_system_id = $paymentSystem.val();
        data.receiver = $receiver.val();

        if($obtainRurAmount.is("[readonly]")) {
            data.ra_amount = $outgoingRaAmount.val();
        } else {
            data.rur_amount = $obtainRurAmount.val();
        }

        $.radomJsonPost("/payment/outgoing/init.json", data, function() {
            bootbox.alert("Ваша заявка на вывод средств поступила в обработку");
            self.hide();
        });
    }

    function onSubmitButtonClick() {
        $form.submit();
    }

    function calculatePaymentAmount() {
        var raAmount = parseFloat($outgoingRaAmount.val());
        var commission = parseFloat($paymentSystem.find("option:selected").attr("data-ramera-comission"));
        var rurAmount = (raAmount * (100 - commission) / 100).toFixed(2);
        $obtainRurAmount.val(isNaN(rurAmount) ? "0.00" : rurAmount);
    }

    function calculateReplenishmentAmount() {
        var rurAmount = parseFloat($obtainRurAmount.val());
        var commission = parseFloat($paymentSystem.find("option:selected").attr("data-ramera-comission"));
        var raAmount = ((100 * rurAmount) / (100 - commission)).toFixed(2);
        $outgoingRaAmount.val(isNaN(raAmount) ? "0.00" : raAmount);
    }

    function calculateAmount() {
        if ($obtainRurAmount.is("[readonly]")) {
            calculatePaymentAmount();
        } else {
            calculateReplenishmentAmount();
        }
    }

    function refreshSubmitButton() {
        if($accountType.val() && $paymentSystem.val()&& $receiver.val()&& $outgoingRaAmount.val()&& $obtainRurAmount.val()) {
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
        $dialog = $("div#outgoing-payment-dialog-modal");

        $form = $dialog.find("form");
        $form.submit(onSubmit);

        $accountType = $dialog.find("#account_type_id");
        $accountType.change(refreshSecondPart);
        $accountType.change(refreshSubmitButton);

        $paymentSystem = $dialog.find("#payment_system_id");
        $paymentSystem.change(onChangePaymentSystem);
        $paymentSystem.change(refreshSecondPart);
        $paymentSystem.change(refreshSubmitButton);

        $commission = $dialog.find("#outgoing-payment-commission");

        $secondPart = $dialog.find("#second-part");

        $receiver = $dialog.find("#receiver");
        $receiver.keyup(refreshSubmitButton);

        $outgoingRaAmount = $dialog.find("#outgoing-ra-amount");
        $outgoingRaAmount.moneyInput();
        $outgoingRaAmount.keyup(calculateAmount);
        $outgoingRaAmount.keyup(refreshSubmitButton);
        $outgoingRaAmount.click(function() {
            $outgoingRaAmount.removeAttr("readonly");
            $obtainRurAmount.attr("readonly", "readonly");
        });

        $obtainRurAmount = $dialog.find("#obtain-rur-amount");
        $obtainRurAmount.moneyInput();
        $obtainRurAmount.keyup(calculateAmount);
        $obtainRurAmount.keyup(refreshSubmitButton);
        $obtainRurAmount.click(function() {
            $obtainRurAmount.removeAttr("readonly");
            $outgoingRaAmount.attr("readonly", "readonly");
        });

        $submit = $dialog.find("#outgoing-payment-submit");
        $submit.click(onSubmitButtonClick);
    }

    function prepare() {
        $submit.addClass("disabled");
        $secondPart.hide();
        $accountType.val("");
        $paymentSystem.val("");
        $receiver.val("");
        $outgoingRaAmount.val("0.00");
        $outgoingRaAmount.removeAttr("readonly");
        $obtainRurAmount.val("0.00");
        $obtainRurAmount.removeAttr("readonly");
        $commission.html("");
    }

    this.show = function() {
        prepare();
        $dialog.modal("show");
    }

    this.hide = function() {
        $dialog.modal("hide");
    }

}