package uk.gov.hmcts.reform.divorce.draftservice.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class DraftsResponse {

    private final boolean isDraft;

    private final String draftId;

    private final JsonNode data;

    public static DraftsResponse emptyResponse() {
        return DraftsResponse.builder().build();
    }
}
