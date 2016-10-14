package ru.radom.kabinet.dto.community;

import lombok.Data;

/**
 * Класс - обёртка для получения данных по должностям объединения
 * Created by vgusev on 29.08.2015.
 */
@Data
public class CommunityUserPost {

    private Long id;

    private Long userId;

    private String userName;

    private Long communityId;

    private String communityName;

    private String postName;
}
