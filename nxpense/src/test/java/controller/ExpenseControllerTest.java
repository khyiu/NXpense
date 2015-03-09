package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nxpense.builder.ExpenseDtoBuilder;
import nxpense.domain.User;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseResponseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.security.CustomUserDetails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/application-test-config.xml")
@WebAppConfiguration
public class ExpenseControllerTest {

    private static final String USER_EMAIL = "test@test.com";
    private static final String USER_PASSWORD = "secret";

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private WebApplicationContext wac;

    private DefaultMockMvcBuilder mockMvcBuilder;

    private MockMvc mockMvc;

    @Test
    public void testCreateExpense() throws Exception {
        User mockUser = new User();
        mockUser.setEmail(USER_EMAIL);
        mockUser.setPassword(USER_PASSWORD.toCharArray());

        UserDetails userDetails = new CustomUserDetails(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isCreated());
        mockMvc = mockMvcBuilder.build();

        ExpenseDTO expense = new ExpenseDtoBuilder()
                .setDate(new Date())
                .setAmount(BigDecimal.TEN)
                .setDescription("Description of a new expense")
                .setSource(ExpenseSource.DEBIT_CARD)
                .build();
        String expenseJson = om.writeValueAsString(expense);

        RequestBuilder requestBuilder = post("/expense/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(expenseJson);
        MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
        String responseContent = result.getResponse().getContentAsString();

        ExpenseResponseDTO expenseDto = om.readValue(responseContent, ExpenseResponseDTO.class);
        assertThat(expenseDto.getId()).isNotNull();
    }
}
