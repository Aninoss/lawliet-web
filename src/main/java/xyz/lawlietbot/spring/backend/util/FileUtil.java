package xyz.lawlietbot.spring.backend.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static boolean writeInputStreamToFile(InputStream inputStream, File file) {
        try (inputStream) {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            Files.write(buffer, file);
            return true;
        } catch (IOException e) {
            LOGGER.error("File error", e);
        }
        return false;
    }

}
