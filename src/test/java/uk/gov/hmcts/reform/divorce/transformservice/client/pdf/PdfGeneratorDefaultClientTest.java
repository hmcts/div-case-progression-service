package uk.gov.hmcts.reform.divorce.transformservice.client.pdf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.transformservice.client.TransformationHttpEntityFactory;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfGenerateDocumentRequest;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.PdfGenerateDocumentRequestMapper;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PdfGeneratorDefaultClientTest {

    @Mock
    private PdfGeneratorClientConfiguration clientConfiguration;

    @Mock
    private TransformationHttpEntityFactory httpEntityFactory;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PdfGenerateDocumentRequestMapper pdfGenerateDocumentRequestMapper;

    @InjectMocks
    private PdfGeneratorDefaultClient pdfGeneratorClient;

    @Test
    public void createCaseReturnsCreateEvent() {
        HttpEntity<PdfGenerateDocumentRequest> httpEntity = mock(HttpEntity.class);
        ResponseEntity<PdfFile> responseEntity = mock(ResponseEntity.class);
        PdfFile pdfFile = mock(PdfFile.class);
        CreateEvent submittedCase = new CreateEvent();
        final String urlString = "anUrl";
        final String authToken = "test";

        PdfGenerateDocumentRequest pdfGenerateDocumentRequest = new PdfGenerateDocumentRequest("templateName",
            new HashMap<>());
        when(pdfGenerateDocumentRequestMapper.toPdfGenerateDocumentRequest(eq(submittedCase)))
            .thenReturn(pdfGenerateDocumentRequest);
        when(httpEntityFactory.createRequestEntityForPdfGeneratorGet(pdfGenerateDocumentRequest, authToken))
            .thenReturn(httpEntity);
        when(clientConfiguration.getPdfGeneratorUrl()).thenReturn(urlString);

        when(restTemplate.exchange(eq(urlString), eq(HttpMethod.POST), eq(httpEntity), eq(PdfFile.class)))
            .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(pdfFile);

        assertEquals(pdfFile, pdfGeneratorClient.generatePdf(submittedCase, authToken));

        verify(httpEntityFactory).createRequestEntityForPdfGeneratorGet(pdfGenerateDocumentRequest, authToken);
        verify(clientConfiguration).getPdfGeneratorUrl();
        verify(restTemplate).exchange(eq(urlString), eq(HttpMethod.POST), eq(httpEntity), eq(PdfFile.class));
        verify(responseEntity).getBody();

        verifyNoMoreInteractions(httpEntityFactory, restTemplate, responseEntity);
    }

}
