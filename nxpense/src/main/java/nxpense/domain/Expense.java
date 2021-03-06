package nxpense.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "EXPENSE_TYPE")
@Table(name = "EXPENSE")
@SequenceGenerator(name = "EXPENSE_SEQ", sequenceName = "expense_seq", initialValue = 1, allocationSize = 1)
public abstract class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EXPENSE_SEQ")
    private Integer id;

    @Version
    private Integer version;

    @ManyToOne(optional = false)
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false)
    private User user;

    @Temporal(TemporalType.DATE)
    private Date date;

    private BigDecimal amount;

    private String description;

    @Basic(optional = false)
    private Integer position;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "EXPENSE_TAG",
            joinColumns = {
                    @JoinColumn(name = "EXPENSE_ID", nullable = false, updatable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "TAG_ID", nullable = false, updatable = false)
            })
    private Set<Tag> tags = new HashSet<Tag>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "EXPENSE_ATTACHMENT", joinColumns = {
            @JoinColumn(name = "EXPENSE_ID")
    })
    private List<Attachment> attachments = new ArrayList<Attachment>();

    private boolean verified;

    public Integer getId() {
        return this.id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPosition() {
        return this.position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean addTag(Tag tag) {
        return this.tags.add(tag);
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Expense expense = (Expense) o;

        if (!amount.equals(expense.amount)) {
            return false;
        }

        if (!date.equals(expense.date)) {
            return false;
        }

        if (!description.equals(expense.description)) {
            return false;
        }

        if (!user.equals(expense.user)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}