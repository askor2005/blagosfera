package ru.radom.kabinet.utils;

public class ImagesUtils {

	public static String getResizeUrl(String url, String resize) {
		if (url == null) {
			return null;
		}
		int dotIndex= url.lastIndexOf(".");
		return url.substring(0, dotIndex) + "_" + resize + url.substring(dotIndex);
	}
	
}
