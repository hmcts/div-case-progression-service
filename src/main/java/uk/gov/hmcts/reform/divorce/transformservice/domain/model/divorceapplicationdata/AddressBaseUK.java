package uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddressBaseUK {
    @ApiModelProperty(value = "Address line 1")
    private String addressLine1;
    @ApiModelProperty(value = "Address line 2")
    private String addressLine2;
    @ApiModelProperty(value = "Address line 3")
    private String addressLine3;
    @ApiModelProperty(value = "Post Code")
    private String postCode;
    @ApiModelProperty(value = "Town")
    private String postTown;
    @ApiModelProperty(value = "County")
    private String county;
    @ApiModelProperty(value = "Country")
    private String country;
}
