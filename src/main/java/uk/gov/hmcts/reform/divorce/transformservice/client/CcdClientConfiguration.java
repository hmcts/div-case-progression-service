package uk.gov.hmcts.reform.divorce.transformservice.client;

import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;

public interface CcdClientConfiguration {

    String getCreateCaseUrl(String jwtId);

    String getSubmitCaseUrl(String jwtId);

    String getStartEventUrl(UserDetails userDetails, Long caseId, String eventId);

    String getCreateCaseEventUrl(UserDetails userDetails, Long caseId);
}
