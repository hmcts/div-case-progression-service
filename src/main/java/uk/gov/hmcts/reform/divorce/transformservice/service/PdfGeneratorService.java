package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.client.pdf.PdfGeneratorClient;
import uk.gov.hmcts.reform.divorce.transformservice.client.pdf.PdfGeneratorException;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;

@Component
public class PdfGeneratorService implements PdfService {

    private static final String FILE_NAME_FORMAT = "d8petition%s";

    @Autowired
    private PdfGeneratorClient pdfGeneratorClient;

    @Override
    public PdfFile generatePdf(CreateEvent caseDetails) {
        try {
            PdfFile pdfFile = pdfGeneratorClient.generatePdf(caseDetails);

            if (pdfFile != null) {
                pdfFile.setFileName(String.format(FILE_NAME_FORMAT, caseDetails.getCaseDetails().getCaseId()));
            }

            return pdfFile;

        } catch (Exception e) {
            throw new PdfGeneratorException(e.getMessage(), e);
        }
    }
}
