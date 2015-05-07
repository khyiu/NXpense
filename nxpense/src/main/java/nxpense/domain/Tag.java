package nxpense.domain;

import nxpense.helper.ColorConverter;

import javax.persistence.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "TAG")
@SequenceGenerator(name = "TAG_SEQ", sequenceName = "tag_seq")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAG_SEQ")
    private Integer id;

    private String name;

    @Convert(converter = ColorConverter.class)
    @Column(name="BACKGROUND_COLOR", nullable = false, length = 7)
    private Color backgroundColor;

    @Convert(converter = ColorConverter.class)
    @Column(name="FOREGROUND_COLOR", nullable = false, length = 7)
    private Color foregroundColor;

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

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
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
