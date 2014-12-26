package nxpense.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "TAG")
@SequenceGenerator(name = "TAG_SEQ", sequenceName = "tag_seq")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAG_SEQ")
    private Integer id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentTag")
    private List<Tag> subTags;

    @ManyToOne
    @JoinColumn(name = "PARENT_TAG_ID")
    private Tag parentTag;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    private Set<Expense> expenses;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<Tag> getSubTags() {
	return subTags;
    }

    public void setSubTags(List<Tag> subTags) {
	this.subTags = subTags;
    }

    public Tag getParentTag() {
	return parentTag;
    }

    public void setParentTag(Tag parentTag) {
	this.parentTag = parentTag;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof Tag)) {
	    return false;
	}
	Tag other = (Tag) obj;
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	return true;
    }

}
