package nxpense.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("CREDIT")
public class CreditExpense extends Expense {

	private boolean verified;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private CreditCard card;

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}
}
