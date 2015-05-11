package nxpense.service;


import nxpense.domain.Tag;
import nxpense.domain.User;
import nxpense.dto.TagDTO;
import nxpense.exception.BadRequestException;
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
}
