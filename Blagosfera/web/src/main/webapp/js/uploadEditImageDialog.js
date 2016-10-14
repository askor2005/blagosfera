$(document).ready(function () {
    UploadEditImageDialog.init();
});

var UploadEditImageDialog = new function() {
    var dialog = this;
    var onSuccessCallback = null;
    var imageUrl = null;
    var angle = 0;

    var $dialog;
    var $cancelButton;
    var $nextButton;

    var $imageWrapper;
    var $editableImage;

    var $imageRotateLeftButton;
    var $imageRotateRightButton;

    // поворачивает $editableImage на alpha градусов от текущего угла
    function rotate(alpha) {
        angle+=alpha;
        $editableImage.css("transform","rotate("+angle+"deg)");
    }

    function onNextButton() {
        if (angle % 360 != 0) {
            $.radomJsonPost("/images/edit.json", {
                src: imageUrl,
                angle: angle
            }, function (r) {
                onSuccess(r.image);
            });
        } else {
            onSuccess(imageUrl);
        }
    }

    function onSuccess(resultUrl) {
        if(onSuccessCallback) {
            onSuccessCallback(dialog,resultUrl);
        }
    }

    function clearAndRefresh() {
        angle = 0;
        rotate(angle);
        $editableImage.attr("src", imageUrl);
    }

    this.init = function () {
        $dialog = $("div#upload-edit-image-dialog");

        $imageWrapper = $dialog.find("#image-wrapper");
        $editableImage = $dialog.find("img#editable-image");

        $imageRotateLeftButton = $dialog.find("#image-rotate-left");
        $imageRotateLeftButton.click(function() {
            rotate(-90);
        });

        $imageRotateRightButton = $dialog.find("#image-rotate-right");
        $imageRotateRightButton.click(function() {
            rotate(90);
        });

        $cancelButton = $dialog.find("button#cancel-button");
        $cancelButton.click(function() {
            dialog.hide();
        });

        $nextButton = $dialog.find("button#next-button");
        $nextButton.click(function() {
            onNextButton();
        });
    }

    this.show = function (params) {
        if(params) {
            onSuccessCallback = params.onSuccess;
            imageUrl = params.imageUrl;
        } else {
            onSuccessCallback = null;
            imageUrl = null;
        }

        clearAndRefresh();
        $dialog.modal("show");
    }

    this.hide = function () {
        $dialog.modal("hide");
    }
};