package helper;

import nxpense.builder.ExpenseDtoBuilder;
import nxpense.domain.CreditExpense;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseResponseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.dto.PageDTO;
import nxpense.helper.ExpenseConverter;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class ExpenseConverterTest {

    private static final String EXPENSE_DESCRIPTION = "Test groceries";
    private static final BigDecimal EXPENSE_AMOUNT = new BigDecimal(49.95);
    private static final Date EXPENSE_DATE = new DateTime(2015, 3, 1, 0, 0).toDate();
    private static final int EXPENSE_POSITION = 15;

    @Test
    public void testDtoToEntity_DebitExpense() {
        ExpenseDTO expenseDTO = new ExpenseDtoBuilder()
                .setDate(EXPENSE_DATE)
                .setAmount(EXPENSE_AMOUNT)
                .setDescription(EXPENSE_DESCRIPTION)
                .setSource(ExpenseSource.DEBIT_CARD)
                .setPosition(EXPENSE_POSITION)
                .build();

        Expense expense = ExpenseConverter.dtoToEntity(expenseDTO);

        assertThat(expense).isNotNull().isInstanceOf(DebitExpense.class);
        assertThat(expense.getDate()).isEqualTo(EXPENSE_DATE);
        assertThat(expense.getAmount()).isEqualTo(EXPENSE_AMOUNT);
        assertThat(expense.getDescription()).isEqualTo(EXPENSE_DESCRIPTION);
        assertThat(expense.getPosition()).isEqualTo(EXPENSE_POSITION);
    }

    @Test
    public void testDtoToEntity_CreditExpense() {
        ExpenseDTO expenseDTO = new ExpenseDtoBuilder()
                .setDate(EXPENSE_DATE)
                .setAmount(EXPENSE_AMOUNT)
                .setDescription(EXPENSE_DESCRIPTION)
                .setSource(ExpenseSource.CREDIT_CARD)
                .setPosition(EXPENSE_POSITION)
                .build();

        Expense expense = ExpenseConverter.dtoToEntity(expenseDTO);

        assertThat(expense).isNotNull().isInstanceOf(CreditExpense.class);
        assertThat(expense.getDate()).isEqualTo(EXPENSE_DATE);
        assertThat(expense.getAmount()).isEqualTo(EXPENSE_AMOUNT);
        assertThat(expense.getDescription()).isEqualTo(EXPENSE_DESCRIPTION);
        assertThat(expense.getPosition()).isEqualTo(EXPENSE_POSITION);
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

    @Test
    public void testEntityToResponseDto_creditExpense() {
        Expense expense = new CreditExpense() {
            @Override
            public Integer getId() {
                return 5;
            }
        };
        expense.setDate(EXPENSE_DATE);
        expense.setAmount(EXPENSE_AMOUNT);
        expense.setDescription(EXPENSE_DESCRIPTION);

        ExpenseResponseDTO expenseDto = ExpenseConverter.entityToResponseDto(expense);
        assertThat(expenseDto.getId()).isEqualTo(5);
        assertThat(expenseDto.getAmount()).isEqualTo(EXPENSE_AMOUNT);
        assertThat(expenseDto.getSource()).isEqualTo(ExpenseSource.CREDIT_CARD);
    }

    @Test
    public void testEntityToResponseDto_debitExpense() {
        Expense expense = new DebitExpense() {
            @Override
            public Integer getId() {
                return 5;
            }
        };
        expense.setDate(EXPENSE_DATE);
        expense.setAmount(EXPENSE_AMOUNT);
        expense.setDescription(EXPENSE_DESCRIPTION);

        ExpenseResponseDTO expenseDto = ExpenseConverter.entityToResponseDto(expense);
        assertThat(expenseDto.getId()).isEqualTo(5);
        assertThat(expenseDto.getAmount()).isEqualTo(EXPENSE_AMOUNT);
        assertThat(expenseDto.getSource()).isEqualTo(ExpenseSource.DEBIT_CARD);
    }

    @Test
    public void testExpensePageToExpensePageDto() {
        List<Expense> expenses = new ArrayList<Expense>();
        Expense expense = new DebitExpense() {
            @Override
            public Integer getId() {
                return 5;
            }
        };
        expense.setDate(EXPENSE_DATE);
        expense.setAmount(EXPENSE_AMOUNT);
        expense.setDescription(EXPENSE_DESCRIPTION);
        expenses.add(expense);

        int totalNumberOfResult = 100;
        int page = 5;
        int pageSize = 25;
        Sort.Direction direction = Sort.Direction.ASC;
        String [] props = {"amount"};
        PageRequest pageRequest = new PageRequest(page, pageSize, direction, props);


        Page<Expense> expensePage = new PageImpl<Expense>(expenses, pageRequest, totalNumberOfResult);
        PageDTO<ExpenseResponseDTO> expensePageDto = ExpenseConverter.expensePageToExpensePageDto(expensePage);

        assertThat(expensePageDto.getNumberOfItems()).isEqualTo(expenses.size());
        assertThat(expensePageDto.getPageNumber()).isEqualTo(page);
        assertThat(expensePageDto.getPageSize()).isEqualTo(pageSize);
        assertThat(expensePageDto.getSortDirection()).isEqualTo(direction);
        assertThat(expensePageDto.getSortProperty()).isEqualTo("amount");
        assertThat(expensePageDto.getItems().get(0).getId()).isEqualTo(5);
    }
}
