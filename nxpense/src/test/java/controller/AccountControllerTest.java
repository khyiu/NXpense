package controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/application-test-config.xml")
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountControllerTest {

    @Resource
    private FilterChainProxy springSecurityFilterChain;
    
    @Autowired
    private WebApplicationContext wac;

    private DefaultMockMvcBuilder mockMvcBuilder;

    private MockMvc mockMvc;
    
    @Test
    public void testCreateNewAccount() throws Exception {
	mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
	mockMvcBuilder.alwaysExpect(status().isOk());
	mockMvc = mockMvcBuilder.build();
	
	RequestBuilder requestBuilder = post("/account/new")
						.param("email", "new@test.com")
						.param("password", "pwd1234")
						.param("passwordRepeat", "pwd1234");
	MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
	String responseContent = result.getResponse().getContentAsString();
	assertThat(responseContent).contains("/view/home.html");
    }
    
    @Test
    public void testCreateNewAccount_ExistingUser() throws Exception {
	mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
	mockMvcBuilder.alwaysExpect(status().isConflict());
	mockMvc = mockMvcBuilder.build();
	
	RequestBuilder requestBuilder = post("/account/new")
						.param("email", "new@test.com")
						.param("password", "pwd1234")
						.param("passwordRepeat", "pwd1234");
	MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
	result.getResponse().getContentAsString();
    }
    
    @Test
    public void testCreateNewAccount_PasswordMismatch() throws Exception {
	mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
	mockMvcBuilder.alwaysExpect(status().isConflict());
	mockMvc = mockMvcBuilder.build();
	
	RequestBuilder requestBuilder = post("/account/new")
						.param("email", "new@test.com")
						.param("password", "pwd1234")
						.param("passwordRepeat", "pwd4321");
	mockMvc.perform(requestBuilder).andDo(print()).andReturn();
    }
    
    @Test
    public void testLogin() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .alwaysExpect(status().isOk())
                .addFilters(this.springSecurityFilterChain)
                .build();
        
	RequestBuilder requestBuilder = post("/login")
			.param("email", "new@test.com")
			.param("password", "pwd1234")
			.param("remember_me", "true");
	MockHttpServletResponse response = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();
	String successRedirection = response.getContentAsString();
	
	assertThat(successRedirection).isEqualTo("/view/home.html");
	assertThat(response.getHeaderNames()).isEmpty();
    }
    
    @Test
    public void testLogin_WrongPassword() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .alwaysExpect(status().isOk())
                .addFilters(this.springSecurityFilterChain)
                .build();
        
	RequestBuilder requestBuilder = post("/login")
			.param("email", "new@test.com")
			.param("password", "wrongPassword")
			.param("remember_me", "true");
	MockHttpServletResponse response = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();
	String headerValue = response.getHeader("nxpense-login-error");
	
	assertThat(headerValue).isNotNull().isEqualTo("Bad credentials");
    }
    
    @Test
    public void testLogin_UnexistingEmail() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .alwaysExpect(status().isOk())
                .addFilters(this.springSecurityFilterChain)
                .build();
        
	RequestBuilder requestBuilder = post("/login")
			.param("email", "unexisting@test.com")
			.param("password", "pwd1234")
			.param("remember_me", "true");
	MockHttpServletResponse response = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();
	String headerValue = response.getHeader("nxpense-login-error");
	
	assertThat(headerValue).isNotNull().isEqualTo("Bad credentials");
    }
}
