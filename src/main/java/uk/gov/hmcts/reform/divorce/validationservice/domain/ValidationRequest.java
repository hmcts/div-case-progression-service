package uk.gov.hmcts.reform.divorce.validationservice.domain;

import java.util.Map;

import lombok.Data;

@Data
public class ValidationRequest {

    private Map<String, Object> data;

    private String formId;

    private String sectionId;
}