package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseProgressionApplication.class)
public class DocumentCollectionMapperTest {
    private static final String FILE_URL =
        "http://em-api-gateway-web:3404/documents/3627acc4-cb3b-4c95-9588-fea94e6c5855";
    private static final String FILE_NAME = "test-file";

    @Autowired
    private DocumentCollectionMapper mapper;

    @MockBean
    private DocumentUrlRewrite documentUrlRewrite;

    @Before
    public void setUp() {
        given(documentUrlRewrite.getDocumentUrl(FILE_URL)).willReturn(FILE_URL);
    }

    @Test
    public void shouldMapUploadedFileToCollectionMember() throws Exception {
        Date createdOn = java.sql.Date.valueOf(LocalDate.of(2017, 11, 28));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        UploadedFile uploadedFile = UploadedFile.builder()
            .fileName(FILE_NAME)
            .createdOn(createdOn)
            .fileUrl(FILE_URL)
            .build();

        CollectionMember<Document> collectionMember = mapper.map(uploadedFile);

        Document document = collectionMember.getValue();

        assertNull(collectionMember.getId());
        assertEquals(FILE_NAME, document.getDocumentFileName());
        assertEquals(dateFormat.format(createdOn), document.getDocumentDateAdded());
        assertEquals("", document.getDocumentComment());
        assertEquals("", document.getDocumentEmailContent());
        assertEquals("other", document.getDocumentType());
        assertEquals(DocumentLink.builder().documentUrl(FILE_URL).build(), document.getDocumentLink());
    }

    @Test
    public void shouldReturnNullWithNullInput() throws Exception {
        assertNull(mapper.map(null));
    }

    @Test
    public void shouldIgnoreMissingCreatedOnField() throws Exception {
        UploadedFile uploadedFile = UploadedFile.builder()
            .fileName(FILE_NAME)
            .fileUrl(FILE_URL)
            .build();

        CollectionMember<Document> collectionMember = mapper.map(uploadedFile);

        Document document = collectionMember.getValue();

        assertEquals(FILE_NAME, document.getDocumentFileName());
        assertEquals("", document.getDocumentComment());
        assertEquals("", document.getDocumentEmailContent());
        assertEquals("other", document.getDocumentType());
        assertEquals(DocumentLink.builder().documentUrl(FILE_URL).build(), document.getDocumentLink());
    }

    @Test
    public void shouldCallTheDocumentUrlRewriteToUpdateTheUrl() {
        UploadedFile uploadedFile = UploadedFile.builder()
            .fileName(FILE_NAME)
            .fileUrl(FILE_URL)
            .build();

        CollectionMember<Document> collectionMember = mapper.map(uploadedFile);

        Document document = collectionMember.getValue();

        verify(documentUrlRewrite).getDocumentUrl(FILE_URL);
    }
}
