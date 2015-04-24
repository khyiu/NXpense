package nxpense.dto;

import nxpense.domain.CreditExpense;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;

/**
 *
 */
public enum ExpenseSource {
    DEBIT_CARD, CREDIT_CARD;

    public Expense getEmptyExpenseInstance() {
        switch(this) {
            case DEBIT_CARD:
                return new DebitExpense();
            case CREDIT_CARD:
                return new CreditExpense();
            default:
                throw new IllegalArgumentException("Unsupported expense source!");
        }
    }
}
