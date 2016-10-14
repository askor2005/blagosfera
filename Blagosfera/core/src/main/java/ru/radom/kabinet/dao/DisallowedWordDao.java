package ru.radom.kabinet.dao;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.radom.kabinet.model.DisallowedType;
import ru.radom.kabinet.model.DisallowedWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ebelyaev on 10.09.2015.
 */
@Repository("disallowedWordDao")
public class DisallowedWordDao extends Dao<DisallowedWord> {
    public List<DisallowedWord> getByType(DisallowedType type) {
        return find(Restrictions.eq("type", type));
    }

    public List<String> getStringsByType(DisallowedType type) {
        List<DisallowedWord> disallowedWords = getByType(type);
        List<String> result = new ArrayList<>(disallowedWords.size());
        for (DisallowedWord word: disallowedWords) {
            result.add(word.getWord());
        }
        return result;
    }
}
