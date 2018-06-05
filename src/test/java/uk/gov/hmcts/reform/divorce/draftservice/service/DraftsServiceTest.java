package uk.gov.hmcts.reform.divorce.draftservice.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static junit.framework.TestCase.assertNull;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.hmcts.reform.divorce.draftservice.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.draftservice.domain.CreateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.draftservice.domain.UpdateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.factory.DraftModelFactory;
import uk.gov.hmcts.reform.divorce.draftservice.factory.EncryptionKeyFactory;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class DraftsServiceTest {

    private static final String JWT = "Bearer hgsdja87wegqeuf...";
    private static final String SECRET = "secret";
    private static final String DRAFT_ID = "1";
    private static final String USER_ID = "60";

    @InjectMocks
    private DraftsService underTest;
    @Mock
    private DraftModelFactory modelFactory;
    @Mock
    private EncryptionKeyFactory keyFactory;
    @Mock
    private DraftStoreClient client;
    @Mock
    private DraftList draftList;
    @Mock
    private CreateDraft createDraft;
    @Mock
    private UpdateDraft updateDraft;
    @Mock
    private Draft draft;
    @Mock
    private UserService userService;
    @Mock
    private AwaitingPaymentCaseRetriever awaitingPaymentCaseRetriever;
    private JsonNode requestContent;


    @Before
    public void setUp() throws Exception {
        String requestContentAsString = FileUtils.readFileToString(
            new File(getClass().getResource("/fixtures/divorce/submit-request-body.json").toURI()),
            Charset.defaultCharset());

        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(requestContentAsString);

        when(client.getAll(JWT, SECRET)).thenReturn(draftList);

        when(draft.getId()).thenReturn(DRAFT_ID);
        when(draft.getDocument()).thenReturn(requestContent);

        when(draftList.getPaging()).thenReturn(new DraftList.PagingCursors(null));

        when(modelFactory.createDraft(requestContent)).thenReturn(createDraft);
        when(modelFactory.updateDraft(requestContent)).thenReturn(updateDraft);

        when(keyFactory.createEncryptionKey(USER_ID)).thenReturn(SECRET);

        when(userService.getUserDetails(JWT)).thenReturn(UserDetails.builder().id(USER_ID).build());
    }

    @Test
    public void saveDraftShouldNotCreateOrUpdateADraftIfAlreadyExistsInCCD() {

        Map<String, Object> caseData = new HashMap<>();
        caseData.put("state", "notAwaitingPayment");

        List<Map<String, Object>> cases = new ArrayList<>();
        cases.add(caseData);

        when(awaitingPaymentCaseRetriever.getCases(USER_ID, JWT)).thenReturn(cases);

        underTest.saveDraft(JWT, requestContent);

        verify(client, never())
            .createDraft(JWT, SECRET, createDraft);
        verify(client, never())
            .updateDraft(JWT, USER_ID, SECRET, updateDraft);
    }

    @Test
    public void saveDraftShouldCreateANewDraftIfTheDraftDoesNotExistInCCDOrInDraftstore() {
        when(awaitingPaymentCaseRetriever.getCases(USER_ID, JWT)).thenReturn(Collections.emptyList());
        when(draftList.getData()).thenReturn(Collections.emptyList());

        underTest.saveDraft(JWT, requestContent);

        verify(client)
            .createDraft(JWT, SECRET, createDraft);
        verify(client, times(0))
            .updateDraft(any(), any(), any(), any());
    }

    @Test
    public void saveDraftShouldOverrideTheExistingDraftIfADivorceDraftExistsInCCDAndInDraftstore() {
        when(awaitingPaymentCaseRetriever.getCases(USER_ID, JWT)).thenReturn(Collections.emptyList());
        when(draftList.getData()).thenReturn(Collections.singletonList(draft));
        when(modelFactory.isDivorceDraft(draft)).thenReturn(true);

        underTest.saveDraft(JWT, requestContent);

        verify(client)
            .updateDraft(JWT, DRAFT_ID, SECRET, updateDraft);
        verify(client, times(0))
            .createDraft(any(), any(), any());
    }

    @Test
    public void saveDraftShouldCreateANewDraftWhenADraftExistsButItIsNotADivorceDraft() {
        when(draftList.getData()).thenReturn(Collections.singletonList(draft));
        when(modelFactory.isDivorceDraft(draft)).thenReturn(false);

        underTest.saveDraft(JWT, requestContent);

        verify(client)
            .createDraft(JWT, SECRET, createDraft);
        verify(client, times(0))
            .updateDraft(any(), any(), any(), any());
    }

    @Test
    public void getDraftShouldReturnTheDraftContentWhenTheDraftExists() {
        when(draftList.getData()).thenReturn(Collections.singletonList(draft));
        when(modelFactory.isDivorceDraft(draft)).thenReturn(true);

        JsonNode draftsContent = underTest.getDraft(JWT);

        assertEquals(requestContent, draftsContent);
    }

    @Test
    public void getDraftShouldReturnNullWhenTheDraftDoesNotExist() {
        when(draftList.getData()).thenReturn(Collections.emptyList());

        JsonNode draftsContent = underTest.getDraft(JWT);

        assertNull(draftsContent);
    }

    @Test
    public void getDraftShouldReturnNullWhenADraftExistsButItIsNotADivorceDraft() {
        when(draftList.getData()).thenReturn(Collections.singletonList(draft));
        when(modelFactory.isDivorceDraft(draft)).thenReturn(false);

        JsonNode draftsContent = underTest.getDraft(JWT);

        assertNull(draftsContent);
    }

    @Test
    public void getDraftShouldGetADivorceDraftFromTheSecondPageOfDrafts() {
        when(draftList.getData()).thenReturn(Collections.singletonList(draft));
        when(modelFactory.isDivorceDraft(draft)).thenReturn(false);
        when(draftList.getPaging()).thenReturn(new DraftList.PagingCursors("10"));

        underTest.getDraft(JWT);

        verify(client).getAll(JWT, SECRET, "10");
    }

    @Test
    public void deleteDraftShouldDeleteTheDraftIfADivorceDraftExists() {
        when(draftList.getData()).thenReturn(Collections.singletonList(draft));
        when(modelFactory.isDivorceDraft(draft)).thenReturn(true);

        underTest.deleteDraft(JWT);

        verify(client).deleteDraft(JWT, DRAFT_ID);
    }

    @Test
    public void deleteDraftShouldNotDeleteAnythingIfThereAreNoDrafts() {
        when(draftList.getData()).thenReturn(Collections.emptyList());

        underTest.deleteDraft(JWT);

        verify(client, times(0))
            .deleteDraft(any(), any());
    }

    @Test
    public void deleteDraftShouldNotDeleteTheDraftIfItIsNotADivorceDraft() {
        when(draftList.getData()).thenReturn(Collections.singletonList(draft));
        when(modelFactory.isDivorceDraft(draft)).thenReturn(false);

        underTest.deleteDraft(JWT);

        verify(client, times(0))
            .deleteDraft(any(), any());
    }

}
