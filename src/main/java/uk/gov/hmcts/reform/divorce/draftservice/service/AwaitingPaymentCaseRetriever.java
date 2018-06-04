package uk.gov.hmcts.reform.divorce.draftservice.service;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;

@Component
@Slf4j
public class AwaitingPaymentCaseRetriever {

    private static final String CASE_STATE = "state";
    private static final String AWAITING_PAYMENT_STATE = "awaitingpayment";
    private final RetrieveCcdClient retrieveCcdClient;

    @Autowired
    public AwaitingPaymentCaseRetriever(RetrieveCcdClient retrieveCcdClient) {
        this.retrieveCcdClient = retrieveCcdClient;
    }

    public List<Map<String, Object>> getCases(String userId, String jwt) {

        List<Map<String, Object>> cases = retrieveCcdClient.getCases(userId, jwt);

        List<Map<String, Object>> awaitingPaymentCases = cases.stream()
                .filter(caseData -> {
                    Object status = caseData.get(CASE_STATE);
                    return status == null ? false : status.toString().equalsIgnoreCase(AWAITING_PAYMENT_STATE);
                })
                .collect(toList());

        if (awaitingPaymentCases.isEmpty()) {
            log.debug("No cases found awaiting payment");
            return Collections.emptyList();
        }

        log.debug(String.format("Found %s cases awaiting payment", awaitingPaymentCases.size()));

        return awaitingPaymentCases;
    }
}
