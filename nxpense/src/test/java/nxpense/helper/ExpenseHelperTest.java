package nxpense.helper;

import nxpense.builder.AttachmentResponseDTOBuilder;
import nxpense.domain.Attachment;
import nxpense.domain.Expense;
import nxpense.dto.AttachmentResponseDTO;
import nxpense.dto.ExpenseSource;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseHelperTest {

    private static final Date DATE = new LocalDate(2015, 1, 1).toDate();
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(123.45);
    private static final String DESCRIPTION = "An expense description";

    private static final String ATTACHMENT_NAME_1 = "file1.txt";
    private static final String ATTACHMENT_NAME_1_BIS = "file1(1).txt";
    private static final String ATTACHMENT_NAME_2 = "file2";

    private static final List<MultipartFile> MULTIPART_FILES = new ArrayList(){{
        add(new MockMultipartFile(ATTACHMENT_NAME_1, ATTACHMENT_NAME_1, "text/plain", new byte[]{}));
        add(new MockMultipartFile(ATTACHMENT_NAME_2, ATTACHMENT_NAME_2, "text/plain", new byte[]{}));
    }};

    @Test
    public void testOverwriteFields_noSource() {
        Expense destination = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();

        ExpenseHelper.overwriteFields(null, destination);
        assertThat(destination.getDate()).isNull();
        assertThat(destination.getAmount()).isNull();
        assertThat(destination.getDescription()).isNull();
    }

    @Test
    public void testOverwriteFields() {
        Expense source = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        source.setDate(DATE);
        source.setAmount(AMOUNT);
        source.setDescription(DESCRIPTION);
        Expense destination = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();

        ExpenseHelper.overwriteFields(source, destination);
        assertThat(destination.getDate()).isEqualTo(DATE);
        assertThat(destination.getAmount()).isEqualTo(AMOUNT);
        assertThat(destination.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    public void testAssociateFilesToExpense_nullMultiparts() {
        Expense expense = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        ExpenseHelper.associateFilesToExpense(expense, null);
        assertThat(expense.getAttachments()).isEmpty();
    }

    @Test
    public void testAssociateFilesToExpense_emptyMultiparts() {
        Expense expense = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        ExpenseHelper.associateFilesToExpense(expense, Collections.<MultipartFile>emptyList());
        assertThat(expense.getAttachments()).isEmpty();
    }

    @Test
    public void testAssociateFilesToExpense() {
        Expense expense = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        ExpenseHelper.associateFilesToExpense(expense, MULTIPART_FILES);
        assertThat(expense.getAttachments()).hasSize(2);
        assertThat(expense.getAttachments()).contains(new Attachment(ATTACHMENT_NAME_1, new byte[]{}));
        assertThat(expense.getAttachments()).contains(new Attachment(ATTACHMENT_NAME_2, new byte[]{}));
    }

    @Test
    public void testAssociateFilesToExpense_existFileWithSameName() {
        Expense expense = ExpenseSource.DEBIT_CARD.getEmptyExpenseInstance();
        expense.getAttachments().add(new Attachment(ATTACHMENT_NAME_1, new byte[]{}));

        ExpenseHelper.associateFilesToExpense(expense, MULTIPART_FILES);
        assertThat(expense.getAttachments()).hasSize(3);
        assertThat(expense.getAttachments()).contains(new Attachment(ATTACHMENT_NAME_1, new byte[]{}));
        assertThat(expense.getAttachments()).contains(new Attachment(ATTACHMENT_NAME_2, new byte[]{}));
        assertThat(expense.getAttachments()).contains(new Attachment(ATTACHMENT_NAME_1_BIS, new byte[]{}));
    }
}
