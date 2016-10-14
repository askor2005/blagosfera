package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;

import java.beans.PropertyEditorSupport;

@Component("communityMemberPropertyEditor")
public class CommunityMemberPropertyEditor extends PropertyEditorSupport {

	@Autowired
	private CommunityMemberDao communityMemberDao;

	@Override
	public void setAsText(String text) {
		setValue(communityMemberDao.getById(Long.parseLong(text)));
	}
}
