package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.CcdToPaymentMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class DraftResponseFactoryTest {

    private static final String CASE_STATE = "state";
    private static final String AWAITING_PAYMENT_STATUS = "awaitingPayment";

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_multiple_cases_in_awaiting_payment() {

        // given
        List<Map<String, Object>> listOfCases = new ArrayList<>();

        Map<String, Object> caseData1 = new HashMap<>();
        caseData1.put(CASE_STATE, AWAITING_PAYMENT_STATUS);

        Map<String, Object> caseData2 = new HashMap<>();
        caseData2.put(CASE_STATE, AWAITING_PAYMENT_STATUS);

        listOfCases.add(caseData1);
        listOfCases.add(caseData2);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases,
            new CcdToPaymentMapper());

        // then
        assertEquals(false, draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_input_is_null() {

        // given

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(null,
            new CcdToPaymentMapper());

        // then
        assertEquals(false, draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_input_is_empty() {

        // given
        List<Map<String, Object>> listOfCases = Collections.emptyList();

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases,
            new CcdToPaymentMapper());

        // then
        assertEquals(false, draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_draft_response_when_case_exists_with_case_status() {

        // given
        Map<String, Object> caseData = new HashMap<>();
        String status = "awaitingPayment";
        caseData.put(CASE_STATE, status);

        Long caseId = 123L;
        caseData.put("id", caseId);

        Map<String, Object> caseDetails = new HashMap<>();
        String courts = "courtsXYZz";
        caseDetails.put("D8DivorceUnit", courts);

        caseData.put("case_data", caseDetails);

        List<Map<String, Object>> listOfCases = new ArrayList<>();
        listOfCases.add(caseData);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases,
            new CcdToPaymentMapper());

        // then
        JsonNode data = draftsResponse.getData();
        assertEquals(false, draftsResponse.isDraft());
        assertEquals(true, data.get("submissionStarted").asBoolean());
        assertEquals(courts, data.get("courts").asText());
        assertEquals(caseId, (Long) data.get("caseId").asLong());
        assertEquals(status, data.get(CASE_STATE).asText());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_draft_response_with_Payment_Reference_WhenPayment_Success()
        throws IOException {

        // given
        Map<String, Object> caseData = new HashMap<>();
        String status = "awaitingPayment";
        caseData.put(CASE_STATE, status);

        Long caseId = 123L;
        caseData.put("id", caseId);

        Map<String, Object> caseDetails = new HashMap<>();
        caseData.put("case_data", caseDetails);
        caseDetails.put(
            "Payments",
            new ObjectMapper().readTree(
                "[{\"value\":{\"PaymentStatus\":\"Success\",\"PaymentReference\":\"ABCD-PRef\"}}]"
            ));
        List<Map<String, Object>> listOfCases = new ArrayList<>();
        listOfCases.add(caseData);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases,
            new CcdToPaymentMapper());

        // then
        JsonNode data = draftsResponse.getData();
        assertEquals(false, draftsResponse.isDraft());
        assertEquals(caseId, (Long) data.get("caseId").asLong());
        assertEquals("ABCD-PRef", draftsResponse.getData().get("payment_reference").asText());
        assertEquals(status, data.get(CASE_STATE).asText());
    }

}
