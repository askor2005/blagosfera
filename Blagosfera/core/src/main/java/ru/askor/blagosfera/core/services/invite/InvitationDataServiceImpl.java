package ru.askor.blagosfera.core.services.invite;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.services.registrator.RegistratorDataService;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.invite.InviteRelationshipTypeRepository;
import ru.askor.blagosfera.data.jpa.repositories.invite.InvitationRepository;
import ru.askor.blagosfera.data.jpa.specifications.invite.InviteSpecifications;
import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.invite.InviteRelationshipTypeDomain;
import ru.askor.blagosfera.domain.registrator.RegistratorDomain;
import ru.askor.blagosfera.domain.user.SharerStatus;
import ru.radom.kabinet.dto.InviteDto;
import ru.radom.kabinet.dto.InvitedUserDto;
import ru.radom.kabinet.dto.InvitesTableDataDto;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.invite.InvitationEntity;
import ru.radom.kabinet.model.invite.InviteFilter;
import ru.radom.kabinet.model.invite.InviteRelationshipType;
import ru.radom.kabinet.services.registration.RegistratorService;
import ru.radom.kabinet.web.invite.InviteController;
import ru.radom.kabinet.web.invite.dto.InviteCountDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vtarasenko on 15.04.2016.
 */
@Transactional
@Service
public class InvitationDataServiceImpl implements InvitationDataService {
    private static final Logger logger = LoggerFactory.createLogger(InviteController.class);
    //TODO cache включить после того как будет переделан поиск по фильтру с джоинами
    private static final String  GET_BY_EMAIL_CACHE="GET_BY_EMAIL_CACHE_INVITE";
    private static final String GET_BY_ID_CACHE="GET_BY_ID_CACHE_INVITE";
    private static final String GET_BY_HASH_CACHE="GET_BY_HASH_CACHE_INVITE";
    private static final String SEARCH_CACHE="SEARCH_CACHE_INVITE";
    private static final String EXISTS_CACHE="EXISTS_CACHE_INVITE";
    @PersistenceContext(unitName = "kabinetPU", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InviteRelationshipTypeRepository inviteRelationshipRepository;

    @Autowired
    private RegistratorDataService registratorDataService;
    @Override
    //@Cacheable(GET_BY_EMAIL_CACHE)
    public Invitation findFirstByEmail(String email){
        InvitationEntity invite = invitationRepository.findFirstByEmailOrderByIdDesc(email);
        return invite != null ? invite.toDomain() : null;
    }
    @Override
    //@Cacheable(GET_BY_HASH_CACHE)
    public Invitation getByHashUrl(String hashUrl) {
        InvitationEntity invite = invitationRepository.findFirstByHashUrlOrderByIdDesc(hashUrl);
        return invite != null ? invite.toDomain() : null;
    }
   // @Cacheable(EXISTS_CACHE)
    @Override
    public boolean existsInvites(String email) {
        Specifications<InvitationEntity> specifications = Specifications.where(InviteSpecifications.emailEqual(email)).and(InviteSpecifications.statusEqual(0)).and(InviteSpecifications.expireDateGreater(new Date(System.currentTimeMillis())));
        return invitationRepository.count(specifications) > 0;
    }

    @Override
    //@CacheEvict({GET_BY_ID_CACHE,GET_BY_HASH_CACHE,GET_BY_EMAIL_CACHE,EXISTS_CACHE,SEARCH_CACHE})
    public Invitation save(Invitation invite) {
        InvitationEntity inviteEntity = null;
        boolean create = false;
        if (invite.getId() != null) {
            create = true;
            inviteEntity = invitationRepository.findOne(invite.getId());
            assert inviteEntity != null;
        }
        else {
            inviteEntity = new InvitationEntity();
            inviteEntity.setRelationships(new ArrayList<>());
        }
        for (InviteRelationshipTypeDomain inviteRelationshipTypeDomain : invite.getRelationships()) {
            InviteRelationshipType inviteRelationshipType = inviteRelationshipRepository.findOne(inviteRelationshipTypeDomain.getId());
            inviteEntity.getRelationships().add(inviteRelationshipType);
        }
        inviteEntity.setUser(invite.getUser() != null ? userRepository.findOne(invite.getUser().getId()) : null);
        inviteEntity.setCreationDate(invite.getCreationDate());
        inviteEntity.setEmail(invite.getEmail());
        inviteEntity.setExpireDate(invite.getExpireDate());
        inviteEntity.setGuarantee(invite.getGuarantee());
        inviteEntity.setHashUrl(invite.getHashUrl());
        inviteEntity.setHowLongFamiliar(invite.getHowLongFamiliar());
        inviteEntity.setStatus(invite.getStatus());
        inviteEntity.setInvitedFatherName(invite.getInvitedFatherName());
        inviteEntity.setInvitedFirstName(invite.getInvitedFirstName());
        inviteEntity.setInvitedGender(invite.getInvitedGender());
        inviteEntity.setInvitedLastName(invite.getInvitedLastName());
        inviteEntity.setInvitedSharer(invite.getInvitee() != null ? userRepository.findOne(invite.getInvitee().getId()) : null);
        inviteEntity.setLastDateSending(invite.getLastDateSending());
        inviteEntity.setInvitesCount(invite.getInvitesCount());
        inviteEntity = invitationRepository.saveAndFlush(inviteEntity);
        if (create) {
            invite.setId(inviteEntity.getId());
        }
        return inviteEntity.toDomain();
    }

    @Override
    //@Cacheable(GET_BY_ID_CACHE)
    public Invitation getById(Long id) {
        InvitationEntity invite = invitationRepository.findOne(id);
        return invite != null ? invite.toDomain() : null;
    }
    private static final String SELECT_INVITES_BY_FILTER_SQL =
            "SELECT I.*, \n" +
                    "S.ikp, S.deleted, S.verified, S.verifier_id,S.registered_at,S.verification_date,S.search_string,\n" +
                    "FN.string_value AS v_first_name, LN.string_value AS v_last_name, SN.string_value AS v_second_name,\n" +
                    "SV.search_string AS sv_search_string, \n" +
                    "CASE WHEN SS.sharers_stream IS NULL THEN 0 ELSE SS.sharers_stream END, \n" +
                    "CASE WHEN OS.organizations_stream IS NULL THEN 0 ELSE OS.organizations_stream END, \n" +
                    "P.mnemo AS registrator_level\n" +
                    "FROM invites I\n" +
                    "LEFT JOIN (WITH sharers_streams (sharer_id,sharers_stream) AS (\n" +
                    "\tWITH RECURSIVE tree (sharer_id, invited_sharer_id, ROOT) AS (\n" +
                    "\t    -- start from bottom-level entries\n" +
                    "\t    SELECT sharer_id, invited_sharer_id, sharer_id\n" +
                    "\t    FROM invites I\n" +
                    "\t\tINNER JOIN sharers S ON S.id=I.invited_sharer_id\n" +
                    "\t    WHERE S.verified=true\n" +
                    "\n" +
                    "\t    UNION --ALL\n" +
                    "\n" +
                    "\t    -- join the next level, add the number of children to that of already counted ones\n" +
                    "\t    SELECT I.sharer_id, I.invited_sharer_id, ROOT\n" +
                    "\t    FROM invites I \n" +
                    "\t\tINNER JOIN tree ON I.sharer_id=tree.invited_sharer_id \n" +
                    "\t\tINNER JOIN sharers S ON S.id=I.invited_sharer_id\n" +
                    "\t    WHERE S.verified=true\n" +
                    "\t) \n" +
                    "\tSELECT root, COUNT(*) \n" +
                    "\tFROM tree \n" +
                    "\tGROUP BY root\n" +
                    ") SELECT * FROM sharers_streams) AS SS ON SS.sharer_id=I.invited_sharer_id\n" +
                    "LEFT JOIN  (WITH organizations_streams (sharer_id,organizations_stream) AS (\n" +
                    "\tWITH RECURSIVE tree (sharer_id, invited_sharer_id, ROOT, name) AS (\n" +
                    "\t    -- start from bottom-level entries\n" +
                    "\t    SELECT I.sharer_id, I.invited_sharer_id, I.sharer_id, C.name\n" +
                    "\t    FROM invites I\n" +
                    "\t\tINNER JOIN communities C ON I.invited_sharer_id=C.creator_id\n" +
                    "\t    WHERE C.verified=true AND C.deleted=false\n" +
                    "\n" +
                    "\t    UNION --ALL\n" +
                    "\n" +
                    "\t    -- join the next level, add the number of children to that of already counted ones\n" +
                    "\t    SELECT I.sharer_id, I.invited_sharer_id, ROOT, C.name\n" +
                    "\t    FROM invites I \n" +
                    "\t\tINNER JOIN tree ON I.sharer_id=tree.invited_sharer_id\n" +
                    "\t\tINNER JOIN communities C ON I.invited_sharer_id=C.creator_id\n" +
                    "\t    WHERE C.verified=true AND C.deleted=false\n" +
                    "\t)\n" +
                    "\tSELECT root, COUNT(*) + (SELECT COUNT(id) FROM communities C WHERE C.verified=true AND C.deleted=false AND creator_id=root)\n" +
                    "\tFROM tree \n" +
                    "\tGROUP BY root\n" +
                    ") SELECT * FROM organizations_streams) AS OS ON OS.sharer_id=I.invited_sharer_id\n" +
                    "LEFT JOIN sharers S ON S.id=I.invited_sharer_id\n" +
                    "LEFT JOIN sharers SV ON S.verifier_id=SV.id\n" +
                    "LEFT JOIN (\n" +
                    "\tSELECT CM.sharer_id, CP.mnemo\n" +
                    "\tFROM community_members CM\n" +
                    "\tINNER JOIN community_members_posts CMP ON CM.id=CMP.member_id\n" +
                    "\tINNER JOIN community_posts CP ON CMP.post_id=CP.id\n" +
                    "\tWHERE CP.mnemo ILIKE 'registrator.%'\n" +
                    ") AS P ON I.invited_sharer_id=P.sharer_id\n" +
                    "LEFT JOIN (\n" +
                    "\tSELECT FV.object_id, FV.string_value\n" +
                    "\tFROM field_values FV\n" +
                    "\tINNER JOIN fields F ON F.id=FV.field_id \n" +
                    "\tWHERE FV.object_type='SHARER' AND (F.internal_name='FIRSTNAME')\n" +
                    ") AS FN ON S.verifier_id=FN.object_id\n" +
                    "LEFT JOIN (\n" +
                    "\tSELECT FV.object_id, FV.string_value\n" +
                    "\tFROM field_values FV\n" +
                    "\tINNER JOIN fields F ON F.id=FV.field_id \n" +
                    "\tWHERE FV.object_type='SHARER' AND (F.internal_name='LASTNAME')\n" +
                    ") AS LN ON S.verifier_id=LN.object_id\n" +
                    "LEFT JOIN (\n" +
                    "\tSELECT FV.object_id, FV.string_value\n" +
                    "\tFROM field_values FV\n" +
                    "\tINNER JOIN fields F ON F.id=FV.field_id \n" +
                    "\tWHERE FV.object_type='SHARER' AND (F.internal_name='SECONDNAME')\n" +
                    ") AS SN ON S.verifier_id=SN.object_id\n";
    @Override
    //@Cacheable(SEARCH_CACHE)
    public InvitesTableDataDto getListByFilter(Long userId, InviteFilter filter) {
        String select = SELECT_INVITES_BY_FILTER_SQL;

        String where = "";

        List<String> clauses = new ArrayList<>();
        clauses.add("I.sharer_id=" + userId);
        /*if(lastLoaded != null) {
            clauses.add("I.id<"+lastLoaded.getId());
        }*/

        if(filter.getGuaranteeFilter() != null) {
            if(filter.getGuaranteeFilter()==1) {
                clauses.add("I.guarantee=TRUE");
            } else if(filter.getGuaranteeFilter()==0) {
                clauses.add("I.guarantee=FALSE");
            }
        }

        if(filter.getFromFamiliarYears() != null) {
            clauses.add("I.how_long_familiar>="+filter.getFromFamiliarYears());
        }
        if(filter.getToFamiliarYears() != null) {
            clauses.add("I.how_long_familiar<="+filter.getToFamiliarYears());
        }

        if(!StringUtils.isBlank(filter.getEmail())) {
            List<String> or = new ArrayList<>();
            or.add("I.invited_email ILIKE '%"+filter.getEmail() + "%'");
            or.add("I.invited_first_name ILIKE '%"+filter.getEmail() + "%'");
            or.add("I.invited_last_name ILIKE '%"+filter.getEmail() + "%'");
            or.add("I.invited_father_name ILIKE '%"+filter.getEmail() + "%'");
            clauses.add("("+StringUtils.join(or, " OR ")+")");
        }

        if(filter.getInviteStatus() != null) {
            Integer s = filter.getInviteStatus();
            switch(s) {
                case 0: // 0 - Принято
                    clauses.add("I.status=1");
                    clauses.add("S.deleted=FALSE");
                    break;
                case 1: // 1 - В ожидании
                    clauses.add("I.status=0");
                    clauses.add("I.expire_date>'" + new Timestamp((new Date()).getTime())+"'");
                    break;
                case 2: // 2 - Просрочено
                    clauses.add("I.expire_date<'" + new Timestamp((new Date()).getTime())+"'");
                    clauses.add("I.status=0");
                    break;
                case 3: // 3 - Отклонено
                    clauses.add("I.status=2");
                    break;
                case 4: // 4 - Профиль перенесён в архив
                    clauses.add("S.deleted=TRUE");
                    break;
                case 5: // 5 - Профиль удалён
                    clauses.add("TRUE=FALSE"); // возвращаем пустой список
                    break;
            }
        }

        if(filter.getFromDate() != null) {
            clauses.add("I.creation_date>='" + new Timestamp(filter.getFromDate().getTime())+"'");
        }
        if(filter.getToDate() != null) {
            clauses.add("I.creation_date<='" + new Timestamp(filter.getToDate().getTime())+"'");
        }

        if(filter.getFromSharersCount() != null) {
            clauses.add("SS.sharers_stream>=" + filter.getFromSharersCount());
        }
        if(filter.getToSharersCount() != null) {
            clauses.add("SS.sharers_stream<=" + filter.getToSharersCount());
        }

        if(filter.getFromOrganizationsCount() != null) {
            clauses.add("OS.organizations_stream>=" + filter.getFromOrganizationsCount());
        }
        if(filter.getToOrganizationsCount() != null) {
            clauses.add("OS.organizations_stream<=" + filter.getToOrganizationsCount());
        }

        if(filter.getRegisterFromDate()!= null) {
            clauses.add("S.registered_at>='" + new Timestamp(filter.getRegisterFromDate().getTime())+"'");
        }
        if(filter.getRegisterToDate() != null) {
            clauses.add("S.registered_at<='" + new Timestamp(filter.getRegisterToDate().getTime())+"'");
        }

        if(filter.getFromInvitesCount() != null) {
            clauses.add("I.invites_count>=" + filter.getFromInvitesCount());
            clauses.add("I.status=0");
        }
        if(filter.getToInvitesCount() != null) {
            clauses.add("I.invites_count<=" + filter.getToInvitesCount());
            clauses.add("I.status=0");
        }

        if(filter.getSexFilter() != null) {
            if(filter.getSexFilter()==1) {
                clauses.add("I.invited_gender='М'");
            } else if(filter.getSexFilter()==0) {
                clauses.add("I.invited_gender='Ж'");
            }
        }

        if(filter.getVerifiedFilter() != null) {
            if(filter.getVerifiedFilter()==1) {
                clauses.add("S.verified=TRUE");
            } else if(filter.getVerifiedFilter()==0) {
                clauses.add("S.verified=FALSE");
            }
        }

        if(filter.getVerifiedFromDate()!= null) {
            clauses.add("S.verification_date>='" + new Timestamp(filter.getVerifiedFromDate().getTime())+"'");
        }
        if(filter.getVerifiedToDate() != null) {
            clauses.add("S.verification_date<='" + new Timestamp(filter.getVerifiedToDate().getTime())+"'");
        }

        if(filter.getRegistratorLevel() != null) {
            Integer l = filter.getRegistratorLevel();
            switch(l) {
                case 0: // 0 - высший ранг
                    clauses.add("P.mnemo='registrator.level0'");
                    break;
                case 1: // 1 - 1 ранг
                    clauses.add("P.mnemo='registrator.level1'");
                    break;
                case 2: // 2 - 2 ранг
                    clauses.add("P.mnemo='registrator.level2'");
                    break;
                case 3: // 3 - 3 ранг
                    clauses.add("P.mnemo='registrator.level3'");
                    break;
            }
        }

        if(!StringUtils.isBlank(filter.getVerifierName())) {
            clauses.add("SV.search_string ILIKE '%"+filter.getVerifierName() + "%'");
        }

        where = "WHERE I.hash_url is not null and I.hash_url != '' ";
        if(clauses.size()>0) {
            where += "AND " + StringUtils.join(clauses, " AND ") + " ";
        }

        StringBuilder orderBuilder = new StringBuilder();

        switch (filter.getSortColumnIndex()) {
            case 1:
                orderBuilder.append("ORDER BY I.creation_date ");
                break;
            case 2:
                orderBuilder.append("ORDER BY I.invited_last_name ");
                break;
            case 3:
                orderBuilder.append("ORDER BY I.status ");
                break;
            default:
                orderBuilder.append("ORDER BY I.id ");
                break;
        }
        if (filter.isSortDirection()) {
            orderBuilder.append(" DESC ");
        } else {
            orderBuilder.append(" ASC ");
        }

        int page = filter.getPage() - 1;
        orderBuilder.append(" ");
        orderBuilder.append("LIMIT " + filter.getPerPage());
        orderBuilder.append(" OFFSET " + filter.getPerPage() * page);
        String order = orderBuilder.toString();

        String sql = select + where + order;
        String countSql = select + where;
        Query query = em.createNativeQuery(sql);
        Query countQuery = em.createNativeQuery(countSql);
        List rows = query.getResultList();
        int count = countQuery.getResultList().size();

        List<InviteDto> invites = new ArrayList<>(rows.size());

        for (Object row: rows) {
            invites.add(createInviteDtoFromRow(row));
        }

        InvitesTableDataDto result = new InvitesTableDataDto();
        result.setCount(count);
        result.setInvites(invites);

        return result;
    }

    // 0 - Принято
    // 1 - В ожидании
    // 2 - Просрочено
    // 3 - Отклонено
    // 4 - Профиль перенесён в архив
    // 5 - Профиль удалён
    private Integer getInviteStatus(Integer status, Boolean isExpired, Boolean isDeleted) {
        Integer result = 0;

        if(status != null) {
            switch(status) {
                case 0: // 0 - еще не принято
                    result = 1;
                    break;
                case 1: // 1 - принято
                    result = 0;
                    break;
                case 2: // 2 - отклонено
                    result = 3;
                    break;
            }
        }

        if(result == 1 && isExpired != null && isExpired) {
            result = 2; // просрочено
        }

        if(isDeleted != null && isDeleted) {
            result = 4; // Профиль перенесён в архив
        }

        return result;
    }

    private int getRegistratorLevel(String registratorLevelMnemo) {
        int registratorLevel = -1;
        if(registratorLevelMnemo != null) {
            switch(registratorLevelMnemo) {
                case "registrator.level0":
                    registratorLevel = 0;
                    break;
                case "registrator.level1":
                    registratorLevel = 1;
                    break;
                case "registrator.level2":
                    registratorLevel = 2;
                    break;
                case "registrator.level3":
                    registratorLevel = 3;
                    break;
                default:
                    registratorLevel = -1;
                    break;
            }
        }
        return registratorLevel;
    }

    private InviteDto createInviteDtoFromRow(Object row) {
        Object[] r = (Object[]) row;

        Long inviteId = ((r[0] != null)  ? ((BigInteger)r[0]).longValue() : null);
        Date creationDate = (Date) r[1];
        String invitedEmail = (String) r[2];
        Boolean isGuarantee = (Boolean) r[3];
        Integer howLongFamiliar = r[4] == null ? 0 : (Integer) r[4];
        String invitedFatherName = (String) r[5];
        String invitedFirstName = (String) r[6];
        String invitedGender = (String) r[7];
        String invitedLastName = (String) r[8];
        Long inviterSharerId = ((r[9] != null)  ? ((BigInteger)r[9]).longValue() : null);
        String hashUrl = (String) r[10];
        Integer inviteStatus = r[11] == null ? -1 : (Integer) r[11];
        Date expireDate = (Date) r[12];
        Boolean isExpired = expireDate.before(new Date());
        Date lastSending = (Date) r[13];
        Long invitedSharerId = ((r[14] != null)  ? ((BigInteger)r[14]).longValue() : null);
        Integer invitesCount = ((r[15] != null)  ? ((Integer)r[15]): 1);
        String ikp = (String) r[16];
        Boolean isDeleted = (Boolean) r[17];
        Boolean isVerified = (Boolean) r[18];
        Long verifierId = ((r[19] != null)  ? ((BigInteger)r[19]).longValue() : null);
        Date registrationDate = (Date) r[20];
        Date verificationDate = (Date) r[21];
        String searchString = (String) r[22];

        String verifierFirstName = (String) r[23];
        String verifierLastName = (String) r[24];
        String verifierSecondName = (String) r[25];
        String verifierShortName = (StringUtils.isBlank(verifierLastName) ? "" : verifierLastName) +
                (StringUtils.isBlank(verifierFirstName) ? " " : (verifierFirstName.length()>0 ? (" " + verifierFirstName.charAt(0)+".") : "")) +
                (StringUtils.isBlank(verifierSecondName) ? " " : (verifierSecondName.length()>0 ? (" " + verifierSecondName.charAt(0)+".") : "")) ;

        int sharersStream =  ((r[27] != null)  ? ((BigInteger)r[27]).intValue() : 0);
        int organizationStream = ((r[28] != null)  ? ((BigInteger)r[28]).intValue() : 0);

        String registratorLevel = (String) r[29];

        InviteDto inviteDto = new InviteDto();
        inviteDto.setId(inviteId);
        inviteDto.setInviteStatus(getInviteStatus(inviteStatus, isExpired, isDeleted));
        inviteDto.setCreationDate(creationDate);
        inviteDto.setExpireDate(expireDate);
        inviteDto.setEmail(invitedEmail);
        inviteDto.setInvitedLastName(invitedLastName);
        inviteDto.setInvitedFirstName(invitedFirstName);
        inviteDto.setInvitedFatherName(invitedFatherName);
        inviteDto.setInvitedGender(invitedGender);
        inviteDto.setGuarantee(isGuarantee);
        inviteDto.setHowLongFamiliar(howLongFamiliar);
        inviteDto.setLastDateSending(lastSending);
        inviteDto.setInvitesCount(invitesCount);

        if(invitedSharerId != null) {
            InvitedUserDto invitedSharerDto = new InvitedUserDto();

            invitedSharerDto.setIkp(ikp);
            invitedSharerDto.setVerified(isVerified ? 1 : 0);
            invitedSharerDto.setVerifier(verifierShortName);
            invitedSharerDto.setVerifierId(verifierId);
            invitedSharerDto.setVerifierFirstName(verifierFirstName);
            invitedSharerDto.setVerifierSecondName(verifierSecondName);
            invitedSharerDto.setVerifierLastName(verifierLastName);
            invitedSharerDto.setVerificationDate(verificationDate);
            invitedSharerDto.setRegistrationDate(registrationDate);
            invitedSharerDto.setRegistratorLevel(getRegistratorLevel(registratorLevel));
            invitedSharerDto.setStreamSharers(sharersStream);
            invitedSharerDto.setStreamOrganizations(organizationStream);

            inviteDto.setInvitedUser(invitedSharerDto);
        }

        return inviteDto;
    }

    @Override
    public InviteCountDto getInviteCountData(Long userId) {
        // TODO Нужно прокешить данные
        InviteCountDto result = new InviteCountDto();
        int countRegistered = 0;
        int countVerified = 0;
        int countRegistrators = 0;
        Long registratorsLevel3InvitedSharersCount = 0L;
        Long registratorsLevel2InvitedSharersCount = 0L;
        Long registratorsLevel1InvitedSharersCount = 0L;
        List<InvitationEntity> invites = invitationRepository.findByUser_IdAndHashUrlIsNotNullAndHashUrlNot(userId, "");

        if (invites != null) {
            for (InvitationEntity invite : invites) {
                UserEntity invitedUser = invite.getInvitedSharer();
                if (invitedUser != null) {
                    if (BooleanUtils.toBooleanDefaultIfNull(invitedUser.getVerified(), false)) {
                        countVerified++;
                    }
                    if (!invitedUser.isDeleted() && !invitedUser.isArchived()) {
                        countRegistered++;
                        RegistratorDomain registratorDomain = registratorDataService.getRegistratorDtoById(invitedUser.getId());
                        if (registratorDomain != null) {
                            countRegistrators++;
                            if (registratorDomain.getLevel() != null) {
                                int registratorLevel = getRegistratorLevel(registratorDomain.getLevel().getMnemo());
                                if (registratorLevel == 1) {
                                    registratorsLevel1InvitedSharersCount++;
                                }
                                else if (registratorLevel == 2) {
                                    registratorsLevel2InvitedSharersCount++;
                                }
                                else if (registratorLevel == 3) {
                                    registratorsLevel3InvitedSharersCount++;
                                }
                            }
                        }
                    }
                }
            }
        }
        result.setRegistratorsLevel1InvitedSharersCount(registratorsLevel1InvitedSharersCount);
        result.setRegistratorsLevel2InvitedSharersCount(registratorsLevel2InvitedSharersCount);
        result.setRegistratorsLevel3InvitedSharersCount(registratorsLevel3InvitedSharersCount);
        result.setCountRegisterd(countRegistered);
        result.setCountRegistrators(countRegistrators);
        result.setCountVerified(countVerified);
        //result.setCommonCount(commonCount);
        return result;
    }

    @Override
    public InvitationEntity findAcceptedInvitationByUserId(Long userId) {
        List<InvitationEntity> invitations = invitationRepository.findAllByStatusAndInvitedUser_Id(1, userId);
        if (invitations.size() > 0) return invitations.get(0);
        return null;
    }

}
