package uk.gov.hmcts.reform.divorce.transformservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;

@Component
@Slf4j
public class SanitiseService {

    public CoreCaseData sanitiseCase(final CaseDetails caseDetails) {
        // TODO Implement sanitisation
        return caseDetails.getCaseData();
    }

}
