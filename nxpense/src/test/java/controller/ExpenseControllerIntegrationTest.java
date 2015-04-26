package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nxpense.builder.ExpenseDtoBuilder;
import nxpense.domain.User;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseResponseDTO;
import nxpense.dto.ExpenseSource;
import nxpense.dto.PageDTO;
import nxpense.security.CustomUserDetails;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/application-test-config.xml")
@WebAppConfiguration
@Transactional(readOnly = true)
public class ExpenseControllerIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseControllerIntegrationTest.class);

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
    private static final String[] SORT_PROPS = {"amount"};

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private DataSource datasource;

    @Autowired
    private WebApplicationContext wac;

    private DefaultMockMvcBuilder mockMvcBuilder;

    private MockMvc mockMvc;

    private IDatabaseConnection getDBConnection() throws SQLException, DatabaseUnitException {
        IDatabaseConnection iConnection = new DatabaseConnection(java.sql.DriverManager.getConnection("jdbc:h2:mem:nxpense;DB_CLOSE_DELAY=-1;user=sa;password=sa"));
        return iConnection;
    }

    private IDataSet loadDataSet(String pathToDataset) throws DataSetException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(pathToDataset);
        IDataSet dataset = new FlatXmlDataSetBuilder().build(inputStream);
        return dataset;
    }

    @Before
    public void setUp() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getDBConnection(), loadDataSet("dataset/expense-controller-integration-test-dataset.xml"));
    }

    private void mockAuthenticatedUser(int mockUserId) {
        User mockUser = new User();

        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockUser, Integer.valueOf(mockUserId));
            idField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            LOGGER.error("Attempt to access a field that does not exist.", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Attempt to access a non-accessible field.", e);
        }

        mockUser.setEmail(USER_EMAIL);
        mockUser.setPassword(USER_PASSWORD.toCharArray());

        UserDetails userDetails = new CustomUserDetails(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
}
