package uk.gov.hmcts.reform.divorce.transformservice.client.pdf;

import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;

public interface PdfGeneratorClient {

    PdfFile generatePdf(CreateEvent caseDetailsWrap, String authorizationToken) ;
}
