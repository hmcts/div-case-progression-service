package uk.gov.hmcts.reform.divorce.transformservice.service.validation;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

public interface SubmissionValidator {

    boolean isValid(DivorceSession divorceSession);
}
