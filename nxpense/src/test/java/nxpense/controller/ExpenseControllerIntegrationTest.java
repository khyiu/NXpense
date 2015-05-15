package nxpense.controller;

import nxpense.builder.ExpenseDtoBuilder;
import nxpense.dto.*;
import nxpense.security.CustomUserDetails;
import org.dbunit.operation.DatabaseOperation;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

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
public class ExpenseControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String PARAM_PAGE = "page";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_DIRECTION = "direction";
    private static final String PARAM_PROPERTIES = "properties";
    private static final String PARAM_IDS = "ids";

    private static final Integer PAGE = 1;
    private static final Integer PAGE_SIZE = 25;
    private static final Sort.Direction SORT_DIRECTION = Sort.Direction.DESC;
    private static final String[] SORT_PROPS = {"amount"};

    @Before
    public void setUp() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getDBConnection(), loadDataSet("dataset/expense-controller-integration-test-dataset.xml"));
    }

    @Test
    public void testCreateExpense() throws Exception {
        mockAuthenticatedUser(1);

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

        RequestBuilder requestBuilder = post("/expense")
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

        RequestBuilder requestBuilder = post("/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(expenseJson);
        MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isEmpty();
    }

    @Test
    public void testListPage() throws Exception {
        mockAuthenticatedUser(1);

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
        assertThat(responsePage.getItems()).hasSize(3);
    }

    @Test
    public void testDelete() throws Exception {
        mockAuthenticatedUser(2);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = delete("/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .param(PARAM_IDS, "6");

        mockMvc.perform(requestBuilder).andDo(print());

        int numberOfExpenseUser2 = JdbcTestUtils.countRowsInTableWhere(new JdbcTemplate(datasource), "EXPENSE", "user_id = 2");
        assertThat(numberOfExpenseUser2).isEqualTo(2);
    }

    @Test
    public void testDelete_notOwner() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = delete("/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .param(PARAM_IDS, "6");

        mockMvc.perform(requestBuilder).andDo(print());

        int numberOfExpenseRow = JdbcTestUtils.countRowsInTable(new JdbcTemplate(datasource), "EXPENSE");
        assertThat(numberOfExpenseRow).isEqualTo(12);
    }

    @Test
    public void testUpdateExpense() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        BigDecimal newAmount = new BigDecimal(88.88);
        String newDescription = "Description updated in integration test";
        LocalDate newDate = new LocalDate(2016, 1, 1);

        ExpenseDTO expenseDto = new ExpenseDtoBuilder()
                .setDescription(newDescription)
                .setAmount(newAmount)
                .setDate(newDate)
                .setSource(ExpenseSource.DEBIT_CARD)
                .build();

        RequestBuilder requestBuilder = put("/expense/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(expenseDto));

        MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        ExpenseResponseDTO responseDto = om.readValue(jsonResponse, ExpenseResponseDTO.class);
        assertThat(responseDto.getAmount()).isEqualTo(newAmount);
        assertThat(responseDto.getDescription()).isEqualTo(newDescription);
        assertThat(responseDto.getDate()).isEqualTo(newDate);
    }

    @Test
    public void testUpdateExpense_keepDateUnchanged() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        BigDecimal newAmount = new BigDecimal(88.88);
        String newDescription = "Description updated in integration test";
        LocalDate newDate = new LocalDate(2015, 1, 1);

        ExpenseDTO expenseDto = new ExpenseDtoBuilder()
                .setDescription(newDescription)
                .setAmount(newAmount)
                .setDate(newDate)
                .setSource(ExpenseSource.DEBIT_CARD)
                .build();

        RequestBuilder requestBuilder = put("/expense/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(expenseDto));

        MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        ExpenseResponseDTO responseDto = om.readValue(jsonResponse, ExpenseResponseDTO.class);
        assertThat(responseDto.getAmount()).isEqualTo(newAmount);
        assertThat(responseDto.getDescription()).isEqualTo(newDescription);
        assertThat(responseDto.getDate()).isEqualTo(newDate);
    }

    @Test
    public void testGetBalance() throws Exception {
        mockAuthenticatedUser(4);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = get("/expense/balance")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        BalanceInfoDTO balance = om.readValue(jsonResponse, BalanceInfoDTO.class);

        assertThat(balance.getNonVerified()).isEqualTo(new BigDecimal("-8.05"));
        assertThat(balance.getVerified()).isEqualTo(new BigDecimal("1508.45"));
        assertThat(balance.getGlobal()).isEqualTo(new BigDecimal("1500.40"));
    }
}
