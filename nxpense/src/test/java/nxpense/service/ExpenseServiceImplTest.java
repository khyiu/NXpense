package nxpense.service;


import nxpense.builder.AttachmentResponseDTOBuilder;
import nxpense.builder.ExpenseDtoBuilder;
import nxpense.domain.*;
import nxpense.dto.AttachmentResponseDTO;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.dto.VersionedSelectionItem;
import nxpense.exception.BadRequestException;
import nxpense.exception.ForbiddenActionException;
import nxpense.exception.RequestCannotCompleteException;
import nxpense.exception.UnauthenticatedException;
import nxpense.helper.SecurityPrincipalHelper;
import nxpense.repository.ExpenseRepository;
import nxpense.repository.TagRepository;
import nxpense.repository.UserRepository;
import nxpense.security.CustomUserDetails;
import nxpense.service.api.AttachmentService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    private TagRepository tagRepository;

    @Mock
    private SecurityPrincipalHelper securityPrincipalHelper;

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private static final BigDecimal AMOUNT = BigDecimal.TEN;
    private static final LocalDate DATE = new LocalDate();
    private static final String DESCRIPTION = "Some DESCRIPTION";

    private static final List<Integer> EXPENSE_IDS = Arrays.asList(1);
    private static final List<VersionedSelectionItem> EXPENSE_SELECTION = Arrays.asList(new VersionedSelectionItem(1, 1));
    private static final Integer EXPENSE_ID = 1;
    private static final Integer EXPENSE_ID_UNEXISTING = 10;
    private static final Integer EXPENSE_ID_NOT_CURRENT_USER = 100;
    private static final Integer TAG_ID = 1;
    private static final String TAG_NAME=  "Electricity bill";
    private static final Integer TAG_ID_UNEXISTING = 10;
    private static final Integer TAG_ID_NOT_CURRENT_USER = 100;

    private static final String ATTACHMENT_FILENAME_1 = "file1.txt";
    private static final String ATTACHMENT_FILENAME_2 = "file2.txt";
    private static final List<MultipartFile> ATTACHMENTS = Arrays.asList(
            (MultipartFile) new MockMultipartFile(ATTACHMENT_FILENAME_1, new byte[0]),
            (MultipartFile) new MockMultipartFile(ATTACHMENT_FILENAME_2, new byte[0])
    );

    private static final ExpenseDTO EXPENSE_DTO = new ExpenseDtoBuilder()
            .setSource(ExpenseSource.DEBIT_CARD)
            .setAmount(AMOUNT)
            .setDate(DATE)
            .setDescription(DESCRIPTION)
            .build();


    private static final String ATTACHMENT_URL_1 = "/nxpense/attach/1/" + ATTACHMENT_FILENAME_1;
    private static final String ATTACHMENT_URL_2 = "/nxpense/attach/1/" + ATTACHMENT_FILENAME_2;

    private static final Attachment ATTACHMENT_1 = new Attachment(ATTACHMENT_FILENAME_1, new byte[]{});
    private static final Attachment ATTACHMENT_2 = new Attachment(ATTACHMENT_FILENAME_2, new byte[]{});

    @Before
    public void initMocks() {
        given(userRepository.save(any(User.class))).willAnswer(new Answer<User>() {

            public User answer(InvocationOnMock invocation) throws Throwable {
                return (User) invocation.getArguments()[0];
            }
        });

        given(expenseRepository.findByIdAndUser(EXPENSE_ID_UNEXISTING, mockUser)).willReturn(null);

        given(expenseRepository.findOne(EXPENSE_ID)).willAnswer(new Answer<Expense>() {

            @Override
            public Expense answer(InvocationOnMock invocation) throws Throwable {
                Expense expense = new DebitExpense();
                Field idField = expense.getClass().getSuperclass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(expense, EXPENSE_ID);
                expense.setUser(mockUser);
                expense.setDate(DATE.toDate());
                expense.setAmount(AMOUNT);
                expense.setDescription(DESCRIPTION);

                Tag tag = new Tag();
                tag.setName(TAG_NAME);
                tag.addExpense(expense);
                expense.getTags().add(tag);

                mockUser.addExpense(expense);
                return expense;
            }
        });

        given(expenseRepository.findOne(EXPENSE_ID_NOT_CURRENT_USER)).willAnswer(new Answer<Expense>() {

            @Override
            public Expense answer(InvocationOnMock invocation) throws Throwable {
                Expense expense = new DebitExpense();
                Field idField = expense.getClass().getSuperclass().getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(expense, EXPENSE_ID_NOT_CURRENT_USER);

                User notCurrentUser = new User();
                idField = User.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(notCurrentUser, Integer.parseInt("9999"));
                expense.setUser(notCurrentUser);
                notCurrentUser.addExpense(expense);

                return expense;
            }
        });

        given(expenseRepository.findByIdInAndUser(EXPENSE_IDS, mockUser)).willAnswer(new Answer<List<Expense>>() {
            @Override
            public List<Expense> answer(InvocationOnMock invocation) throws Throwable {
                List<Expense> expenses = new ArrayList<Expense>();
                Expense expense = new DebitExpense();
                expense.setDescription("Dummy Debit Expense");
                expenses.add(expense);
                return expenses;
            }
        });

        given(expenseRepository.save(any(Expense.class))).willAnswer(new Answer<Expense>() {
            @Override
            public Expense answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgumentAt(0, Expense.class);
            }
        });

        given(securityPrincipalHelper.getCurrentUser()).willReturn(mockUser);

        given(tagRepository.findOne(TAG_ID_UNEXISTING)).willReturn(null);

        given(tagRepository.findOne(TAG_ID_NOT_CURRENT_USER)).willAnswer(new Answer<Tag>() {

            @Override
            public Tag answer(InvocationOnMock invocation) throws Throwable {
                Tag tag = new Tag();
                Field idField = Tag.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(tag, TAG_ID_NOT_CURRENT_USER);
                return tag;
            }
        });

        given(tagRepository.findOne(TAG_ID)).willAnswer(new Answer<Tag>() {

            @Override
            public Tag answer(InvocationOnMock invocation) throws Throwable {
                Tag tag = new Tag();
                tag.setName(TAG_NAME);
                Field idField = Tag.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(tag, TAG_ID);
                mockUser.addTag(tag);
                return tag;
            }
        });
    }

    @Test
    public void testCreateNewExpense() {
        ArgumentCaptor<Expense> expenseArgument = ArgumentCaptor.forClass(Expense.class);

        Expense expense = new DebitExpense();
        expense.setAmount(AMOUNT);
        expense.setDate(DATE.toDate());
        expense.setDescription(DESCRIPTION);
        expense.setUser(mockUser);

        expenseService.createNewExpense(EXPENSE_DTO, ATTACHMENTS);
        Mockito.<CrudRepository>verify(expenseRepository).save(expenseArgument.capture());

        assertThat(expenseArgument.getValue()).isEqualTo(expense);
        assertThat(expenseArgument.getValue().getUser().getExpenses()).contains(expense);
        assertThat(expenseArgument.getValue().getAttachments()).hasSize(2);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateNewExpense_nullInputValue() {
        expenseService.createNewExpense(null, ATTACHMENTS);
    }

    @Test(expected = UnauthenticatedException.class)
    public void testCreateNewExpense_unauthenticatedUser() {
        given(securityPrincipalHelper.getCurrentUser()).willCallRealMethod();
        UserDetails userDetails = new CustomUserDetails(null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        expenseService.createNewExpense(EXPENSE_DTO, ATTACHMENTS);
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
        ArgumentCaptor<Expense> expenseArgument = ArgumentCaptor.forClass(Expense.class);
        expenseService.deleteExpense(EXPENSE_SELECTION);

        Mockito.<CrudRepository>verify(expenseRepository).delete(expenseArgument.capture());
        assertThat(expenseArgument.getValue().getDescription()).isEqualTo("Dummy Debit Expense");
    }

    @Test(expected = RequestCannotCompleteException.class)
    public void testUpdateExpense_unexisting() {
        expenseService.updateExpense(EXPENSE_ID_UNEXISTING, EXPENSE_DTO, ATTACHMENTS);
    }

    @Test(expected = BadRequestException.class)
    public void testAssociateTagToExpense_unexistingExpense() {
        expenseService.associateTagToExpense(EXPENSE_ID_UNEXISTING, TAG_ID_UNEXISTING);
    }

    @Test(expected = BadRequestException.class)
    public void testAssociateTagToExpense_unexistingTag() {
        expenseService.associateTagToExpense(EXPENSE_ID, TAG_ID_UNEXISTING);
    }

    @Test(expected = ForbiddenActionException.class)
    public void testAssociateTagToExpense_nonCurrentUserExpense() {
        expenseService.associateTagToExpense(EXPENSE_ID_NOT_CURRENT_USER, TAG_ID);
    }

    @Test(expected = ForbiddenActionException.class)
    public void testAssociateTagToExpense_nonCurrentUserTag() {
        expenseService.associateTagToExpense(EXPENSE_ID, TAG_ID_NOT_CURRENT_USER);
    }

    @Test
    public void testAssociateTagToExpense() {
        Expense updatedExpense = expenseService.associateTagToExpense(EXPENSE_ID, TAG_ID);

        assertThat(updatedExpense.getTags()).hasSize(1);
        assertThat(updatedExpense.getTags().iterator().next().getName()).isEqualTo(TAG_NAME);
    }

    @Test(expected = RequestCannotCompleteException.class)
    public void testRemoveTagFromExpense_nonExistingExpense() {
        expenseService.removeTagFromExpense(EXPENSE_ID_UNEXISTING, TAG_NAME);
    }

    @Test(expected = RequestCannotCompleteException.class)
    public void testRemoveTagFromExpense_nonExistingTag() {
        expenseService.removeTagFromExpense(EXPENSE_ID, "a non associated tag name");
    }

    @Test
    public void testRemoveTagFromExpense() {
        Expense updatedExpense = expenseService.removeTagFromExpense(EXPENSE_ID, TAG_NAME);
        assertThat(updatedExpense.getTags()).hasSize(0);
    }

    @Test
    public void testGetExpense() {
        Expense expense = expenseService.getExpense(EXPENSE_ID);
        assertThat(expense).isNotNull();
        assertThat(expense.getId()).isEqualTo(EXPENSE_ID);
    }

    @Test
    public void testUpdateExpenseRemainingExistingAttachments_keepOne() {
        Expense expense = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        expense.getAttachments().add(ATTACHMENT_1);
        expense.getAttachments().add(ATTACHMENT_2);

        AttachmentResponseDTO remainingAttachment = new AttachmentResponseDTOBuilder()
                .setFilename(ATTACHMENT_FILENAME_2)
                .setFileUrl(ATTACHMENT_URL_2)
                .setFileSize(0)
                .build();

        List<AttachmentResponseDTO> remainingAttachments = new ArrayList<>();
        remainingAttachments.add(remainingAttachment);

        expenseService.updateExpenseRemainingExistingAttachments(expense, remainingAttachments);
        assertThat(expense.getAttachments()).hasSize(1);
        assertThat(expense.getAttachments()).containsOnly(ATTACHMENT_2);
    }

    @Test
    public void testUpdateExpenseRemainingExistingAttachments_keepAll() {
        Expense expense = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        expense.getAttachments().add(ATTACHMENT_1);
        expense.getAttachments().add(ATTACHMENT_2);

        AttachmentResponseDTO remainingAttachment1 = new AttachmentResponseDTOBuilder()
                .setFilename(ATTACHMENT_FILENAME_1)
                .setFileUrl(ATTACHMENT_URL_1)
                .setFileSize(0)
                .build();

        AttachmentResponseDTO remainingAttachment2 = new AttachmentResponseDTOBuilder()
                .setFilename(ATTACHMENT_FILENAME_2)
                .setFileUrl(ATTACHMENT_URL_2)
                .setFileSize(0)
                .build();

        List<AttachmentResponseDTO> remainingAttachments = new ArrayList<>();
        remainingAttachments.add(remainingAttachment1);
        remainingAttachments.add(remainingAttachment2);

        expenseService.updateExpenseRemainingExistingAttachments(expense, remainingAttachments);
        assertThat(expense.getAttachments()).hasSize(2);
        assertThat(expense.getAttachments()).contains(ATTACHMENT_1);
        assertThat(expense.getAttachments()).contains(ATTACHMENT_2);
    }

    @Test
    public void testUpdateExpenseRemainingExistingAttachments_keepNoneEmpty() {
        Expense expense = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        expense.getAttachments().add(ATTACHMENT_1);
        expense.getAttachments().add(ATTACHMENT_2);
        expenseService.updateExpenseRemainingExistingAttachments(expense, Collections.<AttachmentResponseDTO>emptyList());
        assertThat(expense.getAttachments()).isEmpty();
    }

    @Test
    public void testUpdateExpenseRemainingExistingAttachments_keepNoneNull() {
        Expense expense = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        expense.getAttachments().add(ATTACHMENT_1);
        expense.getAttachments().add(ATTACHMENT_2);
        expenseService.updateExpenseRemainingExistingAttachments(expense, null);
        assertThat(expense.getAttachments()).isEmpty();
    }
}
