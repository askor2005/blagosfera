package ru.radom.kabinet.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author dfilinberg
 * 
 * Шаблон Email писем уведомлений, отправляемых пользователям.
 * Поля subject и body позволяют использовать EL выражения, параметризируются
 * параметрыми определяемыми каждый раз в конкретной ситуации, по умолчанию
 * гарантировано присутвует один параметр sharer - пользователь кому отправляется емейл
 * 
 */
@Entity
@Table(name = "email_templates")
public class EmailTemplate extends LongIdentifiable {

    @Column(length = 1000, unique = true)
    private String title;
    @Column(length = 1000, name = "send_from")
    private String from;
    @Column(length = 1000)
    private String subject;
    @Column(length = 1000000)
    private String body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
