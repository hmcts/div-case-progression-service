package uk.gov.hmcts.reform.divorce.util;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceLoader {

    /**
     * Load JSON.
     *
     * @param filePath path to the file
     * @return the string
     * @throws Exception when there is an error loading the resource
     */
    public static String loadJSON(final String filePath) throws Exception {
        return new String(loadResource(filePath), Charset.forName("utf-8"));
    }

    /**
     * Load Resource.
     *
     * @param filePath path to the file
     * @return resource binary
     * @throws Exception when there is an error loading the resource
     */
    public static byte[] loadResource(final String filePath) throws Exception {
        URL url = ResourceLoader.class.getClassLoader().getResource(filePath);

        if (url == null) {
            throw new IllegalArgumentException(String.format("Could not find resource in path %s", filePath));
        }

        return Files.readAllBytes(Paths.get(url.toURI()));
    }
}
