package uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddressBaseUkSession {
    @ApiModelProperty(value = "addressLine1")
    private String addressLine1;
    @ApiModelProperty(value = "addressLine2")
    private String addressLine2;
    @ApiModelProperty(value = "addressLine3")
    private String addressLine3;
    @ApiModelProperty(value = "postTown")
    private String postTown;
    @ApiModelProperty(value = "postCode")
    private String postCode;
    @ApiModelProperty(value = "county")
    private String county;
    @ApiModelProperty(value = "country")
    private String country;
}
