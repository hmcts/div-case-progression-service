package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.draftservice.domain.CreateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.UpdateDraft;

@Component
public class DraftModelFactory {

    @Value("${draft.store.api.document.type}")
    private String documentType;

    @Value("${draft.store.api.max.age}")
    private int maxAge;


    public CreateDraft createDraft(JsonNode data) {
        return new CreateDraft(data, documentType, maxAge);
    }

    public UpdateDraft updateDraft(JsonNode data) {
        return new UpdateDraft(data, documentType);
    }

    public boolean isDivorceDraft(Draft draft) {
        return draft.getType().equalsIgnoreCase(documentType);
    }

}
