package nxpense.builder;

import nxpense.dto.ExpenseDTO;
import nxpense.dto.ExpenseSource;

import java.math.BigDecimal;
import java.util.Date;

public class ExpenseDtoBuilder {

    private Date date;
    private BigDecimal amount;
    private String description;
    private ExpenseSource source;

    public ExpenseDtoBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public ExpenseDtoBuilder setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public ExpenseDtoBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ExpenseDtoBuilder setSource(ExpenseSource source) {
        this.source = source;
        return this;
    }

    public ExpenseDTO build() {
        ExpenseDTO expenseDto = new ExpenseDTO();

        expenseDto.setDate(date);
        expenseDto.setAmount(amount);
        expenseDto.setDescription(description);
        expenseDto.setSource(source);

        return expenseDto;
    }
}
