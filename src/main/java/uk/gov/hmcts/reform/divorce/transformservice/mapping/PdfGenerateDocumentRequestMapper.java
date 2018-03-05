package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfGenerateDocumentRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class PdfGenerateDocumentRequestMapper {

    private static final String TEMPLATE_NAME = "divorceminipetition";

    public PdfGenerateDocumentRequest toPdfGenerateDocumentRequest(CreateEvent caseDetailsWrap) {
        Map<String, Object> values = new HashMap<>();
        values.put("caseDetails", caseDetailsWrap.getCaseDetails());
        return new PdfGenerateDocumentRequest(TEMPLATE_NAME, values);
    }

}
