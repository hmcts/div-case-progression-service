package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DivorceCaseToCCDMapperTestUtil {

    public static Object jsonToObject(final String absoluteFilePath,
                                      final Class<?> testClass, Class<?> targetObject)
        throws IOException, URISyntaxException {

        URI uri = testClass.getClassLoader().getResource(absoluteFilePath).toURI();
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
            new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8")), targetObject);

    }

}
