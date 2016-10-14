package ru.radom.kabinet.model.discussion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.radom.kabinet.model.LongIdentifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discussion_topics")
public class DiscussionTopic extends LongIdentifiable {

	@ManyToOne
	@JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_discussion_topic_parent"))
	private DiscussionTopic parent;
	
	@JsonIgnore
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private Set<DiscussionTopic> children = new HashSet<>();
	
	private String title;
	
	public DiscussionTopic() {}

	public DiscussionTopic getParent() {
		return parent;
	}

	public void setParent(DiscussionTopic parent) {
		this.parent = parent;
	}

	public Set<DiscussionTopic> getChildren() {
		return children;
	}

	public void setChildren(Set<DiscussionTopic> children) {
		this.children = children;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
}
