package nxpense.service.api;

import nxpense.dto.ExpenseDTO;

/**
 *
 */
public interface ExpenseService {

    /**
     * Converts the given expense DTO instance into an instance of {@link nxpense.domain.Expense} and persists it in the DB.
     * @param expenseDTO Expense representation sent by the client and that is to be persisted.
     * <br>
     * <b>NOTE</b>
     * This method is transactional and will rollback for any thrown instance of {@link java.lang.Exception}
     */
    public void createNewExpense(ExpenseDTO expenseDTO);
}
