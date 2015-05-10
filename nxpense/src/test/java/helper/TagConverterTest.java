package helper;


import nxpense.builder.TagDtoBuilder;
import nxpense.domain.Tag;
import nxpense.dto.TagDTO;
import nxpense.dto.TagResponseDTO;
import nxpense.helper.TagConverter;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TagConverterTest {

    private static final String TAG_NAME_ROOT = "Bill";
    private static final String BACKGROUND_COLOR_ROOT = "#FF0000";
    private static final Color BACKGROUND_COLOR_ROOT_RGB = new Color(255, 0, 0);
    private static final String FOREGROUND_COLOR_ROOT = "#FFFFFF";
    private static final Color FOREGROUND_COLOR_ROOT_RGB = new Color(255, 255, 255);

    private static final String TAG_NAME_CHILD_1 = "Electricity";
    private static final String BACKGROUND_COLOR_CHILD_1 = "#123456";
    private static final Color BACKGROUND_COLOR_CHILD_1_RGB = new Color(18, 52, 86);
    private static final String FOREGROUND_COLOR_CHILD_1 = "#654321";
    private static final Color FOREGROUND_COLOR_CHILD_1_RGB = new Color(101, 67, 33);

    private static final String TAG_NAME_CHILD_2 = "Water";
    private static final String BACKGROUND_COLOR_CHILD_2 = "#112233";
    private static final Color BACKGROUND_COLOR_CHILD_2_RGB = new Color(17, 34, 51);
    private static final String FOREGROUND_COLOR_CHILD_2 = "#332211";
    private static final Color FOREGROUND_COLOR_CHILD_2_RGB = new Color(51, 34, 17);

    private static final TagDTO TAG_DTO = buildTagDto();
    private static final Tag TAG = buildTag();

    private static TagDTO buildTagDto() {
        List<TagDTO> subTags = new ArrayList<TagDTO>();

        TagDTO subTag = new TagDtoBuilder()
                .setName(TAG_NAME_CHILD_1)
                .setBackgroundColor(BACKGROUND_COLOR_CHILD_1)
                .setForegroundColor(FOREGROUND_COLOR_CHILD_1)
                .build();
        subTags.add(subTag);

        subTag = new TagDtoBuilder()
                .setName(TAG_NAME_CHILD_2)
                .setBackgroundColor(BACKGROUND_COLOR_CHILD_2)
                .setForegroundColor(FOREGROUND_COLOR_CHILD_2)
                .build();
        subTags.add(subTag);

        TagDTO rootTag = new TagDtoBuilder()
                .setName(TAG_NAME_ROOT)
                .setBackgroundColor(BACKGROUND_COLOR_ROOT)
                .setForegroundColor(FOREGROUND_COLOR_ROOT)
                .setSubTags(subTags)
                .build();

        return rootTag;
    }

    private static Tag buildTag() {
        Tag tag = new Tag() {
            @Override
            public Integer getId() {
                return 1;
            }
        };

        tag.setName(TAG_NAME_ROOT);
        tag.setBackgroundColor(BACKGROUND_COLOR_ROOT_RGB);
        tag.setForegroundColor(FOREGROUND_COLOR_ROOT_RGB);

        Tag subTag = new Tag() {
            @Override
            public Integer getId() {
                return 2;
            }
        };

        subTag.setName(TAG_NAME_CHILD_1);
        subTag.setBackgroundColor(BACKGROUND_COLOR_CHILD_1_RGB);
        subTag.setForegroundColor(FOREGROUND_COLOR_CHILD_1_RGB);
        tag.addSubTag(subTag);

        return tag;
    }

    @Test
    public void testDtoToEntity() {
        Tag tag = TagConverter.dtoToEntity(TAG_DTO);

        assertThat(tag.getName()).isEqualTo(TAG_NAME_ROOT);
        assertThat(tag.getBackgroundColor()).isEqualTo(BACKGROUND_COLOR_ROOT_RGB);
        assertThat(tag.getForegroundColor()).isEqualTo(FOREGROUND_COLOR_ROOT_RGB);
        assertThat(tag.getSubTags()).hasSize(2);

        assertThat(tag.getSubTags().get(0).getName()).isEqualTo(TAG_NAME_CHILD_1);
        assertThat(tag.getSubTags().get(0).getParentTag()).isEqualTo(tag);
        assertThat(tag.getSubTags().get(0).getBackgroundColor()).isEqualTo(BACKGROUND_COLOR_CHILD_1_RGB);
        assertThat(tag.getSubTags().get(0).getForegroundColor()).isEqualTo(FOREGROUND_COLOR_CHILD_1_RGB);

        assertThat(tag.getSubTags().get(1).getName()).isEqualTo(TAG_NAME_CHILD_2);
        assertThat(tag.getSubTags().get(1).getParentTag()).isEqualTo(tag);
        assertThat(tag.getSubTags().get(1).getBackgroundColor()).isEqualTo(BACKGROUND_COLOR_CHILD_2_RGB);
        assertThat(tag.getSubTags().get(1).getForegroundColor()).isEqualTo(FOREGROUND_COLOR_CHILD_2_RGB);
    }

    @Test
    public void testDtoToEntity_nullInput() {
        Tag tag = TagConverter.dtoToEntity(null);
        assertThat(tag).isNull();
    }

    @Test
    public void testEntityToResponseDto() {
        TagResponseDTO tagResponseDto = TagConverter.entityToResponseDto(TAG);
        assertThat(tagResponseDto.getId()).isEqualTo(1);
        assertThat(tagResponseDto.getBackgroundColor()).isEqualToIgnoringCase(BACKGROUND_COLOR_ROOT);
        assertThat(tagResponseDto.getForegroundColor()).isEqualToIgnoringCase(FOREGROUND_COLOR_ROOT);

        assertThat(tagResponseDto.getSubTags()).hasSize(1);
        assertThat(tagResponseDto.getSubTags().get(0).getName()).isEqualTo(TAG_NAME_CHILD_1);
        assertThat(tagResponseDto.getSubTags().get(0).getBackgroundColor()).isEqualToIgnoringCase(BACKGROUND_COLOR_CHILD_1);
        assertThat(tagResponseDto.getSubTags().get(0).getForegroundColor()).isEqualToIgnoringCase(FOREGROUND_COLOR_CHILD_1);
    }
}
