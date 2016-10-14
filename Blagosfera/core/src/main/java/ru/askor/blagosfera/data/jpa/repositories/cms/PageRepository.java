package ru.askor.blagosfera.data.jpa.repositories.cms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.askor.blagosfera.data.jpa.entities.cms.PageEntity;

import java.util.List;

/**
 * Created by vtarasenko on 30.03.2016.
 */
public interface PageRepository  extends JpaRepository<PageEntity, Long>, JpaSpecificationExecutor<PageEntity> {

    List<PageEntity> findByTitleIgnoreCaseContaining(String title);

    PageEntity findOneByPath(String path);
}
