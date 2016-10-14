package ru.askor.blagosfera.domain.community.schema;

import ru.askor.blagosfera.domain.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 22.03.2016.
 */
public class CommunitySchemaUnit {

    private Long id;
    private CommunitySchemaUnitType type;
    private String name;
    private User manager;
    private List<CommunitySchemaConnection> connections = new ArrayList<>();
    private List<CommunitySchemaConnection> incomingConnections = new ArrayList<>();
    private List<CommunitySchemaUnitMember> members = new ArrayList<>();
    private Long schemaId;
    private int x;
    private int y;
    private int width;
    private int height;
    private String bgColor;
    private String managerIkp;
    private String managerFullName;
    private String draw2dId;

    public CommunitySchemaUnit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CommunitySchemaUnitType getType() {
        return type;
    }

    public void setType(CommunitySchemaUnitType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public List<CommunitySchemaConnection> getConnections() {
        return connections;
    }

    public List<CommunitySchemaConnection> getIncomingConnections() {
        return incomingConnections;
    }

    public List<CommunitySchemaUnitMember> getMembers() {
        return members;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Long schemaId) {
        this.schemaId = schemaId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getManagerIkp() {
        return managerIkp;
    }

    public void setManagerIkp(String managerIkp) {
        this.managerIkp = managerIkp;
    }

    public String getManagerFullName() {
        return managerFullName;
    }

    public void setManagerFullName(String managerFullName) {
        this.managerFullName = managerFullName;
    }

    public String getDraw2dId() {
        return draw2dId;
    }

    public void setDraw2dId(String draw2dId) {
        this.draw2dId = draw2dId;
    }
}
