package controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/application-test-config.xml")
@WebAppConfiguration
public class AccountControllerTest {

    @Autowired
    protected WebApplicationContext wac;

    protected DefaultMockMvcBuilder mockMvcBuilder;

    protected MockMvc mockMvc;

    @Before
    public void setup() throws ParserConfigurationException, SAXException {
	mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
	mockMvcBuilder.alwaysExpect(status().isOk());
	mockMvc = mockMvcBuilder.build();
    }

    @Test
    public void testCreateNewAccount() throws Exception {
	RequestBuilder requestBuilder = post("/account/new")
						.param("email", "new@test.com")
						.param("password", "pwd1234")
						.param("passwordRepeat", "pwd1234");
	MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
	String responseContent = result.getResponse().getContentAsString();
	assertThat(responseContent).isEqualTo("/view/home.html");
    }
}
