package uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class CreateDraft {

    private final JsonNode document;
    private final String type;
    @JsonProperty("max_age")
    private final int maxAge;

}
