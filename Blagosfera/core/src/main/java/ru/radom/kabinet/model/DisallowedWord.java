package ru.radom.kabinet.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Таблица запрещённых слов
 *
 * Created by ebelyaev on 10.09.2015.
 */
@Entity
@Table(name = "disallowed_words")
public class DisallowedWord extends LongIdentifiable {
    @Column(name = "word", nullable = false, length = 200)
    private String word;

    @Column(name = "type", nullable = false)
    private DisallowedType type;

    public DisallowedWord() {
    }

    public DisallowedWord(String word, DisallowedType type) {
        this.word = word;
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public DisallowedType getType() {
        return type;
    }

    public void setType(DisallowedType type) {
        this.type = type;
    }
}
