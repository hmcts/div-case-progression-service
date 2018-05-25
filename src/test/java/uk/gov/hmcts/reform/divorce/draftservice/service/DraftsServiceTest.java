package uk.gov.hmcts.reform.divorce.draftservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DraftsServiceTest {

    private static final String JWT = "Bearer hgsdja87wegqeuf...";
    private static final String SECRET = "secret";
    private static final String DRAFT_ID = "1";
    private static final String USER_ID = "60";

    @Mock
    private DraftList draftList;

    @Mock
    private DraftStoreClient mockDraftStoreClient;

    @Mock
    private CreateDraft createDraft;

    @Mock
    private DraftModelFactory mockDraftModelFactory;

    @Mock
    private EncryptionKeyFactory mockEncryptionKeyFactory;

    @Mock
    private UpdateDraft updateDraft;

    @Mock
    private DraftsRetrievalService mockDraftsRetrievalService;

    @Mock
    private Draft draft;

    @Mock
    private UserService mockUserService;

    private JsonNode requestContent;

    private DraftsService underTest;

    @Before
    public void setUp() throws Exception {

        String requestContentAsString = FileUtils.readFileToString(
                new File(getClass().getResource("/fixtures/divorce/submit-request-body.json").toURI()),
                Charset.defaultCharset());

        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(requestContentAsString);

        when(mockDraftStoreClient.getAll(JWT, SECRET)).thenReturn(draftList);

        when(mockEncryptionKeyFactory.createEncryptionKey(USER_ID)).thenReturn(SECRET);

        when(draft.getId()).thenReturn(DRAFT_ID);
        when(draft.getDocument()).thenReturn(requestContent);

        when(mockUserService.getUserDetails(JWT)).thenReturn(UserDetails.builder().id(USER_ID).build());

        underTest = new DraftsService(mockDraftsRetrievalService, mockUserService, mockEncryptionKeyFactory, mockDraftStoreClient,
                mockDraftModelFactory);
    }

    @Test
    public void saveDraftShouldCreateANewDraftIfTheDraftDoesNotExist() {
        when(mockDraftsRetrievalService.getDivorceDraft(JWT, SECRET))
                .thenReturn(Optional.empty());

        when(draftList.getData()).thenReturn(Collections.emptyList());

        underTest.saveDraft(JWT, requestContent);

        verify(mockDraftStoreClient)
                .createDraft(JWT, SECRET, createDraft);
        verify(mockDraftStoreClient, times(0))
                .updateDraft(any(), any(), any(), any());
    }

    @Test
    public void saveDraftShouldOverrideTheExistingDraftIfADivorceDraftExists() {

        when(mockDraftsRetrievalService.getDivorceDraft(JWT, SECRET))
                .thenReturn(Optional.of(draft));
        when(mockDraftModelFactory.isDivorceDraft(draft)).thenReturn(true);

        underTest.saveDraft(JWT, requestContent);

        verify(mockDraftStoreClient)
                .updateDraft(JWT, DRAFT_ID, SECRET, updateDraft);
        verify(mockDraftStoreClient, times(0))
                .createDraft(any(), any(), any());
    }

    @Test
    public void deleteDraftShouldDeleteTheDraftIfADivorceDraftExists() {
        when(mockDraftsRetrievalService.getDivorceDraft(JWT, SECRET))
                .thenReturn(Optional.of(draft));
        when(mockDraftModelFactory.isDivorceDraft(draft)).thenReturn(true);

        underTest.deleteDraft(JWT);

        verify(mockDraftStoreClient).deleteDraft(JWT, DRAFT_ID);
    }

    @Test
    public void deleteDraftShouldNotDeleteAnythingIfThereAreNoDrafts() {
        when(mockDraftsRetrievalService.getDivorceDraft(JWT, SECRET))
                .thenReturn(Optional.empty());

        underTest.deleteDraft(JWT);

        verify(mockDraftStoreClient, times(0))
                .deleteDraft(any(), any());
    }
}
