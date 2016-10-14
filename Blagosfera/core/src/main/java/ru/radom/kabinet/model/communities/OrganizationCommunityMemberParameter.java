package ru.radom.kabinet.model.communities;

import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;

/**
 * Произвольные параметры участника объединения - юр лицо
 * Created by vgusev on 22.10.2015.
 */
@Entity
@Table(name = OrganizationCommunityMemberParameter.TABLE_NAME)
public class OrganizationCommunityMemberParameter extends LongIdentifiable{

    public static final String TABLE_NAME = "organization_community_member_parameters";

    @Column(name = "param_name", length = 10000)
    private String paramName;

    @Column(name = "param_value", length = 10000)
    private String paramValue;

    @JoinColumn(name = "rganization_community_member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private OrganizationCommunityMemberEntity organizationCommunityMember;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public OrganizationCommunityMemberEntity getOrganizationCommunityMember() {
        return organizationCommunityMember;
    }

    public void setOrganizationCommunityMember(OrganizationCommunityMemberEntity organizationCommunityMember) {
        this.organizationCommunityMember = organizationCommunityMember;
    }
}
