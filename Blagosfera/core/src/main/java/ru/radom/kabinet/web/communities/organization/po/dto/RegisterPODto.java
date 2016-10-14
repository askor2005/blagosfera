package ru.radom.kabinet.web.communities.organization.po.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.domain.Address;
import ru.radom.kabinet.json.ShortDateTimeDeserializer;
import ru.radom.kabinet.json.ShortDateTimeSerializer;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Обёртка данных для регистрации ПО
 * Created by vgusev on 12.12.2015.
 */
@Data
public class RegisterPODto {

    /**
     * Создатель запроса на регистрацию
     */
    private Long ownerId;
    /**
     * Полное название ПО на русском
     */
    private String name;
    /**
     * Короткое название ПО на русском
     */
    private String nameShort;
    /**
     * Полное название ПО на английском
     */
    private String engName;
    /**
     * Короткое название ПО на английском
     */
    private String engNameShort;
    /**
     * ИДы учредителей ПО
     */
    private List<Long> founderIds;
    /**
     * Краткое описание целей и задач
     */
    private String shortTargets;
    /**
     * Полное описание целей и задач
     */
    private String fullTargets;
    /**
     * ИД основного вида деятельности
     */
    private Long mainOkvedId;
    /**
     * ИДы дополнительных видов деятельности
     */
    private List<Long> additionalOkvedIds;
    /**
     * Выбранные сфера деятельности
     */
    private List<Long> activityScopeIds;
    /**
     * Устав ПО
     */
    private String organizationRegulations;
    /**
     * Устав ПО был сгенерирован
     */
    private Boolean isGeneratedRegulations;
    /**
     * Юр адрес ПО
     */
    private Address address;
    /**
     * ИД универсального списка - страна юр адреса
     */
    private Long countryId;
    /**
     * Код региона юр адреса
     */
    private String regionCode;
    /**
     * Короткое описание района юр. адреса
     */
    private String districtDescriptionShort;
    /**
     * Короткое описание города юр. адреса
     */
    private String cityDescriptionShort;
    /**
     * Короткое описание улицы юр. адреса
     */
    private String streetDescriptionShort;
    /**
     * Описание строения юр. адреса
     */
    private String buildingDescription;
    /**
     * Описание офиса юр. адреса
     */
    private String officeDescription;
    /**
     * Тип владения офисом по юр адресу
     */
    private Long officeOwnerShipType;
    /**
     * Период аренды офсиа по юр адресу
     */
    private String officeRentPeriod;
    /**
     *
     */
    private List<FieldFilePODto> officeDocuments;
    /**
     * Фактический адрес ПО
     */
    private Address factAddress;
    /**
     * ИД универсального списка - страна фактического адреса
     */
    private Long factCountryId;
    /**
     * Код региона юр адреса
     */
    private String factRegionCode;
    /**
     * Короткое описание района факт. адреса
     */
    private String factDistrictDescriptionShort;
    /**
     * Короткое описание города факт. адреса
     */
    private String factCityDescriptionShort;
    /**
     * Короткое описание улицы факт. адреса
     */
    private String factStreetDescriptionShort;
    /**
     * Описание строения факт. адреса
     */
    private String factBuildingDescription;
    /**
     * Описание офиса факт. адреса
     */
    private String factOfficeDescription;

    /**
     * Тип владения офисом по факт. адресу
     */
    private Long factOfficeOwnerShipType;
    /**
     * Период аренды офсиа по факт. адресу
     */
    private String factOfficeRentPeriod;
    /**
     *
     */
    private List<FieldFilePODto> factOfficeDocuments;
    /**
     * Члены Совета Общества
     */
    private Set<Long> participantsInSovietIds;
    /**
     * Члены ревизионной комиссии
     */
    private Set<Long> participantsInAuditCommitteeIds;
    /**
     * Дата начала собрания
     */
    @JsonSerialize(using = ShortDateTimeSerializer.class)
    @JsonDeserialize(using = ShortDateTimeDeserializer.class)
    private Date startDateBatchVoting;
    /**
     * Количество часов собрания
     */
    //private int countHoursBatchVoting;

    @JsonSerialize(using = ShortDateTimeSerializer.class)
    @JsonDeserialize(using = ShortDateTimeDeserializer.class)
    private Date registrationEndDateBatchVoting;

    @JsonSerialize(using = ShortDateTimeSerializer.class)
    @JsonDeserialize(using = ShortDateTimeDeserializer.class)
    private Date endDateBatchVoting;

    /**
     * Очередь web сокета куда слать сообщение что собрание создано
     */
    private String registerPoOrganizationQueue;
    /**
     * Описание учредительного собрания
     */
    private String batchVotingDescription;

    /**
     * Флаг - открытое\тайное голосование
     */
    private Boolean secretVoting;
    /**
     * Дата окончания действия полномочий членов Совета
     */
    /*@JsonDeserialize(using = TimeStampDateDeserializer.class)
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date sovietMembersOfficeEndDate;*/
    /**
     * Дата окончания действия полномочий председателя Совета
     */
    /*@JsonDeserialize(using = TimeStampDateDeserializer.class)
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date presidentOfSovietOfficeEndDate;*/
    /**
     *
     */
    private String entranceShareFees;
    /**
     *
     */
    private String minShareFees;
    /**
     *
     */
    private String membershipFees;
    /**
     *
     */
    private String communityEntranceShareFees;
    /**
     *
     */
    private String communityMinShareFees;
    /**
     *
     */
    private String communityMembershipFees;
    /**
     * Код формы объединения организации
     */
    private String associationFormCode;
    /**
     *
     */
    /*@JsonDeserialize(using = TimeStampDateDeserializer.class)
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date auditMembersOfficeEndDate;*/

