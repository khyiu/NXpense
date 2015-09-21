package nxpense.dto;


public class AttachmentResponseDTO {

    private String filename;
    private String fileUrl;
    private Integer fileSize;

    public AttachmentResponseDTO() {

    }

    public AttachmentResponseDTO(String filename, String fileUrl, Integer fileSize) {
        this.filename = filename;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Integer getFileSize() {
        return fileSize;
    }
}
