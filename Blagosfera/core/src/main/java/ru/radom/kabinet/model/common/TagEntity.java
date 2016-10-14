package ru.radom.kabinet.model.common;

import org.hibernate.validator.constraints.NotEmpty;
import ru.askor.blagosfera.domain.common.Tag;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Сущность тега, используемого в новостях
 */
@Entity
@Table(name = "tags", indexes = @Index(columnList = "text ASC, usage_count DESC", name="tags_text_and_usage_count_index"))
public class TagEntity extends LongIdentifiable {

    /*
     * --------->FIELDS REGION<-------------
     */
    @NotEmpty
    @Column(name="text", length = 32, nullable = false, unique = true)
    private String text;

    @Column(name = "usage_count", nullable = false, columnDefinition = "bigint default 0")
    private Long usageCount;
	/*
     * --------->END FIELDS REGION<-------------
     */


    /*
     * --------->CONSTRUCTORS REGION<-------------
     */
    public TagEntity() { }

    public TagEntity(Tag domain) {
        setId(domain.getId());
        this.text = domain.getText();
        this.usageCount = domain.getUsageCount();
    }

	/*
     * --------->END CONSTRUCTORS REGION<-------------
     */

    /*
     * --------->GETTERS AND SETTERS REGION<-------------
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Long usageCount) {
        this.usageCount = usageCount;
    }
	/*
     * --------->END GETTERS AND SETTERS REGION<-------------
     */


    public Tag toDomain() {
        Tag result = new Tag();

        result.setId(getId());
        result.setText(text);
        result.setUsageCount(usageCount);

        return result;
    }
}
