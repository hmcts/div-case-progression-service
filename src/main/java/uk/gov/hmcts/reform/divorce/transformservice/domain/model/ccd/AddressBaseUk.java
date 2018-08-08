package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddressBaseUk {
    @JsonProperty ("AddressLine1")
    private String addressLine1;
    @JsonProperty ("AddressLine2")
    private String addressLine2;
    @JsonProperty ("AddressLine3")
    private String addressLine3;
    @JsonProperty ("PostTown")
    private String postTown;
    @JsonProperty ("PostCode")
    private String postCode;
    @JsonProperty ("County")
    private String county;
    @JsonProperty ("Country")
    private String country;
}
