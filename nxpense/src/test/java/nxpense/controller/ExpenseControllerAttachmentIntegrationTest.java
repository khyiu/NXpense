package nxpense.controller;

import nxpense.service.AttachmentServiceImpl;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/application-test-config.xml")
@WebAppConfiguration
@Transactional(readOnly = true)
public class ExpenseControllerAttachmentIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AttachmentServiceImpl attachmentService;

    @Before
    public void setUp() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getDBConnection(), loadDataSet("dataset/expense-controller-integration-test-dataset.xml"));

        Properties prop = new Properties();
        prop.load(ExpenseControllerIntegrationTest.class.getResourceAsStream("/app-properties/nxpense-test-config.properties"));
        Field attachmentDirField = ReflectionUtils.findField(AttachmentServiceImpl.class, "attachmentDir");
        attachmentDirField.setAccessible(true);
        attachmentDirField.set(attachmentService, prop.getProperty("attachmentDir"));
    }

    @After
    public void cleanup() throws IllegalAccessException {
        Field attachmentDirField = ReflectionUtils.findField(AttachmentServiceImpl.class, "attachmentDir");
        attachmentDirField.setAccessible(true);
        String attachmentTestDir = attachmentDirField.get(attachmentService).toString();

        deleteFilesAndDirectories(new File(attachmentTestDir));
    }

    private void deleteFilesAndDirectories(File file) {
        if(file.isFile()) {
            file.delete();
        } else {
            for(File f : file.listFiles()) {
                deleteFilesAndDirectories(f);
                f.delete();
            }
        }
    }

    @Test
    public void testAccessAttachment() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = get("/attach/1/test.txt");

        mockMvc.perform(requestBuilder).andDo(print());
    }

    @Test
    public void testAccessAttachment_unexistingExpense() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isBadRequest());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = get("/attach/100/test.txt");

        mockMvc.perform(requestBuilder).andDo(print());
    }

    @Test
    public void testAccessAttachment_unauthorized() throws Exception {
        mockAuthenticatedUser(2);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isForbidden());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = get("/attach/1/test.txt");

        mockMvc.perform(requestBuilder).andDo(print());
    }

    @Test
    public void testAccessAttachment_unexistingAttachment() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isNotFound());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = get("/attach/1/unexisting.txt");

        mockMvc.perform(requestBuilder).andDo(print());
    }
}
