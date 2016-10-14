package ru.radom.kabinet.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Настройки SMTP сервера для отправки уведомлений (сообщений) на электронную почту
 * @author dfilinberg
 */
@Entity
@Table(name = "smtp_servers")
public class SmtpServer extends LongIdentifiable {
    @Column(name = "smtp_host")
    private String host;

    private int port;

    private String username;

    private String password;

    private String protocol;

    @Column(name = "is_using")
    private boolean using;

    private boolean debug;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isUsing() {
        return using;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }
}