package ru.radom.kabinet.model.letterofauthority;

import ru.radom.kabinet.dto.LetterOfAuthorityAttributeDto;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

/**
 * Дополнительные атрибуты доверенности.
 * Created by vgusev on 08.10.2015.
 */
@Entity
@Table(name = "letters_of_authority_attributes")
public class LetterOfAuthorityAttributeEntity extends LongIdentifiable {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "letter_authority_id", nullable = false)
    private LetterOfAuthorityEntity letterOfAuthority;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LetterOfAuthorityEntity getLetterOfAuthority() {
        return letterOfAuthority;
    }

    public void setLetterOfAuthority(LetterOfAuthorityEntity letterOfAuthority) {
        this.letterOfAuthority = letterOfAuthority;
    }

    public LetterOfAuthorityAttributeDto toDto() {
        LetterOfAuthorityAttributeDto letterOfAuthorityAttributeDto = new LetterOfAuthorityAttributeDto();
        letterOfAuthorityAttributeDto.id = this.getId();
        letterOfAuthorityAttributeDto.name = this.getName();
        letterOfAuthorityAttributeDto.value = this.getValue();
        return letterOfAuthorityAttributeDto;
    }
}
