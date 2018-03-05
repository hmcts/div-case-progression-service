package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.DocumentType;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.UploadedFileType;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseProgressionApplication.class)
public class DocumentTypeMapperTest {

    @Autowired
    private DocumentTypeMapper mapper;

    @Test
    public void shouldMapPetitionUploadedFileTypeToPetitionDocumentType() throws Exception {
        UploadedFileType uploadedFileType = UploadedFileType.PETITION;

        assertEquals(DocumentType.PETITION, mapper.map(uploadedFileType));
    }

    @Test
    public void shouldMapUnknownUploadedFileTypeToOtherDocumentType() throws Exception {
        UploadedFileType uploadedFileType = UploadedFileType.UNKNOWN;

        assertEquals(DocumentType.OTHER, mapper.map(uploadedFileType));
    }

    @Test
    public void shouldMapNullUploadedFileTypeToOtherDocumentType() throws Exception {
        assertEquals(DocumentType.OTHER, mapper.map(null));
    }

    @Test
    public void shouldMapUnmappedUploadedFileTypesToOtherDocumentType() throws Exception {
        assertEquals(DocumentType.OTHER, mapper.map(UploadedFileType.MARRIAGE_CERTIFICATE));
    }
}