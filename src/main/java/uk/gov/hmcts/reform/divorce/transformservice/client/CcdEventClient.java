package uk.gov.hmcts.reform.divorce.transformservice.client;

import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CaseEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

public interface CcdEventClient {
    
    CreateEvent startEvent(String jwt, Long caseId, String eventId);

    CaseEvent createCaseEvent(String encodedJwt, Long caseId, CaseDataContent caseDataContent);
}
