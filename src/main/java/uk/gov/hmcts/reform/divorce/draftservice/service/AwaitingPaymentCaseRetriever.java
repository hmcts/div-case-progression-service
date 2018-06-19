package uk.gov.hmcts.reform.divorce.draftservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class AwaitingPaymentCaseRetriever {

    private static final String CASE_STATE = "state";
    private static final String AWAITING_PAYMENT_STATE = "awaitingpayment";
    private final RetrieveCcdClient retrieveCcdClient;
    private final Boolean checkCcdEnabled;

    @Autowired
    public AwaitingPaymentCaseRetriever(RetrieveCcdClient retrieveCcdClient,
                                        @Value("${draft.api.ccd.check.enabled}") String checkCcdEnabled) {
        this.retrieveCcdClient = retrieveCcdClient;
        this.checkCcdEnabled = Boolean.valueOf(checkCcdEnabled);
    }

    public List<Map<String, Object>> getCases(String userId, String jwt) {

        if (!checkCcdEnabled) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> cases = retrieveCcdClient.getCases(userId, jwt);

        List<Map<String, Object>> awaitingPaymentCases = cases.stream()
                .filter(caseData -> {
                    Object status = caseData.get(CASE_STATE);
                    return status != null && status.toString().equalsIgnoreCase(AWAITING_PAYMENT_STATE);
                })
                .collect(toList());

        if (awaitingPaymentCases.isEmpty()) {
            log.debug("No cases found awaiting payment");
            return Collections.emptyList();
        }

        log.info(String.format("Found %s cases awaiting payment", awaitingPaymentCases.size()));

        return awaitingPaymentCases;
    }
}
