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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DraftResponseFactoryTest {

    private static final String CASE_STATE = "state";
    private static final String AWAITING_PAYMENT_STATUS = "awaitingPayment";
    private static final String REJECTED_STATUS = "Rejected";
    private static final String MULTIPLE_REJECTED_CASES_STATE = "MultipleRejectedCases";
    private static final String CASE_DATA = "case_data";
    private static final String COURTS = "courtsXYZz";
    private static final String D_8_DIVORCE_UNIT = "D8DivorceUnit";
    private static final String ID = "id";
    private static final Long CASE_ID = 123L;

    private static Map<String, Object> CASE_DATA_1 = new HashMap<>();
    private static Map<String, Object> CASE_DATA_2 = new HashMap<>();
    private static Map<String, Object> CASE_DETAILS = new HashMap<>();
    private static List<Map<String, Object>> LIST_OF_CASES = new ArrayList<>();

    @Test
    public void buildDraftResponseFromDraft_should_return_empty_response_when_input_is_null() {

        // given

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromDraft(null);

        // then
        assertFalse(draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_input_is_null() {

        // given

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(null);

        // then
        assertFalse(draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_input_is_empty() {

        // given
        LIST_OF_CASES = Collections.emptyList();

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(LIST_OF_CASES);

        // then
        assertFalse(draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_draft_response_when_case_exists_with_case_status() {

        // given
        CASE_DATA_1.put(CASE_STATE, AWAITING_PAYMENT_STATUS);
        CASE_DATA_1.put(ID, CASE_ID);

        CASE_DETAILS.put(D_8_DIVORCE_UNIT, COURTS);
        CASE_DATA_1.put(CASE_DATA, CASE_DETAILS);

        LIST_OF_CASES = new ArrayList<>();
        LIST_OF_CASES.add(CASE_DATA_1);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(LIST_OF_CASES);

        // then
        JsonNode data = draftsResponse.getData();
        assertFalse(draftsResponse.isDraft());
        assertTrue(data.get("submissionStarted").asBoolean());
        assertEquals(COURTS, data.get("courts").asText());
        assertEquals(CASE_ID, (Long) data.get("caseId").asLong());
        assertEquals(AWAITING_PAYMENT_STATUS, data.get("state").asText());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_multiple_cases_are_all_rejected() {

        // given
        CASE_DATA_1.put(CASE_STATE, REJECTED_STATUS);
        CASE_DATA_2.put(CASE_STATE, REJECTED_STATUS);

        LIST_OF_CASES.add(CASE_DATA_1);
        LIST_OF_CASES.add(CASE_DATA_2);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(LIST_OF_CASES);

        // then
        assertFalse(draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseDataShouldReturn1ValidResponseWhenOnly1CaseIsNotRejectedInMultipleCases() {
        // if only 1 case is not "Rejected" only return this non rejected case

        // given
        CASE_DETAILS.put("D8DivorceUnit", COURTS);

        CASE_DATA_1.put("state", REJECTED_STATUS);
        CASE_DATA_1.put("id", CASE_ID);
        CASE_DATA_1.put("case_data", CASE_DETAILS);

        CASE_DATA_2.put("state", AWAITING_PAYMENT_STATUS);
        CASE_DATA_2.put("id", CASE_ID);
        CASE_DATA_2.put("case_data", CASE_DETAILS);

        LIST_OF_CASES.add(CASE_DATA_1);
        LIST_OF_CASES.add(CASE_DATA_2);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(LIST_OF_CASES);

        // then
        JsonNode data = draftsResponse.getData();
        assertFalse(draftsResponse.isDraft());
        assertTrue(data.get("submissionStarted").asBoolean());
        assertEquals(AWAITING_PAYMENT_STATUS, data.get("state").asText());
    }

    @Test
    public void buildDraftResponseFromCaseData_shouldReturnCustomResponse_whenMultipleCasesAreNotRejected() {
        // if multiple cases are not "Rejected"  - Display new page at /contact-divorce-team 

        // given
        CASE_DETAILS.put("D8DivorceUnit", COURTS);

        CASE_DATA_1.put("state", AWAITING_PAYMENT_STATUS);
        CASE_DATA_1.put("id", CASE_ID);
        CASE_DATA_1.put("case_data", CASE_DETAILS);

        CASE_DATA_2.put("state", AWAITING_PAYMENT_STATUS);
        CASE_DATA_2.put("id", CASE_ID);
        CASE_DATA_2.put("case_data", CASE_DETAILS);

        LIST_OF_CASES.add(CASE_DATA_1);
        LIST_OF_CASES.add(CASE_DATA_2);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(LIST_OF_CASES);

        // then
        JsonNode data = draftsResponse.getData();
        assertFalse(draftsResponse.isDraft());
        assertTrue(data.get("submissionStarted").asBoolean());
        assertEquals(MULTIPLE_REJECTED_CASES_STATE, data.get("state").asText());
    }
}
