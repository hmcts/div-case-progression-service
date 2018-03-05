package uk.gov.hmcts.reform.divorce.transformservice.client;

import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

public interface CcdClient {
    CreateEvent createCase(String jwt);

    SubmitEvent submitCase(String encodedJwt, CaseDataContent caseDataContent);
}
