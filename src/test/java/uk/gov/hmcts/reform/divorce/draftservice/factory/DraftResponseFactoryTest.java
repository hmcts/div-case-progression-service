package uk.gov.hmcts.reform.divorce.draftservice.factory;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class DraftResponseFactoryTest {

    private static final String NOT_AWAITING_PAYMENT_STATUS = "notAwaitingPayment";
    private static final String CASE_STATUS = "status";

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_no_cases_in_awaiting_payment() {

        // given
        List<LinkedHashMap> listOfCases = new ArrayList<>();

        LinkedHashMap caseData = new LinkedHashMap();
        caseData.put(CASE_STATUS, NOT_AWAITING_PAYMENT_STATUS);

        listOfCases.add(caseData);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases);

        // then
        assertEquals(false, draftsResponse.isDraft());
        assertNull(draftsResponse.getData());
        assertNull(draftsResponse.getDraftId());
    }

    @Test
    public void buildDraftResponseFromCaseData_should_return_empty_response_when_multiple_cases_in_awaiting_payment() {

        // given
        List<LinkedHashMap> listOfCases = new ArrayList<>();

        LinkedHashMap caseData1 = new LinkedHashMap();
        caseData1.put(CASE_STATUS, NOT_AWAITING_PAYMENT_STATUS);

        LinkedHashMap caseData2 = new LinkedHashMap();
        caseData2.put(CASE_STATUS, NOT_AWAITING_PAYMENT_STATUS);

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
        List<LinkedHashMap> listOfCases = null;

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
        List<LinkedHashMap> listOfCases = Collections.emptyList();

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
        List<LinkedHashMap> listOfCases = new ArrayList<>();

        LinkedHashMap caseData1 = new LinkedHashMap();
        caseData1.put(CASE_STATUS, NOT_AWAITING_PAYMENT_STATUS);


        LinkedHashMap caseData2 = new LinkedHashMap();
        caseData2.put(CASE_STATUS, "awaitingPayment");
        Long caseId = 123L;
        caseData2.put("id", caseId);

        LinkedHashMap caseDetails = new LinkedHashMap();
        String courts = "courtsXYZz";
        caseDetails.put("D8DivorceUnit", courts);

        caseData2.put("case_data", caseDetails);

        listOfCases.add(caseData1);
        listOfCases.add(caseData2);

        // when
        DraftsResponse draftsResponse = DraftResponseFactory.buildDraftResponseFromCaseData(listOfCases);

        // then
        JsonNode data = draftsResponse.getData();
        assertEquals(false, draftsResponse.isDraft());
        assertEquals(true, data.get("submissionStarted").asBoolean());
        assertEquals(courts, data.get("courts").asText());
        assertEquals(caseId, (Long) data.get("case_id").asLong());
    }
}
