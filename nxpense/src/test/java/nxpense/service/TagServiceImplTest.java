package nxpense.service;

import nxpense.builder.TagDtoBuilder;
import nxpense.domain.Tag;
import nxpense.dto.TagDTO;
import nxpense.exception.BadRequestException;
import nxpense.exception.ForbiddenActionException;
import nxpense.exception.RequestCannotCompleteException;
import nxpense.helper.SecurityPrincipalHelper;
import nxpense.repository.TagRepository;
import nxpense.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.awt.*;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest extends AbstractServiceTest {

    private static final Integer ID_NON_EXISTING_TAG = 11;
    private static final Integer ID_NON_CURRENT_USER_TAG = 22;
    private static final Integer ID_CURRENT_USER_TAG = 33;

    private static final Tag TAG = buildTag();

    private static final String TAG_NAME_ROOT = "Bill";
    private static final String TAG_NAME_ROOT_UPDATED = "Bill (updated)";
    private static final String BACKGROUND_COLOR_ROOT = "#FF0000";
    private static final Color BACKGROUND_COLOR_ROOT_RGB = new Color(255, 0, 0);
    private static final String FOREGROUND_COLOR_ROOT = "#FFFFFF";
    private static final Color FOREGROUND_COLOR_ROOT_RGB = new Color(255, 255, 255);

    private static final TagDTO TAG_DTO = new TagDtoBuilder()
            .setName(TAG_NAME_ROOT)
            .setBackgroundColor(BACKGROUND_COLOR_ROOT)
            .setForegroundColor(FOREGROUND_COLOR_ROOT)
            .build();

    private static final TagDTO TAG_DTO_FOR_UPDATE = new TagDtoBuilder()
            .setName(TAG_NAME_ROOT_UPDATED)
            .setBackgroundColor(BACKGROUND_COLOR_ROOT)
            .setForegroundColor(FOREGROUND_COLOR_ROOT)
            .build();

    private static Tag buildTag() {
        Tag tag = new Tag();

        try {
            Field idField = tag.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(tag, ID_CURRENT_USER_TAG);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        tag.setName(TAG_NAME_ROOT);
        tag.setBackgroundColor(BACKGROUND_COLOR_ROOT_RGB);
        tag.setForegroundColor(FOREGROUND_COLOR_ROOT_RGB);
        return tag;
    }

    @Before
    public void initMocks() {
        given(securityPrincipalHelper.getCurrentUser()).willReturn(mockUser);

        given(tagRepository.save(any(Tag.class))).will(new Answer<Tag>() {
            @Override
            public Tag answer(InvocationOnMock invocation) throws Throwable {
                // Set a dummy ID to simulate the ID generation during save process
                Tag tagToBeSaved = invocation.getArgumentAt(0, Tag.class);
                Field fieldId = tagToBeSaved.getClass().getDeclaredField("id");
                fieldId.setAccessible(true);
                fieldId.set(tagToBeSaved, 88);
                return tagToBeSaved;
            }
        });

        given(tagRepository.findOne(ID_NON_EXISTING_TAG)).willReturn(null);
        given(tagRepository.findOne(ID_CURRENT_USER_TAG)).willReturn(TAG);
        given(tagRepository.findOne(ID_NON_CURRENT_USER_TAG)).willReturn(TAG);
    }

    @Mock
    private SecurityPrincipalHelper securityPrincipalHelper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test(expected = BadRequestException.class)
    public void testCreateNewTag_nullDto() {
        tagService.createNewTag(null);
    }

    @Test
    public void testCreateNewTag() {
        Tag tag = tagService.createNewTag(TAG_DTO);
        assertThat(tag.getId()).isNotNull();
        assertThat(tag.getName()).isEqualTo(TAG_NAME_ROOT);
        assertThat(tag.getBackgroundColor()).isEqualTo(BACKGROUND_COLOR_ROOT_RGB);
        assertThat(tag.getForegroundColor()).isEqualTo(FOREGROUND_COLOR_ROOT_RGB);
    }

    @Test(expected = RequestCannotCompleteException.class)
    public void testCreateNewTag_alreadyExists() {
        Tag tag = new Tag();
        tag.setName(TAG_NAME_ROOT);
        mockUser.addTag(tag);

        tagService.createNewTag(TAG_DTO);
    }

    @Test
    public void testGetCurrentUserTags() {
        tagService.getCurrentUserTags();
        verify(tagRepository).findByUserOrderByName(mockUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTag_nullId() {
        tagService.deleteTag(null);
    }

    @Test(expected = RequestCannotCompleteException.class)
    public void testDeleteTag_noTagWithId() {
        tagService.deleteTag(ID_NON_EXISTING_TAG);
    }

    @Test(expected = ForbiddenActionException.class)
    public void testDeleteTag_currentUserNotOwner() {
        tagService.deleteTag(ID_NON_CURRENT_USER_TAG);
    }

    @Test
    public void testDeleteTag() {
        mockUser.addTag(TAG);
        tagService.deleteTag(ID_CURRENT_USER_TAG);
        verify(tagRepository).delete(TAG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateTag_nullId() {
        tagService.updateTag(null, TAG_DTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateTag_nullBody() {
        tagService.updateTag(ID_CURRENT_USER_TAG, null);
    }

    @Test(expected = RequestCannotCompleteException.class)
    public void testUpdateTag_nonExistingTag() {
        tagService.updateTag(ID_NON_EXISTING_TAG, TAG_DTO);
    }

    @Test(expected = ForbiddenActionException.class)
    public void testUpdateTag_nonCurrentUserTag() {
        tagService.updateTag(ID_NON_CURRENT_USER_TAG, TAG_DTO);
    }

    @Test
    public void testUpdateTag() {
        mockUser.addTag(TAG);
        Tag updatedTag = tagService.updateTag(ID_CURRENT_USER_TAG, TAG_DTO_FOR_UPDATE);
        assertThat(updatedTag.getName()).isEqualTo(TAG_NAME_ROOT_UPDATED);
    }
}
