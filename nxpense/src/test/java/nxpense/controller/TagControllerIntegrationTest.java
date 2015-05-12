package nxpense.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import nxpense.builder.TagDtoBuilder;
import nxpense.dto.TagDTO;
import nxpense.dto.TagResponseDTO;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/application-test-config.xml")
@WebAppConfiguration
@Transactional
public class TagControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testCreateTag() throws Exception {
        mockAuthenticatedUser(2);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isCreated());
        mockMvc = mockMvcBuilder.build();

        TagDTO tagDto = new TagDtoBuilder()
                .setName("Bill")
                .setBackgroundColor("#00FF00")
                .setForegroundColor("#FFFFFF")
                .build();

        String tagJson = om.writeValueAsString(tagDto);

        RequestBuilder requestBuilder = post("/tag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tagJson);

        MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
        String responseContent = result.getResponse().getContentAsString();

        TagResponseDTO tagResponseDto = om.readValue(responseContent, TagResponseDTO.class);
        assertThat(tagResponseDto.getId()).isNotNull();

        int numberOfTagUser2 = JdbcTestUtils.countRowsInTableWhere(new JdbcTemplate(datasource), "TAG", "user_id = 2");
        assertThat(numberOfTagUser2).isEqualTo(1);
    }

    @Test
    public void testCreateTag_alreadyExists() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getDBConnection(), loadDataSet("dataset/expense-controller-integration-test-dataset.xml"));
        mockAuthenticatedUser(2);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isConflict());
        mockMvc = mockMvcBuilder.build();

        TagDTO tagDto = new TagDtoBuilder()
                .setName("Mobile phone")
                .setBackgroundColor("#00FF00")
                .setForegroundColor("#FFFFFF")
                .build();

        String tagJson = om.writeValueAsString(tagDto);

        RequestBuilder requestBuilder = post("/tag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tagJson);
        mockMvc.perform(requestBuilder).andDo(print()).andReturn();
    }

    @Test
    public void testGetCurrentUserTags() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getDBConnection(), loadDataSet("dataset/expense-controller-integration-test-dataset.xml"));
        mockAuthenticatedUser(3);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = get("/tag/user")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        List<TagResponseDTO> tagResponseDtos = om.readValue(jsonResponse, new TypeReference<List<TagResponseDTO>>(){});

        assertThat(tagResponseDtos).hasSize(4);
        assertThat(tagResponseDtos.get(0).getName()).isEqualTo("Gas");
        assertThat(tagResponseDtos.get(1).getName()).isEqualTo("Groceries");
        assertThat(tagResponseDtos.get(2).getName()).isEqualTo("Internet subscription");
        assertThat(tagResponseDtos.get(3).getName()).isEqualTo("Mobile phone");
    }

    @Test
    public void testDeleteTag_nonExistingTag() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getDBConnection(), loadDataSet("dataset/expense-controller-integration-test-dataset.xml"));
        mockAuthenticatedUser(3);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isConflict());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = delete("/tag/999")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);
    }

    @Test
    public void testDeleteTag_nonCurrentUserTag() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getDBConnection(), loadDataSet("dataset/expense-controller-integration-test-dataset.xml"));
        mockAuthenticatedUser(3);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isForbidden());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = delete("/tag/1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);
    }

    @Test
    public void testDeleteTag() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getDBConnection(), loadDataSet("dataset/expense-controller-integration-test-dataset.xml"));
        mockAuthenticatedUser(3);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isNoContent());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = delete("/tag/5")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);
    }
}
