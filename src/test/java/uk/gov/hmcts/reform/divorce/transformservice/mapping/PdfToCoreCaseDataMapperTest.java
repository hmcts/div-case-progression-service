package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Document;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseProgressionApplication.class)
public class PdfToCoreCaseDataMapperTest {

    @Autowired
    private PdfToCoreCaseDataMapper mapper;

    @Test
    public void mapperIsMapping() {
        CoreCaseData coreCaseData = new CoreCaseData();

        PdfFile pdfFile = PdfFile.builder().url("OneUrl").fileName("OneName").build();
        CoreCaseData mapped = mapper.toCoreCaseData(pdfFile, coreCaseData);

        Document result = mapped.getD8Documents().get(0).getValue();

        assertEquals("petition", result.getDocumentType());
        assertEquals("OneName", result.getDocumentFileName());
        assertEquals("OneUrl", result.getDocumentLink().getDocumentUrl());
        assertEquals("OneUrl/binary", result.getDocumentLink().getDocumentBinaryUrl());
        assertEquals("OneName.pdf", result.getDocumentLink().getDocumentFilename());
    }
}
