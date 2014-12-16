package nxpense.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "CREDIT_CARD")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "card-seq", sequenceName = "card_seq")
    private long id;

    private String name;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
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
	
	if (!(obj instanceof CreditCard)) {
	    return false;
	}
	
	CreditCard other = (CreditCard) obj;
	
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
