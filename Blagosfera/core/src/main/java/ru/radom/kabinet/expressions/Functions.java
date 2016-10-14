package ru.radom.kabinet.expressions;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.ApplicationContext;
import padeg.lib.Padeg;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.section.SectionAccessType;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.dao.settings.SharerSettingDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.fields.FieldFileEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.model.web.Section;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.PassportCitizenshipSettings;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.section.SectionServiceImpl;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.utils.VarUtils;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Functions {

	private static ApplicationContext applicationContext;

	public static void setApplicationContext(ApplicationContext applicationContext) {
		Functions.applicationContext = applicationContext;
	}

	public static String resizeImage(String url, String resize) {
		int lastDotIndex = url.lastIndexOf(".");
		if (lastDotIndex == -1) {
			return "";
		}
		return url.substring(0, lastDotIndex) + "_" + resize + url.substring(lastDotIndex);
	}

	public static String getSimpleClassName(Object object) {
		Class clazz = object instanceof HibernateProxy ? object.getClass().getSuperclass() : object.getClass();
		if (Proxy.isProxyClass(clazz)) {
			return clazz.getSuperclass().getSimpleName();
		} else {
			return clazz.getSimpleName();
		}
	}

	public static UserEntity getSharerById(Long sharerId) {
		SharerDao sharerDao = applicationContext.getBean(SharerDao.class);
		return sharerDao.getById(sharerId);
	}

	public static String getSetting(String key, String defaultValue) {
		SharerSettingDao sharerSettingDao = applicationContext.getBean(SharerSettingDao.class);
		return SecurityUtils.getUser() != null ? sharerSettingDao.get(SecurityUtils.getUser().getId(), key, defaultValue) : null;
	}

	public static Integer getTimeZone() {
		Integer result = null;
		String timeZoneId = getSetting("profile.timezone", null);
		if (timeZoneId != null) {
			TimeZone tz = TimeZone.getTimeZone(timeZoneId);
			if (tz != null) {
				result = tz.getRawOffset() / 1000 / 60 / 60;
			}
		}
		return result;
	}

	public static Integer getServerTimeZone() {
		TimeZone timezone = TimeZone.getDefault();
		return timezone.getOffset(new Date().getTime()) / 1000 / 60 / 60;
	}

	public static long getLongSetting(String key, long defaultValue) {
		SharerSettingDao sharerSettingDao = applicationContext.getBean(SharerSettingDao.class);
		return sharerSettingDao.getLong(SecurityUtils.getUser().getId(), key, defaultValue);
	}

	public static boolean getBooleanSetting(String key, boolean defaultValue) {
		SharerSettingDao sharerSettingDao = applicationContext.getBean(SharerSettingDao.class);
		return sharerSettingDao.getBoolean(SecurityUtils.getUser().getId(), key, defaultValue);
	}

	public static String getDeclension(long number, String string1, String string24, String string50) {
		return StringUtils.getDeclension(number, string1, string24, string50);
	}

	public static String replaceLinks(String text) {
		List<String> parts = new ArrayList<String>();
		List<Integer> indexes = new ArrayList<Integer>();
		int fromIndex = 0;
		while (true) {
			int index = text.indexOf("<a ", fromIndex);
			if (index == -1) {
				break;
			}
			indexes.add(index);
			fromIndex = index;
			index = text.indexOf("/a>", fromIndex);
			if (index == -1) {
				break;
			}
			index += 3;
			indexes.add(index);
			fromIndex = index;
		}
		for (int i = 0; i <= indexes.size(); i++) {
			fromIndex = (i == 0 ? 0 : indexes.get(i - 1));
			int toIndex = i < indexes.size() ? indexes.get(i) : -1;
			parts.add(toIndex != -1 ? text.substring(fromIndex, toIndex) : text.substring(fromIndex));
		}
		String result = "";
		for (int i = 0; i < parts.size(); i++) {
			if (i % 2 == 1) {
				result += parts.get(i);
			} else {
				String[] words = parts.get(i).split("[ \n\t;<>&]");
				for (String word : words) {
					word = word.replace("\n", "");
					Pattern pattern = Pattern.compile("^(https?:\\/\\/|ssh:\\/\\/|ftp:\\/\\/|file:\\/|www\\.|(?:mailto:)?[A-Z0-9._%+\\-]+@)(.+)$");
					Matcher matcher = pattern.matcher(word);
					if (matcher.matches()) {
						String part1 = matcher.group(1);
						String part2 = matcher.group(2);
						if ("www.".equals(part1)) {
							part1 = "http://www.";
						} else if (part1.matches("@$") && !part1.matches("^mailto:")) {
							part1 = "mailto:" + part1;
						}
						String href = part1 + part2;
						parts.set(i, parts.get(i).replace(word, "<a target='_blank' href='" + href + "'>" + matcher.group(1) + matcher.group(2) + "</a>"));
					}
				}
				result += parts.get(i);
			}
		}
		return result;
	}
	
	public static String getHumanReadableDatesDistanceAccusative(int hoursDistance) {
		return DateUtils.getHumanReadableDistanceAccusative(hoursDistance);
	}
	
	public static int getDatesDistanceHours(Date from, Date to) {
		return DateUtils.getDistanceHours(from, to);
	}
	
	public static Date now() {
		return new Date();
	}
	
	public static String formatMoney(BigDecimal money) {
		return StringUtils.formatMoney(money);
	}
	
	public static boolean isActiveCommunityMember() {
		RequestContext radomRequestContext = applicationContext.getBean(RequestContext.class);
		return radomRequestContext.isActiveCommunityMember();
	}
	
	public static boolean isRootCommunityCreator() {
		RequestContext radomRequestContext = applicationContext.getBean(RequestContext.class);
		return radomRequestContext.isRootCommunityCreator();
	}
	
	public static boolean hasCommunityPermission(String permission) {
		CommunitiesService communitiesService = applicationContext.getBean(CommunitiesService.class);
		RequestContext radomRequestContext = applicationContext.getBean(RequestContext.class);
		return communitiesService.hasPermission(radomRequestContext.getCommunity(), SecurityUtils.getUser().getId(), permission);
	}

	// Проверка, что участник установленный в полях директор объединения
	/*public static boolean isCommunityDirector() {
		CommunitiesService communitiesService = applicationContext.getBean(CommunitiesService.class);
		RequestContext radomRequestContext = applicationContext.getBean(RequestContext.class);
		Long directorId = communitiesService.getCommunityDirectorId(radomRequestContext.getCommunity());
		return SecurityUtils.getUser().getId().equals(directorId);
	}*/
	
	public static boolean startsWith(String string, String start) {
		return string.startsWith(start);
	}
	
	public static String communityLabel(String root, String nonRoot) {
		RequestContext radomRequestContext = applicationContext.getBean(RequestContext.class);
		return radomRequestContext.getCommunity().isRoot() ? root : nonRoot;
	}
	
	public static boolean isSharer() {
		return SecurityUtils.getUserDetails() != null;
	}
	
	public static boolean hasRole(String role) {
        return SecurityUtils.getUserDetails() != null && SecurityUtils.getUserDetails().hasRole(role);
	}
	
	public static String checkWebsitePrefix(String website) {
		if (StringUtils.hasLength(website) && !website.startsWith("http://") && !website.startsWith("https://")) {
			website = "http://" + website;
		}
		return website; 
	}

	public static String escape(String string) {
		return StringEscapeUtils.escapeJava(string);
	}

	public static String unescape(String string) {
		return StringEscapeUtils.unescapeJava(string);
	}
	/**
	 * Получить значение сисметного параметра по его наименованию.
	 * @param key - наименование системного параметра.
	 * @param defaultValue
	 * @return
	 */
	public static String systemParameter(String key, String defaultValue) {
        SettingsManager settingsManager = applicationContext.getBean(SettingsManager.class);
		return settingsManager.getSystemSetting(key, defaultValue);
	}

	public static String getRameraListEditorItemTextById(String id) {
		String result = "";
		RameraListEditorItemDAO rameraListEditorItemDAO = applicationContext.getBean(RameraListEditorItemDAO.class);
		RameraListEditorItem rameraListEditorItem = rameraListEditorItemDAO.getById(VarUtils.getLong(id, -1l));
		if(rameraListEditorItem != null) {
			result = rameraListEditorItem.getText();
		}
		return result;
	}

	public static String getRameraListEditorItemMnemoCodeById(String id) {
		String result = "";
		RameraListEditorItemDAO rameraListEditorItemDAO = applicationContext.getBean(RameraListEditorItemDAO.class);
		RameraListEditorItem rameraListEditorItem = rameraListEditorItemDAO.getById(VarUtils.getLong(id, -1l));
		if(rameraListEditorItem != null) {
			result = rameraListEditorItem.getMnemoCode();
		}
		return result;
	}

	/**
	 * Текст на основе пола участника
	 * @param userEntity
	 * @param maleText
	 * @param femaleText
	 * @return
	 */
	public static String getSharerTextBySex(UserEntity userEntity, String maleText, String femaleText){
		return userEntity.getSex() ? maleText : femaleText;
	}

	/**
	 * Сколнение выражения по пажежу
	 * @param value
	 * @param padegIndex
	 * @return
	 */
	public static String getPadeg(String value, int padegIndex) {
		return Padeg.getOfficePadeg(value, padegIndex);
	}

	/**
	 * Склонение ФИО участника по падежу
	 * @param userEntity
	 * @param padegIndex
	 * @return
	 */
	public static String getSharerPadeg(UserEntity userEntity, int padegIndex) {
		return Padeg.getFIOPadegFS(userEntity.getFullName(), userEntity.getSex(), padegIndex);
	}
	public static String getSharerPadeg(User user, int padegIndex) {
		return Padeg.getFIOPadegFS(user.getFullName(), user.isSex(), padegIndex);
	}

	/**
	 * Возвращает объект с настройками отображения полей паспотрных данных для разных стран
	 * @return
	 */
	public static String getPassportCitizenshipSettings() {
		return new Gson().toJson(PassportCitizenshipSettings.getInstance());
	}

	/**
	 * Проверка является ли строка URL
	 * @param stringUrl
	 * @return
	 */
	public static boolean isUrl(String stringUrl) {
		try {
			URL url = new URL(stringUrl);
		} catch(MalformedURLException e) {
			return false;
		}
		return true;
	}

	/**
	 * Проверка доступа текущего участника к секции
	 * @param section - страница или пунк меню к которому проверяется доступ
	 * @return
	 */
	public static boolean checkSectionAccessType(Section section) {
		boolean result = true;
		SectionAccessType sectionAccessType = section.getAccessType();
		if(sectionAccessType != null) {
			User user = SecurityUtils.getUser();
			switch(sectionAccessType) {
				case REGISTERED:
					if (user == null) result = false;
					break;
				case VERIFIED:
					if (user != null && !user.isVerified()) result = false;
					break;
			}
		}

		return result;
	}

	/**
	 * Склоняет слово "человек" к числу
	 * @param number - количество человек
	 * @return
	 */
	public static String numberHumans(Long number) {
		String form1 = "человек";
		String form2 = "человека";
		String form5 = "человек";

		long n = number % 100;
		long n1 = n % 10;
		if (n > 10 && n < 20) return form5;
		if (n1 > 1 && n1 < 5) return form2;
		if (n1 == 1) return form1;
		return form5;
	}

	// Получить первое объединение по форме объединения
	public static Community getFirstCommunityByAssociationFormCode(String associationFormCode) {
        CommunityDataService communitiesService = applicationContext.getBean(CommunityDataService.class);
		return communitiesService.getByAssociationFormInternalName(associationFormCode);
	}

	// Объединие с редакторами благосферы
	public static Community getEditorsCommunity() {
		return getFirstCommunityByAssociationFormCode(Community.BLAGOSFERA_EDITORS_ASSOCIATION_FORM_CODE);
	}

	// Получить первый файл из поля
	public static FieldFileEntity getFirstFieldFile(FieldValueEntity fieldValue) {
		return fieldValue != null && fieldValue.getFieldFiles() != null && fieldValue.getFieldFiles().size() > 0 ? fieldValue.getFieldFiles().get(0) : null;
	}

	// Первый прикреплённый файл - урл
	public static String getFirstFieldFileUrl(FieldValueEntity fieldValue) {
		FieldFileEntity fieldFile = getFirstFieldFile(fieldValue);
		String result = fieldFile != null ? fieldFile.getUrl() : null;
		return result;
	}

	public static String getSectionLinkByName(String name) {
		SectionServiceImpl sectionService = applicationContext.getBean(SectionServiceImpl.class);
		return sectionService.getSectionLinkByName(name);
	}
}
