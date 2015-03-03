package nxpense.controller;

import nxpense.domain.Expense;
import nxpense.dto.ExpenseDTO;
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


    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ResponseEntity<Void> createExpense(@RequestBody ExpenseDTO newExpenseDTO) {
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
