package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nxpense.builder.ExpenseDtoBuilder;
import nxpense.domain.User;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseResponseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.dto.PageDTO;
import nxpense.security.CustomUserDetails;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/application-test-config.xml")
@WebAppConfiguration
@Transactional(readOnly = true)
public class ExpenseControllerTest {

    private static final String USER_EMAIL = "test@test.com";
    private static final String USER_PASSWORD = "secret";

    private static final String PARAM_PAGE = "page";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_DIRECTION = "direction";
    private static final String PARAM_PROPERTIES = "properties";
    private static final String PARAM_IDS = "ids";

    private static final Integer PAGE = 1;
    private static final Integer PAGE_SIZE = 25;
    private static final Sort.Direction SORT_DIRECTION = Sort.Direction.DESC;
    private static final String [] SORT_PROPS = {"amount"};
    private static final String EXPENSE_ID_1 = "1";
    private static final String EXPENSE_ID_2 = "2";

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private WebApplicationContext wac;

    private DefaultMockMvcBuilder mockMvcBuilder;

    private MockMvc mockMvc;

    private void mockAuthenticatedUser() {
        User mockUser = new User();
        mockUser.setEmail(USER_EMAIL);
        mockUser.setPassword(USER_PASSWORD.toCharArray());

        UserDetails userDetails = new CustomUserDetails(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testCreateExpense() throws Exception {
        mockAuthenticatedUser();

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isCreated());
        mockMvc = mockMvcBuilder.build();

        ExpenseDTO expense = new ExpenseDtoBuilder()
                .setDate(new LocalDate())
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

    @Test
    public void testCreateExpense_unauthenticated() throws Exception {
        UserDetails userDetails = new CustomUserDetails(null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isBadRequest());
        mockMvc = mockMvcBuilder.build();

        ExpenseDTO expense = new ExpenseDtoBuilder()
                .setDate(new LocalDate())
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
        assertThat(responseContent).isEmpty();
    }

    @Test
    public void testListPage() throws Exception {
        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = get("/expense/page")
                .contentType(MediaType.APPLICATION_JSON)
                .param(PARAM_PAGE, PAGE.toString())
                .param(PARAM_SIZE, PAGE_SIZE.toString())
                .param(PARAM_DIRECTION, SORT_DIRECTION.toString())
                .param(PARAM_PROPERTIES, SORT_PROPS[0]);

        MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
        PageDTO<ExpenseResponseDTO> responsePage = om.readValue(result.getResponse().getContentAsString(), PageDTO.class);

        assertThat(responsePage.getPageSize()).isEqualTo(PAGE_SIZE);
        assertThat(responsePage.getSortProperty()).isEqualTo(SORT_PROPS[0]);
        assertThat(responsePage.getPageNumber()).isEqualTo(PAGE);
        assertThat(responsePage.getSortDirection()).isEqualTo(SORT_DIRECTION);
    }

    @Test
    public void testDelete() throws Exception {
        mockAuthenticatedUser();

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = delete("/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .param(PARAM_IDS, EXPENSE_ID_1)
                .param(PARAM_IDS, EXPENSE_ID_2);

        mockMvc.perform(requestBuilder).andDo(print());
    }
}
