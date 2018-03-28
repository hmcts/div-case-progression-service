package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CollectionMember;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Document;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.DocumentLink;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.UploadedFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseProgressionApplication.class)
public class DocumentCollectionMapperTest {
    @Autowired
    private DocumentCollectionMapper mapper;

    @Test
    public void shouldMapUploadedFileToCollectionMember() throws Exception {
        String fileName = "test-file";
        Date createdOn = java.sql.Date.valueOf(LocalDate.of(2017, 11, 28));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileUrl = "http://em-api-gateway-web:3404/documents/3627acc4-cb3b-4c95-9588-fea94e6c5855";

        UploadedFile uploadedFile = UploadedFile.builder()
                .fileName(fileName)
                .createdOn(createdOn)
                .fileUrl(fileUrl)
                .build();

        CollectionMember<Document> collectionMember = mapper.map(uploadedFile);

        Document document = collectionMember.getValue();

        assertNull(collectionMember.getId());
        assertEquals(fileName, document.getDocumentFileName());
        assertEquals(dateFormat.format(createdOn), document.getDocumentDateAdded());
        assertEquals("", document.getDocumentComment());
        assertEquals("", document.getDocumentEmailContent());
        assertEquals("other", document.getDocumentType());
        assertEquals(DocumentLink.builder().documentUrl(fileUrl).build(), document.getDocumentLink());
    }

    @Test
    public void shouldReturnNullWithNullInput() throws Exception {
        assertNull(mapper.map(null));
    }

    @Test
    public void shouldIgnoreMissingCreatedOnField() throws Exception {
        String fileName = "test-file";
        String fileUrl = "http://em-api-gateway-web:3404/documents/3627acc4-cb3b-4c95-9588-fea94e6c5855";

        UploadedFile uploadedFile = UploadedFile.builder()
                .fileName(fileName)
                .fileUrl(fileUrl)
                .build();

        CollectionMember<Document> collectionMember = mapper.map(uploadedFile);

        Document document = collectionMember.getValue();

        assertEquals(fileName, document.getDocumentFileName());
        assertEquals("", document.getDocumentComment());
        assertEquals("", document.getDocumentEmailContent());
        assertEquals("other", document.getDocumentType());
        assertEquals(DocumentLink.builder().documentUrl(fileUrl).build(), document.getDocumentLink());
    }
}