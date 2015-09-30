package nxpense.security;

import nxpense.domain.Expense;
import nxpense.domain.User;
import nxpense.exception.BadRequestException;
import nxpense.exception.ForbiddenActionException;
import nxpense.exception.ResourceNotFoundException;
import nxpense.helper.SecurityPrincipalHelper;
import nxpense.service.api.AttachmentService;
import nxpense.service.api.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

public class AttachmentAccessInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentAccessInterceptor.class);
    private static final Pattern ATTACHMENT_ACCESS_URL_PATTERN = Pattern.compile("(http|https)(://)(\\S+)(/attach/)(\\d+)(/)(\\S+)");

    @Autowired
    private SecurityPrincipalHelper securityPrincipalHelper;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private ExpenseService expenseService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURL().toString();

        if (ATTACHMENT_ACCESS_URL_PATTERN.matcher(requestUrl).matches()) {
            LOGGER.debug("Applying security check on attachment access through URL: {}", requestUrl);

            String[] tokens = request.getRequestURL().toString().split("/");
            String filename = tokens[tokens.length - 1];
            Integer expenseId = Integer.valueOf(tokens[tokens.length - 2]);

            User currentUser = securityPrincipalHelper.getCurrentUser();
            String targetAttachmentFilename = convertRequestUrlFilenameToString(filename);
            checkAttachmentAccessibility(expenseId, targetAttachmentFilename, currentUser);

            attachmentService.createAttachment(expenseId, targetAttachmentFilename);
        }


        return super.preHandle(request, response, handler);
    }

    private String convertRequestUrlFilenameToString(String attachmentFilename) {
        return attachmentFilename.replaceAll("%20", " ");
    }

    private void checkAttachmentAccessibility(int expenseId, String attachmentFilename, User currentUser) {
        Expense expense = expenseService.getExpense(expenseId);

        if (expense == null) {
            throw new BadRequestException("Expense with ID = " + expenseId + " does not exist!");
        }

        if (!expense.getUser().equals(currentUser)) {
            throw new ForbiddenActionException("You're not authorized to access the requested attachment");
        }

        if (!expense.getAttachments().stream().anyMatch(attachment -> attachment.getFilename().equals(attachmentFilename))) {
            throw new ResourceNotFoundException("Attachment with filename = " + attachmentFilename + " does not exist");
        }
    }
}
