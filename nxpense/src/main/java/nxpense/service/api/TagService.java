package nxpense.service.api;


import nxpense.domain.Tag;
import nxpense.dto.TagDTO;

import java.util.List;

public interface TagService {

    /**
     * Converts the given tag transfer object representation into an entity and saves it in the DB.
     * @param tagDTO representation of the tag item to be created in database
     * @return {@code Tag} entity that has been saved in DB
     */
    public Tag createNewTag(TagDTO tagDTO);

    /**
     * Retrieves the tags that are associated to the current user
     * @return List of tags that are associated to the current user
     */
    public List<Tag> getCurrentUserTags();

    /**
     * Deletes the {@link nxpense.domain.Tag} item whose ID is equal to the one that is specified.
     * @param tagId ID of the tag to be deleted
     * @throws java.lang.IllegalArgumentException if the specified tag ID is null
     * @throws nxpense.exception.RequestCannotCompleteException if no tag exists with the specified ID
     * @throws nxpense.exception.ForbiddenActionException if tag to delete does not belong to current user
     */
    public void deleteTag(Integer tagId);

    /**
     * Updates the tag {@link nxpense.domain.Tag} item whose ID is equal to the one that is specified, with the provided content
     * @param tagId ID of the tag to be updated
     * @param tagBody content to update the tag with
     * @return state of the updated tag
     * @throws java.lang.IllegalArgumentException if the specified tag ID is null or if no tag body is provided
     * @throws nxpense.exception.RequestCannotCompleteException if no tag exists with the specified ID
     * @throws nxpense.exception.ForbiddenActionException if tag to update does not belong to current user
     */
    public Tag updateTag(Integer tagId, TagDTO tagBody);
}
