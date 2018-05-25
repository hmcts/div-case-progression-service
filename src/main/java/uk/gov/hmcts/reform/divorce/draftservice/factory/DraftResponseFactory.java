package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * TODO: write about the class
 *
 * @author wjtlopez
 */
@Component
public class DraftResponseFactory {

    public static DraftsResponse buildDraftResponseFromDraft(Draft draft) {
        return new DraftsResponse(true, draft.getDocument(), draft.getId());
    }

    public static DraftsResponse buildDraftResponseFromCaseData(List<LinkedHashMap> listOfCases) {

        if (listOfCases == null || listOfCases.isEmpty()) {
            return DraftsResponse.EMPTY_RESPONSE();
        }

        ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
        LinkedHashMap linkedHashMap = listOfCases.get(0);
        jsonNode.put("case_id", (Long) linkedHashMap.get("id"));
        LinkedHashMap caseData = (LinkedHashMap) linkedHashMap.get("case_data");
        jsonNode.put("courts", (String) caseData.get("D8DivorceUnit"));
        jsonNode.put("submissionStarted", true);

        return new DraftsResponse(false, jsonNode);
    }
}
