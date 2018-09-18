package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class Value {

    @JsonProperty("FeeDescription")
    private String feeDescription;

    @JsonProperty("FeeVersion")
    private String feeVersion;

    @JsonProperty("FeeCode")
    private String feeCode;

    @JsonProperty("FeeAmount")
    private String feeAmount;
}
