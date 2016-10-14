package ru.radom.kabinet.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.radom.kabinet.json.TimeStampDateSerializer;

import java.util.Date;

/**
 * Created by ebelyaev on 06.11.2015.
 */
@Data
public class InviteDto {
    private Long id;

    //дата приглашения
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date creationDate;

    //дата истечения приглашения
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date expireDate;

    // адрес электронной почты приглашенного
    private String email;

    //фамилия приглашенного
    private String invitedLastName;

    //имя приглашенного
    private String invitedFirstName;

    //отчество приглашенного
    private String invitedFatherName;

    //пол приглашенного (М / Ж)
    private String invitedGender;

    //признак ручаюсь за него или не ручаюсь
    private boolean isGuarantee;

    //сколько лет знаком
    private int howLongFamiliar;

    private int inviteStatus;
    // 0 - Принято
    // 1 - В ожидании
    // 2 - Просрочено
    // 3 - Отклонено
    // 4 - Профиль перенесён в архив
    // 5 - Профиль удалён

    //дата последней отправки приглашения на почту
    @JsonSerialize(using = TimeStampDateSerializer.class)
    private Date lastDateSending;

    //ссылкка на созданного пользователя если пользователь принял приглашение
    private InvitedUserDto invitedUser;

    private int invitesCount;


}
