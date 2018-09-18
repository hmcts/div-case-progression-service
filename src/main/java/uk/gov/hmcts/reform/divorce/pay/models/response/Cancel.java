package uk.gov.hmcts.reform.divorce.pay.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Cancel {

    @JsonProperty("method")
    private String method;

    @JsonProperty("href")
    private String href;

}
