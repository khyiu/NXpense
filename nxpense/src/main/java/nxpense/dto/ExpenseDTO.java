package nxpense.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import nxpense.helper.serialization.CustomLocalDateDeserializer;
import nxpense.helper.serialization.CustomLocalDateSerializer;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpenseDTO {

    protected Integer version;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate date;

    private BigDecimal amount;

    private ExpenseSource source;

    private String description;

    private Integer position;

    private List<TagResponseDTO> tags = new ArrayList<TagResponseDTO>();

    private List<AttachmentResponseDTO> attachments = new ArrayList<AttachmentResponseDTO>();

    public LocalDate getDate() {
        return date;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ExpenseSource getSource() {
        return source;
    }

    public void setSource(ExpenseSource source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean addTags(TagResponseDTO tag) {
        return this.tags.add(tag);
    }

    public List<TagResponseDTO> getTags() {
        Collections.sort(this.tags);
        return this.tags;
    }

    public void setAttachments(List<AttachmentResponseDTO> attachments) {
        this.attachments = attachments;
    }

    public List<AttachmentResponseDTO> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }
}
