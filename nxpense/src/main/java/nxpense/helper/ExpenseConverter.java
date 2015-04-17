package nxpense.helper;

import nxpense.builder.PageDtoBuilder;
import nxpense.domain.CreditExpense;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseResponseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.dto.PageDTO;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExpenseConverter {

    private ExpenseConverter() {
        /* Utility class empty constructor */
    }

    public static Expense dtoToEntity(ExpenseDTO expenseDTO) {
        Expense expense = null;

        if(expenseDTO != null) {
            expense = getExpenseEntityInstance(expenseDTO.getSource());
            expense.setAmount(expenseDTO.getAmount());
            expense.setDate(expenseDTO.getDate().toDate());
            expense.setDescription(expenseDTO.getDescription());
            expense.setPosition(expenseDTO.getPosition());
        }

        return expense;
    }

    public static ExpenseResponseDTO entityToResponseDto(Expense expense) {
        ExpenseResponseDTO expenseDto = null;

        if(expense != null) {
            expenseDto = new ExpenseResponseDTO();
            expenseDto.setId(expense.getId());
            copyAttributeValues(expenseDto, expense);
        }

        return expenseDto;
    }

    public static PageDTO<ExpenseResponseDTO> expensePageToExpensePageDto(Page<Expense> expensePage) {
        PageDTO<ExpenseResponseDTO> expensePageDto = null;

        if(expensePage != null) {
            List<ExpenseResponseDTO> items = expenseListToExpenseResponseDtoList(expensePage.getContent());

            String sortProperty = null;
            Sort.Direction sortDirection = null;
            Iterator<Sort.Order> orders = expensePage.getSort().iterator();

            while(orders.hasNext() && sortProperty == null) {
                Sort.Order order = orders.next();
                sortProperty = order.getProperty();
                sortDirection = order.getDirection();
            }

            expensePageDto = new PageDtoBuilder<ExpenseResponseDTO>()
                    .setPageNumber(expensePage.getNumber())
                    .setPageSize(expensePage.getSize())
                    .setNumberOfItems(expensePage.getNumberOfElements())
                    .setItems(items)
                    .setSortProperty(sortProperty)
                    .setSortDirection(sortDirection)
                    .build();
        }

        return expensePageDto;
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

    private static void copyAttributeValues(ExpenseDTO targetDto, Expense sourceEntity) {
        if(targetDto != null && sourceEntity != null) {
            targetDto.setPosition(sourceEntity.getPosition());
            targetDto.setDate(new LocalDate(sourceEntity.getDate().getTime()));
            targetDto.setAmount(sourceEntity.getAmount());
            targetDto.setDescription(sourceEntity.getDescription());

            if(sourceEntity instanceof DebitExpense) {
                targetDto.setSource(ExpenseSource.DEBIT_CARD);
            } else if(sourceEntity instanceof CreditExpense) {
                targetDto.setSource(ExpenseSource.CREDIT_CARD);
            } else {
                throw new UnsupportedOperationException("Value copy from entity to DTO object is not yet supported for entity of type: " + sourceEntity.getClass());
            }
        }
    }

    private static List<ExpenseResponseDTO> expenseListToExpenseResponseDtoList(List<Expense> expenses) {
        List<ExpenseResponseDTO> expenseResponseDTOs = new ArrayList<ExpenseResponseDTO>();

        if(expenses != null && !expenses.isEmpty()) {
            for(Expense expense : expenses) {
                expenseResponseDTOs.add(entityToResponseDto(expense));
            }
        }

        return expenseResponseDTOs;
    }
}
