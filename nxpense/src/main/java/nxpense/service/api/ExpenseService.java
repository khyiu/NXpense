package nxpense.service.api;

import nxpense.domain.Expense;
import nxpense.dto.BalanceInfoDTO;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.VersionedSelectionItem;
import nxpense.exception.RequestCannotCompleteException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *
 */
public interface ExpenseService {


    /**
     * Converts the given expense DTO instance into an instance of {@link nxpense.domain.Expense} and persists it in the DB
     * alongside the given list of attached files.
     * @param expenseDTO Expense representation sent by the client and that is to be persisted.
     * @param attachments List of files to be associated to the Expense to be persisted.
     * <br>
     * <b>NOTE</b>
     * This method is transactional and will rollback for any thrown instance of {@link java.lang.Exception}
     * @return DB-synchronized {@link nxpense.domain.Expense} object that has been created and persisted.
     */
    public Expense createNewExpense(ExpenseDTO expenseDTO, List<MultipartFile> attachments);

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
     * Updates the expense with the specified {@code id} with the information held in the {@code expenseDTO} object,
     * and associates the {@code attachments} list of File to it.
     * @param id ID of the expense item to be updated
     * @param expenseDTO Object containing the new state of the expense to be updated
     * @param attachments File to be associated with the updated Expense item
     * @return The updated version of the expense item
     */
    public Expense updateExpense(int id, ExpenseDTO expenseDTO, List<MultipartFile> attachments);

    /**
     * Deletes the {@link nxpense.domain.Expense} items from DB, whose ID is contained in the specified selection.
     * @param selection list of identifier and version number of the expense items to delete
     * @throws RequestCannotCompleteException if the specified list of ID is null
     */
    public void deleteExpense(List<VersionedSelectionItem> selection) throws RequestCannotCompleteException;

    /**
     * Retrieves the balance information related to the current user.
     * @return Object containing balance information such as the sum of verified expenses and the sum of non-verified expenses
     */
    public BalanceInfoDTO getBalanceInfo();

    /**
     * Associates the Tag identified by {@code tagId} to the expense identified by {@code expenseId}
     * @param expenseId ID of the expense to which the tag with ID = {@code tagId} is to be associated with
     * @param tagId ID of the tag to be associated with the target expense item
     * @return the updated expense
     * @throws nxpense.exception.BadRequestException if no expense found for {@code expenseId} or no tag found for {@code tagId}
     * @throws nxpense.exception.ForbiddenActionException if specified expense or specified tag does not belong to current user
     */
    public Expense associateTagToExpense(int expenseId, int tagId);
}
