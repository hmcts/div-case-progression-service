package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.transformservice.client.pdf.PdfGeneratorClient;
import uk.gov.hmcts.reform.divorce.transformservice.client.pdf.PdfGeneratorException;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PdfGeneratorServiceTest {
    private  static final String AUTH_TOKEN = "test";

    @Mock
    private PdfGeneratorClient pdfGeneratorClient;

    @InjectMocks
    private PdfGeneratorService pdfGeneratorService;

    @Test
    public void pdfGeneratorReturnAPdf() {

        final Long caseId = 990L;
        final String url = "oneUrl";
        CreateEvent submittedCase = new CreateEvent();
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId(caseId + "");
        submittedCase.setCaseDetails(caseDetails);


        PdfFile pdfFile = PdfFile.builder().url(url).build();

        when(pdfGeneratorClient.generatePdf(submittedCase, AUTH_TOKEN)).thenReturn(pdfFile);

        PdfFile pdfFileGenerated = pdfGeneratorService.generatePdf(submittedCase, AUTH_TOKEN);
        assertThat(pdfFileGenerated).isEqualTo(pdfFile);
        assertThat(pdfFileGenerated.getUrl()).isEqualTo(url);
        assertThat(pdfFileGenerated.toString()).isEqualTo("PdfFile(url=oneUrl, fileName=d8petition990)");

        verify(pdfGeneratorClient).generatePdf(submittedCase, AUTH_TOKEN);
        verifyNoMoreInteractions(pdfGeneratorClient);
    }

    @Test(expected = PdfGeneratorException.class)
    public void pdfGeneratorServiceMapExceptionsToPdfGeneratorException() {

        CreateEvent submittedCase = new CreateEvent();

        RuntimeException exception = mock(RuntimeException.class);

        doThrow(exception).when(pdfGeneratorClient).generatePdf(eq(submittedCase), eq(AUTH_TOKEN));

        pdfGeneratorService.generatePdf(submittedCase, AUTH_TOKEN);
    }

    @Test
    public void pdfGeneratorReturnsNullReturnNull() {
        CreateEvent submittedCase = new CreateEvent();
        when(pdfGeneratorClient.generatePdf(submittedCase, AUTH_TOKEN)).thenReturn(null);

        assertThat(pdfGeneratorService.generatePdf(submittedCase, AUTH_TOKEN)).isNull();
    }
}
