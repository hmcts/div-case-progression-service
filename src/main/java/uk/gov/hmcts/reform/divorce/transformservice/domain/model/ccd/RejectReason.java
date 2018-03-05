package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class RejectReason {
    
    @JsonProperty("RejectReasonType")
    private String rejectReasonType;

    @JsonProperty("RejectReasonText")
    private String rejectReasonText;

}
