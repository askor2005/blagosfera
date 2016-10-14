package ru.radom.kabinet.dto;

import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityAttributeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Dto для представления данных в extjs grid
 * Created by vgusev on 08.10.2015.
 */
public class LetterOfAuthorityAttributesGridDto {

    private boolean success = true;

    private int total = 0;

    private List<LetterOfAuthorityAttributeDto> items = new ArrayList<>();

    public LetterOfAuthorityAttributesGridDto(boolean success, int total, List<LetterOfAuthorityAttributeDto> items) {
        this.success = success;
        this.total = total;
        this.items = items;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<LetterOfAuthorityAttributeDto> getItems() {
        return items;
    }

    public void setItems(List<LetterOfAuthorityAttributeDto> items) {
        this.items = items;
    }

    public static LetterOfAuthorityAttributesGridDto successDtoFromDomain(int count, List<LetterOfAuthorityAttributeEntity> entityItems) {
        List<LetterOfAuthorityAttributeDto> attributeDtos = new ArrayList<>();
        for (LetterOfAuthorityAttributeEntity attributeEntity : entityItems) {
            attributeDtos.add(attributeEntity.toDto());
        }
        return new LetterOfAuthorityAttributesGridDto(true, count, attributeDtos);
    }

    public static LetterOfAuthorityAttributesGridDto failDto() {
        return new LetterOfAuthorityAttributesGridDto(false, 0, null);
    }
}
