package uk.gov.hmcts.reform.divorce.draftservice.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;


@Data
public class DraftsResponse {

    public static DraftsResponse emptyResponse() {
        return new DraftsResponse(false, null, StringUtils.EMPTY);
    }

    private final boolean isDraft;

    private final String draftId;

    private final JsonNode data;

    public DraftsResponse(boolean isDraft, JsonNode data, String draftId) {
        this.isDraft = isDraft;
        this.data = data;
        this.draftId = draftId;
    }

    public DraftsResponse(boolean isDraft, JsonNode data) {
        this.isDraft = isDraft;
        this.data = data;
        this.draftId = StringUtils.EMPTY;
    }

}
