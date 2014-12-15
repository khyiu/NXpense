package nxpense.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DEBIT")
public class DebitExpense extends Expense {

}
