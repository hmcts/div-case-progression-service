package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddressBaseUk {

    @JsonProperty("addressLine1")
    private String addressLine1;

    @JsonProperty("addressLine2")
    private String addressLine2;

    @JsonProperty("addressLine3")
    private String addressLine3;

    @JsonProperty("postTown")
    private String postTown;

    @JsonProperty("postCode")
    private String postCode;

    @JsonProperty("county")
    private String county;

    @JsonProperty("country")
    private String country;
}
