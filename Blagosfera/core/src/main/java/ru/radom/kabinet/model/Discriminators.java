package ru.radom.kabinet.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ru.askor.blagosfera.data.jpa.entities.account.SharebookEntity;
import ru.askor.blagosfera.data.jpa.entities.account.TransactionEntity;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.discussion.CommentEntity;
import ru.radom.kabinet.model.news.News;
import ru.radom.kabinet.model.notifications.SystemAccountEntity;

@Deprecated
public class Discriminators {
	public static final String SYSTEM_ACCOUNT = "SYSTEM_ACCOUNT";
	public static final String SHARER = "SHARER";
	public static final String SHARER_BOOK = "SHARER_BOOK";
	public static final String COMMUNITY = "COMMUNITY";
	public static final String NEWS = "NEWS";
	public static final String CONTACT = "CONTACT";
	public static final String COMMUNITY_MEMBER = "COMMUNITY_MEMBER";
	public static final String TRANSACTION = "TRANSACTION";
	public static final String COMMENT = "COMMENT";
	public static final String DIALOG = "DIALOG";

	private final static BiMap<Class<?>, String> MAP = HashBiMap.create ();

	static {
		MAP.put(UserEntity.class, SHARER);
		MAP.put(SharebookEntity.class, SHARER_BOOK);
		MAP.put(SystemAccountEntity.class, SYSTEM_ACCOUNT);
		MAP.put(CommunityEntity.class, COMMUNITY);
		MAP.put(News.class, NEWS);
		MAP.put(ContactEntity.class, CONTACT);
		MAP.put(CommunityMemberEntity.class, COMMUNITY_MEMBER);
		MAP.put(TransactionEntity.class, TRANSACTION);
		MAP.put(CommentEntity.class, COMMENT);
		MAP.put(DialogEntity.class, DIALOG);
	}

	public static String get(Class<?> clazz) {
		String discriminator = MAP.get(clazz);
		if (discriminator == null) {
			discriminator = MAP.get(clazz.getSuperclass());
		}
		return discriminator;
	}

    public static Class getClass(final String discriminator) {
        return MAP.inverse().get(discriminator);
    }
}