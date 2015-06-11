package nxpense.dto;

import java.io.Serializable;

public class VersionedSelectionItem implements Serializable {

    private static final long serialVersionUID = 7261662254355709718L;

    private Integer id;
    private Integer version;

    public VersionedSelectionItem() {

    }

    public VersionedSelectionItem(Integer id, Integer version) {
        this.id = id;
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
