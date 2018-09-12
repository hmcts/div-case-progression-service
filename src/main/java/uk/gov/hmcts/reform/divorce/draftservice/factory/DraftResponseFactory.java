package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.io.IOException;
import java.util.Collections;
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

    public static DraftsResponse buildDraftResponseFromDraft(Draft draft) {
        //check if draft is null
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

        if (CollectionUtils.isEmpty(listOfCasesInCCD)) {
            log.debug("No case found to build draft response");
            return DraftsResponse.emptyResponse();
        }

        if (listOfCasesInCCD.size() > 1) {
            log.info("Multiple cases found. Building empty draft response");
            return DraftsResponse.emptyResponse();
        }

        Map<String, Object> caseDetails = listOfCasesInCCD.get(0);

        log.debug("Building draft response from existing case {} in CCD", caseDetails.get(ID));

        ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
        jsonNode.put(CASE_ID, (Long) caseDetails.get(ID));
        Map<String, Object> caseData = (Map<String, Object>) caseDetails.get(CASE_DATA);
        jsonNode.put(COURTS, (String) caseData.get(D_8_DIVORCE_UNIT));
        jsonNode.put(SUBMISSION_STARTED, true);
        jsonNode.put(CASE_STATE, (String) caseDetails.get(CASE_STATE));
        findPaymentRef(caseData).ifPresent(paymentRef -> jsonNode.put(PAYMENT_REFERENCE, paymentRef));

        return DraftsResponse.builder()
            .isDraft(false)
            .data(jsonNode)
            .build();
    }

    private static Optional<String> findPaymentRef(Map<String, Object> caseData){
        log.info("Payment record from CCD getPayments {}", caseData.get("Payments"));
        Optional<String> validPayRef = Optional.empty();
        if (caseData.get("Payments") != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode payments = objectMapper.readTree((String) caseData.get("Payments"));
                log.info("Payment record from CCD Payments {}", payments);
                if (payments.isArray() && payments.size() > 0) {
                    JsonNode payRef = payments.get(0).get("value").get("PaymentReference");
                    log.info("Payment record from CCD payRef {}", payRef);
                    if (payRef != null) {
                        validPayRef = Optional.of(payRef.textValue());
                    }
                }
            } catch (IOException ioe) {
                throw new RuntimeException("Error mapping payment", ioe);
            }
        }
        return validPayRef;

    }
}
