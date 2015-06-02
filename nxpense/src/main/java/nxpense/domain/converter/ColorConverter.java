package nxpense.domain.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.awt.*;

/**
 * The {@code ColorConverter} class is used to convert {@link java.awt.Color} objects to their corresponding hexadecimal representation.
 * This class is intended to be used when persisting/retrieving entities with attributes of type {@code Color}, in the database.
 */
@Converter
public class ColorConverter implements AttributeConverter<Color, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColorConverter.class);

    @Override
    public String convertToDatabaseColumn(Color attribute) {
        if (attribute == null) {
            return null;
        }

        LOGGER.debug("Converting color to HEX - color: {}", attribute);
        String hex = String.format("#%02x%02x%02x", attribute.getRed(), attribute.getGreen(), attribute.getBlue());
        LOGGER.debug("Converting color to HEX - result: [{}]", hex);
        return hex;
    }

    @Override
    public Color convertToEntityAttribute(String dbColorValue) {
        Color res = null;
        LOGGER.debug("Converting HEX to color - HEX: [{}]", dbColorValue);

        if(!StringUtils.isEmpty(dbColorValue)) {
            res = Color.decode(dbColorValue);
            LOGGER.debug("Converting HEX to color - color: {}", res);
        }

        return res;
    }
}
