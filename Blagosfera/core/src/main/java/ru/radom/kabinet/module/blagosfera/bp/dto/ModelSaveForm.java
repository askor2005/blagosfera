package ru.radom.kabinet.module.blagosfera.bp.dto;

/**
 * Created by Otts Alexey on 30.10.2015.<br/>
 * Форма которая приходить при сохранении модели
 */
public class ModelSaveForm {

    private String model;

    private String svg;

    public ModelSaveForm() {
    }

    public ModelSaveForm(String model, String svg) {
        this.model = model;
        this.svg = svg;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }
}
