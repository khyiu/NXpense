package helper;

import nxpense.builder.ExpenseDtoBuilder;
import nxpense.domain.CreditExpense;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.helper.ExpenseConverter;
import org.joda.time.DateTime;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class ExpenseConverterTest {

    private static final String EXPENSE_DESCRIPTION = "Test groceries";
    private static final BigDecimal EXPENSE_AMOUNT = new BigDecimal(49.95);
    private static final Date EXPENSE_DATE = new DateTime(2015, 3, 1, 0, 0).toDate();

    @Test
    public void testDtoToEntity_DebitExpense() {
        ExpenseDTO expenseDTO = new ExpenseDtoBuilder()
                .setDate(EXPENSE_DATE)
                .setAmount(EXPENSE_AMOUNT)
                .setDescription(EXPENSE_DESCRIPTION)
                .setSource(ExpenseSource.DEBIT_CARD)
                .build();

        Expense expense = ExpenseConverter.dtoToEntity(expenseDTO);

        assertThat(expense).isNotNull().isInstanceOf(DebitExpense.class);
        assertThat(expense.getDate()).isEqualTo(EXPENSE_DATE);
        assertThat(expense.getAmount()).isEqualTo(EXPENSE_AMOUNT);
        assertThat(expense.getDescription()).isEqualTo(EXPENSE_DESCRIPTION);
    }

    @Test
    public void testDtoToEntity_CreditExpense() {
        ExpenseDTO expenseDTO = new ExpenseDtoBuilder()
                .setDate(EXPENSE_DATE)
                .setAmount(EXPENSE_AMOUNT)
                .setDescription(EXPENSE_DESCRIPTION)
                .setSource(ExpenseSource.CREDIT_CARD)
                .build();

        Expense expense = ExpenseConverter.dtoToEntity(expenseDTO);

        assertThat(expense).isNotNull().isInstanceOf(CreditExpense.class);
        assertThat(expense.getDate()).isEqualTo(EXPENSE_DATE);
        assertThat(expense.getAmount()).isEqualTo(EXPENSE_AMOUNT);
        assertThat(expense.getDescription()).isEqualTo(EXPENSE_DESCRIPTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDtoToEntity_NullExpenseSource() {
        ExpenseDTO expenseDTO = new ExpenseDtoBuilder()
                .setDate(EXPENSE_DATE)
                .setAmount(EXPENSE_AMOUNT)
                .setDescription(EXPENSE_DESCRIPTION)
                .build();

        ExpenseConverter.dtoToEntity(expenseDTO);
    }
}
