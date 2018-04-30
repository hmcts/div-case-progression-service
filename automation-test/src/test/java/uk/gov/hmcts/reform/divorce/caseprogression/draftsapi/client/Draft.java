package uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Draft {
    private final String id;
    private final JsonNode document;
    private final String type;
}
