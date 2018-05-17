package uk.gov.hmcts.reform.divorce.transformservice.client;

import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

import java.util.List;

public interface CcdClient {
    CreateEvent createCase(UserDetails userDetails, String jwt);

    SubmitEvent submitCase(UserDetails userDetails, String encodedJwt, CaseDataContent caseDataContent);

    List<CaseDataContent> getCase(String userToken, String queryParams);
}
