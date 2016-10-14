package ru.radom.kabinet.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.TextEscapeUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author dfilinberg
 */
public class EmailTemplateContextFunctions {

    private final static Logger logger = LoggerFactory.getLogger(EmailTemplateContextFunctions.class);

    public static String escapeHtml(String string) {
        return TextEscapeUtils.escapeEntities(string);
    }

    public static String formatDate(Date date, String pattern) {
        try {
            if (date != null && StringUtils.hasLength(pattern)) {
                return new SimpleDateFormat(pattern).format(date);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

	public static String resizeImage(String url, String resize) {
		int lastDotIndex = url.lastIndexOf(".");
		return url.substring(0, lastDotIndex) + "_" + resize + url.substring(lastDotIndex);
	}
    
}
