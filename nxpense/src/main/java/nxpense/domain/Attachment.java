package nxpense.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class Attachment {
    private String filename;

    @Lob
    @Column(name = "CONTENT")
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
