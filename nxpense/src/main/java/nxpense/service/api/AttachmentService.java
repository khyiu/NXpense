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
     * @param ownerId ID of the user to which the file belongs. This ID determines the sub-directory in which the file to be
     *                deleted is located.
     * @param expenseId ID of the expense item with which the attachment is associated
     * @param filename name of the file to be removed
     */
    public void deleteAttachment(Integer ownerId, Integer expenseId, String filename);
}
