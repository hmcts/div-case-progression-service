package uk.gov.hmcts.reform.divorce.draftservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.Getter;

@Data
public class CreateDraft {

    private final JsonNode document;
    private final String type;
    @JsonProperty("max_age")
    private final int maxAge;

}
