package nxpense.helper;

import nxpense.builder.ExpenseDtoBuilder;
import nxpense.domain.CreditExpense;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.domain.Tag;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseResponseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.dto.PageDTO;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class ExpenseConverterTest {

    private static final String EXPENSE_DESCRIPTION = "Test groceries";
    private static final BigDecimal EXPENSE_AMOUNT = new BigDecimal(49.95);
    private static final LocalDate EXPENSE_DATE = new LocalDate(2015, 3, 1);
    private static final int EXPENSE_POSITION = 15;

    private static final String TAG_NAME_1 = "Bill";
    private static final String TAG_NAME_2 = "Mobile phone";
    private static final Tag TAG1 = createTag(TAG_NAME_1);
    private static final Tag TAG2 = createTag(TAG_NAME_2);

    private static Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }

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
        assertThat(expense.getDate()).isEqualTo(EXPENSE_DATE.toDate());
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
        assertThat(expense.getDate()).isEqualTo(EXPENSE_DATE.toDate());
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
        expense.setDate(EXPENSE_DATE.toDate());
        expense.setAmount(EXPENSE_AMOUNT);
        expense.setDescription(EXPENSE_DESCRIPTION);
        expense.addTag(TAG1);
        expense.addTag(TAG2);

        ExpenseResponseDTO expenseDto = ExpenseConverter.entityToResponseDto(expense);
        assertThat(expenseDto.getId()).isEqualTo(5);
        assertThat(expenseDto.getAmount()).isEqualTo(EXPENSE_AMOUNT);
        assertThat(expenseDto.getSource()).isEqualTo(ExpenseSource.CREDIT_CARD);
        assertThat(expenseDto.getTags()).hasSize(2);
        assertThat(expenseDto.getTags().get(0).getName()).isEqualTo(TAG_NAME_1);
        assertThat(expenseDto.getTags().get(1).getName()).isEqualTo(TAG_NAME_2);
    }

    @Test
    public void testEntityToResponseDto_debitExpense() {
        Expense expense = new DebitExpense() {
            @Override
            public Integer getId() {
                return 5;
            }
        };
        expense.setDate(EXPENSE_DATE.toDate());
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
        expense.setDate(EXPENSE_DATE.toDate());
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
