package nxpense.service;


import nxpense.builder.ExpenseDtoBuilder;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.domain.User;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.exception.BadRequestException;
import nxpense.exception.RequestCannotCompleteException;
import nxpense.exception.UnauthenticatedException;
import nxpense.helper.SecurityPrincipalHelper;
import nxpense.repository.ExpenseRepository;
import nxpense.repository.UserRepository;
import nxpense.security.CustomUserDetails;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExpenseServiceImplTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private SecurityPrincipalHelper securityPrincipalHelper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private static final BigDecimal AMOUNT = BigDecimal.TEN;
    private static final LocalDate DATE = new LocalDate();
    private static final String DESCRIPTION = "Some DESCRIPTION";

    private static final List<Integer> EXPENSE_IDS = Arrays.asList(1, 2, 3);
    private static final Integer EXPENSE_ID_UNEXISTING = 100;

    private static final ExpenseDTO EXPENSE_DTO = new ExpenseDtoBuilder()
            .setSource(ExpenseSource.DEBIT_CARD)
            .setAmount(AMOUNT)
            .setDate(DATE)
            .setDescription(DESCRIPTION)
            .build();

    @Before
    public void initMocks() {
        given(userRepository.save(any(User.class))).willAnswer(new Answer<User>() {

            public User answer(InvocationOnMock invocation) throws Throwable {
                return (User) invocation.getArguments()[0];
            }
        });

        given(expenseRepository.findByIdAndUser(EXPENSE_ID_UNEXISTING, mockUser)).willReturn(null);

        given(securityPrincipalHelper.getCurrentUser()).willReturn(mockUser);
    }

    @Test
    public void testCreateNewExpense() {
        ArgumentCaptor<Expense> expenseArgument = ArgumentCaptor.forClass(Expense.class);

        Expense expense = new DebitExpense();
        expense.setAmount(AMOUNT);
        expense.setDate(DATE.toDate());
        expense.setDescription(DESCRIPTION);
        expense.setUser(mockUser);

        expenseService.createNewExpense(EXPENSE_DTO);
        Mockito.<CrudRepository>verify(expenseRepository).save(expenseArgument.capture());

        assertThat(expenseArgument.getValue()).isEqualTo(expense);
        assertThat(expenseArgument.getValue().getUser().getExpenses()).contains(expense);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateNewExpense_nullInputValue() {
        expenseService.createNewExpense(null);
    }

    @Test(expected = UnauthenticatedException.class)
    public void testCreateNewExpense_unauthenticatedUser() {
        given(securityPrincipalHelper.getCurrentUser()).willCallRealMethod();
        UserDetails userDetails = new CustomUserDetails(null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        expenseService.createNewExpense(EXPENSE_DTO);
    }

    @Test
    public void testGetPageExpenses() {
        int page = 5;
        int pageSize = 25;
        Sort.Direction direction = Sort.Direction.ASC;
        String [] props = {"test"};

        PageRequest expectedPageRequest = new PageRequest(page, pageSize, direction, props);

        expenseService.getPageExpenses(page, pageSize, direction, props);
        verify(expenseRepository).findAllByUser(expectedPageRequest, mockUser);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPageExpenses_noSortingProperties() {
        int page = 5;
        int pageSize = 25;
        Sort.Direction direction = Sort.Direction.ASC;
        String [] props = {};

        expenseService.getPageExpenses(page, pageSize, direction, props);
    }

    @Test(expected = RequestCannotCompleteException.class)
    public void testDeleteExpense_noExpenseIds() {
        expenseService.deleteExpense(null);
    }

    @Test
    public void testDeleteExpense() {
        expenseService.deleteExpense(EXPENSE_IDS);

        verify(expenseRepository).decrementSameDateHigherPosition(EXPENSE_IDS, mockUser);
        verify(expenseRepository).deleteByIdInAndUser(mockUser, EXPENSE_IDS);
    }

    @Test(expected = RequestCannotCompleteException.class)
    public void testUpdateExpense_unexisting() {
        expenseService.updateExpense(EXPENSE_ID_UNEXISTING, EXPENSE_DTO);
    }
}