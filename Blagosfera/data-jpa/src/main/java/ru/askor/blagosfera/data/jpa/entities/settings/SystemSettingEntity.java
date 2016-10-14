package ru.askor.blagosfera.data.jpa.entities.settings;

import org.hibernate.annotations.Type;
import ru.askor.blagosfera.domain.settings.SystemSetting;

import javax.persistence.*;

@Entity
@Table(name = "system_settings")
public class SystemSettingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_settings_id_generator")
    @SequenceGenerator(name = "system_settings_id_generator", sequenceName = "system_settings_id", allocationSize = 1)
    @Column(name = "id")
    private Long id;

	@Column(name = "key", nullable = false)
    @Type(type="text")
	private String key;

	@Column(name = "val", nullable = false)
    @Type(type="text")
	private String value;

	@Column(name = "description")
    @Type(type="text")
	private String description;

    public SystemSettingEntity() {
    }

    public SystemSetting toDomain() {
        SystemSetting systemSetting = new SystemSetting();
        systemSetting.setId(getId());
        systemSetting.setKey(getKey());
        systemSetting.setValue(getValue());
        systemSetting.setDescription(getDescription());
        return systemSetting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemSettingEntity)) return false;

        SystemSettingEntity that = (SystemSettingEntity) o;

        //return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
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

    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}