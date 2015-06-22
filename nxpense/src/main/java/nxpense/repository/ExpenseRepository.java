package nxpense.repository;

import nxpense.domain.Expense;
import nxpense.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Transactional(readOnly = true)
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    /**
     * Counts the number of {@link nxpense.domain.Expense} items belonging to the specified user and whose date
     * is equal to the specified one
     * @param owner User to which items must belong to in order to be taken into account
     * @param date Date to which items' date is compared to
     * @return number of items belonging to the {@code owner} User and whose date is equal to the {@code date} Date
     */
    public long countByUserAndDate(@Param("owner") User owner, @Param("date") Date date);

    /**
     * Increments the {@code position} attribute of the {@link nxpense.domain.Expense} items: <br/>
     * <ul>
     *      <li>that belong to the specified {@code User}</li>
     *      <li>whose {@code date} is equal to the {@code date} of the expenses whose ID is given in the provided {@code ids} list</li>
     *      <li>whose {@code position} attribute is greater than the {@code position} of the expenses whose ID is given in the provided {@code ids} list</li>
     * </ul>
     * @param ids List of IDs that identify the expense items to which {@code date} and {@code position} attributes are compared
     * @param owner User to which expense items to be modified must belong to
     * @return number of updated items
     */
    @Query("update Expense e set e.position = e.position - 1 " +
            "where e.user = :owner " +
            "and e.id in" +
            "(select e2.id from Expense e2, Expense e3 " +
            "where e2.user = :owner " +
            "and e3.user = :owner " +
            "and e3.id in :ids " +
            "and e2.date = e3.date " +
            "and e2.position > e3.position)")
    @Modifying
    public int decrementSameDateHigherPosition(@Param("ids") List<Integer> ids, @Param("owner") User owner);

    /**
     * Returns a {@link Page} of {@link nxpense.domain.Expense} items meeting the paging restriction provided in the {@code Pageable} object,
     * and belonging to the specified {@code User}.
     * @param pageable Object that holds the paging restrictions to be met
     * @param user to which items to be returned must belong to
     * @return Page of {@link nxpense.domain.Expense} items belonging to the specified {@code user} and meeting the provided restrictions.
     */
    public Page<Expense> findAllByUser(Pageable pageable, User user);

    /**
     * Retrieves the {@link nxpense.domain.Expense} item that has the specified {@code id} and that belongs to the specified {@code user}.
     * @param id ID of the expense to retrieve
     * @param user User to which the retrieved expense must belong to
     * @return The expense with the specified ID and belonging to the specified user
     */
    public Expense findByIdAndUser(int id, User user);

    /**
     * Retrieves the {@link nxpense.domain.Expense} items with specified IDs and belonging to the specified {@code user}.
     * @param ids list of ID from the expenses to be retrieved
     * @param user owner of the expenses to be retrieved
     * @return List of expenses whose ID is in the {@code ids} list and belonging to the {@code user} User.
     */
    public List<Expense> findByIdInAndUser(List<Integer> ids, User user);

    /**
     * Computes the sum of the specified user's expense filtered out by their verification status.
     * @param user Owner of the expenses to be taken into account in the sum calculation
     * @param verified Status of the expenses to be taken into account in the sum calculation
     * @return sum of expenses that belong to {@code user} and whose verification status is equal to {@code verified}
     */
    @Query("select sum(e.amount) from Expense e where e.user = :owner and e.verified = :verificationStatus")
    public BigDecimal sumExpenseByVerificationStatus(@Param("owner")User user, @Param("verificationStatus") boolean verified);
}
