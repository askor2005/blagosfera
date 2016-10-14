package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.events.BlagosferaEvent;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.communities.CommunityLogEventDao;
import ru.radom.kabinet.security.context.RequestContext;

@Deprecated
@Service("logService")
public class LogService {

	@Autowired
	private CommunityDao communityDao;

	@Autowired
	private CommunityLogEventDao communityLogEventDao;

	@Autowired
	private RequestContext radomRequestContext;

    @EventListener
    public void onBlagosferaEvent(BlagosferaEvent event) {
		// TODO Переделать
		/*
		if (event instanceof CommunityMemberEvent) {
			CommunityMemberEvent communityEvent = (CommunityMemberEvent) event;
			switch (communityEvent.getType()) {
			case ACCEPT_INVITE:
			case REJECT_INVITE:
			case CANCEL_REQUEST:
			case JOIN:
			case LEAVE:
			case REQUEST:
				CommunityLogEventEntity communityLogEvent = new CommunityLogEventEntity(new Date(), radomRequestContext.getCurrentSharer(), communityEvent.getMember().getCommunity(), communityEvent.getType());
				communityLogEventDao.save(communityLogEvent);
				break;
			case CANCEL_INVITE:
			case ACCEPT_REQUEST:
			case EXCLUDE:
			case REJECT_REQUEST:
			case INVITE:
				CommunityMemberLogEventEntity communityMemberLogEvent = new CommunityMemberLogEventEntity(new Date(), radomRequestContext.getCurrentSharer(), communityEvent.getMember().getCommunity(), communityEvent.getMember().getUser(), communityEvent.getType());
				communityLogEventDao.save(communityMemberLogEvent);
				break;

			default:
				break;
			}

		} else if (event instanceof NewsEvent) {
			NewsEvent newsEvent = (NewsEvent) event;
			News news = newsEvent.getNews();
			if (news.getScope() instanceof CommunityEntity) {
				switch (newsEvent.getType()) {
				case CREATE:
					communityLogEventDao.save(new CommunityNewsLogEventEntity(new Date(), radomRequestContext.getCurrentSharer(), (CommunityEntity) news.getScope(), news, CommunityEventType.CREATE_NEWS));
					break;
				case EDIT:
					communityLogEventDao.save(new CommunityNewsLogEventEntity(new Date(), radomRequestContext.getCurrentSharer(), (CommunityEntity) news.getScope(), news, CommunityEventType.EDIT_NEWS));
					break;
				case DELETE:
					communityLogEventDao.save(new CommunityNewsLogEventEntity(new Date(), radomRequestContext.getCurrentSharer(), (CommunityEntity) news.getScope(), news, CommunityEventType.DELETE_NEWS));
					break;
				default:
					break;
				}
			}
		}*/

	}

}
