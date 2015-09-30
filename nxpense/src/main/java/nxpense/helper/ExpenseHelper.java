package nxpense.helper;


import nxpense.domain.Attachment;
import nxpense.domain.Expense;
import nxpense.dto.AttachmentResponseDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

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
                    final Attachment expenseAttachment = MultipartFileConverter.toAttachment(multipartFile);
                    expenseAttachment.setFilename(computeNewAttachmentFilename(expenseAttachment.getFilename(), expense.getAttachments()));
                    expense.getAttachments().add(expenseAttachment);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Failed processing attachment with filename = " + multipartFile.getOriginalFilename(), e);
                }
            }
        }
    }

    private static String computeNewAttachmentFilename(String newAttachmentFilename, List<Attachment> existingAttachments) {
        Integer suffixIndex;
        if(CollectionUtils.isEmpty(existingAttachments) || (suffixIndex = computeSuffixIndex(newAttachmentFilename, existingAttachments)) == 0) {
            return newAttachmentFilename;
        } else {
            if(newAttachmentFilename.contains(".")) {
                int extensionSeparatorIdx = newAttachmentFilename.lastIndexOf(".");
                return newAttachmentFilename.substring(0, extensionSeparatorIdx) + "(" + suffixIndex + ")" + newAttachmentFilename.substring(extensionSeparatorIdx);
            } else {
                return newAttachmentFilename + "(" + suffixIndex + ")";
            }
        }
    }

    private static int computeSuffixIndex(String newAttachmentFilename, List<Attachment> existingAttachments) {
        if (!StringUtils.isEmpty(existingAttachments)) {
            List<Attachment> sameBasenameAttachments = existingAttachments.stream().filter(
                    a -> haveSameFilenameBase(newAttachmentFilename, a.getFilename())
            ).collect(Collectors.<Attachment>toList());

            return CollectionUtils.isEmpty(sameBasenameAttachments) ? 0 : sameBasenameAttachments.size();
        }

        return 0;
    }

    private static boolean haveSameFilenameBase(String newAttachmentFilename, String existingAttachmentFilename) {
        if(newAttachmentFilename != null && existingAttachmentFilename != null) {
            if(newAttachmentFilename.equals(existingAttachmentFilename)) {
                return true;
            }

            String newBaseName = newAttachmentFilename;
            String existingBaseName = existingAttachmentFilename;

            if(newBaseName.contains(".") && existingBaseName.contains(".")) {
                newBaseName = newAttachmentFilename.substring(0, newAttachmentFilename.lastIndexOf("."));
                existingBaseName = existingAttachmentFilename.substring(0, existingAttachmentFilename.lastIndexOf("."));
            }

            String temp = existingBaseName.replace(newBaseName, "");
            return temp.matches("\\(\\d+\\)");
        }

        return false;
    }

    /**
     * Updates the current state of an existing expense item regarding the attachments that are associated to it, based
     * on the list of attachments that have been identified as remaining.
     * @param expense Expense item to be updated
     * @param remainingAttachments List of attachments that are still associated to {@code expense}
     */
    public static void updateExpenseRemainingExistingAttachments(Expense expense, List<AttachmentResponseDTO> remainingAttachments) {
        if(CollectionUtils.isEmpty(remainingAttachments)) {
            expense.getAttachments().clear();
        } else {
            ListIterator<Attachment> listIt = expense.getAttachments().listIterator();

            while(listIt.hasNext()) {
                Attachment existingAttachment = listIt.next();
                boolean keepAttachment = remainingAttachments.stream().anyMatch(ra -> ra.getFilename().equals(existingAttachment.getFilename()));

                if(!keepAttachment) {
                    listIt.remove();
                }
            }
        }
    }
}
