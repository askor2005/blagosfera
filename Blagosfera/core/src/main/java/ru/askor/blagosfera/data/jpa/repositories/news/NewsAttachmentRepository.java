package ru.askor.blagosfera.data.jpa.repositories.news;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.news.NewsAttachment;

/**
 * Хранилище сущностей класса NewsAttachment
 */
public interface NewsAttachmentRepository extends JpaRepository<NewsAttachment, Long> {
}
