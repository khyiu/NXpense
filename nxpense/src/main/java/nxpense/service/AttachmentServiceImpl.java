package nxpense.service;

import nxpense.domain.Attachment;
import nxpense.domain.Expense;
import nxpense.exception.ServerException;
import nxpense.service.api.AttachmentService;
import nxpense.service.api.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    @Value("${attachmentDir}")
    private String attachmentDir;

    @Autowired
    private ExpenseService expenseService;

    @Override
    public void createAttachment(Integer expenseId, String filename) {
        Path attachmentPath = Paths.get(attachmentDir, expenseId.toString(), filename);

        if (Files.notExists(attachmentPath, LinkOption.NOFOLLOW_LINKS)) {
            LOGGER.info("Requested attachment does not exist on disk -> to be created");

            try {
                Files.createDirectories(Paths.get(attachmentDir, expenseId.toString()));
                Files.createFile(attachmentPath);
                Expense expense = expenseService.getExpense(expenseId);
                List<Attachment> attachments = expense.getAttachments().stream().filter(a -> a.getFilename().equals(filename)).collect(Collectors.toList());
                Files.write(attachmentPath, attachments.get(0).getByteContent(), LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
                throw new ServerException("An error occurred making the requested attachment available", e);
            }
        }
    }

    @Override
    public void deleteAttachment(Integer ownerId, Integer expenseId, String filename) {
        // todo implement

    }
}
