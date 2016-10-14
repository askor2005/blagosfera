package ru.radom.kabinet.web.user.dto;

import lombok.Data;
import ru.radom.kabinet.model.OkvedEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by dream_000 on 10.05.2016.
 */
@Data
public class OkvedTreeDto {

    private Long id;
    private String text;
    private String code;
    private boolean leaf;
    private Boolean checked;
    private boolean expanded;

    public OkvedTreeDto(OkvedEntity okved, List<Long> checkedIds, List<Long> expandedIds) {
        setId(okved.getId());
        setText(okved.getLongName());
        setCode(okved.getCode());
        setLeaf(okved.getChildren().size() == 0);
        if (okved.getCode() != null && okved.getCode().length() > 4) {
            setChecked(checkedIds.contains(okved.getId()));
        }
        setExpanded(expandedIds.contains(okved.getId()));
    }

    public static List<OkvedTreeDto> toListDto(List<OkvedEntity> okveds, List<Long> checkedIds, List<Long> expandedIds) {
        List<OkvedTreeDto> result = null;
        if (okveds != null) {
            result = new ArrayList<>();
            for (OkvedEntity okvedEntity : okveds) {
                result.add(new OkvedTreeDto(okvedEntity, checkedIds, expandedIds));
            }
        }
        return result;
    }
}
