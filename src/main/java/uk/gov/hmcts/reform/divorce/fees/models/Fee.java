package uk.gov.hmcts.reform.divorce.fees.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by mrganeshraja on 13/06/2018.
 */
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fee {

    String feeCode;

    double amount;

    int  version;

    String description;
}