    /**
     * ПО имеет печать и штамп
     */
    private String hasStamp;
    /**
     * Кто утверждает положение ПО
     */
    private String whoApprovePosition;
    /**
     * Количество дней для рассмотрения заявления о выходе из ПО
     */
    private String countDaysToQuiteFromPo;
    /**
     * Кто утверждает дату выплат пайщику
     */
    private String whoApproveDatePay;
    /**
     * Количество месяцев на выплату
     */
    private String countMonthToSharerPay;
    /**
     * Момент с которого начинает отсчитываться время на выплату
     */
    private String startPeriodPay;
    /**
     * Что происходит в случае смерти пайщика
     */
    private String onShareDeath;
    /**
     * Сумма заёмных средств решение взять которуюй принимает Общее собрание
     */
    private String minCreditApproveSovietPO;
    /**
     * Количество МРОТ в сделке решение по которой принимает Общее собрание
     */
    private String minContractSumApproveSovietPO;
    /**
     * Срок на который избираются члены и председатель совета
     */
    private String sovietOfficePeriod;
    /**
     * На какой основе работает председатель совета
     */
    private String presidentOfSovietKindWorking;
    /**
     * Срок на который избираются члены и председатель правления
     */
    private String participantsOfBoardOfficePeriod;
    /**
     * Количество дней между заседаниями правления
     */
    private String countDaysPerMeetingOfBoard;
    /**
     * Кворум членов правления на заседаниях (в процентах)
     */
    private String quorumMeetingOfBoard;
    /**
     * Частота отчета правления перед обществом
     */
    private String boardReportFrequency;
    /**
     * Срок, на который избираются члены ревизионной комиссии
     */
    private String participantsAuditCommitteeOfficePeriod;

    /**
     * ИД должности директора оргранизации
     */
    private Long directorPositionId;

    /**
     * Список филиалов ПО
     */
    private List<RegisterPOBranchDto> branches;
    /**
     * Список представительств ПО
      */
    private List<RegisterPOBranchDto> representations;

    private Boolean isNeedCreateChat;

    private Boolean addChatToProtocol;

    private Long countParticipantsInSoviet;

    private Long countParticipantsInAuditCommittee;

    private String participantsSourceType;

    private String activityTypesText;

    public boolean isPoHasStamp() {
        return BooleanUtils.toBoolean(hasStamp);
    }

    public boolean isNeedCreateChat() {
        return BooleanUtils.toBooleanDefaultIfNull(isNeedCreateChat, false);
    }

    private boolean isAddChatToProtocol() {
        return BooleanUtils.toBooleanDefaultIfNull(addChatToProtocol, false);
    }


    // ИДы блоков для отображения ошибок
    private String errorBlockPoName;
    private String errorBlockPoNameShort;
    private String errorBlockPoEngName;
    private String errorBlockPoEngNameShort;

    private String errorBlockPoFounders;
    private String errorBlockPoShortTargets;
    private String errorBlockPoFullTargets;
    private String errorBlockPoMainOkved;
    private String errorBlockPoAdditionalOkveds;

    private String errorBlockActivityScopes;

    private String errorBlockPoAddress;
    private String errorBlockPoCountry;
    private String errorBlockPoRegion;
    private String errorBlockPoCity;
    private String errorBlockPoStreet;
    private String errorBlockPoBuilding;
    private String errorBlockPoRoom;

    private String errorBlockPoOrganizationRegulations;

    private String errorBlockPoParticipantsInSoviet;
    private String errorBlockPoParticipantsInAuditCommittee;

    private String errorBlockPoStartDateBatchVoting;
    private String errorBlockPoRegistrationEndDateBatchVoting;
    private String errorBlockPoEndDateBatchVoting;

    //private String errorBlockPoCountHoursBatchVoting;

    private String errorBlockPoBatchVotingDescription;
    private String errorBlockPoSecretVoting;

    private String errorBlockPoOfficeOwnerShipType;
    private String errorBlockPoOfficeRentPeriod;

    //private String errorBlockPoSovietMembersOfficeEndDate;
    //private String errorBlockPoPresidentOfSovietOfficeEndDate;

    private String errorBlockPoEntranceShareFees;
    private String errorBlockPoMinShareFees;
    private String errorBlockPoMembershipFees;
    private String errorBlockPoCommunityEntranceShareFees;
    private String errorBlockPoCommunityMinShareFees;
    private String errorBlockPoCommunityMembershipFees;

    private String errorBlockPoAssociationFormBlock;

    //private String errorBlockPoAuditMembersOfficeEndDate;

    private String errorBlockPoSovietOfficePeriod;
    private String errorBlockPoPresidentOfSovietKindWorking;
    private String errorBlockPoParticipantsOfBoardOfficePeriod;
    private String errorBlockPoCountDaysPerMeetingOfBoard;
    private String errorBlockPoQuorumMeetingOfBoard;
    private String errorBlockPoBoardReportFrequency;
    private String errorBlockPoParticipantsAuditCommitteeOfficePeriod;
    private String errorBlockPoHasStamp;
    private String errorBlockPoWhoApprovePosition;
    private String errorBlockPoCountDaysToQuiteFromPo;
    private String errorBlockPoWhoApproveDatePay;
    private String errorBlockPoCountMonthToSharerPay;
    private String errorBlockPoStartPeriodPay;
    private String errorBlockPoOnShareDeath;
    private String errorBlockPoMinCreditApproveSovietPO;
    private String errorBlockPoMinContractSumApproveSovietPO;

    private String errorBlockPoDirectorPosition;

    private String errorBlockPoIsNeedCreateChat;
    private String errorBlockPoAddChatToProtocol;

    private String errorBlockPoCountParticipantsInSoviet;
    private String errorBlockPoCountParticipantsInAuditCommittee;
}
