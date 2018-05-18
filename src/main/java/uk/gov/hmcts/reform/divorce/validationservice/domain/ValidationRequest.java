package uk.gov.hmcts.reform.divorce.validationservice.domain;

import lombok.Data;

import java.util.Map;

@Data
public class ValidationRequest {

    private Map<String, Object> data;

    private String formId;

    private String sectionId;
}