require.config({
    baseUrl: "/js/",
    urlArgs: "v=" + window._app_version,
    paths: {
        text: "require/text",
        css: "require/css",
        backbone: "backbone/backbone",
        "backbone/components": "backbone/components",
        oryx: "bpeditor/Editor",
        "prototype": "prototype-1.5.1",
        mousetrap: "mousetrap.min",
        "Backbone.ModelBinder": "backbone/Backbone.ModelBinder",
        "Backbone.CollectionBinder": "backbone/Backbone.CollectionBinder",
        "noty": "noty/jquery.noty.packaged",
        "typeahead": "bootstrap-typeahead.js"
    },
    shim: {
        'backbone': {
            deps: ['underscore', "jquery"],
            exports: 'Backbone'
        },
        'underscore': {
            exports: '_'
        },
        oryx: {
            deps: ["autogrow", "prototype", "path_parser", "bpeditor/i18n/translation_signavio_en_us"]
        },
        "bpeditor/i18n/translation_signavio_en_us": {
            deps: ["bpeditor/i18n/translation_en_us"]
        },
        "autogrow": {
            deps: ["jquery"]
        }
    }

});
