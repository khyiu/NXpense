package nxpense.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import nxpense.builder.ExpenseDtoBuilder;
import nxpense.dto.*;
import nxpense.repository.ExpenseRepository;
import nxpense.security.CustomUserDetails;
import org.dbunit.operation.DatabaseOperation;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

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

    private static final Integer PAGE = 1;
    private static final Integer PAGE_SIZE = 25;
    private static final Sort.Direction SORT_DIRECTION = Sort.Direction.DESC;
    private static final String[] SORT_PROPS = {"amount"};

    private static final String ATTACHMENT_FILENAME_1 = "file1.txt";
    private static final String ATTACHMENT_FILENAME_2 = "file2.txt";
    private static final MockMultipartFile attachmentsPart1 = new MockMultipartFile("attachments", ATTACHMENT_FILENAME_1, "", new byte[]{});
    private static final MockMultipartFile attachmentsPart2 = new MockMultipartFile("attachments", ATTACHMENT_FILENAME_2, "", new byte[]{});

    @Autowired
    private ExpenseRepository expenseRepository;


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

        MockMultipartFile expenseJsonPart = new MockMultipartFile("expense", "", "application/json", expenseJson.getBytes());


        RequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload("/expense")
                .file(expenseJsonPart)
                .file(attachmentsPart1)
                .file(attachmentsPart2)
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
        MockMultipartFile expenseJsonPart = new MockMultipartFile("expense", "", "application/json", expenseJson.getBytes());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload("/expense")
                .file(expenseJsonPart)
                .file(attachmentsPart1)
                .file(attachmentsPart2);
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
        PageDTO responsePage = om.readValue(result.getResponse().getContentAsString(), PageDTO.class);

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
                .content("[{\"id\": 6,\"version\": 1}]");

        mockMvc.perform(requestBuilder).andDo(print());
        assertThat(expenseRepository.findOne(6)).isNull();
    }

    @Test
    public void testDelete_notOwner() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = delete("/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"id\": 6,\"version\": 1}]");

        mockMvc.perform(requestBuilder).andDo(print());
        assertThat(expenseRepository.findOne(6)).isNotNull();
    }

    @Test
    public void testUpdateExpense() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        BigDecimal newAmount = BigDecimal.valueOf(88.88);
        String newDescription = "Description updated in integration test";
        LocalDate newDate = new LocalDate(2016, 1, 1);

        ExpenseDTO expenseDto = new ExpenseDtoBuilder()
                .setDescription(newDescription)
                .setAmount(newAmount)
                .setDate(newDate)
                .setSource(ExpenseSource.DEBIT_CARD)
                .setVersion(1)
                .build();

        MockMultipartFile expenseJsonPart = new MockMultipartFile("expense", "", "application/json", om.writeValueAsString(expenseDto).getBytes());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload("/expense/1")
                .file(expenseJsonPart)
                .file(attachmentsPart1)
                .file(attachmentsPart2);

        MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        ExpenseResponseDTO responseDto = om.readValue(jsonResponse, ExpenseResponseDTO.class);
        assertThat(responseDto.getAmount()).isEqualTo(newAmount);
        assertThat(responseDto.getDescription()).isEqualTo(newDescription);
        assertThat(responseDto.getDate()).isEqualTo(newDate);
    }

    @Test
    public void testUpdateExpense_OutOfSync() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().is(499));
        mockMvc = mockMvcBuilder.build();

        BigDecimal newAmount = BigDecimal.valueOf(88.88);
        String newDescription = "Description updated in integration test";
        LocalDate newDate = new LocalDate(2016, 1, 1);

        ExpenseDTO expenseDto = new ExpenseDtoBuilder()
                .setDescription(newDescription)
                .setAmount(newAmount)
                .setDate(newDate)
                .setSource(ExpenseSource.DEBIT_CARD)
                .setVersion(0)
                .build();

        MockMultipartFile expenseJsonPart = new MockMultipartFile("expense", "", "application/json", om.writeValueAsString(expenseDto).getBytes());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload("/expense/1")
                .file(expenseJsonPart)
                .file(attachmentsPart1)
                .file(attachmentsPart2);

        mockMvc.perform(requestBuilder).andDo(print()).andReturn();
    }

    @Test
    public void testUpdateExpense_keepDateUnchanged() throws Exception {
        mockAuthenticatedUser(1);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        BigDecimal newAmount = BigDecimal.valueOf(88.88);
        String newDescription = "Description updated in integration test";
        LocalDate newDate = new LocalDate(2015, 1, 1);

        ExpenseDTO expenseDto = new ExpenseDtoBuilder()
                .setDescription(newDescription)
                .setAmount(newAmount)
                .setDate(newDate)
                .setSource(ExpenseSource.DEBIT_CARD)
                .build();

        MockMultipartFile expenseJsonPart = new MockMultipartFile("expense", "", "application/json", om.writeValueAsString(expenseDto).getBytes());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload("/expense/1")
                .file(expenseJsonPart)
                .file(attachmentsPart1)
                .file(attachmentsPart2);

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

        assertThat(balance.getNonVerified()).isEqualTo(BigDecimal.valueOf(-8.05));
        assertThat(balance.getVerified()).isEqualTo(BigDecimal.valueOf(1508.45));
        assertThat(balance.getGlobal().compareTo(BigDecimal.valueOf(1500.4))).isZero();
    }

    @Test
    public void testAddTagToExpense() throws Exception {
        mockAuthenticatedUser(2);

        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac);
        mockMvcBuilder.alwaysExpect(status().isOk());
        mockMvc = mockMvcBuilder.build();

        RequestBuilder requestBuilder = put("/expense/6/tag")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "1");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        List<TagResponseDTO> tags = om.readValue(jsonResponse, new TypeReference<List<TagResponseDTO>>(){});

        assertThat(tags).isNotNull().hasSize(1);
        assertThat(tags.get(0).getName()).isEqualTo("Mobile phone");
    }
}
