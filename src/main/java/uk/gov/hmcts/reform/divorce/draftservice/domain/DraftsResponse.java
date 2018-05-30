package uk.gov.hmcts.reform.divorce.draftservice.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;


@Data
@Builder
public class DraftsResponse {

    public static DraftsResponse emptyResponse() {
        return DraftsResponse.builder().build();
    }

    private final boolean isDraft;

    private final String draftId;

    private final JsonNode data;
}
