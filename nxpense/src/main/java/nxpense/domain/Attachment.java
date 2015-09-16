package nxpense.domain;

import javax.persistence.*;

@Embeddable
public class Attachment {
    private String filename;

    @Lob
    @Column(name = "CONTENT")
    @Basic(fetch = FetchType.LAZY)
    private byte[] byteContent;

    private Integer size;

    public Attachment() {

    }

    public Attachment(String filename, byte[] byteContent) {
        this.filename = filename;
        this.byteContent = byteContent;
        this.size = byteContent.length;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getByteContent() {
        return byteContent;
    }

    public Integer getSize() {
        return size;
    }
}
