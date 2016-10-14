package ru.radom.kabinet.dao.communities.dto;

/**
 *
 * Created by vgusev on 22.11.2015.
 */
public class FieldValueParameterDto {

    private String internalName;

    private String stringValue;

    public FieldValueParameterDto(String internalName, String stringValue) {
        this.internalName = internalName;
        this.stringValue = stringValue;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getStringValue() {
        return stringValue;
    }
}
