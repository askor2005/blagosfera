package ru.radom.kabinet.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WebUtils {

	private static final String[] HEADERS_TO_TRY = {
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR",
			"HTTP_X_FORWARDED",
			"HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED",
			"HTTP_VIA",
			"REMOTE_ADDR" };

	public static String getClientIpAddress(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}

	public static String getCookie(Cookie[] cookies, String name, String defaultValue) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie.getValue();
				}
			}
		}
		return defaultValue;
	}

	public static String urlDecode(String string) {
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static String urlEncode(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static List<FileItem> parseMultipartRequest(HttpServletRequest request) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(5 * 1024 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			return upload.parseRequest(request);
		} catch (FileUploadException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] getMultipartItemBytes(List<FileItem> items, String fieldName) {
		for (FileItem item : items) {
			if (item.getFieldName().equals(fieldName)) {
				return item.get();
			}
		}
		return null;
	}

	public static int getMultipartItemInt(List<FileItem> items, String fieldName, int defaultValue) {
		for (FileItem item : items) {
			if (item.getFieldName().equals(fieldName)) {
				try {
					return Integer.parseInt(item.getString());
				} catch (Exception e) {
					return defaultValue;
				}
			}
		}
		return defaultValue;
	}

	/**
	 * Получить параметр из запроса.
	 * @param body
	 * @param parameter
	 * @return
	 */
	public static String getValueOfParameter(String body, String parameter) {
		String paramValue = null;
		body = WebUtils.urlDecode(body);
		for (String pair : body.split("&")) {
			String[] parts = pair.split("=");
			if (parts.length > 1) {
				String key = parts[0];
				String value = parts[1];
				if (parameter.equals(key)) {
					paramValue = value.replaceAll("'", "''");
				}
			}
		}
		return paramValue;
	}

    public static String readPostRequestBodyAsString(HttpServletRequest request) {
        return new String(readPostRequestBody(request), StandardCharsets.UTF_8);
    }

    public static byte[] readPostRequestBody(HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
                return IOUtils.toByteArray(request.getInputStream());
            } catch (IOException ignored) {}
        }

        throw new NotImplementedException("method not supported");
    }
}
