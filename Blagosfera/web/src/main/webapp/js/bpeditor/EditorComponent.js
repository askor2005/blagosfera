define([
    "oryx",
    "utils/loadCss",
    "backbone",
    "mousetrap"
], function (oryx, loadCss, Backbone, Mousetrap) {

    var StatusChangedEvent = {
        undo: "undo-enabled",
        redo: "redo-enabled",
        paste: "paste-enabled"
    };

    var EditorComponent = Backbone.View.extend({
        el: jQuery("#canvasSection"),

        jsonModel: {},

        editor: null,

        initialize: function (options) {
            if (options.jsonModel) {
                this.jsonModel = options.jsonModel;
            }
        },

        /**
         * Подготовка, перед построением редактора
         */
        prepare: function () {
            loadCss("/css/bpeditor/editor.css");
            oryx._loadPlugins();
            oryx.Editor.setMissingClasses();
        },

        /**
         * Привязка горячих клавиш после построения редактора
         */
        bindKeyboard: function () {
            var self = this;
            Mousetrap.bind('mod+z', function (e) {
                self.undo();
                return false;
            });

            Mousetrap.bind(['mod+y', 'mod+shift+z'], function (e) {
                self.redo();
                return false;
            });

            Mousetrap.bind('mod+c', function (e) {
                self.copy();
                return false;
            });

            Mousetrap.bind(['mod+v', "shift+insert"], function (e) {
                self.paste();
                return false;
            });

            Mousetrap.bind('del', function (e) {
                self.deleteItem();
                return false;
            });

            Mousetrap.bind('alt++', function (e) {
                self.zoomIn();
                return false;
            });
            Mousetrap.bind('alt+-', function (e) {
                self.zoomOut();
                return false;
            });
        },

        render: function () {
            if (!this.editor) {
                this.prepare();
                this.editor = new oryx.Editor(this.jsonModel);
                this.initUndoRedo();
                this.bindKeyboard();
                this.afterInit();
            }
            return this;
        },

        afterInit: function () {},

        /**
         * Инициализация undo-redo
         */
        initUndoRedo: function () {
            this.undoStack = [];
            this.redoStack = [];

            // Catch all command that are executed and store them on the respective stacks
            this.editor.registerOnEvent(ORYX.CONFIG.EVENT_EXECUTE_COMMANDS, _.bind(function (evt) {

                // If the event has commands
                if (!evt.commands) {
                    return;
                }

                this.undoStack.push(evt.commands);
                this.redoStack = [];

                this.trigger(StatusChangedEvent.undo, true);
                this.trigger(StatusChangedEvent.redo, false);

                // Update
                this.editor.getCanvas().update();
                this.editor.updateSelection();

            }, this));
        },


        //=====commands
        undo: function (services) {

            // Get the last commands
            var lastCommands = this.undoStack.pop();

            if (lastCommands) {
                // Add the commands to the redo stack
                this.redoStack.push(lastCommands);

                // Rollback every command
                for (var i = lastCommands.length - 1; i >= 0; --i) {
                    lastCommands[i].rollback();
                }

                // Update and refresh the canvas
                this.editor.handleEvents({
                    type: ORYX.CONFIG.EVENT_UNDO_ROLLBACK,
                    commands: lastCommands
                });

                // Update
                this.editor.getCanvas().update();
                this.editor.updateSelection();
            }

            this.trigger(StatusChangedEvent.undo, this.undoStack.length !== 0);
            this.trigger(StatusChangedEvent.redo, true);
        },

        redo: function (services) {

            // Get the last commands from the redo stack
            var lastCommands = this.redoStack.pop();

            if (lastCommands) {
                // Add this commands to the undo stack
                this.undoStack.push(lastCommands);

                // Execute those commands
                lastCommands.each(function (command) {
                    command.execute();
                });

                // Update and refresh the canvas
                this.editor.handleEvents({
                    type: ORYX.CONFIG.EVENT_UNDO_EXECUTE,
                    commands: lastCommands
                });

                // Update
                this.editor.getCanvas().update();
                this.editor.updateSelection();
            }

            this.trigger(StatusChangedEvent.undo, true);
            this.trigger(StatusChangedEvent.redo, this.redoStack.length !== 0);
        },

        cut: function () {
            this._getOryxEditPlugin().editCut();
            this.trigger(StatusChangedEvent.paste, true);
        },

        copy: function () {
            this._getOryxEditPlugin().editCopy();
            this.trigger(StatusChangedEvent.paste, true);
        },

        paste: function () {
            this._getOryxEditPlugin().editPaste();
        },

        deleteItem: function () {
            this._getOryxEditPlugin().editDelete();
        },

        addBendPoint: function () {

            var dockerPlugin = this._getOryxDockerPlugin();

            var enableAdd = !dockerPlugin.enabledAdd();
            dockerPlugin.setEnableAdd(enableAdd);
            if (enableAdd) {
                dockerPlugin.setEnableRemove(false);
                document.body.style.cursor = 'pointer';
            } else {
                document.body.style.cursor = 'default';
            }
        },

        removeBendPoint: function () {

            var dockerPlugin = this._getOryxDockerPlugin();

            var enableRemove = !dockerPlugin.enabledRemove();
            dockerPlugin.setEnableRemove(enableRemove);
            if (enableRemove) {
                dockerPlugin.setEnableAdd(false);
                document.body.style.cursor = 'pointer';
            } else {
                document.body.style.cursor = 'default';
            }
        },

        zoomIn: function () {
            this._getOryxViewPlugin().zoom([1.0 + ORYX.CONFIG.ZOOM_OFFSET]);
        },

        zoomOut: function () {
            this._getOryxViewPlugin().zoom([1.0 - ORYX.CONFIG.ZOOM_OFFSET]);
        },

        zoomActual: function () {
            this._getOryxViewPlugin().setAFixZoomLevel(1);
        },

        zoomFit: function () {
            this._getOryxViewPlugin().zoomFitToModel();
        },

        alignVertical: function () {
            this._getOryxArrangmentPlugin().alignShapes([ORYX.CONFIG.EDITOR_ALIGN_MIDDLE]);
        },

        alignHorizontal: function () {
            this._getOryxArrangmentPlugin().alignShapes([ORYX.CONFIG.EDITOR_ALIGN_CENTER]);
        },

        sameSize: function () {
            this._getOryxArrangmentPlugin().alignShapes([ORYX.CONFIG.EDITOR_ALIGN_SIZE]);
        },

        _findPlugin: function (clazz) {
            var res = _.find(this.editor.loadedPlugins, function (plugin) {
                return plugin instanceof clazz;
            });
            if(!res) {
                res = new clazz(this.editor);
                this.editor.loadedPlugins.push(res);
            }
            return res;
        },

        _getOryxViewPlugin: function () {
            return this._findPlugin(ORYX.Plugins.View);
        },

        _getOryxArrangmentPlugin: function () {
            return this._findPlugin(ORYX.Plugins.Arrangement);
        },

        _getOryxDockerPlugin: function () {
            return this._findPlugin(ORYX.Plugins.AddDocker);
        },

        _getOryxEditPlugin: function () {
            return this._findPlugin(ORYX.Plugins.Edit);
        }
    });

    EditorComponent.StatusChangedEvent = StatusChangedEvent;
    return EditorComponent;
});