package nxpense.repository;

import nxpense.domain.Expense;
import nxpense.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(readOnly = true)
public interface ExpenseRepository extends PagingAndSortingRepository<Expense, Integer> {

    /**
     * Counts the number of {@link nxpense.domain.Expense} items belonging to the specified user and whose date
     * is equal to the specified one
     * @param owner User to which items must belong to in order to be taken into account
     * @param date Date to which items' date is compared to
     * @return number of items belonging to the {@code owner} User and whose date is equal to the {@code date} Date
     */
    public long countByUserAndDate(@Param("owner") User owner, @Param("date") Date date);

    /**
     * Deletes {@link nxpense.domain.Expense} items whose ID is in the given list and that belong to the specified user
     * @param owner User to which the items to be deleted belong to
     * @param ids List of ID from the items to be deleted
     */
    @Transactional
    public void deleteByIdInAndUser(@Param("ids") List<Integer> ids, @Param("user") User owner);

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

}
