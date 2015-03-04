package nxpense.helper;

import nxpense.domain.CreditExpense;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseSource;

public class ExpenseConverter {

    private ExpenseConverter() {
        /* Utility class empty constructor */
    }

    public static Expense dtoToEntity(ExpenseDTO expenseDTO) {
        Expense expense = null;

        if(expenseDTO != null) {
            expense = getExpenseEntityInstance(expenseDTO.getSource());
            expense.setAmount(expenseDTO.getAmount());
            expense.setDate(expenseDTO.getDate());
            expense.setDescription(expenseDTO.getDescription());
        }

        return expense;
    }

    private static Expense getExpenseEntityInstance(ExpenseSource expenseSource) {
        if(expenseSource == null) {
            throw new IllegalArgumentException("Cannot create Expense entity instance for NULL expense source");
        }

        switch(expenseSource) {
            case DEBIT_CARD:
                return new DebitExpense();
            case CREDIT_CARD:
                return new CreditExpense();
            default:
                throw new IllegalArgumentException("Unsupported expense source!");
        }
    }
}
