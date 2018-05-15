package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.springframework.http.HttpEntity;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfGenerateDocumentRequest;

import java.util.List;

public interface TransformationHttpEntityFactory {
    HttpEntity<String> createRequestEntityForCcdGet(String encodedJwt);

    HttpEntity<String> createRequestEntityForCcdGetCaseDataContent(String encodedJwt);

    HttpEntity<CaseDataContent> createRequestEntityForSubmitCase(String encodedJwt, CaseDataContent caseDataContent);

    HttpEntity<Object> createRequestEntityForHealthCheck();

    HttpEntity<PdfGenerateDocumentRequest> createRequestEntityForPdfGeneratorGet(
        PdfGenerateDocumentRequest pdfGenerateDocumentRequest, String authorization);
}
