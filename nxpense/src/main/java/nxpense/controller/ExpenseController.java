package nxpense.controller;

import nxpense.domain.Expense;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseResponseDTO;
import nxpense.dto.PageDTO;
import nxpense.helper.ExpenseConverter;
import nxpense.service.api.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Kro on 20/02/15.
 */
@Controller
@RequestMapping("/expense")

public class ExpenseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseService expenseService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ExpenseResponseDTO> createExpense(@RequestBody ExpenseDTO newExpenseDTO) {

        try {
            Expense createdExpense = expenseService.createNewExpense(newExpenseDTO);
            ExpenseResponseDTO responseDto = ExpenseConverter.entityToResponseDto(createdExpense);
            return new ResponseEntity<ExpenseResponseDTO>(responseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Could not complete new expense creation", e);
            return new ResponseEntity<ExpenseResponseDTO>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<PageDTO<ExpenseResponseDTO>> listExpense(@RequestParam Integer page,
                                                        @RequestParam Integer size,
                                                        @RequestParam Sort.Direction direction,
                                                        @RequestParam String[] properties) {

        // NOTE: page number used by spring-data-jpa is zero-based!
        Page<Expense> expensePage = expenseService.getPageExpenses(page - 1, size, direction, properties);
        PageDTO<ExpenseResponseDTO> pageDto = ExpenseConverter.expensePageToExpensePageDto(expensePage);
        // NOTE: increment page number before sending response to client, as we had to pass a zero-based value to the expenseService
        pageDto.setPageNumber(pageDto.getPageNumber() + 1);

        return new ResponseEntity<PageDTO<ExpenseResponseDTO>>(pageDto, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteExpense(@RequestParam List<Integer> ids)  {
        expenseService.deleteExpense(ids);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
