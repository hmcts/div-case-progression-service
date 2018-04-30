package uk.gov.hmcts.reform.divorce.transformservice.client.pdf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.client.TransformationHttpEntityFactory;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfGenerateDocumentRequest;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.PdfGenerateDocumentRequestMapper;

@Component
@Slf4j
public class PdfGeneratorDefaultClient implements PdfGeneratorClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TransformationHttpEntityFactory httpEntityFactory;

    @Autowired
    private PdfGeneratorClientConfiguration pdfGeneratorClientConfiguration;

    @Autowired
    private PdfGenerateDocumentRequestMapper pdfGenerateDocumentRequestMapper;

    @Override
    public PdfFile generatePdf(CreateEvent caseDetailsWrap, String authorization) {
        PdfGenerateDocumentRequest pdfGenerateDocumentRequest =
            pdfGenerateDocumentRequestMapper.toPdfGenerateDocumentRequest(caseDetailsWrap);

        HttpEntity<PdfGenerateDocumentRequest> httpEntity =
            httpEntityFactory.createRequestEntityForPdfGeneratorGet(pdfGenerateDocumentRequest, authorization);

        String url = pdfGeneratorClientConfiguration.getPdfGeneratorUrl();

        PdfFile res = restTemplate.exchange(url, HttpMethod.POST,  httpEntity, PdfFile.class).getBody();

        log.debug("Pdf generated {}" + res);
        return res;
    }

}
