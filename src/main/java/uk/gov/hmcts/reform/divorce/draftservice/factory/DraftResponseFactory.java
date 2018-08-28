package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class DraftResponseFactory {

    private static final String CASE_DATA = "case_data";
    private static final String CASE_ID = "caseId";
    private static final String CASE_STATE = "state";
    private static final String COURTS = "courts";
    private static final String D_8_DIVORCE_UNIT = "D8DivorceUnit";
    private static final String ID = "id";
    private static final String MULTIPLE_REJECTED_CASES_STATE = "MultipleRejectedCases";
    private static final String SUBMISSION_STARTED = "submissionStarted";

    public static DraftsResponse buildDraftResponseFromDraft(Draft draft) {
        if (draft == null) {
            return DraftsResponse.emptyResponse();
        } else {
            return DraftsResponse.builder()
                .isDraft(true)
                .data(draft.getDocument())
                .draftId(draft.getId())
                .build();
        }
    }

    public static DraftsResponse buildDraftResponseFromCaseData(List<Map<String, Object>> listOfNonRejectedCasesInCCD) {

        if (listOfNonRejectedCasesInCCD.size() == 1) {

            return draftResponseBuilder(listOfNonRejectedCasesInCCD);
        }else{

            log.info("Multiple cases found - Multiple are not rejected");
            return draftResponseBuilder(listOfNonRejectedCasesInCCD, MULTIPLE_REJECTED_CASES_STATE);
        }
    }

    public static DraftsResponse draftResponseBuilder(List<Map<String, Object>> listOfCasesInCCD, String customState) {

        Map<String, Object> caseDetails = listOfCasesInCCD.get(0);
        log.debug("Building draft response from existing case {} in CCD", caseDetails.get(ID));

        String caseState = (customState == null) ? (String) caseDetails.get(CASE_STATE) : customState;

        ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
        jsonNode.put(CASE_ID, (Long) caseDetails.get(ID));
        Map<String, Object> caseData = (Map<String, Object>) caseDetails.get(CASE_DATA);
        jsonNode.put(COURTS, (String) caseData.get(D_8_DIVORCE_UNIT));
        jsonNode.put(SUBMISSION_STARTED, true);
        jsonNode.put(CASE_STATE, caseState);

        return DraftsResponse.builder()
            .isDraft(false)
            .data(jsonNode)
            .build();
    }

    public static DraftsResponse draftResponseBuilder(List<Map<String, Object>> listOfCasesInCCD) {

        return draftResponseBuilder(listOfCasesInCCD, null);
    }
}
