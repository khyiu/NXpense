package nxpense.builder;

import nxpense.dto.TagDTO;

import java.util.List;

public class TagDtoBuilder {

    private Integer version;
    private String name;
    private String backgroundColor;
    private String foregroundColor;
    private List<TagDTO> subTags;

    public TagDtoBuilder setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public TagDtoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TagDtoBuilder setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TagDtoBuilder setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public TagDtoBuilder setSubTags(List<TagDTO> subTags) {
        this.subTags = subTags;
        return this;
    }

    public TagDTO build() {
        TagDTO tagDto = new TagDTO();
        tagDto.setVersion(version);
        tagDto.setName(name);
        tagDto.setBackgroundColor(backgroundColor);
        tagDto.setForegroundColor(foregroundColor);
        tagDto.setSubTags(subTags);
        return tagDto;
    }
}
