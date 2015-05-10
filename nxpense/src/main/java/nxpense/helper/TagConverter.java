package nxpense.helper;

import nxpense.domain.Tag;
import nxpense.dto.TagDTO;
import nxpense.dto.TagResponseDTO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class TagConverter {

    private static final ColorConverter COLOR_CONVERTER = new ColorConverter();

    private TagConverter() {
        /* Utility class private constructor */
    }

    public static Tag dtoToEntity(TagDTO tagDto) {
        Tag tag = null;

        if (tagDto != null) {
            tag = new Tag();
            tag.setName(tagDto.getName());
            tag.setBackgroundColor(COLOR_CONVERTER.convertToEntityAttribute(tagDto.getBackgroundColor()));
            tag.setForegroundColor(COLOR_CONVERTER.convertToEntityAttribute(tagDto.getForegroundColor()));

            if (!CollectionUtils.isEmpty(tagDto.getSubTags())) {

                for (TagDTO subTagDto : tagDto.getSubTags()) {
                    Tag subTag = dtoToEntity(subTagDto);
                    tag.addSubTag(subTag);
                }
            }
        }

        return tag;
    }

    public static TagResponseDTO entityToResponseDto(Tag tag) {
        TagResponseDTO tagResponseDTO = null;

        if(tag != null) {
            tagResponseDTO = new TagResponseDTO();
            copyValues(tagResponseDTO, tag);
        }

        return tagResponseDTO;
    }

    private static void copyValues(TagResponseDTO dst, Tag src) {
        dst.setId(src.getId());
        dst.setName(src.getName());
        dst.setBackgroundColor(COLOR_CONVERTER.convertToDatabaseColumn(src.getBackgroundColor()));
        dst.setForegroundColor(COLOR_CONVERTER.convertToDatabaseColumn(src.getForegroundColor()));

        if(!CollectionUtils.isEmpty(src.getSubTags())) {
            List<TagDTO> subTagsDto = new ArrayList<TagDTO>();

            for(Tag subTag : src.getSubTags()) {
                TagResponseDTO subTagDto = new TagResponseDTO();
                copyValues(subTagDto, subTag);
                subTagsDto.add(subTagDto);
            }

            dst.setSubTags(subTagsDto);
        }
    }
}
