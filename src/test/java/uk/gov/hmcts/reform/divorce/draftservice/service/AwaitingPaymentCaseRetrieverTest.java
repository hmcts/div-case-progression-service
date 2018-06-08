package uk.gov.hmcts.reform.divorce.draftservice.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class AwaitingPaymentCaseRetrieverTest {

    private static final String USER_ID = "userId";
    private static final String JWT = "bearer jwt";
    private AwaitingPaymentCaseRetriever underTest;

    @Mock
    private RetrieveCcdClient mockRetrieveCcdClient;

    @Before
    public void setUp() throws Exception {
        String checkCcdEnabled = "true";
        underTest = new AwaitingPaymentCaseRetriever(mockRetrieveCcdClient, checkCcdEnabled);
    }

    @Test
    public void getCases_should_not_call_ccd_if_feature_flag_is_disabled() {

        // given
        String checkCcdEnabled = "false";
        underTest = new AwaitingPaymentCaseRetriever(mockRetrieveCcdClient, checkCcdEnabled);

        // when
        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        // then
        assertEquals(0, casesRetrieved.size());
        verifyZeroInteractions(mockRetrieveCcdClient);
    }

    @Test
    public void getCases_should_return_empty_list_if_no_cases_found() {

        // given
        List<Map<String, Object>> cases = Collections.emptyList();
        given(mockRetrieveCcdClient
                .getCases(USER_ID, JWT))
                .willReturn(cases);

        // when
        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        // then
        assertEquals(0, casesRetrieved.size());
    }

    @Test
    public void getCases_should_return_empty_list_if_no_cases_found_in_awaiting_payment() {

        // given
        Map<String, Object> caseData = new HashMap<>();
        caseData.put("state", "notAwaitingPayment");

        List<Map<String, Object>> cases = new ArrayList<>();
        cases.add(caseData);

        given(mockRetrieveCcdClient
                .getCases(USER_ID, JWT))
                .willReturn(cases);

        // when
        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        // then
        assertEquals(0, casesRetrieved.size());
    }

    @Test
    public void getCases_should_return_empty_list_if_no_state_is_found() {

        // given
        Map<String, Object> caseData = new HashMap<>();

        List<Map<String, Object>> cases = new ArrayList<>();
        cases.add(caseData);

        given(mockRetrieveCcdClient
                .getCases(USER_ID, JWT))
                .willReturn(cases);

        // when
        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        // then
        assertEquals(0, casesRetrieved.size());
    }

    @Test
    public void getCases_should_return_cases_found_in_awaiting_payment() {

        // given
        Map<String, Object> caseData = new HashMap();
        caseData.put("state", "notAwaitingPayment");

        Map<String, Object> caseData2 = new HashMap();
        caseData2.put("state", "awaitingPayment");

        List<Map<String, Object>> cases = new ArrayList<>();
        cases.add(caseData);
        cases.add(caseData2);

        given(mockRetrieveCcdClient
                .getCases(USER_ID, JWT))
                .willReturn(cases);

        // when
        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        // then
        assertEquals(1, casesRetrieved.size());
    }

    @Test
    public void getCases_should_return_cases_found_in_awaiting_payment_irrespective_of_state_case_sensitivity() {

        // given
        Map<String, Object> caseData = new HashMap();
        caseData.put("state", "notAwaitingPayment");

        Map<String, Object> caseData2 = new HashMap();
        caseData2.put("state", "awaitingpayment");

        Map<String, Object> caseData3 = new HashMap();
        caseData3.put("state", "AWAITINGPAYMENT");

        List<Map<String, Object>> cases = new ArrayList<>();
        cases.add(caseData);
        cases.add(caseData2);
        cases.add(caseData3);

        given(mockRetrieveCcdClient
                .getCases(USER_ID, JWT))
                .willReturn(cases);

        // when
        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        // then
        assertEquals(2, casesRetrieved.size());
    }
}
