package uk.gov.hmcts.reform.divorce.validationservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;

@Component
public class ValidationClientImpl implements ValidationClient {

    private static final String VALIDATE_ENDPOINT = "/version/1/validate";

    @Autowired
    private ValidationHttpEntityFactory httpEntityFactory;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${div.validation.service.url}")
    private String validationServiceUrl;
    
    @Override
    public ValidationResponse validate(ValidationRequest request) {
        
        HttpEntity<ValidationRequest> requestEntity = httpEntityFactory.createRequestEntityForValidation(request);

        String validateUrl = validationServiceUrl.concat(VALIDATE_ENDPOINT);

        return restTemplate.exchange(
            validateUrl, HttpMethod.POST, requestEntity, ValidationResponse.class
        ).getBody();
    }
}