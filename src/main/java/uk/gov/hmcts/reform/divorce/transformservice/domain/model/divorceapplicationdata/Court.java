package uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Court {
    private String region;
    private String phone;
    private String divorceCentre;
    private String courtCity;
    private String poBox;
    private String postCode;
    private String openingHours;
    private String email;
    private String phoneNumber;
}
