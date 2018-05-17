package uk.gov.hmcts.reform.divorce.validationservice.service;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.validationservice.client.ValidationClient;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;

@Component
@Slf4j
public class ValidationServiceImpl implements ValidationService {

    private static final String formId = "case-progression";

    @Autowired
    ValidationClient validationClient;
    
    @Override
    public ValidationResponse validate(ValidationRequest request) {
        log.debug("Validation request is:", request);

        return validationClient.validate(request);
    }

    @Override
    public ValidationResponse validateCoreCaseData(CoreCaseData coreCaseData) {
        log.info("Validating coreCaseData");

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> caseData = objectMapper.convertValue(
            coreCaseData, new TypeReference<Map<String, Object>>() {}
        );

        log.debug("Converted coreCaseData to Map");

        ValidationRequest request = new ValidationRequest();

        request.setFormId(formId);
        request.setData(caseData);

        return validationClient.validate(request);
    }
}