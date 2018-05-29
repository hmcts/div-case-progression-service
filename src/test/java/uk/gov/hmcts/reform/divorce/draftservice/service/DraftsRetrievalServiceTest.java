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
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftResponseFactory;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
    private RetrieveCcdClient mockRetrieveCcdClient;

    @Mock
    private DraftStoreClient mockDraftStoreClient;
    @Mock
    private DraftList draftList;
    @Mock
    private CreateDraft createDraft;
    @Mock
    private UpdateDraft updateDraft;
    @Mock
    private Draft mockDraft;

    private DraftResponseFactory draftResponseFactory;

    @Mock
    private JsonNode mockData;

    private DraftsRetrievalService underTest;

    @Before
    public void setUp() throws Exception {


        boolean checkCcdEnabled = true;
        underTest = new DraftsRetrievalService(mockModelFactory,
                mockDraftStoreClient,
                mockRetrieveCcdClient,
                checkCcdEnabled);

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

        LinkedHashMap caseData = new LinkedHashMap();
        String courts = "courtsXYZz";
        caseData.put("D8DivorceUnit", courts);

        LinkedHashMap ccdResponseData = new LinkedHashMap();
        Long caseId = 123L;
        ccdResponseData.put("id", caseId);
        ccdResponseData.put("case_data", caseData);

        List<LinkedHashMap> listOfCases = new ArrayList<>();
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
        assertEquals(caseId, (Long) data.get("case_id").asLong());
    }

    @Test
    public void getDraftShouldReturnNullWhenTheDraftDoesNotExistAndCCdCheckDisabled() {

        // given
        when(mockDraftStoreClient.getAll(JWT, SECRET)).thenReturn(draftList);
        when(draftList.getData()).thenReturn(Collections.emptyList());

        boolean checkCcdEnabled = false;
        underTest = new DraftsRetrievalService(mockModelFactory,
                mockDraftStoreClient,
                mockRetrieveCcdClient,
                checkCcdEnabled);

        // when
        DraftsResponse draftsResponse = underTest.getDraft(JWT, USER_ID, SECRET);

        // then
        assertNull(draftsResponse);
        verifyZeroInteractions(mockRetrieveCcdClient);
    }

    @Test
    public void getDraftShouldReturnNullWhenADraftExistsButItIsNotADivorceDraft() {

        // given
        when(mockDraftStoreClient.getAll(JWT, SECRET)).thenReturn(draftList);
        when(draftList.getData()).thenReturn(Collections.singletonList(mockDraft));
        when(mockModelFactory.isDivorceDraft(mockDraft)).thenReturn(false);

        boolean checkCcdEnabled = false;
        underTest = new DraftsRetrievalService(mockModelFactory,
                mockDraftStoreClient,
                mockRetrieveCcdClient,
                checkCcdEnabled);

        // when
        DraftsResponse draftsResponse = underTest.getDraft(JWT, USER_ID, SECRET);

        // then
        assertNull(draftsResponse);
        verify(mockModelFactory).isDivorceDraft(mockDraft);
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
