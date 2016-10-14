package ru.askor.blagosfera.data.jpa.repositories.rameraListEditor;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditor;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import java.util.List;

/**
 *
 */
public interface RameraListEditorRepository extends JpaRepository<RameraListEditor, Long> {

    RameraListEditor findByName(String name);

}
