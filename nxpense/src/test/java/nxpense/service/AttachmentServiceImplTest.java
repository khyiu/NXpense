package nxpense.service;

import nxpense.domain.Attachment;
import nxpense.domain.DebitExpense;
import nxpense.domain.Expense;
import nxpense.dto.ExpenseSource;
import nxpense.service.api.ExpenseService;
import org.assertj.core.util.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentServiceImplTest extends AbstractServiceTest {

    private static final Integer EXPENSE_ID = 1;
    private static final String ATTACHMENT_FILENAME = "file1.txt";
    private static final Attachment ATTACHMENT = new Attachment(ATTACHMENT_FILENAME, "Hello World".getBytes());

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @Before
    public void initMocks() {
        given(expenseService.getExpense(EXPENSE_ID)).willAnswer(new Answer<Expense>() {

            @Override
            public Expense answer(InvocationOnMock invocation) throws Throwable {
                Expense expense = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
                expense.getAttachments().add(ATTACHMENT);
                return expense;
            }
        });
    }

    @Test
    public void testCreateAttachment() throws IOException, IllegalAccessException {
        Field attachmentDirField = ReflectionUtils.findField(AttachmentServiceImpl.class, "attachmentDir");
        attachmentDirField.setAccessible(true);
        attachmentDirField.set(attachmentService, temporaryFolder.getRoot().getAbsolutePath());

        attachmentService.createAttachment(EXPENSE_ID, ATTACHMENT_FILENAME);
        assertThat(temporaryFolder.getRoot().list()).isNotEmpty().hasSize(1);
        assertThat(temporaryFolder.getRoot().listFiles()[0].list()).isNotEmpty().hasSize(1).contains(ATTACHMENT_FILENAME);
    }

    @Test
    public void deleteAttachment() throws IllegalAccessException, IOException {
        Field attachmentDirField = ReflectionUtils.findField(AttachmentServiceImpl.class, "attachmentDir");
        attachmentDirField.setAccessible(true);
        attachmentDirField.set(attachmentService, temporaryFolder.getRoot().getAbsolutePath());

        Path expensePath = Paths.get(temporaryFolder.getRoot().getAbsolutePath(), EXPENSE_ID.toString());
        File expenseFolder = Files.newFolder(expensePath.toString());

        Path attachmentPath = Paths.get(expenseFolder.getAbsolutePath(), ATTACHMENT_FILENAME);
        File attachment = Files.newFile(attachmentPath.toString());

        assertThat(attachment.exists()).as("Dummy attachment file exists").isTrue();
        attachmentService.deleteAttachment(EXPENSE_ID, ATTACHMENT_FILENAME);
        assertThat(attachment.exists()).as("Dummy attachment file exists (after deletion)").isFalse();
    }
}
