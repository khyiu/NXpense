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


    @Transactional(propagation = Propagation.SUPPORTS)
    @Modifying
    @Query("update Expense e set e.position = e.position + 1 where e.user = :owner and e.position >= :startPosition")
    public int incrementPosition(@Param("owner") User owner, @Param("startPosition") int startPosition);
}
