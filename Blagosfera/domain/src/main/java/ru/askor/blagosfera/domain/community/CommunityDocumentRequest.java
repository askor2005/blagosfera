package ru.askor.blagosfera.domain.community;

import lombok.Data;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 15.07.2016.
 */
@Data
public class CommunityDocumentRequest {

    private Long id;

    private User user;

    private Community community;

    private List<Document> documents = new ArrayList<>();
}
