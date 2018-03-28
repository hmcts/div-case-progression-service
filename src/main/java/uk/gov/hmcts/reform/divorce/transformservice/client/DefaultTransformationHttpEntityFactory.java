package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.common.AuthorizationHeaderService;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfGenerateDocumentRequest;

import java.util.Collections;


@Component
public class DefaultTransformationHttpEntityFactory implements TransformationHttpEntityFactory {

    private final AuthorizationHeaderService authorizationHeaderService;

    @Autowired
    public DefaultTransformationHttpEntityFactory(AuthorizationHeaderService authorizationHeaderService) {
        this.authorizationHeaderService = authorizationHeaderService;
    }

    @Override
    public HttpEntity<String> createRequestEntityForCcdGet(String encodedJwt) {
        HttpHeaders headers = authorizationHeaderService.generateAuthorizationHeaders(encodedJwt);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return new HttpEntity<>(headers);
    }

    @Override
    public HttpEntity<CaseDataContent> createRequestEntityForSubmitCase(String userToken,
                                                                        CaseDataContent caseDataContent) {
        HttpHeaders headers = authorizationHeaderService.generateAuthorizationHeaders(userToken);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return new HttpEntity<>(caseDataContent, headers);
    }

    @Override
    public HttpEntity<Object> createRequestEntityForHealthCheck() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

        return new HttpEntity<>(headers);
    }

    @Override
    public HttpEntity<PdfGenerateDocumentRequest> createRequestEntityForPdfGeneratorGet(
        PdfGenerateDocumentRequest pdfGenerateDocumentRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return new HttpEntity<>(pdfGenerateDocumentRequest, headers);
    }
}
