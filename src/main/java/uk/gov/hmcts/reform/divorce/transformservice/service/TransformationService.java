package uk.gov.hmcts.reform.divorce.transformservice.service;

import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

public interface TransformationService {
    CaseDataContent transform(DivorceSession divorceSession, CreateEvent createEvent, String eventSummary);
}
