package ru.radom.kabinet.web.communities;

import java.util.HashSet;
import java.util.Set;

public class CommunityForm {
	private Long id;
	private String name;
	private Set<Long> okveds = new HashSet<>();
}