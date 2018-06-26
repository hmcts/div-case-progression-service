package uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddressBaseUkSession {
    @ApiModelProperty(value = "AddressLine1")
    private String addressLine1;
    @ApiModelProperty(value = "ddressLine2")
    private String addressLine2;
    @ApiModelProperty(value = "AddressLine3")
    private String addressLine3;
    @ApiModelProperty(value = "PostTown")
    private String postTown;
    @ApiModelProperty(value = "PostCode")
    private String postCode;
    @ApiModelProperty(value = "County")
    private String county;
    @ApiModelProperty(value = "Country")
    private String country;
}
