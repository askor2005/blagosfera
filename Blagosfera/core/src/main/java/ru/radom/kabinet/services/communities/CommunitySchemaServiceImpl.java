package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.data.jpa.repositories.schema.CommunitySchemaConnectionTypeRepository;
import ru.askor.blagosfera.data.jpa.repositories.schema.CommunitySchemaUnitRepository;
import ru.askor.blagosfera.data.jpa.repositories.schema.CommunitySchemaRepository;
import ru.askor.blagosfera.domain.community.schema.CommunitySchema;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaConnection;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnit;
import ru.askor.blagosfera.domain.community.schema.CommunitySchemaUnitMember;
import ru.radom.kabinet.dao.communities.schema.CommunitySchemaConnectionTypesDao;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.schema.*;

import java.util.Iterator;
import java.util.List;

@Transactional
@Service("communitySchemaService")
public class CommunitySchemaServiceImpl implements CommunitySchemaService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunitySchemaConnectionTypesDao communitySchemaConnectionTypesDao;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunitySchemaRepository communitySchemaRepository;

    @Autowired
    private CommunitySchemaUnitRepository communitySchemaUnitRepository;

    @Autowired
    private CommunitySchemaConnectionTypeRepository communitySchemaConnectionTypeRepository;

    public CommunitySchemaServiceImpl() {
    }

    @Override
    public List<CommunitySchemaConnectionTypeEntity> getConnectionTypes() {
        return communitySchemaConnectionTypesDao.findAll();
    }

    @Override
    public void saveSchema(Long communityId, CommunitySchema schema) {
        CommunityEntity community = communityRepository.getOne(communityId);

        CommunitySchemaEntity schemaEntity = communitySchemaRepository.findByCommunity_Id(communityId);

        if (schemaEntity == null) {
            schemaEntity = new CommunitySchemaEntity();
            schemaEntity.setCommunity(community);
        }

        mergeUnits(schemaEntity, schema.getUnits());

        schemaEntity.setBgImageUrl(schema.getBgImageUrl());
        schemaEntity.setWidth(schema.getWidth());
        schemaEntity.setHeight(schema.getHeight());
        schemaEntity.setScrollLeft(schema.getScrollLeft());
        schemaEntity.setScrollTop(schema.getScrollTop());

        communitySchemaRepository.save(schemaEntity);
    }

    private void mergeUnits(CommunitySchemaEntity schemaEntity, List<CommunitySchemaUnit> units) {
        for (Iterator<CommunitySchemaUnitEntity> it = schemaEntity.getUnits().iterator(); it.hasNext();) {
            boolean delete = true;

            for (Iterator<CommunitySchemaUnit> it2 = units.iterator(); it2.hasNext();) {
                CommunitySchemaUnitEntity unitEntity = it.next();
                CommunitySchemaUnit unit = it2.next();

                if (unitEntity.getId().equals(unit.getId())) {
                    updateUnit(unitEntity, unit);
                    it2.remove();
                    delete = false;
                    break;
                }
            }

            if (delete) it.remove();
        }

        for (Iterator<CommunitySchemaUnit> it = units.iterator(); it.hasNext();) {
            CommunitySchemaUnitEntity unitEntity = new CommunitySchemaUnitEntity();
            CommunitySchemaUnit unit = it.next();
            unitEntity.setSchema(schemaEntity);
            updateUnit(unitEntity, unit);
            schemaEntity.getUnits().add(unitEntity);
        }
    }

    private void updateUnit(CommunitySchemaUnitEntity unitEntity, CommunitySchemaUnit unit) {
        unitEntity.setType(unit.getType());
        unitEntity.setName(unit.getName());
        if (unit.getManager() != null) {
            unitEntity.setManager(userRepository.getOne(unit.getManager().getId()));
        }

        mergeConnections(unitEntity, unit, false);
        mergeConnections(unitEntity, unit, true);
        mergeMembers(unitEntity, unit.getMembers());

        unitEntity.setX(unit.getX());
        unitEntity.setY(unit.getY());
        unitEntity.setWidth(unit.getWidth());
        unitEntity.setHeight(unit.getHeight());
        unitEntity.setBgColor(unit.getBgColor());
        unitEntity.setManagerIkp(unit.getManagerIkp());
        unitEntity.setManagerFullName(unit.getManagerFullName());
        unitEntity.setDraw2dId(unit.getDraw2dId());
    }

    private void mergeMembers(CommunitySchemaUnitEntity unitEntity, List<CommunitySchemaUnitMember> members) {
        for (Iterator<CommunitySchemaUnitMemberEntity> it = unitEntity.getMembers().iterator(); it.hasNext();) {
            boolean delete = true;

            for (Iterator<CommunitySchemaUnitMember> it2 = members.iterator(); it2.hasNext();) {
                CommunitySchemaUnitMemberEntity memberEntity = it.next();
                CommunitySchemaUnitMember member = it2.next();

                if (memberEntity.getId().equals(member.getId())) {
                    updateMember(memberEntity, member);
                    it2.remove();
                    delete = false;
                    break;
                }
            }

            if (delete) it.remove();
        }

        for (Iterator<CommunitySchemaUnitMember> it = members.iterator(); it.hasNext();) {
            CommunitySchemaUnitMemberEntity memberEntity = new CommunitySchemaUnitMemberEntity();
            CommunitySchemaUnitMember member = it.next();
            memberEntity.setUnit(unitEntity);
            updateMember(memberEntity, member);
            unitEntity.getMembers().add(memberEntity);
        }
    }

    private void updateMember(CommunitySchemaUnitMemberEntity memberEntity, CommunitySchemaUnitMember member) {
        memberEntity.setUser(userRepository.getOne(member.getUserId()));
        memberEntity.setIkp(member.getIkp());
        memberEntity.setFullName(member.getFullName());
        memberEntity.setEmail(member.getEmail());
    }

    private void mergeConnections(CommunitySchemaUnitEntity unitEntity, CommunitySchemaUnit unit, boolean incoming) {
        List<CommunitySchemaConnectionEntity> connectionEntities = incoming ? unitEntity.getIncomingConnections() : unitEntity.getConnections();
        List<CommunitySchemaConnection> connections = incoming ? unit.getIncomingConnections() : unit.getConnections();

        for (Iterator<CommunitySchemaConnectionEntity> it = connectionEntities.iterator(); it.hasNext();) {
            boolean delete = true;

            for (Iterator<CommunitySchemaConnection> it2 = connections.iterator(); it2.hasNext();) {
                CommunitySchemaConnectionEntity connectionEntity = it.next();
                CommunitySchemaConnection connection = it2.next();

                if (connectionEntity.getId().equals(connection.getId())) {
                    updateConnection(connectionEntity, connection);
                    it2.remove();
                    delete = false;
                    break;
                }
            }

            if (delete) it.remove();
        }

        for (Iterator<CommunitySchemaConnection> it = connections.iterator(); it.hasNext();) {
            CommunitySchemaConnectionEntity connectionEntity = new CommunitySchemaConnectionEntity();
            CommunitySchemaConnection connection = it.next();

            if (incoming) {
                connectionEntity.setSource(communitySchemaUnitRepository.getOne(connection.getSourceUnitId()));
                connectionEntity.setTarget(unitEntity);
            } else {
                connectionEntity.setSource(unitEntity);
                connectionEntity.setTarget(communitySchemaUnitRepository.getOne(connection.getTargetUnitId()));
            }

            updateConnection(connectionEntity, connection);
            connectionEntities.add(connectionEntity);
        }
    }

    private void updateConnection(CommunitySchemaConnectionEntity connectionEntity, CommunitySchemaConnection connection) {
        connectionEntity.setType(communitySchemaConnectionTypeRepository.getOne(connection.getType().getId()));
    }
}
