package uk.gov.hmcts.reform.divorce.validationservice.client;

import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;

public interface ValidationClient {
    
    public ValidationResponse validate(ValidationRequest request);
}