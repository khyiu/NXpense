package nxpense.controller;

import nxpense.domain.Expense;
import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseResponseDTO;
import nxpense.helper.ExpenseConverter;
import nxpense.service.api.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Kro on 20/02/15.
 */
@Controller
@RequestMapping("/expense")
public class ExpenseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseService expenseService;

    @RequestMapping(value = "/new", method = RequestMethod.POST)
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
}
