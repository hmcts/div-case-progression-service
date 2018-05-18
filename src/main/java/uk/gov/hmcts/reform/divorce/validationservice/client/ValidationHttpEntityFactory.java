package uk.gov.hmcts.reform.divorce.validationservice.client;

import org.springframework.http.HttpEntity;

import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;

public interface ValidationHttpEntityFactory {
    
    HttpEntity<ValidationRequest> createRequestEntityForValidation(ValidationRequest request);
}