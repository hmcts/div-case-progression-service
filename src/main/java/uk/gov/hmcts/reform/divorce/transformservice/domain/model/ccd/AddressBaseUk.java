package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddressBaseUk {

    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    private String postTown;

    private String postCode;

    private String county;

    private String country;
}
