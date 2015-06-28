package nxpense.domain;

import javax.persistence.*;

@Embeddable
public class Attachment {
    private String filename;

    @Lob
    @Column(name = "CONTENT")
    @Basic(fetch = FetchType.LAZY)
    private byte[] byteContent;

    public Attachment() {

    }

    public Attachment(String filename, byte[] byteContent) {
        this.filename = filename;
        this.byteContent = byteContent;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getByteContent() {
        return byteContent;
    }
}
