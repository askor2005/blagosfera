package ru.radom.kabinet.module.blagosfera.bp.dto;

/**
 * Created by Otts Alexey on 29.10.2015.<br/>
 * Форма узла дерева БП
 */
public class BPTreeForm {

    private Long id;

    private String name;

    private String position;

    private Integer positionToMove;

    private Long parentId;

    private String type;

    private Boolean moveChildrenOnDelete;

    public BPTreeForm(Long id, String name, String position, Integer positionToMove, Long parentId, String type, Boolean moveChildrenOnDelete) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.positionToMove = positionToMove;
        this.parentId = parentId;
        this.type = type;
        this.moveChildrenOnDelete = moveChildrenOnDelete;
    }

    public BPTreeForm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getPositionToMove() {
        return positionToMove;
    }

    public void setPositionToMove(Integer positionToMove) {
        this.positionToMove = positionToMove;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getMoveChildrenOnDelete() {
        return moveChildrenOnDelete;
    }

    public void setMoveChildrenOnDelete(Boolean moveChildrenOnDelete) {
        this.moveChildrenOnDelete = moveChildrenOnDelete;
    }
}
