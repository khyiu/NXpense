package service;


import nxpense.builder.ExpenseDtoBuilder;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.domain.User;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.exception.BadRequestException;
import nxpense.exception.UnauthenticatedException;
import nxpense.repository.ExpenseRepository;
import nxpense.repository.UserRepository;
import nxpense.security.CustomUserDetails;
import nxpense.service.ExpenseServiceImpl;
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
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExpenseServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private static final String USER_EMAIL = "test@test.com";
    private static final String USER_PASSWORD = "secret";
    private static User mockUser;

    private static final BigDecimal AMOUNT = BigDecimal.TEN;
    private static final Date DATE = new Date();
    private static final String DESCRIPTION = "Some DESCRIPTION";

    @Before
    public void initAuthenticationMock() {
        mockUser = new User();
        mockUser.setEmail(USER_EMAIL);
        mockUser.setPassword(USER_PASSWORD.toCharArray());

        UserDetails userDetails = new CustomUserDetails(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Before
    public void initMocks() {
        given(userRepository.save(any(User.class))).willAnswer(new Answer<User>() {

            public User answer(InvocationOnMock invocation) throws Throwable {
                return (User) invocation.getArguments()[0];
            }
        });
    }

    @Test
    public void testCreateNewExpense() {
        ArgumentCaptor<Expense> expenseArgument = ArgumentCaptor.forClass(Expense.class);

        Expense expense = new DebitExpense();
        expense.setAmount(AMOUNT);
        expense.setDate(DATE);
        expense.setDescription(DESCRIPTION);
        expense.setUser(mockUser);

        ExpenseDTO expenseDto = new ExpenseDtoBuilder()
                .setSource(ExpenseSource.DEBIT_CARD)
                .setAmount(AMOUNT)
                .setDate(DATE)
                .setDescription(DESCRIPTION)
                .build();

        expenseService.createNewExpense(expenseDto);
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
        UserDetails userDetails = new CustomUserDetails(null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ExpenseDTO expenseDto = new ExpenseDtoBuilder()
                .setSource(ExpenseSource.DEBIT_CARD)
                .setAmount(AMOUNT)
                .setDate(DATE)
                .setDescription(DESCRIPTION)
                .build();

        expenseService.createNewExpense(expenseDto);
    }

    @Test
    public void testGetPageExpenses() {
        int page = 5;
        int pageSize = 25;
        Sort.Direction direction = Sort.Direction.ASC;
        String [] props = {"test"};

        PageRequest expectedPageRequest = new PageRequest(page, pageSize, direction, props);
        ArgumentCaptor<PageRequest> argumentCaptor = ArgumentCaptor.forClass(PageRequest.class);

        expenseService.getPageExpenses(page, pageSize, direction, props);
        verify(expenseRepository).findAll(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(expectedPageRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPageExpenses_noSortingProperties() {
        int page = 5;
        int pageSize = 25;
        Sort.Direction direction = Sort.Direction.ASC;
        String [] props = {};

        expenseService.getPageExpenses(page, pageSize, direction, props);
    }
}
