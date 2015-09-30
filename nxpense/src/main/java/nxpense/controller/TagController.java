package nxpense.controller;

import nxpense.domain.Tag;
import nxpense.dto.TagDTO;
import nxpense.dto.TagResponseDTO;
import nxpense.exception.CustomErrorCode;
import nxpense.exception.RequestCannotCompleteException;
import nxpense.helper.TagConverter;
import nxpense.message.CustomResponseHeader;
import nxpense.service.api.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/tag")
public class TagController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagService tagService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TagResponseDTO> createTag(@RequestBody TagDTO tagDto) {
        try {
            Tag persistedTag = tagService.createNewTag(tagDto);
            TagResponseDTO tagResponseDto = TagConverter.entityToResponseDto(persistedTag);
            return new ResponseEntity<TagResponseDTO>(tagResponseDto, HttpStatus.CREATED);
        } catch (RequestCannotCompleteException taee) {
            LOGGER.error("Tag already exists for current user", taee);
            HttpHeaders customHeaders = new HttpHeaders();
            customHeaders.put(CustomResponseHeader.SERVERSIDE_VALIDATION_ERROR_MSG.name(), Arrays.asList(taee.getMessage()));
            return new ResponseEntity<TagResponseDTO>(customHeaders, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<TagResponseDTO>> getCurrentUserTags() {
        List<Tag> currentUserTags = tagService.getCurrentUserTags();
        List<TagResponseDTO> responseTags = new ArrayList<TagResponseDTO>();

        for(Tag tag : currentUserTags) {
            responseTags.add(TagConverter.entityToResponseDto(tag));
        }

        return new ResponseEntity(responseTags, HttpStatus.OK);
    }

    @RequestMapping(value = "/{tagId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteTag(@PathVariable Integer tagId) {
        tagService.deleteTag(tagId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{tagId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<TagResponseDTO> updateTag(@PathVariable Integer tagId, @RequestBody TagDTO tagBody) {
        Tag updatedTag = tagService.updateTag(tagId, tagBody);
        TagResponseDTO tagResponseDTO = TagConverter.entityToResponseDto(updatedTag);
        return new ResponseEntity(tagResponseDTO, HttpStatus.OK);
    }

    @ExceptionHandler(value = {JpaOptimisticLockingFailureException.class})
    public void translateExceptionFromEclipseLink(HttpServletResponse response) {
        response.setStatus(CustomErrorCode.ENTITY_OUT_OF_SYNC.getHttpStatus());

        try {
            PrintWriter writer = response.getWriter()
                    .append("The tag item you working on has been updated/deleted by another session. Please reload tags and retry...");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOGGER.error("An error occurred when writing custom HTTP error code to HTTP response", e);
        }
    }
}
