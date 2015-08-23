package nxpense.helper;

import nxpense.builder.PageDtoBuilder;
import nxpense.domain.CreditExpense;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.domain.Tag;
import nxpense.dto.*;
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

        if (expenseDTO != null) {
            if (expenseDTO.getSource() != null) {
                expense = expenseDTO.getSource().getEmptyExpenseInstance();
            } else {
                throw new IllegalArgumentException("DTO object to be converted into Expense entity must have a 'source' specified");
            }

            expense.setAmount(expenseDTO.getAmount());
            expense.setDate(expenseDTO.getDate().toDate());
            expense.setDescription(expenseDTO.getDescription());
            expense.setPosition(expenseDTO.getPosition());
        }

        return expense;
    }

    public static ExpenseResponseDTO entityToResponseDto(Expense expense) {
        ExpenseResponseDTO expenseDto = null;

        if (expense != null) {
            expenseDto = new ExpenseResponseDTO();
            expenseDto.setId(expense.getId());
            expenseDto.setVersion(expense.getVersion());
            expenseDto.setAttachments(AttachmentConverter.attachmentListToAttachmentResponseDtoList(expense.getAttachments()));

            copyAttributeValues(expenseDto, expense);
            copyTags(expenseDto, expense);
        }

        return expenseDto;
    }

    public static PageDTO<ExpenseResponseDTO> expensePageToExpensePageDto(Page<Expense> expensePage) {
        PageDTO<ExpenseResponseDTO> expensePageDto = null;

        if (expensePage != null) {
            List<ExpenseResponseDTO> items = expenseListToExpenseResponseDtoList(expensePage.getContent());

            String sortProperty = null;
            Sort.Direction sortDirection = null;
            Iterator<Sort.Order> orders = expensePage.getSort().iterator();

            while (orders.hasNext() && sortProperty == null) {
                Sort.Order order = orders.next();
                sortProperty = order.getProperty();
                sortDirection = order.getDirection();
            }

            expensePageDto = new PageDtoBuilder<ExpenseResponseDTO>()
                    .setPageNumber(expensePage.getNumber())
                    .setPageSize(expensePage.getSize())
                    .setNumberOfItems(expensePage.getNumberOfElements())
                    .setTotalNumberOfItems(expensePage.getTotalElements())
                    .setTotalNumberOfPages(expensePage.getTotalPages())
                    .setItems(items)
                    .setSortProperty(sortProperty)
                    .setSortDirection(sortDirection)
                    .build();
        }

        return expensePageDto;
    }

    private static void copyAttributeValues(ExpenseDTO targetDto, Expense sourceEntity) {
        if (targetDto != null && sourceEntity != null) {
            targetDto.setPosition(sourceEntity.getPosition());
            targetDto.setDate(new LocalDate(sourceEntity.getDate().getTime()));
            targetDto.setAmount(sourceEntity.getAmount());
            targetDto.setDescription(sourceEntity.getDescription());

            if (sourceEntity instanceof DebitExpense) {
                targetDto.setSource(ExpenseSource.DEBIT_CARD);
            } else if (sourceEntity instanceof CreditExpense) {
                targetDto.setSource(ExpenseSource.CREDIT_CARD);
            } else {
                throw new UnsupportedOperationException("Value copy from entity to DTO object is not yet supported for entity of type: " + sourceEntity.getClass());
            }
        }
    }

    private static void copyTags(ExpenseDTO targetDto, Expense sourceEntity) {
        if (targetDto != null && sourceEntity != null && sourceEntity.getTags() != null) {
            for(Tag tag : sourceEntity.getTags()) {
                targetDto.addTags(TagConverter.entityToResponseDto(tag));
            }
        }
    }

    private static List<ExpenseResponseDTO> expenseListToExpenseResponseDtoList(List<Expense> expenses) {
        List<ExpenseResponseDTO> expenseResponseDTOs = new ArrayList<ExpenseResponseDTO>();

        if (expenses != null && !expenses.isEmpty()) {
            for (Expense expense : expenses) {
                expenseResponseDTOs.add(entityToResponseDto(expense));
            }
        }

        return expenseResponseDTOs;
    }
}
