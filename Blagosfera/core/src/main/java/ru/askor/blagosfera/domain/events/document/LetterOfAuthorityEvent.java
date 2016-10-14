package ru.askor.blagosfera.domain.events.document;

import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.model.letterofauthority.LetterOfAuthorityEntity;
import ru.radom.kabinet.web.lettersofauthority.dto.LetterOfAuthorityDto;

/**
 * Created by vgusev on 28.09.2015.
 */
public class LetterOfAuthorityEvent extends BlagosferaEvent {

    private LetterOfAuthorityDto letterOfAuthorityDto;

    private LetterOfAuthorityEventType letterOfAuthorityEventType;

    public LetterOfAuthorityEvent(Object source, LetterOfAuthorityEntity letterOfAuthority, LetterOfAuthorityEventType letterOfAuthorityEventType) {
        super(source);
        this.letterOfAuthorityDto = letterOfAuthority.toDto();
        this.letterOfAuthorityEventType = letterOfAuthorityEventType;
    }

    public LetterOfAuthorityDto getLetterOfAuthorityDto() {
        return letterOfAuthorityDto;
    }

    public LetterOfAuthorityEventType getLetterOfAuthorityEventType() {
        return letterOfAuthorityEventType;
    }
}
