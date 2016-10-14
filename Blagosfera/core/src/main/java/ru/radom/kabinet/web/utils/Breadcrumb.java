package ru.radom.kabinet.web.utils;

import java.util.LinkedList;
import java.util.List;

public class Breadcrumb extends LinkedList<BreadcrumbItem> {

	public Breadcrumb add(String title, String link) {
		add(new BreadcrumbItem(title, link));
		return this;
	}
	
	public Breadcrumb addItem(BreadcrumbItem e) {
		super.add(e);
		return this;
	}
	
	public Breadcrumb addItem(List<BreadcrumbItem> all) {
		super.addAll(all);
		return this;
	}
	
}
