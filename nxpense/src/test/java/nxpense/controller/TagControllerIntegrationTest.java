package nxpense.controller;

import nxpense.builder.TagDtoBuilder;
import nxpense.dto.TagDTO;
import nxpense.dto.TagResponseDTO;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/application-test-config.xml")
@WebAppConfiguration
@Transactional(readOnly = true)
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

        int numberOfExpenseUser2 = JdbcTestUtils.countRowsInTableWhere(new JdbcTemplate(datasource), "TAG", "user_id = 2");
        assertThat(numberOfExpenseUser2).isEqualTo(1);
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
}
