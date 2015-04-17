package nxpense.repository;

import nxpense.domain.Expense;
import nxpense.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

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
     * Deletes {@link nxpense.domain.Expense} items whose ID is in the given list
     * @param ids List of ID from the items to be deleted
     */
    public void deleteByIdIn(List<Integer> ids);
}
