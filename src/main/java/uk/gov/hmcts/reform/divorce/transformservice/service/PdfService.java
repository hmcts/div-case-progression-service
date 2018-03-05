package uk.gov.hmcts.reform.divorce.transformservice.service;

import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;

public interface PdfService {
    PdfFile generatePdf(CreateEvent caseDetailsWrap);
}
