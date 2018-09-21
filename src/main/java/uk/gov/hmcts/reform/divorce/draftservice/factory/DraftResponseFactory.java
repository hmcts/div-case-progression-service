package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.CcdToPaymentMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DraftResponseFactory {

    private static final String CASE_ID = "caseId";
    private static final String COURTS = "courts";
    private static final String SUBMISSION_STARTED = "submissionStarted";
    private static final String D_8_DIVORCE_UNIT = "D8DivorceUnit";
    private static final String CASE_DATA = "case_data";
    private static final String CASE_STATE = "state";
    private static final String PAYMENT_REFERENCE = "payment_reference";
    private static final String ID = "id";
    private static final String SUCCESS = "success";
    private static final String MULTIPLE_REJECTED_CASES_STATE = "MultipleRejectedCases";

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

    public static DraftsResponse buildDraftResponseFromCaseData(List<Map<String, Object>> listOfNonRejectedCasesInCCD,
                                                                CcdToPaymentMapper paymentMapper) {

        Map<String, Object> caseDetails = listOfNonRejectedCasesInCCD.get(0);

        if (listOfNonRejectedCasesInCCD.size() == 1) {

            return draftResponseBuilder(caseDetails, paymentMapper);
        } else {
            log.info("Multiple cases found - Multiple are not rejected");
            return draftResponseBuilder(caseDetails, MULTIPLE_REJECTED_CASES_STATE, paymentMapper);
        }
    }

    public static DraftsResponse draftResponseBuilder(Map<String, Object> caseDetails,
                                                      String customState,
                                                      CcdToPaymentMapper paymentMapper) {

        log.debug("Building draft response from existing case {} in CCD", caseDetails.get(ID));

        String caseState = (customState == null) ? (String) caseDetails.get(CASE_STATE) : customState;

        ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
        jsonNode.put(CASE_ID, (Long) caseDetails.get(ID));
        Map<String, Object> caseData = (Map<String, Object>) caseDetails.get(CASE_DATA);
        jsonNode.put(COURTS, (String) caseData.get(D_8_DIVORCE_UNIT));
        jsonNode.put(SUBMISSION_STARTED, true);
        jsonNode.put(CASE_STATE, caseState);
        paymentMapper.ccdToPaymentRef(caseData)
            .stream()
            .filter(p -> Optional.ofNullable(p.getPaymentStatus()).orElse("").equalsIgnoreCase(SUCCESS))
            .findFirst()
            .ifPresent(r -> jsonNode.put(PAYMENT_REFERENCE, r.getPaymentReference()));

        return DraftsResponse.builder()
            .isDraft(false)
            .data(jsonNode)
            .build();
    }

    public static DraftsResponse draftResponseBuilder(Map<String, Object> caseDetails,
                                                      CcdToPaymentMapper paymentMapper) {

        return draftResponseBuilder(caseDetails, null, paymentMapper);
    }
}
