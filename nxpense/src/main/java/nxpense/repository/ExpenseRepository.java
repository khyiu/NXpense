package nxpense.repository;

import nxpense.domain.Expense;
import nxpense.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ExpenseRepository extends PagingAndSortingRepository<Expense, Integer> {

    /**
     * Gets the number of {@link nxpense.domain.Expense} items for the given user, in database, whose <em>date</em> attribute
     * is lower or equal to the specified date
     * @param date Date to which expense items' date is compared to
     * @param owner User to which the retrieved expense items belong to
     * @return number of expense items whose date is lower than or equal to the given date
     */
    @Query("select count(e) from Expense e where e.user = :owner and e.date <= :date")
    public long getNumberOfItemBeforeDate(@Param("owner") User owner, @Param("date") Date date);


    /**
     * Increment by 1 the <em>position</em> attribute for all {@link nxpense.domain.Expense} items that belong to the
     * specified user and whose position is greater or equal to the given one.
     * @param owner User to which the targeted expense items belong to.
     * @param startPosition Position value to filter the expense items that are to be updated.
     * @return number of updated items.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Modifying
    @Query("update Expense e set e.position = e.position + 1 where e.user = :owner and e.position >= :startPosition")
    public int incrementPosition(@Param("owner") User owner, @Param("startPosition") int startPosition);

    /**
     * Deletes {@link nxpense.domain.Expense} items whose ID is in the given list
     * @param ids List of ID from the items to be deleted
     */
    public void deleteByIdIn(List<Integer> ids);
}
