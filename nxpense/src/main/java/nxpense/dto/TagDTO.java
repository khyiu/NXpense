package nxpense.dto;

import java.io.Serializable;
import java.util.List;

public class TagDTO implements Serializable, Comparable<TagDTO>{

    private static final long serialVersionUID = 7213250656506744049L;

    private String name;
    private String backgroundColor;
    private String foregroundColor;

    private List<TagDTO> subTags;

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
}
