package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;

import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
public class DraftResponseFactory {

    private static final String CASE_ID = "case_id";
    private static final String COURTS = "courts";
    private static final String SUBMISSION_STARTED = "submissionStarted";
    private static final String D_8_DIVORCE_UNIT = "D8DivorceUnit";
    private static final String CASE_DATA = "case_data";
    private static final String ID = "id";

    public static DraftsResponse buildDraftResponseFromDraft(Draft draft) {
        return DraftsResponse.builder()
                .isDraft(true)
                .data(draft.getDocument())
                .draftId(draft.getId())
                .build();
    }

    public static DraftsResponse buildDraftResponseFromCaseData(List<LinkedHashMap> listOfCases) {

        if (listOfCases == null || listOfCases.isEmpty()) {
            log.debug("No case found to build draft response");
            return DraftsResponse.emptyResponse();
        }

        List<LinkedHashMap> awaitingPaymentCases = listOfCases.stream()
                .filter(caseData -> {
                    Object status = caseData.get("state");
                    return status == null ? false : status.toString().equalsIgnoreCase("awaitingpayment");
                })
                .collect(toList());

        if (awaitingPaymentCases.isEmpty()) {
            log.debug("No case found awaiting payment to build draft response");
            return DraftsResponse.emptyResponse();
        }

        if (awaitingPaymentCases.size() > 1) {
            log.debug("Multiple cases found awaiting payment. Building empty draft response");
            return DraftsResponse.emptyResponse();
        }

        log.debug("Building draft response from existing case in CCD awaiting payment");

        ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
        LinkedHashMap caseDetails = awaitingPaymentCases.get(0);
        jsonNode.put(CASE_ID, (Long) caseDetails.get(ID));
        LinkedHashMap caseData = (LinkedHashMap) caseDetails.get(CASE_DATA);
        jsonNode.put(COURTS, (String) caseData.get(D_8_DIVORCE_UNIT));
        jsonNode.put(SUBMISSION_STARTED, true);

        return DraftsResponse.builder()
                .isDraft(false)
                .data(jsonNode)
                .build();
    }
}
