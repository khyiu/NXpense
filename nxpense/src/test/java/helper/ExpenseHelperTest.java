package helper;

import nxpense.domain.Expense;
import nxpense.dto.ExpenseSource;
import nxpense.helper.ExpenseHelper;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseHelperTest {

    private static final Date DATE = new LocalDate(2015, 1, 1).toDate();
    private static final BigDecimal AMOUNT = new BigDecimal(123.45);
    private static final String DESCRIPTION = "An expense description";

    @Test
    public void testOverwriteFields_noSource() {
        Expense source = null;
        Expense destination = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();

        ExpenseHelper.overwriteFields(source, destination);
        assertThat(destination.getDate()).isNull();
        assertThat(destination.getAmount()).isNull();
        assertThat(destination.getDescription()).isNull();
    }

    @Test
    public void testOverwriteFields() {
        Expense source = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        source.setDate(DATE);
        source.setAmount(AMOUNT);
        source.setDescription(DESCRIPTION);
        Expense destination = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();

        ExpenseHelper.overwriteFields(source, destination);
        assertThat(destination.getDate()).isEqualTo(DATE);
        assertThat(destination.getAmount()).isEqualTo(AMOUNT);
        assertThat(destination.getDescription()).isEqualTo(DESCRIPTION);
    }
}
