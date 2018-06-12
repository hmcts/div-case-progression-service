package uk.gov.hmcts.reform.divorce.transformservice.client;

import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.YesNoAnswer;

public interface CcdClientConfiguration {

    String getCreateCaseUrl(String userId, YesNoAnswer helpWithFeesAnswer);

    String getSubmitCaseUrl(String userId);

    String getStartEventUrl(UserDetails userDetails, Long caseId, String eventId);

    String getCreateCaseEventUrl(UserDetails userDetails, Long caseId);

    String getRetrieveCaseUrl(String userId);
}
