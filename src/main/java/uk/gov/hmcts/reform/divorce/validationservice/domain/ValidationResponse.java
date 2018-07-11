package uk.gov.hmcts.reform.divorce.validationservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "The response to a validation request")
public class ValidationResponse {
    @ApiModelProperty(value = "The status of the validation")
    private String validationStatus;
    @ApiModelProperty(value = "The warnings returned by the validation")
    private List<String> warnings;
    @ApiModelProperty(value = "The errors returned by the validation")
    private List<String> errors;
}