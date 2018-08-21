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

    public static DraftsResponse buildDraftResponseFromCaseData(List<Map<String, Object>> listOfCasesInCCD) {

        int numberOfPetRejectedCases = 0;
        int numberOfPetNotRejectedCases = 0;

        if (CollectionUtils.isEmpty(listOfCasesInCCD)) {
            log.debug("No case found to build draft response");

            return DraftsResponse.emptyResponse();
        }
        else if (listOfCasesInCCD.size() == 1) {

            return draftResponseBuilder(listOfCasesInCCD);
        }
        else if (listOfCasesInCCD.size() > 1){
            for (Map<String, Object> caseDetails : listOfCasesInCCD) {
                String caseState = (String) caseDetails.get(CASE_STATE);

                if (caseState == "Rejected") {
                    numberOfPetRejectedCases += 1;
                }
                else{
                    numberOfPetNotRejectedCases += 1;
                }
            }
            // replace above with java stream for efficiency


            // if only 1 case is not "Rejected" -  Apply the existing resume logic as per DIV-2658 
            if (numberOfPetNotRejectedCases == 1) {
                for (int i = 0; i < listOfCasesInCCD.size(); i++) {
                    Map<String, Object> caseDetails = listOfCasesInCCD.get(i);
                    String caseState = (String) caseDetails.get(CASE_STATE);

                    if (caseState != "Rejected") {
                        log.info("Multiple cases found - only 1 is not rejected");

                        return draftResponseBuilder(Collections.singletonList(listOfCasesInCCD.get(i)));
                    }
                }
            }

            // if multiple cases are not "Rejected"  - Display new page at /contact-divorce-team 
            if (numberOfPetNotRejectedCases > 1) {
                log.info("Multiple cases found - Multiple are not rejected");

                return draftResponseBuilder(listOfCasesInCCD, MULTIPLE_REJECTED_CASES_STATE);
            }

            //if multiple cases are all "Rejected"  - Start a blank application
            if (numberOfPetRejectedCases == listOfCasesInCCD.size()) {
                log.info("Multiple cases found - all are rejected");

                return DraftsResponse.emptyResponse();
            }
            log.info("No case found to build draft response");

            return DraftsResponse.emptyResponse();
        }
        else{
            log.info("Unhandled situation retrieving cases. Building empty draft response");

            return DraftsResponse.emptyResponse();
        }
    }

    private static DraftsResponse draftResponseBuilder(List<Map<String, Object>> listOfCasesInCCD, String customState){

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

    private static DraftsResponse draftResponseBuilder(List<Map<String, Object>> listOfCasesInCCD) {

        return draftResponseBuilder(listOfCasesInCCD, null);
    }
}
