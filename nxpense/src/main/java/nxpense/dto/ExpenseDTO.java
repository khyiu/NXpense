package nxpense.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import nxpense.helper.serialization.CustomLocalDateDeserializer;
import nxpense.helper.serialization.CustomLocalDateSerializer;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.math.BigDecimal;

public class ExpenseDTO implements Serializable {


    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate date;

    private BigDecimal amount;

    private ExpenseSource source;

    private String description;

    private Integer position;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
