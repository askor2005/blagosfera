package ru.askor.blagosfera.domain.community.schema;

/**
 * Created by mnikitin on 20.06.2016.
 */
public class CommunitySchemaConnection {

    private Long id;
    private CommunitySchemaConnectionType type;
    private Long sourceUnitId;
    private Long targetUnitId;
    private String draw2dId;
    private String sourceDraw2dId;
    private String targetDraw2dId;

    public CommunitySchemaConnection() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CommunitySchemaConnectionType getType() {
        return type;
    }

    public void setType(CommunitySchemaConnectionType type) {
        this.type = type;
    }

    public Long getSourceUnitId() {
        return sourceUnitId;
    }

    public void setSourceUnitId(Long sourceUnitId) {
        this.sourceUnitId = sourceUnitId;
    }

    public Long getTargetUnitId() {
        return targetUnitId;
    }

    public void setTargetUnitId(Long targetUnitId) {
        this.targetUnitId = targetUnitId;
    }

    public String getDraw2dId() {
        return draw2dId;
    }

    public void setDraw2dId(String draw2dId) {
        this.draw2dId = draw2dId;
    }

    public String getSourceDraw2dId() {
        return sourceDraw2dId;
    }

    public void setSourceDraw2dId(String sourceDraw2dId) {
        this.sourceDraw2dId = sourceDraw2dId;
    }

    public String getTargetDraw2dId() {
        return targetDraw2dId;
    }

    public void setTargetDraw2dId(String targetDraw2dId) {
        this.targetDraw2dId = targetDraw2dId;
    }
}
