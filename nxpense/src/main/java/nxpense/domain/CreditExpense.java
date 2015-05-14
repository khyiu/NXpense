package nxpense.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CREDIT")
public class CreditExpense extends Expense {

}
