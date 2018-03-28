package uk.gov.hmcts.reform.divorce.draftservice.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class UpdateDraft {

    private final JsonNode document;
    private final String type;
}
