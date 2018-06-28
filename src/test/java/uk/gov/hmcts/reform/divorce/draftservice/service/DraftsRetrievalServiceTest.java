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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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
    private CaseRetriever mockCaseRetriever;
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
    public void setUp() throws Exception {

        underTest = new DraftsRetrievalService(mockModelFactory,
                mockDraftStoreClient,
            mockCaseRetriever);

        when(draftList.getPaging()).thenReturn(new DraftList.PagingCursors(null));

        when(mockModelFactory.createDraft(mockData)).thenReturn(createDraft);
        when(mockModelFactory.updateDraft(mockData)).thenReturn(updateDraft);
    }

    @Test
    public void getDraftShouldReturnTheDraftContentWhenTheDraftExists() {

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
        when(mockCaseRetriever
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
}
