package nxpense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ExpenseDTO implements Serializable {


    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date date;

    private BigDecimal amount;

    private ExpenseSource source;

    private String description;

    private Integer position;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ExpenseSource getSource() {
        return source;
    }

    public void setSource(ExpenseSource source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
