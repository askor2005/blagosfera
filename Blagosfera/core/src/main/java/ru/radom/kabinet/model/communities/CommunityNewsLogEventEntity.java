package ru.radom.kabinet.model.communities;

import ru.askor.blagosfera.domain.community.CommunityEventType;
import ru.askor.blagosfera.domain.community.log.CommunityLogEvent;
import ru.askor.blagosfera.domain.community.log.CommunityMemberLogEvent;
import ru.askor.blagosfera.domain.community.log.CommunityNewsLogEvent;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.news.News;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue("community_news_event")
public class CommunityNewsLogEventEntity extends CommunityLogEventEntity {

	@JoinColumn(name = "news_id", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private News news;

	public CommunityNewsLogEventEntity() {

	}

	public CommunityNewsLogEventEntity(Date date, UserEntity userEntity, CommunityEntity community, News news, CommunityEventType type) {
		super(date, userEntity, community, type);
		this.news = news;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	@Override
	public CommunityNewsLogEvent toDomain() {
		CommunityLogEvent baseLogEvent = super.toDomain();

		CommunityNewsLogEvent result = new CommunityNewsLogEvent();
		result.setId(baseLogEvent.getId());
		result.setCommunity(baseLogEvent.getCommunity());
		result.setDate(baseLogEvent.getDate());
		result.setType(baseLogEvent.getType());
		result.setUser(baseLogEvent.getUser());
		if (getNews() != null) {
			result.setNews(getNews().toDomain());
		}
		return result;
	}


}
