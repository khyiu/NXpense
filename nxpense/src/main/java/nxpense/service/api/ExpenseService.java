package nxpense.service.api;

import nxpense.domain.Expense;
import nxpense.dto.ExpenseDTO;
import nxpense.exception.RequestCannotCompleteException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

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
     * @return DB-synchronized {@link nxpense.domain.Expense} object that has been created and persisted.
     */
    public Expense createNewExpense(ExpenseDTO expenseDTO);

    /**
     * Retrieves the page corresponding to the requested page number, with the specified page size and that contains
     * expenses that are sorted by the given attributes, in the specified direction.
     * @param pageNumber number of the result page to be returned
     * @param size number of result items per page
     * @param direction sorting direction: ASC/DESC
     * @param properties attributes by which results are sorted
     * @return {@link org.springframework.data.domain.Page} object containing requested expense items
     */
    public Page<Expense> getPageExpenses(Integer pageNumber, Integer size, Sort.Direction direction, String[] properties);

    /**
     * Updates the expense with the specified {@code id} with the information held in the {@code expenseDTO} object.
     * @param id ID of the expense item to be updated
     * @param expenseDTO Object containing the new state of the expense to be updated
     * @return The updated version of the expense item
     */
    public Expense updateExpense(int id, ExpenseDTO expenseDTO);

    /**
     * Deletes the {@link nxpense.domain.Expense} items whose ID is is the specified list of ID from the DB.
     * @param ids List of ID of the {@link nxpense.domain.Expense} items that must be deleted from DB
     * @throws RequestCannotCompleteException if the specified list of ID is null
     */
    public void deleteExpense(List<Integer> ids) throws RequestCannotCompleteException;
}
