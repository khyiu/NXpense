package nxpense.helper.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.LocalDate;

import java.io.IOException;

public class CustomLocalDateSerializer extends JsonSerializer<LocalDate>{

    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        String localDateString = value.toString(ExpenseDateFormat.BELGIAN.getDatePattern());
        jgen.writeString(localDateString);
    }
}
