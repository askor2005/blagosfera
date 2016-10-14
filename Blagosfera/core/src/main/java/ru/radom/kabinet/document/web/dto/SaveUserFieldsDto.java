package ru.radom.kabinet.document.web.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.document.userfields.DocumentUserField;

import java.util.List;

/**
 *
 * Created by vgusev on 07.04.2016.
 */
@Data
public class SaveUserFieldsDto {

    Long documentId;

    List<DocumentUserField> documentUserFields;
}
