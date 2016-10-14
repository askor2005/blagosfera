(function ($) {
    var activeEditorOnClick = null;

    function initImageUpload(editor, collage, maxCountOfAttachments) {
        editor.addButton('radomImageUpload', {
            tooltip: 'Вставить картинку',
            text: '',
            icon: 'image',
            onclick: function () {
                activeEditorOnClick = editor;
                //console.log(activeEditorOnClick.getElement());

                if (collage && $(activeEditorOnClick.getBody()).find("img").length > maxCountOfAttachments - 1) {
                    bootbox.alert('Одна новость не может содержать более ' + maxCountOfAttachments + ' картинок и видео.');
                    return;
                }

                $.radomUpload("image", "/files/upload.json", ["jpg", "jpeg", "png", "bmp", "gif"], function (response) {
                    var img = $('<img src="' + response.url + '"/>').load(function () {
                        var width = this.width;
                        var height = this.height;

                        if (width > 650) {
                            height = height / width * 650;
                            width = 650;
                        }

                        var imgHTML = '<img src="' + response.url + '" width="' + width + '" height="' + height + '" />';
                        activeEditorOnClick.insertContent(imgHTML);
                    });
                });
            }
        });
    }

    function initCountCharsText(editor) {
        setTimeout(function(){
            try {
                var text = editor.getBody().innerText;
                $("#count_chars_editor_" + editor.id).html("Количество символов: " + text.length);
            } catch (e) {
                initCountCharsText(editor);
            }
        }, 100);
    }

    function initCountChars(editor, radomParams) {
        if (radomParams != null && radomParams.countCharsCheck == true) {
            $("#count_chars_editor_" + editor.id).remove();
            $('textarea#' + editor.id).after("<div id='count_chars_editor_" + editor.id + "'></div>");
            editor.on('keyup', function (e) {
                initCountCharsText(editor);
            });

            initCountCharsText(editor);
        }
    }

    $.fn.radomTinyMCE = function (radomParams) {
        var $this = $(this);
        var plugins = [
            "advlist autolink lists link charmap print preview hr anchor pagebreak",
            "searchreplace wordcount contextmenu visualblocks visualchars code fullscreen",
            "insertdatetime media nonbreaking save table directionality",
            "emoticons template paste textcolor colorpicker textpattern image"];

        var toolbar1 = "insertfile undo redo | styleselect | bold italic | " +
            "alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | " +
            "link radomImageUpload | print preview media | forecolor backcolor emoticons";

        var contextMenuItems = "link image inserttable | cell row column deletetable | copy paste cut";

        var collage = false;
        var maxCountOfAttachments;
        var imagesPerRowInCollage;

        if (radomParams && radomParams.useRadomPlaceholder) {
            plugins.push("radomplaceholder noneditable");
            toolbar1 += " | radomplaceholder";
        }

        if (radomParams && radomParams.useRadomParticipantFilter) {
            plugins.push("radomparticipantfilter noneditable");
            toolbar1 += " | radomparticipantfilter";
        }

        if (radomParams && radomParams.useRadomParticipantCustomFields) {
            plugins.push("radomparticipantcustomfields noneditable");
            toolbar1 += " | radomparticipantcustomfields radomparticipantcustomfieldslist";
        }

        if (radomParams && radomParams.useRadomParticipantCustomText) {
            plugins.push("radomparticipantcustomtext noneditable");
            toolbar1 += " | radomparticipantcustomtext";
        }

        if (radomParams && radomParams.useRadomSystemFields) {
            plugins.push("radomsystemfields noneditable");
            toolbar1 += " | radomsystemfields";
        }

        if (radomParams && radomParams.useRadomCopyPasteFields) {
            plugins.push("copypastefield");
            contextMenuItems += " | copyfield pastefield";
        }

        if (radomParams && radomParams.userRadomGroupFields) {
            plugins.push("radomgroupfields noneditable");
            toolbar1 += " | radomgroupfieldstart | radomgroupfieldend";
        }

        if (radomParams && radomParams.collage) {
            plugins.push("noneditable");
            collage = true;
        }

        if (radomParams && radomParams.maxCountOfAttachments) {
            maxCountOfAttachments = radomParams.maxCountOfAttachments;
        }

        if (radomParams && radomParams.imagesPerRowInCollage) {
            imagesPerRowInCollage = radomParams.imagesPerRowInCollage;
        }

        var onCreateFunction = function () {
        };

        if (radomParams && radomParams.onCreate) {
            onCreateFunction = radomParams.onCreate;
        }

        var getSavedDataKey = function (self) {
            var key = "";

            self.each(function () {
                $.each(this.attributes, function () {
                    if (this.specified) {
                        if (this.name == "class" || this.name == "id" || this.name == "name") {
                            key += this.name + this.value;
                        }
                    }
                });
            });

            key = key.trim();
            var keyParts = key.split(" ");
            key = keyParts.join("");

            return document.location.href + key;
        };

        var saveTempDataFunction = function (editor) {
            var key = getSavedDataKey($this);

            if (tinymce.tempKeysEdititorsInPage == null) {
                tinymce.tempKeysEdititorsInPage = [];

                tinymce.clearTempData = function () {
                    for (var index in tinymce.tempKeysEdititorsInPage) {
                        var tmpKey = tinymce.tempKeysEdititorsInPage[index];
                        radomLocalStorage.removeItem(tmpKey);
                    }
                }
            }

            tinymce.tempKeysEdititorsInPage.push(key);

            editor.on('keyup', function (e) {
                if ($(editor.editorContainer).is(":visible")) {
                    radomLocalStorage.setItem(key, editor.getContent());
                }
            });
        };

        var restoreTempDataFunction = function (editor) {
            var key = getSavedDataKey($this);

            if (radomLocalStorage.getItem(key) != null && radomLocalStorage.getItem(key) != '') {
                try {
                    if ($(editor.editorContainer).is(":visible")) {
                        editor.setContent(radomLocalStorage.getItem(key));
                    } else {
                        setTimeout(function () {
                            restoreTempDataFunction(editor)
                        }, 100);
                    }
                } catch (e) {
                    setTimeout(function () {
                        restoreTempDataFunction(editor)
                    }, 100);
                }
            }
        };

        var handleCollage = function (editor, recursive) {
            var $body = $(editor.getBody());

            //Ищем image'ы в теле
            var $images = $body.find('img');

            if ($images.length > maxCountOfAttachments) {
                editor.undoManager.undo();
                //На случай, когда открыта старая новость с большим числом картинок и видео,
                //а параметр ограничения изменился в меньшую сторону вырезаем лишние картики с конца
                $images = $body.find('img');

                if ($images.length - maxCountOfAttachments > 0) {
                    for (var i = $images.length - 1; i >= maxCountOfAttachments; --i) {
                        $($images[i]).remove();
                    }
                }

                bootbox.alert('Одна новость не может содержать более ' + maxCountOfAttachments + ' картинок и видео.');
                return;
            }

            //В коллаже не должно быть ничего, кроме картинок - обрабатываем
            //Удаляем текстовые узлы
            $.each($body.find(".radom_collage"), function () {
                var $col = $(this);

                $col.contents().filter(
                    function () {
                        return this.nodeType == 3;
                    }
                ).remove();

                //Удаляем элементы
                $col.contents(':not("img")').remove();
            });

            var imagesInBody = $images.length;

            if (imagesInBody == 0) {
                //Обрабатывать нечего
                return;
            }

            var $collage = $body.find(".radom_collage");

            if ($collage.length == 0) {
                //Коллажа нет - создаем
                $body.prepend('<section class="radom_collage mceNonEditable"></section>');
                $collage = $body.find(".radom_collage");
            } else if ($collage.length > 1) {
                //Больше одного коллажа быть не должно
                $.each($collage, function (index) {
                    if (index != 0) {
                        $.each($($collage[index]).find('img'), function () {
                            $($collage[0]).append($(this));
                        });
                    }
                });

                for (var i = $collage.length - 1; i > 0; --i) {
                    $($collage[i]).remove();
                }
            }

            //body всегда начинается с коллажа
            if ($body.contents()[0] != $collage[0]) {
                $body.prepend($collage[0]);
            }

            var imagesInCollage = $($collage[0]).find("img").length;

            if (imagesInBody == imagesInCollage) {
                //Все image'ы в коллаже
                $.each($collage.find('img'), function () {
                    $(this).css('-webkit-box-sizing', 'border-box');
                    $(this).css('box-sizing', 'border-box');
                });

                $($collage[0]).radomCollage(imagesPerRowInCollage);

                if (recursive) {
                    editor.fire('change', ['recursive']);
                }

                return;
            }

            //Включаем "бесхозные" image'ы в коллаж
            $.each($images, function () {
                if (!$.contains($collage[0], this)) {
                    $collage.append($(this));
                }
            });

            $($collage[0]).radomCollage(imagesPerRowInCollage);

            if (recursive) {
                editor.fire('change', ['recursive']);
            }
        };


        if (radomParams && radomParams.checkChange) {
            $this.tinymce({
                language: 'ru',
                language_url: '/js/tinymce/langs/ru.js',
                theme: "modern",
                plugins: plugins,
                contextmenu: contextMenuItems,
                toolbar1: toolbar1,
                content_css: ["/document/service/fieldsStyles", "/css/tinymce.fix.css"],
                object_resizing: !collage,
                readonly: radomParams.readonly ? 1 : 0,
                image_advtab: true,
                height : radomParams ? radomParams.height : null,
                setup: function (editor) {
                    onCreateFunction(editor);
                    if (radomParams == null || radomParams.useTempData == null || radomParams.useTempData == true) {
                        restoreTempDataFunction(editor);
                    }
                    saveTempDataFunction(editor);
                    initImageUpload(editor, collage, maxCountOfAttachments);

                    initCountChars(editor, radomParams);

                    editor.on('change', function (e) {
                        $('textarea#' + editor.id).attr('data-field-changed', true).trigger('click');
                    });
                }
            });
        } else {
            $this.tinymce({
                language: 'ru',
                language_url: '/js/tinymce/langs/ru.js',
                theme: "modern",
                plugins: plugins,
                contextmenu: contextMenuItems,
                toolbar1: toolbar1,
                object_resizing: !collage,
                content_css: ["/document/service/fieldsStyles", "/css/tinymce.fix.css"],
                readonly: radomParams && radomParams.readonly ? 1 : 0,
                image_advtab: true,
                height : radomParams ? radomParams.height : null,
                setup: function (editor) {
                    onCreateFunction(editor);
                    if (radomParams == null || radomParams.useTempData == null || radomParams.useTempData == true) {
                        restoreTempDataFunction(editor);
                    }
                    saveTempDataFunction(editor);
                    initImageUpload(editor, collage, maxCountOfAttachments);

                    initCountChars(editor, radomParams);

                    //Для корректной поддержки коллажа обрабатываем любые изменения
                    if (collage) {
                        editor.on('change', function (e) {
                            if (e[0] == "recursive") {
                                handleCollage(editor);
                            } else {
                                handleCollage(editor, true);
                            }
                        });
                    }
                }
            });
        }
    };
})(jQuery);