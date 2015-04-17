package nxpense.helper.serialization;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public enum ExpenseDateFormat {

    BELGIAN("dd/MM/yyyy");

    private String datePattern;

    ExpenseDateFormat(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public DateTimeFormatter getFormatter() {
        return DateTimeFormat.forPattern(datePattern);
    }
}
