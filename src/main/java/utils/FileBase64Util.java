package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class FileBase64Util {

    public static String fileToBase64(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static File base64ToTempFile(String base64, String suffix) throws IOException {
        byte[] decoded = Base64.getDecoder().decode(base64);
        File tempFile = File.createTempFile("upload-", suffix);
        Files.write(tempFile.toPath(), decoded);
        return tempFile;
    }
}