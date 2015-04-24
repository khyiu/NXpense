package nxpense.service;

import nxpense.domain.Expense;
import nxpense.domain.User;
import nxpense.dto.ExpenseDTO;
import nxpense.exception.BadRequestException;
import nxpense.exception.RequestCannotCompleteException;
import nxpense.exception.UnauthenticatedException;
import nxpense.helper.ExpenseConverter;
import nxpense.helper.ExpenseHelper;
import nxpense.repository.ExpenseRepository;
import nxpense.repository.UserRepository;
import nxpense.security.CustomUserDetails;
import nxpense.service.api.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service("expenseService")
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional(rollbackFor = {Exception.class})
    public Expense createNewExpense(ExpenseDTO expenseDTO) {
        Expense expense = ExpenseConverter.dtoToEntity(expenseDTO);

        if (expense == null) {
            LOGGER.warn("Attempt to create a NULL expense!");
            throw new BadRequestException("Cannot persist a NULL expense entity");
        }

        User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (currentUser == null) {
            throw new UnauthenticatedException("User does not seem to be authenticated!");
        }

        // First, call save( ) on existing user, to merge the detached User instance associated to security context...
        currentUser = userRepository.save(currentUser);

        int newExpensePosition = (int) expenseRepository.countByUserAndDate(currentUser, expense.getDate());
        LOGGER.debug("Position of new expense with date {} for user with email {} = {}", expense.getDate(), currentUser.getEmail(), newExpensePosition);

        expense.setPosition(newExpensePosition);
        currentUser.addExpense(expense);
        expense.setUser(currentUser);

        return expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Page<Expense> getPageExpenses(Integer pageNumber, Integer size, Sort.Direction direction, String[] properties) {
        PageRequest pageRequest = new PageRequest(pageNumber, size, direction, properties);
        User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        return expenseRepository.findAllByUser(pageRequest, currentUser);
    }

    @Transactional(rollbackFor = {Exception.class})
    public Expense updateExpense(int id, ExpenseDTO expenseDTO) {
        User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        // First, call save( ) on existing user, to merge the detached User instance associated to security context...
        currentUser = userRepository.save(currentUser);
        Expense existingExpense = expenseRepository.findByIdAndUser(id, currentUser);

        if(existingExpense == null) {
            throw new RequestCannotCompleteException("Expense [" + id + "] either does not exist, or does not belong to user [" + currentUser + "]");
        }

        // date didn't change --> updating the existing expense's content
        if(existingExpense.getDate().equals(expenseDTO.getDate().toDate())) {
            LOGGER.debug("Updating expense [{}] -> update", id);
            ExpenseHelper.overwriteFields(ExpenseConverter.dtoToEntity(expenseDTO), existingExpense);
            expenseRepository.save(existingExpense);
        } else {
            // date did change --> delete existing one + creating new one by using existing service method in order
            // properly handle the 'position' attribute
            LOGGER.debug("Updating expense [{}] -> delete + update", id);
            deleteExpense(Arrays.asList(id));
            createNewExpense(expenseDTO);
        }

        return existingExpense;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteExpense(List<Integer> ids) throws RequestCannotCompleteException {
        if(ids == null) {
            throw new RequestCannotCompleteException("Cannot proceed to expense deletion with a NULL list of IDs.");
        }

        User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        // First, call save( ) on existing user, to merge the detached User instance associated to security context...
        currentUser = userRepository.save(currentUser);

        expenseRepository.decrementSameDateHigherPosition(ids, currentUser);
        int numberDeletedItems = expenseRepository.deleteByIdInAndUser(currentUser, ids);
        LOGGER.info("User {} deleted {} item(s)", currentUser, numberDeletedItems);
    }
}
