package nxpense.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "USER_ACCOUNT")
@SequenceGenerator(name = "ACCOUNT_SEQ", sequenceName = "account_seq")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCOUNT_SEQ")
    private Integer id;

    @Temporal(TemporalType.DATE)
    private Date verifiedDate;

    @Column(name = "VERIFIED_CAPITAL")
    private BigDecimal verifiedCapital;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ACCOUNT_ID", referencedColumnName = "ID")
    private List<Tag> tags;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "USER_ACCOUNT_ID", referencedColumnName = "ID")
    private List<CreditCard> creditCards;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "USER_ACCOUNT_ID", referencedColumnName = "ID")
    private List<Expense> expenses;

    public Date getVerifiedDate() {
	return verifiedDate;
    }

    public void setVerifiedDate(Date verifiedDate) {
	this.verifiedDate = verifiedDate;
    }

    public BigDecimal getVerifiedCapital() {
	return verifiedCapital;
    }

    public void setVerifiedCapital(BigDecimal verifiedCapital) {
	this.verifiedCapital = verifiedCapital;
    }

    public List<Tag> getTags() {
	return tags;
    }

    public void setTags(List<Tag> tags) {
	this.tags = tags;
    }

    public List<CreditCard> getCreditCards() {
	return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
	this.creditCards = creditCards;
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

	if (getClass() != obj.getClass()) {
	    return false;
	}

	UserAccount other = (UserAccount) obj;
	if (id != other.id) {
	    return false;
	}

	return true;
    }
}
