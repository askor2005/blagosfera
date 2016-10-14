package ru.radom.kabinet.model.invite;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import ru.radom.kabinet.json.TimeStampDateDeserializer;

import java.util.Date;

/**
 * Created by ebelyaev on 06.11.2015.
 */
@Getter
public class InviteFilter {
    // Поиск по имени или e-mail
    private String email;

    // Статус приглашения
    private Integer inviteStatus;
    // 0 - Принято
    // 1 - В ожидании
    // 2 - Просрочено
    // 3 - Отклонено
    // 4 - Профиль перенесён в архив
    // 5 - Профиль удалён

    // Дата приглашения
    @JsonDeserialize(using = TimeStampDateDeserializer.class)
    private Date fromDate;
    @JsonDeserialize(using = TimeStampDateDeserializer.class)
    private Date toDate;

    // Дата регистрации
    @JsonDeserialize(using = TimeStampDateDeserializer.class)
    private Date registerFromDate;
    @JsonDeserialize(using = TimeStampDateDeserializer.class)
    private Date registerToDate;

    // Сертифицирован
    private Integer verifiedFilter;
    // -1 - не выбрано
    // 0 - нет
    // 1 - да

    // Дата сертификации
    @JsonDeserialize(using = TimeStampDateDeserializer.class)
    private Date verifiedFromDate;
    @JsonDeserialize(using = TimeStampDateDeserializer.class)
    private Date verifiedToDate;

    // Имя регистратора
    private String verifierName;

    // Созданный поток физических лиц
    private Integer fromSharersCount;
    private Integer toSharersCount;

    // Созданный поток юридических лиц
    private Integer fromOrganizationsCount;
    private Integer toOrganizationsCount;

    // Я ручаюсь
    private Integer guaranteeFilter;
    // -1 - не выбрано
    // 0 - нет
    // 1 - да

    // Пол
    private Integer sexFilter;
    // -1 - не выбрано
    // 0 - Ж
    // 1 - М

    // Количество отправленных приглашений
    private Integer fromInvitesCount;
    private Integer toInvitesCount;

    // Сколько лет знакомы
    private Integer fromFamiliarYears;
    private Integer toFamiliarYears;

    // Является Регистратором
    private Integer registratorLevel;
    // -1 - нет
    // 1 - 1 ранг
    // 2 - 2 ранг
    // 3 - 3 ранг

    // Поле сортировки 0 - ИД, 1 - Дата, 2 - Имя, 3 - Статус
    private int sortColumnIndex;

    private boolean sortDirection;

    private int page;

    private int perPage;


    /*public InviteFilter(String email, Integer inviteStatus, Date fromDate, Date toDate, Date registerFromDate,
                        Date registerToDate, Integer verifiedFilter, Date verifiedFromDate, Date verifiedToDate,
                        String verifierName, Integer fromSharersCount, Integer toSharersCount, Integer fromOrganizationsCount,
                        Integer toOrganizationsCount, Integer guaranteeFilter, Integer sexFilter, Integer fromInvitesCount,
                        Integer toInvitesCount, Integer fromFamiliarYears, Integer toFamiliarYears, Integer registratorLevel,
                        int sortColumnIndex, boolean sortDirection) {
        this.email = email;
        this.inviteStatus = inviteStatus;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.registerFromDate = registerFromDate;
        this.registerToDate = registerToDate;
        this.verifiedFilter = verifiedFilter;
        this.verifiedFromDate = verifiedFromDate;
        this.verifiedToDate = verifiedToDate;
        this.verifierName = verifierName;
        this.fromSharersCount = fromSharersCount;
        this.toSharersCount = toSharersCount;
        this.fromOrganizationsCount = fromOrganizationsCount;
        this.toOrganizationsCount = toOrganizationsCount;
        this.guaranteeFilter = guaranteeFilter;
        this.sexFilter = sexFilter;
        this.fromInvitesCount = fromInvitesCount;
        this.toInvitesCount = toInvitesCount;
        this.fromFamiliarYears = fromFamiliarYears;
        this.toFamiliarYears = toFamiliarYears;
        this.registratorLevel = registratorLevel;
        this.sortColumnIndex = sortColumnIndex;
        this.sortDirection = sortDirection;
    }*/


}
