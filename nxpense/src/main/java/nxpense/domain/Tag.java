package nxpense.domain;

import com.google.common.collect.ImmutableList;
import nxpense.domain.converter.ColorConverter;

import javax.persistence.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "TAG")
@SequenceGenerator(name = "TAG_SEQ", sequenceName = "tag_seq", allocationSize = 1, initialValue = 1)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAG_SEQ")
    private Integer id;

    @Version
    private Integer version;

    private String name;

    @Convert(converter = ColorConverter.class)
    @Column(name="BACKGROUND_COLOR", nullable = false, length = 7)
    private Color backgroundColor;

    @Convert(converter = ColorConverter.class)
    @Column(name="FOREGROUND_COLOR", nullable = false, length = 7)
    private Color foregroundColor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentTag", fetch = FetchType.EAGER)
    private List<Tag> subTags = new ArrayList<Tag>();

    @ManyToOne
    @JoinColumn(name = "PARENT_TAG_ID")
    private Tag parentTag;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    private Set<Expense> expenses = new HashSet<Expense>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    public Integer getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

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
        return ImmutableList.copyOf(subTags);
    }

    public boolean addSubTag(Tag subTag) {
        subTag.setParentTag(this);
        return subTags.add(subTag);
    }

    public boolean removeSubTag(Tag subTag) {
        subTag.setParentTag(null);
        return subTags.remove(subTag);
    }

    public Tag getParentTag() {
        return parentTag;
    }

    private void setParentTag(Tag parentTag) {
        this.parentTag = parentTag;
    }

    public boolean addExpense(Expense expense) {
        return this.expenses.add(expense);
    }

    public boolean removeExpense(Expense expense) {
        return this.expenses.remove(expense);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
