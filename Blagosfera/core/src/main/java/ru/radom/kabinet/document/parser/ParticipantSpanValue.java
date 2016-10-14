package ru.radom.kabinet.document.parser;

import lombok.Data;
import org.w3c.dom.Element;
import ru.radom.kabinet.document.dto.DocumentParticipantSourceDto;
import ru.radom.kabinet.document.model.DocumentParticipantEntity;

/**
 *
 * Created by vgusev on 10.02.2016.
 */
@Data
public class ParticipantSpanValue {

    private DocumentParticipantSourceDto participant;

    private Element span;

    private String value;

    public ParticipantSpanValue(DocumentParticipantSourceDto participant, String value) {
        this.participant = participant;
        this.value = value;
    }
}
