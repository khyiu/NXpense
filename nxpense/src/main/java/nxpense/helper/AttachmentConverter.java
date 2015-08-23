package nxpense.helper;

import nxpense.domain.Attachment;
import nxpense.dto.AttachmentResponseDTO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AttachmentConverter {

    private AttachmentConverter() {
        /* Utility class empty constructor */
    }

    public static AttachmentResponseDTO entityToResponseDto(Attachment attachment) {
        AttachmentResponseDTO responseDto = null;

        if (attachment != null) {
            // todo fix URL to file
            responseDto = new AttachmentResponseDTO(attachment.getFilename(), attachment.getFilename(), attachment.getSize());
        }

        return responseDto;
    }

    public static List<AttachmentResponseDTO> attachmentListToAttachmentResponseDtoList(List<Attachment> attachments) {
        List<AttachmentResponseDTO> responseDtos = new ArrayList<AttachmentResponseDTO>();

        if(!CollectionUtils.isEmpty(attachments)) {
            for(Attachment attachment : attachments) {
                responseDtos.add(entityToResponseDto(attachment));
            }
        }

        return responseDtos;
    }
}
