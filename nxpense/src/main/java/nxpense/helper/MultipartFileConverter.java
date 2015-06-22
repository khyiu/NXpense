package nxpense.helper;

import nxpense.domain.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by Kro on 16/06/15.
 */
public class MultipartFileConverter {

    private MultipartFileConverter() {

    }

    public static Attachment toAttachment(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null) {
            return null;
        }

        return new Attachment(multipartFile.getOriginalFilename(), multipartFile.getBytes());
    }
}
