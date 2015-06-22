package nxpense.helper;


import nxpense.domain.Attachment;
import nxpense.domain.Expense;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class ExpenseHelper {
    private ExpenseHelper() {

    }

    /**
     * Overwrites the {@code destination} expense item's fields values with values coming from the {@code source} expense item
     * without regard to their respective source (see {@link nxpense.dto.ExpenseSource}) and {@link nxpense.domain.Expense#position}.
     *
     * @param source      Expense item whose value overwrites the destination item's values
     * @param destination Expense item whose values are to be overwritten
     */
    public static void overwriteFields(Expense source, Expense destination) {
        if (source != null && destination != null) {
            destination.setDate(source.getDate());
            destination.setAmount(source.getAmount());
            destination.setDescription(source.getDescription());
        }
    }

    /**
     * Converts the given {@code MultipartFile} objects into instances of {@link nxpense.domain.Attachment} and associate
     * them to the specified {@code expense}.
     * @param expense Expense to which the Multipart files are to be associated
     * @param multipartFiles Files to be associated as attachments to the {@code expense}
     * @throws java.lang.IllegalArgumentException if any of the given MultipartFile objects cannot be processed.
     * In such case, the whole operation is discarded.
     */
    public static void associateFilesToExpense(Expense expense, List<MultipartFile> multipartFiles) {
        if (expense != null && multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                try {
                    Attachment expenseAttachment = MultipartFileConverter.toAttachment(multipartFile);
                    expense.getAttachments().add(expenseAttachment);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Failed processing attachment with filename = " + multipartFile.getOriginalFilename());
                }
            }
        }
    }
}
