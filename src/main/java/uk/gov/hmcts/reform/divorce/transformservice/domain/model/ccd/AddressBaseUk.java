package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddressBaseUk {
    @JsonProperty (value = "AddressLine1")
    private String addressLine1;
    @JsonProperty (value = "AddressLine2")
    private String addressLine2;
    @JsonProperty (value = "AddressLine3")
    private String addressLine3;
    @JsonProperty (value = "PostTown")
    private String postTown;
    @JsonProperty (value = "PostCode")
    private String postCode;
    @JsonProperty (value = "County")
    private String county;
    @JsonProperty (value = "Country")
    private String country;
}
