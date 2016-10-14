'use strict';

(function (root, factory) {
    if (typeof define === 'function' && define.amd) { // AMD support
        define('askortinymce', ['jquery', 'tinymce'], function (jQuery, tinymce) {
            return (root.askorTinymce = factory(root, jQuery, tinymce));
        });
    } else {
        root.askorTinymce = factory(root, jQuery, tinymce);
    }
}(this, function (root, $, tinymce) {
    //console.log(root, $, tinymce);
    var oldGlobal = root.askorTinymce;
    var pluginName = 'askorTinymce';

    function AskorTinymce(element, options) {
        var self = this;
        self.element = element;
        self.settings = $.extend({}, $.fn[pluginName]['options']['tinymce'], options);
        self.editor = undefined;

        self.destroy = function () {
            self.editor.remove();
            $.removeData(self.element, pluginName);
        };

        self.init = function () {
            tinymce.init($.extend({}, self.settings, {
                target: self.element,
                init_instance_callback: function (editor) {
                    self.editor = editor;
                }
            }));
        };

        self.init(self);
    }

    $.fn[pluginName] = function (options) {
        return this.each(function () {
            if (!$.data(this, pluginName)) {
                $.data(this, pluginName, new AskorTinymce(this, options));
            }
        });
    };

    $.fn[pluginName]['noConflict'] = function () {
        root.askorTinymce = oldGlobal;
        return oldGlobal;
    };

    $.fn[pluginName]['options'] = {
        tinymce: {
            //skin: '../../../../lib/tinymce/custom',
            inline: true,
            automatic_uploads: false,
            image_advtab: true,
            file_browser_callback_types: 'image', // 'file image media'
            elementpath: true,
            save_enablewhendirty: false,
            language: 'ru',
            language_url: '/js/tinymce/langs/ru.js',

            valid_elements: '*[*]',
            //invalid_elements: 'p',

            plugins: [
                "image imagetools",
                "save autosave",
                "code",
                "contextmenu",
                //"fullscreen",
                "advlist autolink lists link charmap print preview hr anchor pagebreak",
                "searchreplace visualblocks wordcount visualchars",
                "insertdatetime media nonbreaking table directionality paste directionality template textcolor colorpicker textpattern"
            ],

            image_class_list: [
                {title: 'original', value: ''},
                {title: 'big', value: 'big'},
                {title: 'half', value: 'half'},
                {title: 'small', value: 'small'}
            ],

            menu: {
                file: {title: 'File', items: 'newdocument | restoredraft | print | fullscreen'},
                edit: {title: 'Edit', items: 'undo redo | cut copy paste pastetext | selectall'},
                insert: {title: 'Insert', items: 'link media | template hr'},
                view: {title: 'View', items: 'visualaid'},
                format: {
                    title: 'Format',
                    items: 'bold italic underline strikethrough superscript subscript | formats | removeformat'
                },
                table: {title: 'Table', items: 'inserttable tableprops deletetable | cell row column'},
                tools: {title: 'Tools', items: 'spellchecker code'}
            },

            menubar: 'file edit insert view format table tools',

            toolbar: ['save restoredraft | fullscreen | insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | print preview media | forecolor backcolor emoticons'],

            contextmenu: 'newdocument restoredraft print fullscreen | undo redo cut copy paste pastetext selectall | link media template hr | visualaid | bold italic underline strikethrough superscript subscript formats removeformat | inserttable tableprops deletetable cell row column | spellchecker code',

            contextmenu_never_use_native: false,

            max_width: 300,

            file_browser_callback: function (field_name, url, type, win) {
                var field = win.document.getElementById(field_name);
                field.value = '';

                if (type == 'image') {
                    var input = $('<input type="file" accept="image/*">');

                    input.change(function () {
                        var file = this.files[0];

                        if (file) {
                            var reader = new FileReader();

                            reader.addEventListener("load", function () {
                                var data = reader.result;

                                if (data && data.startsWith('data:image/')) {
                                    field.value = reader.result;
                                    field.readOnly = true;

                                    $(field).keydown(function (e) {
                                        if ((e.keyCode == 8) || (e.keyCode == 46)) {
                                            field.value = '';
                                            field.readOnly = false;
                                        }
                                    });
                                }
                            }, false);

                            reader.readAsDataURL(file);
                        }
                    });

                    input.click();
                }
            },

            save_onsavecallback: function (editor) {
                editor.uploadImages(function (success) {
                    // TODO save to server

                    var content = editor.getContent();
                    console.log(content);
                });
            },

            images_upload_handler: function (blobInfo, success, failure) {
                alert('ща загрузим картинку на сервер');
                success('https://images.blagosfera.su//images/VGHF3HUFH5J/MRMOHVTOAD.jpg');
            }
        }
    };
}));