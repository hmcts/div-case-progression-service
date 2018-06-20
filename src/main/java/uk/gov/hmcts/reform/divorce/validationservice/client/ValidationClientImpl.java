package uk.gov.hmcts.reform.divorce.validationservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;

@Component
@Slf4j
public class ValidationClientImpl implements ValidationClient {

    @Autowired
    ValidationHttpEntityFactory httpEntityFactory;

    @Autowired
    RestTemplate restTemplate;

    @Value("${div.validation.service.url}")
    private String validationServiceUrl;

    private static final String VALIDATE_ENDPOINT = "/version/1/validate";
    
    @Override
    public ValidationResponse validate(ValidationRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        
        HttpEntity<ValidationRequest> requestEntity = httpEntityFactory.createRequestEntityForValidation(request);

        String validateUrl = validationServiceUrl.concat(VALIDATE_ENDPOINT);

        log.info("Calling validation endpoint at:", validateUrl);
        try {
            log.debug("With request entity:", objectMapper.writeValueAsString(requestEntity));
        } catch (JsonProcessingException exception) {
            log.error("Unable to parse request entity:", exception);
        }

        ValidationResponse response = restTemplate.exchange(
            validateUrl, HttpMethod.POST, requestEntity, ValidationResponse.class
        ).getBody();

        try {
            log.debug("Validation response is:", objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException exception) {
            log.error("Unable to parse response:", exception);
        }

        return response;
    }
}