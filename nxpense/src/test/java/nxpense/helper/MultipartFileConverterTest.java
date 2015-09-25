package nxpense.helper;

import nxpense.domain.Attachment;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Kro on 25/09/2015.
 */
public class MultipartFileConverterTest {

    private static final String MULTIPART_FILE_NAME = "test.txt";
    private static final byte [] BYTE_CONTENT = new byte[]{1, 2, 3};

    @Test
    public void testToAttachment() throws IOException {
        Attachment attachment = MultipartFileConverter.toAttachment(new MockMultipartFile(MULTIPART_FILE_NAME, MULTIPART_FILE_NAME, "text/plain", BYTE_CONTENT));

        assertThat(attachment).isNotNull();
        assertThat(attachment.getFilename()).isEqualTo(MULTIPART_FILE_NAME);
        assertThat(attachment.getByteContent()).isEqualTo(BYTE_CONTENT);
    }

    @Test
    public void testToAttachment_nullInput() throws IOException {
        assertThat(MultipartFileConverter.toAttachment(null)).isNull();
    }

}
