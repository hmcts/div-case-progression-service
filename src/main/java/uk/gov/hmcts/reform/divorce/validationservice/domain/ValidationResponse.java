package uk.gov.hmcts.reform.divorce.validationservice.domain;

import java.util.List;

import lombok.Data;

@Data
public class ValidationResponse {

    private String validationStatus;

    private List<String> warnings;

    private List<String> errors;
}