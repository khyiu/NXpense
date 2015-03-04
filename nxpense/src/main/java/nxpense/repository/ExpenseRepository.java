package nxpense.repository;

import nxpense.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
}
