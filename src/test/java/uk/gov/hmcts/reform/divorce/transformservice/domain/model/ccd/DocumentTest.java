package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.UploadedFileTest;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class DocumentTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Document document;
    private String json;
    private String jsonWithNullFieldsIgnored;

    @Before
    public void setUp() throws Exception {
        json = FileUtils.readFileToString(
            new File(UploadedFileTest.class.getResource("/fixtures/model/ccd/Document.json").toURI()),
            Charset.defaultCharset());

        jsonWithNullFieldsIgnored = FileUtils.readFileToString(new File(UploadedFileTest.class
                .getResource("/fixtures/model/ccd/DocumentNullFieldsIgnored.json").toURI()),
            Charset.defaultCharset());

        document = new Document();
        document.setDocumentType("marriageCert");
        document.setDocumentFileName("test-file-name");
        document.setDocumentLink(DocumentLink.builder().documentUrl("http://localhost/document").build());
        document.setDocumentEmailContent("test-email-content");
        document.setDocumentComment("test-comment");
        document.setDocumentDateAdded("2017-01-01");
    }

    @Test
    public void shouldMarshalJsonStringToObject() throws Exception {
        ObjectReader objectReader = objectMapper.readerFor(Document.class);

        assertEquals(document, objectReader.readValue(json));
    }

    @Test
    public void shouldUnmarshalObjectToJsonString() throws Exception {
        ObjectWriter objectWriter = objectMapper
            .writer(new SimpleDateFormat("yyyy-MM-dd"))
            .withDefaultPrettyPrinter();

        assertEquals(jsonWithNullFieldsIgnored.trim(), objectWriter.writeValueAsString(document));
    }
}
