package uk.gov.hmcts.reform.divorce.validationservice.service;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;

public interface ValidationService {
    
    public ValidationResponse validate(ValidationRequest request);

    public ValidationResponse validateCoreCaseData(CoreCaseData coreCaseData);
}