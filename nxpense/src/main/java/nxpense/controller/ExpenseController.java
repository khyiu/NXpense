package nxpense.controller;

import nxpense.dto.ExpenseDTO;
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
    public ResponseEntity<Void> createExpense(@RequestBody ExpenseDTO newExpenseDTO) {

        try {
            expenseService.createNewExpense(newExpenseDTO);
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error("Could not complete new expense creation", e);
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }
}
