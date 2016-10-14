package ru.radom.kabinet.dto.news.subscribe;

/**
 * Dto автора новостей
 */
public class AuthorDto {

    //Идентификатор
    private Long id;
    //Фамилия И.О.
    private String shortName;
    //Ссылка на миниатюру аватара
    private String avatarSrc;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAvatarSrc() {
        return avatarSrc;
    }

    public void setAvatarSrc(String avatarSrc) {
        this.avatarSrc = avatarSrc;
    }
}
