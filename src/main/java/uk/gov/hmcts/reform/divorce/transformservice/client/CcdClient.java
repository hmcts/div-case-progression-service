package uk.gov.hmcts.reform.divorce.transformservice.client;

import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

import java.util.List;

public interface CcdClient {
    CreateEvent createCase(String jwt);

    SubmitEvent submitCase(String encodedJwt, CaseDataContent caseDataContent);

    List<CaseDataContent> getCase(String userToken, String queryParams);
}
