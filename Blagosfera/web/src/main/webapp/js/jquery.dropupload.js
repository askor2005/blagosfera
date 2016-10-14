(function ($, window) {

    function uploadFile(url, file, dropCallBackResponse, statusCallBack, uploadSuccessCallBack) {
        var form_data = new FormData();
        form_data.append("file", file)
        $.ajax({
            type: "POST",
            //contentType: attr("enctype", "multipart/form-data"),
            processData: false,
            contentType: false,
            url: url,
            data: form_data,
            xhr: function() {
                var xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener("progress", function(evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = evt.loaded / evt.total;
                        percentComplete = parseInt(percentComplete * 100);
                        if (percentComplete != 100) {
                            statusCallBack(dropCallBackResponse, percentComplete);
                        }
                    }
                }, false);

                return xhr;
            },
            success: function (data) {
                uploadSuccessCallBack(dropCallBackResponse, data);
            }
        });
    }


    $.fn.radomDropUpload = function (url, dragOverCallBack, dragLeaveCallBack, dropCallBack, statusCallBack, uploadSuccessCallBack) {
        var result = this;
        if (typeof(window.FileReader) == 'undefined') {
            result = false;
        }

        if (result != false) {
            this.on("dragover", function () {
                dragOverCallBack();
                return false;
            });

            this.on("dragleave", function () {
                dragLeaveCallBack();
                return false;
            });

            this.on("drop", function (event) {
                event.preventDefault();
                var file = event.originalEvent.dataTransfer.files[0];
                // Результат выполнения внешенй функции
                var dropCallBackResponse = dropCallBack(file);
                uploadFile(url, file, dropCallBackResponse, statusCallBack, uploadSuccessCallBack);
            });
        }

        return result;
    };

})(jQuery, window);