package uk.gov.hmcts.reform.divorce.draftservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.draftservice.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.draftservice.domain.CreateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftsResponse;
import uk.gov.hmcts.reform.divorce.draftservice.domain.UpdateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftModelFactory;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DraftsRetrievalServiceTest {

    private static final String JWT = "Bearer hgsdja87wegqeuf...";
    private static final String SECRET = "secret";
    private static final String DRAFT_ID = "1";
    private static final String USER_ID = "60";

    @Mock
    private DraftModelFactory mockModelFactory;
    @Mock
    private DraftStoreClient mockDraftStoreClient;
    @Mock
    private RetrieveCcdClient mockRetrieveCcdClient;
    @Mock
    private DraftList draftList;
    @Mock
    private CreateDraft createDraft;
    @Mock
    private UpdateDraft updateDraft;
    @Mock
    private Draft mockDraft;

    @Mock
    private JsonNode mockData;

    private DraftsRetrievalService underTest;

    @Before
    public void setUp() {

        underTest = new DraftsRetrievalService(mockModelFactory,
            mockDraftStoreClient,
            mockRetrieveCcdClient);

        when(draftList.getPaging()).thenReturn(new DraftList.PagingCursors(null));

        when(mockModelFactory.createDraft(mockData)).thenReturn(createDraft);
        when(mockModelFactory.updateDraft(mockData)).thenReturn(updateDraft);
    }

    @Test
    public void getDraftShouldReturnTheDraftContentWhenTheDraftExistsAndCaseDoesnt() {

        // given
        when(mockDraftStoreClient.getAll(JWT, SECRET)).thenReturn(draftList);
        when(draftList.getData()).thenReturn(Collections.singletonList(mockDraft));
        when(mockDraft.getId()).thenReturn(DRAFT_ID);
        when(mockDraft.getDocument()).thenReturn(mockData);
        when(mockModelFactory.isDivorceDraft(mockDraft)).thenReturn(true);

        // when
        DraftsResponse draftsResponse = underTest.getDraft(JWT, USER_ID, SECRET);

        // then
        assertEquals(mockData, draftsResponse.getData());
        assertEquals(true, draftsResponse.isDraft());
        assertEquals(DRAFT_ID, draftsResponse.getDraftId());
    }

    @Test
    public void getDraftShouldReturnResponseWhenDraftNotFoundButCaseIsFound() {

        // given
        when(mockDraftStoreClient.getAll(JWT, SECRET)).thenReturn(null);

        Map<String, Object> caseData = new HashMap();
        String courts = "courtsXYZz";
        caseData.put("D8DivorceUnit", courts);

        Map<String, Object> ccdResponseData = new HashMap();
        Long caseId = 123L;
        ccdResponseData.put("id", caseId);
        ccdResponseData.put("state", "awaitingPayment");
        ccdResponseData.put("case_data", caseData);

        List<Map<String, Object>> listOfCases = new ArrayList<>();
        listOfCases.add(ccdResponseData);
        when(mockRetrieveCcdClient
            .getCases(USER_ID, JWT))
            .thenReturn(listOfCases);

        // when
        DraftsResponse draftsResponse = underTest.getDraft(JWT, USER_ID, SECRET);

        // then
        JsonNode data = draftsResponse.getData();
        assertEquals(false, draftsResponse.isDraft());
        assertEquals(true, data.get("submissionStarted").asBoolean());
        assertEquals(courts, data.get("courts").asText());
        assertEquals(caseId, (Long) data.get("caseId").asLong());
    }

    @Test
    public void getDraftShouldReturnCaseWhenBothCaseAndDraftExist() {

        //given
        when(mockDraftStoreClient.getAll(JWT, SECRET)).thenReturn(draftList);
        when(draftList.getData()).thenReturn(Collections.singletonList(mockDraft));
        when(mockDraft.getId()).thenReturn(DRAFT_ID);
        when(mockDraft.getDocument()).thenReturn(mockData);
        when(mockModelFactory.isDivorceDraft(mockDraft)).thenReturn(true);

        Map<String, Object> caseData = new HashMap();
        String courts = "courtsXYZz";
        caseData.put("D8DivorceUnit", courts);

        Map<String, Object> ccdResponseData = new HashMap();
        Long caseId = 123L;
        ccdResponseData.put("id", caseId);
        ccdResponseData.put("state", "awaitingPayment");
        ccdResponseData.put("case_data", caseData);

        List<Map<String, Object>> listOfCases = new ArrayList<>();
        listOfCases.add(ccdResponseData);
        when(mockRetrieveCcdClient
            .getCases(USER_ID, JWT))
            .thenReturn(listOfCases);

        // when
        DraftsResponse draftsResponse = underTest.getDraft(JWT, USER_ID, SECRET);

        // then
        JsonNode data = draftsResponse.getData();
        assertEquals(false, draftsResponse.isDraft());
        assertEquals(true, data.get("submissionStarted").asBoolean());
        assertEquals(courts, data.get("courts").asText());
        assertEquals(caseId, (Long) data.get("caseId").asLong());
        //should not call draft at all since case has been found
        verify(mockDraftStoreClient, never()).getAll(JWT, SECRET);
    }

    @Test
    public void getDraftShouldGetADivorceDraftFromTheSecondPageOfDrafts() {

        // given
        when(mockDraftStoreClient.getAll(JWT, SECRET)).thenReturn(draftList);
        when(draftList.getData()).thenReturn(Collections.singletonList(mockDraft));
        when(mockModelFactory.isDivorceDraft(mockDraft)).thenReturn(false);
        when(draftList.getPaging()).thenReturn(new DraftList.PagingCursors("10"));

        // when
        underTest.getDraft(JWT, USER_ID, SECRET);

        // then
        verify(mockDraftStoreClient).getAll(JWT, SECRET, "10");
    }

    @Test
    public void getDraftShouldCorrectlyFilterNonRejectedCasesFromRetrievedCasesFromCCD() {

        //given
        when(mockDraftStoreClient.getAll(JWT, SECRET)).thenReturn(draftList);
        when(draftList.getData()).thenReturn(Collections.singletonList(mockDraft));
        when(mockDraft.getId()).thenReturn(DRAFT_ID);
        when(mockDraft.getDocument()).thenReturn(mockData);
        when(mockModelFactory.isDivorceDraft(mockDraft)).thenReturn(true);

        Map<String, Object> caseData = new HashMap();
        String courts = "courtsXYZz";
        caseData.put("D8DivorceUnit", courts);
        Long caseId = 123L;

        Map<String, Object> ccdResponseDataSet1 = new HashMap();
        ccdResponseDataSet1.put("id", caseId);
        ccdResponseDataSet1.put("state", "awaitingPayment");
        ccdResponseDataSet1.put("case_data", caseData);

        Map<String, Object> ccdResponseDataSet2 = new HashMap();
        ccdResponseDataSet2.put("id", caseId);
        ccdResponseDataSet2.put("state", "awaitingPayment");
        ccdResponseDataSet2.put("case_data", caseData);

        Map<String, Object> ccdResponseDataSet3 = new HashMap();
        ccdResponseDataSet3.put("id", caseId);
        ccdResponseDataSet3.put("state", "issued");
        ccdResponseDataSet3.put("case_data", caseData);

        Map<String, Object> ccdResponseDataSet4 = new HashMap();
        ccdResponseDataSet4.put("id", caseId);
        ccdResponseDataSet4.put("state", "rejected");
        ccdResponseDataSet4.put("case_data", caseData);

        List<Map<String, Object>> listOfCases = new ArrayList<>();
        listOfCases.add(ccdResponseDataSet1);
        listOfCases.add(ccdResponseDataSet2);
        listOfCases.add(ccdResponseDataSet3);
        listOfCases.add(ccdResponseDataSet4);
        when(mockRetrieveCcdClient
            .getCases(USER_ID, JWT))
            .thenReturn(listOfCases);

        // when
        List<Map<String, Object>> listOfNonRejectedCasesInCCD = underTest.getAllNonRejectedCases(listOfCases);

        // then
        assertEquals(3, listOfNonRejectedCasesInCCD.size());
    }
}
