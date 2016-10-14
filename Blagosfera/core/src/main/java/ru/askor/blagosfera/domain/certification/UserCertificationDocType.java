package ru.askor.blagosfera.domain.certification;

/**
 * Created by Maxim Nikitin on 02.02.2016.
 */
public class UserCertificationDocType {

    private Long id;
    private String name;
    private String title;
    private int minFiles;

    public UserCertificationDocType() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMinFiles() {
        return minFiles;
    }

    public void setMinFiles(int minFiles) {
        this.minFiles = minFiles;
    }
}
