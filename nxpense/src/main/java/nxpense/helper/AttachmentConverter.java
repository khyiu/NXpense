package nxpense.helper;

import nxpense.domain.Attachment;
import nxpense.dto.AttachmentResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AttachmentConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentConverter.class);
    private static final Properties APP_CONFIG = new Properties();

    static {
        try {
            APP_CONFIG.load(AttachmentConverter.class.getResourceAsStream("/app-properties/nxpense-config.properties"));
        } catch (IOException e) {
            LOGGER.error("Failed loading application config properties file", e);
        }
    }

    private AttachmentConverter() {
        /* Utility class empty constructor */
    }

    public static AttachmentResponseDTO entityToResponseDto(int expenseId, Attachment attachment) {
        AttachmentResponseDTO responseDto = null;

        if (attachment != null) {
            responseDto = new AttachmentResponseDTO(attachment.getFilename(), getAttachmentFileUrl(expenseId, attachment.getFilename()), attachment.getSize());
        }

        return responseDto;
    }

    private static String getAttachmentFileUrl(int expenseId, String filename) {
        StringBuilder sb = new StringBuilder();
        sb.append(APP_CONFIG.get("attachmentCtx"));
        sb.append(expenseId);
        sb.append("/");
        sb.append(filename);
        return sb.toString();
    }

    public static List<AttachmentResponseDTO> attachmentListToAttachmentResponseDtoList(int expenseId, List<Attachment> attachments) {
        List<AttachmentResponseDTO> responseDtos = new ArrayList<AttachmentResponseDTO>();

        if(!CollectionUtils.isEmpty(attachments)) {
            for(Attachment attachment : attachments) {
                responseDtos.add(entityToResponseDto(expenseId, attachment));
            }
        }

        return responseDtos;
    }
}
