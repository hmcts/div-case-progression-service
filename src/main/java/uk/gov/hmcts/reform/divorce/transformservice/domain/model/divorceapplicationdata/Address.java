package uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Address {
    @ApiModelProperty(value = "Address post code.")
    private String postcode;
    @JsonProperty("address")
    @ApiModelProperty(value = "Address.")
    private List<String> addressField;
    @ApiModelProperty(value = "Town.")
    private String town;
    @ApiModelProperty(value = "Is the address confirmed?")
    private boolean addressConfirmed;
    @ApiModelProperty(value = "Is the postcode valid?")
    private boolean validPostcode;
    @ApiModelProperty(value = "Is there an error in the postcode?")
    private boolean postcodeError;
    @ApiModelProperty(value = "URL.")
    private String url;
    @ApiModelProperty(value = "Address Base UK")
    private AddressBaseUkSession addressBaseUK;
}
