package nxpense.service;

import nxpense.domain.Expense;
import nxpense.domain.Tag;
import nxpense.domain.User;
import nxpense.dto.BalanceInfoDTO;
import nxpense.dto.ExpenseDTO;
import nxpense.exception.BadRequestException;
import nxpense.exception.ForbiddenActionException;
import nxpense.exception.RequestCannotCompleteException;
import nxpense.helper.ExpenseConverter;
import nxpense.helper.ExpenseHelper;
import nxpense.helper.SecurityPrincipalHelper;
import nxpense.repository.ExpenseRepository;
import nxpense.repository.TagRepository;
import nxpense.service.api.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service("expenseService")
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    @Autowired
    private SecurityPrincipalHelper securityPrincipalHelper;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private TagRepository tagRepository;

    @Transactional(rollbackFor = {Exception.class})
    public Expense createNewExpense(ExpenseDTO expenseDTO) {
        Expense expense = ExpenseConverter.dtoToEntity(expenseDTO);

        if (expense == null) {
            LOGGER.warn("Attempt to create a NULL expense!");
            throw new BadRequestException("Cannot persist a NULL expense entity");
        }

        User currentUser = securityPrincipalHelper.getCurrentUser();
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
        User currentUser = securityPrincipalHelper.getCurrentUser();
        return expenseRepository.findAllByUser(pageRequest, currentUser);
    }

    @Transactional(rollbackFor = {Exception.class})
    public Expense updateExpense(int id, ExpenseDTO expenseDTO) {
        User currentUser = securityPrincipalHelper.getCurrentUser();
        Expense existingExpense = expenseRepository.findByIdAndUser(id, currentUser);

        if (existingExpense == null) {
            throw new RequestCannotCompleteException("Expense [" + id + "] either does not exist, or does not belong to user [" + currentUser + "]");
        }

        // synchronizing the version number with the one sent back by client
        existingExpense.setVersion(expenseDTO.getVersion());

        // date didn't change --> updating the existing expense's content
        if (existingExpense.getDate().equals(expenseDTO.getDate().toDate())) {
            LOGGER.debug("Updating expense [{}] -> update", id);
            ExpenseHelper.overwriteFields(ExpenseConverter.dtoToEntity(expenseDTO), existingExpense);
            return expenseRepository.save(existingExpense);
        } else {
            // date did change --> delete existing one + creating new one by using existing service method in order
            // properly handle the 'position' attribute
            LOGGER.debug("Updating expense [{}] -> delete + update", id);
            deleteExpense(Arrays.asList(id));
            return createNewExpense(expenseDTO);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteExpense(List<Integer> ids) throws RequestCannotCompleteException {
        if (ids == null) {
            throw new RequestCannotCompleteException("Cannot proceed to expense deletion with a NULL list of IDs.");
        }

        User currentUser = securityPrincipalHelper.getCurrentUser();
        expenseRepository.decrementSameDateHigherPosition(ids, currentUser);
        int numberDeletedItems = expenseRepository.deleteByIdInAndUser(currentUser, ids);
        LOGGER.info("User {} deleted {} item(s)", currentUser, numberDeletedItems);
    }

    @Transactional(readOnly = true)
    public BalanceInfoDTO getBalanceInfo() {
        User currentUser = securityPrincipalHelper.getCurrentUser();
        BigDecimal sumVerified = expenseRepository.sumExpenseByVerificationStatus(currentUser, true);
        BigDecimal sumNonVerified = expenseRepository.sumExpenseByVerificationStatus(currentUser, false);

        sumVerified = sumVerified == null ? BigDecimal.ZERO : sumVerified;
        sumNonVerified = sumNonVerified == null ? BigDecimal.ZERO : sumNonVerified;

        return new BalanceInfoDTO(sumVerified, sumNonVerified, sumVerified.add(sumNonVerified));
    }

    @Transactional
    @Override
    public Expense associateTagToExpense(int expenseId, int tagId) {
        User currentUser = securityPrincipalHelper.getCurrentUser();
        Expense expense = expenseRepository.findOne(expenseId);

        if(expense == null) {
            LOGGER.warn("Attempt to add tag to an expense that wasn't found (ID={})", expenseId);
            throw new BadRequestException("No expense found for tag association");
        }

        Tag tag = tagRepository.findOne(tagId);

        if(tag == null) {
            LOGGER.warn("Attempt to add a tag that wasn't found (ID={})", tagId);
            throw new BadRequestException("No tag found for tag association");
        }

        if(!expense.getUser().getId().equals(currentUser.getId())) {
            LOGGER.warn("Attempt to update an expense that does not belong to current user (ID={})", expenseId);
            throw new ForbiddenActionException("Cannot update expense that does not belong to current user");
        }

        if(!currentUser.ownTag(tag)) {
            LOGGER.warn("Attempt to associate a tag that does not belong to current user (ID={})", tagId);
            throw new ForbiddenActionException("Cannot associate a tag that does not belong to current user");
        }

        expense.addTag(tag);
        tag.addExpense(expense);
        return expense;
    }
}
