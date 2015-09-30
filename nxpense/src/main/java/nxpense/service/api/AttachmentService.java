package nxpense.service.api;

/**
 * The {@code AttachmentService} interface exposes methods to manage attachment files in the server's file system.
 */
public interface AttachmentService {

    /**
     * Physically creates (if it does not already exist) the file whose binary content is given, in the server's file
     * system and name this file after the provided file name.
     * @param expenseId ID of the expense item with which the attachment is associated
     * @param filename name under which the file is to be created
     */
    public void createAttachment(Integer expenseId, String filename);

    /**
     * Physically removes the file with name {@code filename} from the server's file system.
     * @param expenseId ID of the expense item with which the attachment is associated
     * @param filename name of the file to be removed
     */
    public void deleteAttachment(Integer expenseId, String filename);
}
