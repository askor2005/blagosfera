package ru.askor.blagosfera.domain.listEditor;

/**
 * Created by vgusev on 02.06.2015.
 * Типы виджетов.
 */
public enum RameraListEditorType {
    COMBOBOX {
        @Override
        public String toString() {
            return "combobox";
        }
    },
    CHECKBOX {
        @Override
        public String toString() {
            return "checkbox";
        }
    },
    RADIOBUTTON {
        @Override
        public String toString() {
            return "radiobutton";
        }
    };
}
