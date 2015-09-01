package nxpense.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpenseResponseDTO extends ExpenseDTO {

    private Integer id;
    private List<AttachmentResponseDTO> attachments = new ArrayList<AttachmentResponseDTO>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAttachments(List<AttachmentResponseDTO> attachments) {
        this.attachments = attachments;
    }

    public List<AttachmentResponseDTO> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public Integer getNbAttachment() {
        return attachments.size();
    }
}
