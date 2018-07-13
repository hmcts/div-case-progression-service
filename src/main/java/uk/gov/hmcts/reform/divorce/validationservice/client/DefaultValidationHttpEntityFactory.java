package uk.gov.hmcts.reform.divorce.validationservice.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;

@Component
public class DefaultValidationHttpEntityFactory implements ValidationHttpEntityFactory {
    
    @Override
    public HttpEntity<ValidationRequest> createRequestEntityForValidation(ValidationRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return new HttpEntity<>(request, headers);
    }
}