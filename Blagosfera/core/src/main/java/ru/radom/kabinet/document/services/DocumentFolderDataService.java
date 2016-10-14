package ru.radom.kabinet.document.services;

import ru.askor.blagosfera.domain.document.DocumentFolder;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
public interface DocumentFolderDataService {

    DocumentFolder getById(Long id);

    DocumentFolder delete(Long id);

    DocumentFolder save(DocumentFolder documentFolder);

}
