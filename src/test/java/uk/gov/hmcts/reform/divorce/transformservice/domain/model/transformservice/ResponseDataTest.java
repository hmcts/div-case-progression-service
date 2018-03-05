package uk.gov.hmcts.reform.divorce.transformservice.domain.model.transformservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Document;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.DocumentLink;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.DocumentType;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.UploadedFileTest;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.ResponseData;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class ResponseDataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseData responseData;
    private String json;

    @Before
    public void setUp() throws Exception {
        json = FileUtils.readFileToString(new File(UploadedFileTest.class.getResource("/fixtures/model/ccd/ResponseData.json").toURI()), Charset.defaultCharset());

        responseData = new ResponseData(999, "success");
        responseData.setCaseId(999);
        responseData.setStatus("success");
    }

    @Test
    public void shouldMarshalJsonStringToObject() throws Exception {
        ObjectReader objectReader = objectMapper.readerFor(ResponseData.class);

        assertEquals(responseData, objectReader.readValue(json));
    }

}