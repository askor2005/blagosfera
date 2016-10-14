package ru.askor.blagosfera.data.jpa.repositories.rameraListEditor;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import java.util.List;

/**
 *
 */
public interface RameraListEditorItemRepository extends JpaRepository<RameraListEditorItem, Long> {

    List<RameraListEditorItem> findByIdIn(List<Long> ids);

    RameraListEditorItem findByMnemoCode(String code);

}
