package ru.radom.kabinet.services.bpmhandlers.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityData;
import ru.askor.blagosfera.domain.community.OkvedDomain;
import ru.askor.blagosfera.domain.field.Field;
import ru.askor.blagosfera.domain.field.FieldFile;
import ru.askor.blagosfera.domain.field.FieldType;
import ru.askor.blagosfera.domain.field.FieldsGroup;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.dao.OkvedDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.model.OkvedEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldFileEntity;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.community.dto.BPMCreateCommunityDto;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.FieldConstants;
import ru.radom.kabinet.utils.VarUtils;

import java.util.*;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("createCommunityHandler")
@Transactional
public class BPMCreateCommunityHandler implements BPMHandler {

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private OkvedDao okvedDao;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private CommunitiesService communitiesService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        BPMCreateCommunityDto bpmCreateCommunityDto = serializeService.toObject(parameters, BPMCreateCommunityDto.class);
        String formattedDate = DateUtils.formatDate(new Date(), DateUtils.Format.DATE);

        Community community = new Community();
        community.setFullRuName(bpmCreateCommunityDto.getFullName());
        community.setAccessType(bpmCreateCommunityDto.getAccessType());
        community.setVisible(!bpmCreateCommunityDto.isInvisible());
        community.setAvatarUrl(bpmCreateCommunityDto.getAvatarUrl());
        community.setCreatedAt(bpmCreateCommunityDto.getCreatedAt());
        if (bpmCreateCommunityDto.getParentId() != null && bpmCreateCommunityDto.getParentId() > -1l) {
            Community parent = communityDataService.getByIdMinData(bpmCreateCommunityDto.getParentId());
            community.setParent(parent);
        }

        if (bpmCreateCommunityDto.getMainOkvedId() != null) {
            OkvedEntity okvedEntity = okvedDao.getById(bpmCreateCommunityDto.getMainOkvedId());
            community.setMainOkved(OkvedEntity.toDomainSafe(okvedEntity));
        }

        if (bpmCreateCommunityDto.getOkvedIds() != null)  {
            List<OkvedEntity> okvedEntities = okvedDao.getByIds(new ArrayList<>(bpmCreateCommunityDto.getOkvedIds()));
            for (OkvedEntity okvedEntity : okvedEntities) {
                OkvedDomain okved = okvedEntity.toDomain();
                community.getOkveds().add(okved);
            }
        }

        if (bpmCreateCommunityDto.getActivityScopeIds() != null && !bpmCreateCommunityDto.getActivityScopeIds().isEmpty()) {
            List<RameraListEditorItem> rameraListEditorItems = rameraListEditorItemDAO.getByIds(new ArrayList<>(bpmCreateCommunityDto.getActivityScopeIds()));
            community.getActivityScopes().addAll(RameraListEditorItem.toDomainList(rameraListEditorItems));
        }

        List<FieldEntity> fields = fieldDao.getByInternalNames(new ArrayList<>(bpmCreateCommunityDto.getFields().keySet()));
        Map<String, String> fieldValues = bpmCreateCommunityDto.getFields();
        Map<Long, FieldsGroup> allFieldsGroups = new HashMap<>();
        for (FieldEntity field : fields) {
            String value = fieldValues.get(field.getInternalName());
            if (field.getType() == FieldType.DATE) {
                // Если значение поля с датой - timestamp
                Long timeStamp = VarUtils.getLong(value, null);
                if (timeStamp != null) {
                    value = DateUtils.formatDate(new Date(timeStamp), DateUtils.Format.DATE);
                }
            }
            // Подставляем текущую дату в полях, где это нужно
            if ("formattedDate".equals(value)) {
                value = formattedDate;
            }
            // Если поле - система налогооблажения
            // TODO Говнокод - надо что то придумать
            if (field.getInternalName().equals(FieldConstants.COMMUNITY_TAXATION_SYSTEM_FIELD_NAME)) {
                RameraListEditorItem rameraListEditorItem = rameraListEditorItemDAO.getByText(value);
                if (rameraListEditorItem != null) {
                    value = String.valueOf(rameraListEditorItem.getId());
                }
            }

            FieldsGroupEntity fieldsGroup = field.getFieldsGroup();
            FieldsGroup fieldGroupDomain;
            if (!allFieldsGroups.containsKey(fieldsGroup.getId())) {
                fieldGroupDomain = fieldsGroup.toDomain(false, false);
                allFieldsGroups.put(fieldsGroup.getId(), fieldGroupDomain);
            } else {
                fieldGroupDomain = allFieldsGroups.get(fieldsGroup.getId());
            }

            if (value != null) {
                Field communityField = new Field();
                communityField.setId(field.getId());
                communityField.setType(field.getType());
                communityField.setInternalName(field.getInternalName());
                communityField.setValue(value);
                if (bpmCreateCommunityDto.getFieldFiles().containsKey(field.getInternalName())) {
                    List<FieldFileEntity> fieldFiles = bpmCreateCommunityDto.getFieldFiles().get(field.getInternalName());
                    if (fieldFiles != null) {
                        List<FieldFile> communityFieldFiles = new ArrayList<>();
                        for (FieldFileEntity fieldFile : fieldFiles) {
                            FieldFile communityFieldFile = new FieldFile();
                            communityFieldFile.setId(fieldFile.getId());
                            communityFieldFile.setName(fieldFile.getName());
                            communityFieldFile.setUrl(fieldFile.getUrl());
                            communityFieldFiles.add(communityFieldFile);
                        }
                        communityField.setFieldFiles(communityFieldFiles);
                    }
                }
                fieldGroupDomain.getFields().add(communityField);
            }
        }
        community.setCommunityData(new CommunityData());
        community.getCommunityData().setFieldGroups(new ArrayList<>(allFieldsGroups.values()));

        User creator = userDataService.getByIdFullData(bpmCreateCommunityDto.getCreatorId());

        Set<Long> memberIds = bpmCreateCommunityDto.getMemberIds();
        memberIds.remove(bpmCreateCommunityDto.getCreatorId());

        List<User> members = null;
        if (memberIds != null) {
            members = userDataService.getByIds(new ArrayList<>(memberIds));

        }

        Set<User> receiversNotification = new HashSet<>();
        receiversNotification.addAll(members);
        receiversNotification.add(creator);

        community = communitiesService.createCommunity(community, creator, members, new ArrayList<>(receiversNotification));

        return community.getId();
    }
}