package uk.gov.hmcts.reform.divorce.validationservice.domain;

import lombok.Data;

import java.util.List;

@Data
public class ValidationResponse {

    private String validationStatus;

    private List<String> warnings;

    private List<String> errors;
}