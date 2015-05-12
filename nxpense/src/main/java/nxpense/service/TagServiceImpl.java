package nxpense.service;


import nxpense.domain.Tag;
import nxpense.domain.User;
import nxpense.dto.TagDTO;
import nxpense.exception.BadRequestException;
import nxpense.exception.ForbiddenActionException;
import nxpense.exception.RequestCannotCompleteException;
import nxpense.helper.SecurityPrincipalHelper;
import nxpense.helper.TagConverter;
import nxpense.repository.TagRepository;
import nxpense.service.api.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    private SecurityPrincipalHelper securityPrincipalHelper;

    @Autowired
    private TagRepository tagRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tag createNewTag(TagDTO tagDTO) {
        Tag tag = TagConverter.dtoToEntity(tagDTO);

        if (tag == null) {
            LOGGER.warn("Attempt to create a NULL tag!");
            throw new BadRequestException("Cannot persist a NULL tag entity");
        }

        User currentUser = securityPrincipalHelper.getCurrentUser();

        if (currentUser.ownTag(tag)) {
            LOGGER.warn("Attempt to create a tag with name [{}] that already exists for user [{}]", tag.getName(), currentUser);
            throw new RequestCannotCompleteException("The provided tag name is already associated with an existing tag");
        }

        currentUser.addTag(tag);
        return tagRepository.save(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getCurrentUserTags() {
        User currentUser = securityPrincipalHelper.getCurrentUser();
        List<Tag> currentUserTags = tagRepository.findByUserOrderByName(currentUser);

        LOGGER.debug("Number of tags retrieved for user [{}]: {}", currentUser, currentUserTags.size());
        return currentUserTags;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Integer tagId) {
        if(tagId == null) {
            throw new IllegalArgumentException("Cannot delete tag with NULL ID!");
        }

        User currentUser = securityPrincipalHelper.getCurrentUser();
        Tag tagToDelete = tagRepository.findOne(tagId);

        if(tagToDelete == null) {
            LOGGER.warn("Tag deletion: no tag found with ID [{}]", tagId);
            throw new RequestCannotCompleteException("No tag found for specified ID");
        }

        // NOTE: several users may have tags with a same name. Given that Tag entity's identity is based on sole name, must perform an ID comparison too.
        if(!currentUser.ownTag(tagToDelete) || !currentUser.getTag(tagToDelete.getName()).getId().equals(tagId)) {
            LOGGER.warn("Trying to delete a tag [{}] that does not belong to current user [{}]!", tagId, currentUser);
            throw new ForbiddenActionException("Cannot delete tag that does not belong to current user");
        }

        currentUser.removeTag(tagToDelete);
        tagRepository.delete(tagToDelete);
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public Tag updateTag(Integer tagId, TagDTO tagBody) {
        if(tagId == null || tagBody == null) {
            throw new IllegalArgumentException("Both tag ID and tag content must be provided");
        }

        User currentUser = securityPrincipalHelper.getCurrentUser();
        Tag tagToUpdate = tagRepository.findOne(tagId);

        if(tagToUpdate == null) {
            LOGGER.warn("Tag update: no tag found with ID [{}]", tagId);
            throw new RequestCannotCompleteException("No tag found with specified ID");
        }

        if(!currentUser.ownTag(tagToUpdate) || !currentUser.getTag(tagToUpdate.getName()).getId().equals(tagId)) {
            LOGGER.warn("Trying to update a tag [{}] that does not belong to current user [{}]!", tagId, currentUser);
            throw new ForbiddenActionException("Cannot update tag that does not belong to current user");
        }

        Tag newTagContent = TagConverter.dtoToEntity(tagBody);
        tagToUpdate.setName(newTagContent.getName());
        tagToUpdate.setBackgroundColor(newTagContent.getBackgroundColor());
        tagToUpdate.setForegroundColor(newTagContent.getForegroundColor());

        return tagToUpdate;
    }
}
