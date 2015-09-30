package nxpense.domain;

import javax.persistence.*;
import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Attachment that = (Attachment) o;

        if (!filename.equals(that.filename)) {
            return false;
        }

        if (!Arrays.equals(byteContent, that.byteContent)) {
            return false;
        }

        return !(size != null ? !size.equals(that.size) : that.size != null);

    }

    @Override
    public int hashCode() {
        int result = filename.hashCode();
        result = 31 * result + (byteContent != null ? Arrays.hashCode(byteContent) : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }
}
