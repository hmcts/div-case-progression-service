package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class DraftResponseFactoryTest {

    private static final String NOT_AWAITING_PAYMENT_STATUS = "notAwaitingPayment";
    private static final String CASE_STATUS = "state";
    private static final String AWAITING_PAYMENT_STATUS = "awaitingPayment";

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_multiple_cases_in_awaiting_payment() {

        // given
        List<Map<String, Object>> listOfCases = new ArrayList<>();

        Map<String, Object> caseData1 = new HashMap();
        caseData1.put(CASE_STATUS, AWAITING_PAYMENT_STATUS);

        Map<String, Object> caseData2 = new HashMap();
        caseData2.put(CASE_STATUS, AWAITING_PAYMENT_STATUS);

        listOfCases.add(caseData1);
        listOfCases.add(caseData2);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases);

        // then
        assertEquals(false, draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_input_is_null() {

        // given
        List<Map<String, Object>> listOfCases = null;

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases);

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
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases);

        // then
        assertEquals(false, draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_draft_response_when_case_exists_in_awaiting_payment() {

        // given
        Map<String, Object> caseData2 = new HashMap();
        caseData2.put(CASE_STATUS, "awaitingPayment");
        Long caseId = 123L;
        caseData2.put("id", caseId);

        Map<String, Object> caseDetails = new HashMap();
        String courts = "courtsXYZz";
        caseDetails.put("D8DivorceUnit", courts);

        caseData2.put("case_data", caseDetails);

        List<Map<String, Object>> listOfCases = new ArrayList<>();
        listOfCases.add(caseData2);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases);

        // then
        JsonNode data = draftsResponse.getData();
        assertEquals(false, draftsResponse.isDraft());
        assertEquals(true, data.get("submissionStarted").asBoolean());
        assertEquals(courts, data.get("courts").asText());
        assertEquals(caseId, (Long) data.get("caseId").asLong());
    }
}
