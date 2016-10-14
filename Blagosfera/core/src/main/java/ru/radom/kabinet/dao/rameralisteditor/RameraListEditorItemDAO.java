package ru.radom.kabinet.dao.rameralisteditor;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.dao.Dao;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditor;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;

import java.util.List;

/**
 * Created by vgusev on 02.06.2015.
 */
@Repository("rameraListEditorItemDAO")
public class RameraListEditorItemDAO extends Dao<RameraListEditorItem> {

    public RameraListEditorItem getByCode(String code){
        List<RameraListEditorItem> list = find(Restrictions.eq("mnemoCode", code));
        RameraListEditorItem result = null;
        if (list != null && list.size() > 0) {
            result = list.get(0);
        }
        return result;
    }

    public RameraListEditorItem getByText(String text){
        List<RameraListEditorItem> list = find(Restrictions.eq("text", text));
        RameraListEditorItem result = null;
        if (list != null && list.size() > 0) {
            result = list.get(0);
        }
        return result;
    }

    public RameraListEditorItem getByEditorAndText(RameraListEditor editor, String text){
        List<RameraListEditorItem> list = find(Restrictions.eq("listEditor", editor),Restrictions.eq("text", text));
        RameraListEditorItem result = null;
        if (list != null && list.size() > 0) {
            result = list.get(0);
        }
        return result;
    }
}
