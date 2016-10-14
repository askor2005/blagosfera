package ru.radom.kabinet.utils;

import net.sf.uadetector.ReadableDeviceCategory;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

public class UserAgentUtils {

	public static String getBrowser(String useragent) {
		UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
		ReadableUserAgent agent = parser.parse(useragent);
		return agent.getFamily().getName();
	}

	public static String getOs(String useragent) {
		UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
		ReadableUserAgent agent = parser.parse(useragent);
		return agent.getOperatingSystem().getName();
	}

	public static String getDevice(String useragent) {
		UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
		ReadableUserAgent agent = parser.parse(useragent);
		ReadableDeviceCategory category = agent.getDeviceCategory();

		switch (category.getCategory()) {
		case GAME_CONSOLE:
			return "Игровая консоль";
		case OTHER:
			return "Не определено";
		case PDA:
			return "Коммуникатор";
		case PERSONAL_COMPUTER:
			return "Персональный компьютер";
		case SMART_TV:
			return "Телевизор";
		case SMARTPHONE:
			return "Смартфон";
		case TABLET:
			return "Планшет";
		case UNKNOWN:
			return "Не определено";
		case WEARABLE_COMPUTER:
			return "Переносной компьютер";
		default:
			return "Не определено";
		}
	}

}
