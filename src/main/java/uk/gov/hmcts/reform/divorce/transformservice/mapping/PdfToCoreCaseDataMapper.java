package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.*;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;

import java.util.ArrayList;
import java.util.List;

@Component
public class PdfToCoreCaseDataMapper {

    private static final String HAL_BINARY_RESPONSE_CONTEXT_PATH = "/binary";
    private static final String PDF_FILE_EXTENSION = ".pdf";
    private static final String DOCUMENT_TYPE_PETITION = "petition";

    public CoreCaseData toCoreCaseData(PdfFile pdfFile, CoreCaseData coreCaseData) {
        Document document = toPdfDocument(pdfFile);
        List<CollectionMember<Document>> lists = new ArrayList<>();
        CollectionMember<Document> members = new CollectionMember<>();
        members.setValue(document);
        lists.add(members);
        coreCaseData.setD8Documents(lists);
        return coreCaseData;
    }

    private Document toPdfDocument(PdfFile pdfFile) {
        Document pdfDocument = new Document();
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl(pdfFile.getUrl())
                .documentBinaryUrl(pdfFile.getUrl() + HAL_BINARY_RESPONSE_CONTEXT_PATH)
                .documentFilename(pdfFile.getFileName() + PDF_FILE_EXTENSION)
                .build();
        pdfDocument.setDocumentLink(documentLink);
        pdfDocument.setDocumentFileName(pdfFile.getFileName());
        pdfDocument.setDocumentType(DOCUMENT_TYPE_PETITION);
        return pdfDocument;
    }
}