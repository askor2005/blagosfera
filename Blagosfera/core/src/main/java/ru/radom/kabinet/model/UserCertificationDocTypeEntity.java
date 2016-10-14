package ru.radom.kabinet.model;

import ru.askor.blagosfera.domain.certification.UserCertificationDocType;

import javax.persistence.*;

@Entity
@Table(name = "user_certification_doc_types")
public class UserCertificationDocTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_certification_doc_types_id_generator")
    @SequenceGenerator(name = "user_certification_doc_types_id_generator", sequenceName = "user_certification_doc_types_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "min_files", nullable = false)
    private Integer minFiles;

    public UserCertificationDocTypeEntity() {
    }

    public UserCertificationDocType toDomain() {
        UserCertificationDocType result = new UserCertificationDocType();
        result.setId(getId());
        result.setName(getName());
        result.setTitle(getTitle());
        result.setMinFiles(getMinFiles());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCertificationDocTypeEntity)) return false;

        UserCertificationDocTypeEntity that = (UserCertificationDocTypeEntity) o;

        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getMinFiles() {
        return minFiles;
    }

    public void setMinFiles(Integer minFiles) {
        this.minFiles = minFiles;
    }
}
