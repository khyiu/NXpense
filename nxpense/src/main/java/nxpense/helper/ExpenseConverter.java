package nxpense.helper;

import nxpense.domain.CreditExpense;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseResponseDTO;
import nxpense.dto.ExpenseSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static ExpenseDTO entityToDto(Expense expense) {
        ExpenseDTO expenseDTO = null;

        if(expense != null) {
            expenseDTO = new ExpenseDTO();
            copyAttributeValues(expenseDTO, expense);
        }

        return expenseDTO;
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
            targetDto.setDate(sourceEntity.getDate());
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
}
