package ru.radom.kabinet.web.utils;

public class BreadcrumbItem {

	private String title;
	private String link;

	public BreadcrumbItem(String title, String link) {
		super();
		this.title = title;
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

}
