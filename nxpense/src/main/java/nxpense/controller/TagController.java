package nxpense.controller;

import nxpense.domain.Tag;
import nxpense.dto.TagDTO;
import nxpense.dto.TagResponseDTO;
import nxpense.exception.BadRequestException;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

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
        } catch (BadRequestException e) {
            LOGGER.error("Could not complete new tag creation", e);
            return new ResponseEntity<TagResponseDTO>(HttpStatus.BAD_REQUEST);
        }
    }
}
