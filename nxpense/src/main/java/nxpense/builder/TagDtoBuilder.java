package nxpense.builder;

import nxpense.dto.TagDTO;

import java.util.List;

public class TagDtoBuilder {

    private String name;
    private String backgroundColor;
    private String foregroundColor;
    private List<TagDTO> subTags;

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
        tagDto.setName(name);
        tagDto.setBackgroundColor(backgroundColor);
        tagDto.setForegroundColor(foregroundColor);
        tagDto.setSubTags(subTags);
        return tagDto;
    }
}
