package nxpense.dto;

import java.util.List;

public class TagDTO implements Comparable<TagDTO>{
    private Integer version;
    private String name;
    private String backgroundColor;
    private String foregroundColor;

    private List<TagDTO> subTags;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public List<TagDTO> getSubTags() {
        return subTags;
    }

    public void setSubTags(List<TagDTO> subTags) {
        this.subTags = subTags;
    }

    @Override
    public int compareTo(TagDTO otherTag) {
        if(otherTag == null) {
            return 1;
        }

        return this.name.compareTo(otherTag.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagDTO tagDTO = (TagDTO) o;

        return name.equals(tagDTO.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
