package nxpense.builder;


import nxpense.dto.AttachmentResponseDTO;

public class AttachmentResponseDTOBuilder {
    private String filename;
    private String fileUrl;
    private Integer fileSize;

    public AttachmentResponseDTOBuilder setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public AttachmentResponseDTOBuilder setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public AttachmentResponseDTOBuilder setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public AttachmentResponseDTO build() {
        return new AttachmentResponseDTO(filename, fileUrl, fileSize);
    }
}