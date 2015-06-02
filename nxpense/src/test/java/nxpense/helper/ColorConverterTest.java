package nxpense.helper;


import nxpense.domain.converter.ColorConverter;
import org.junit.Test;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ColorConverterTest {

    private static final Color LIGHT_GREY = new Color(64, 64, 64);
    private static final Color SOME_PURPLE = new Color(92, 46, 138);

    private static final String HEX_BLACK = "#000000";
    private static final String HEX_LIGHT_GREY = "#404040";
    private static final String HEX_SOME_PURPLE = "#5C2E8A";

    private ColorConverter converter = new ColorConverter();

    @Test
    public void testConvertToDatabaseColumn() {
        String hex = converter.convertToDatabaseColumn(LIGHT_GREY);
        assertThat(hex).isEqualToIgnoringCase(HEX_LIGHT_GREY);
    }

    @Test
    public void testConvertToDatabaseColumn_1() {
        String hex = converter.convertToDatabaseColumn(Color.BLACK);
        assertThat(hex).isEqualToIgnoringCase(HEX_BLACK);
    }

    @Test
    public void testConvertToDatabaseColumn_2() {
        String hex = converter.convertToDatabaseColumn(SOME_PURPLE);
        assertThat(hex).isEqualToIgnoringCase(HEX_SOME_PURPLE);
    }

    @Test
    public void testConvertToEntityAttribute() {
        Color color = converter.convertToEntityAttribute(HEX_LIGHT_GREY);
        assertThat(color).isEqualTo(LIGHT_GREY);
    }

    @Test
    public void testConvertToEntityAttribute_1() {
        Color color = converter.convertToEntityAttribute(HEX_BLACK);
        assertThat(color).isEqualTo(Color.BLACK);
    }

    @Test
    public void testConvertToEntityAttribute_2() {
        Color color = converter.convertToEntityAttribute(HEX_SOME_PURPLE);
        assertThat(color).isEqualTo(SOME_PURPLE);
    }
}
