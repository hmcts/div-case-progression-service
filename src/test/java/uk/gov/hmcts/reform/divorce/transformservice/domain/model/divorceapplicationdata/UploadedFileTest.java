package uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class UploadedFileTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Date createdOn = java.sql.Date.valueOf(LocalDate.of(2017, 11, 28));
    private Date modifiedOn = java.sql.Date.valueOf(LocalDate.of(2017, 11, 29));

    private UploadedFile uploadedFile = UploadedFile.builder()
        .fileName("marriage-certificate.pdf")
        .fileUrl("http://em-api-gateway-web:3404/documents/3627acc4-cb3b-4c95-9588-fea94e6c5855")
        .createdBy(8)
        .lastModifiedBy(8)
        .createdOn(createdOn)
        .modifiedOn(modifiedOn)
        .mimeType("application/pdf")
        .status("OK")
        .build();

    private String json;

    @Before
    public void setUp() throws Exception {
        json = FileUtils.readFileToString(
            new File(UploadedFileTest.class.getResource("/fixtures/model/divorce/UploadedFile.json").toURI()),
            Charset.defaultCharset()
        );
    }

    @Test
    public void shouldMarshalJsonStringToObject() throws Exception {
        ObjectReader objectReader = objectMapper.readerFor(UploadedFile.class);

        assertEquals(uploadedFile, objectReader.readValue(json));
    }

    @Test
    public void shouldUnMarshalObjectToJsonString() throws Exception {
        ObjectWriter objectWriter = objectMapper
            .writer(new SimpleDateFormat("yyyy-MM-dd"))
            .withDefaultPrettyPrinter();

        assertEquals(json, objectWriter.writeValueAsString(uploadedFile));
    }
}
