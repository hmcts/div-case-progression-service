package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddressBaseUk {

    private String AddressLine1;

    private String AddressLine2;

    private String AddressLine3;

    private String PostTown;

    private String PostCode;

    private String County;

    private String Country;
}
