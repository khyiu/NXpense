package nxpense.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "EXPENSE_TYPE")
@Table(name = "EXPENSE")
@SequenceGenerator(name = "EXPENSE_SEQ", sequenceName = "expense_seq")

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "source", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DebitExpense.class, name = "DEBIT_CARD"),
        @JsonSubTypes.Type(value = CreditExpense.class, name = "CREDIT_CARD"),
})
public abstract class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EXPENSE_SEQ")
    private Integer id;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date date;

    private BigDecimal amount;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "EXPENSE_TAG",
        joinColumns = {
            @JoinColumn(name = "EXPENSE_ID", nullable = false, updatable = false)
        },
        inverseJoinColumns = {
            @JoinColumn(name = "TAG_ID", nullable = false, updatable = false)
        })
    private Set<Tag> tags;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
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
        if (!(obj instanceof Expense)) {
            return false;
        }
        Expense other = (Expense) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
}