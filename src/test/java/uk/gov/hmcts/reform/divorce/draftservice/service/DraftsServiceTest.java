package uk.gov.hmcts.reform.divorce.draftservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DraftsServiceTest {

    private static final String JWT = "Bearer hgsdja87wegqeuf...";
    private static final String SECRET = "secret";
    private static final String DRAFT_ID = "1";

    @Mock
    private DraftModelFactory modelFactory;

    @Mock
    private EncryptionKeyFactory keyFactory;

    @Mock
    private DraftStoreClient client;

    @InjectMocks
    private DraftsService underTest;

    @Mock
    private DraftList draftList;

    @Mock
    private CreateDraft createDraft;

    @Mock
    private UpdateDraft updateDraft;

    @Mock
    private Draft draft;

    private JsonNode requestContent;


    @Before
    public void setUp() throws Exception {
        String requestContentAsString = FileUtils.readFileToString(
            new File(getClass().getResource("/fixtures/divorce/submit-request-body.json").toURI()),
            Charset.defaultCharset());

        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(requestContentAsString);

        when(client.getAll(JWT, SECRET)).thenReturn(draftList);

        when(draftList.getPaging()).thenReturn(new DraftList.PagingCursors(null));

        when(modelFactory.createDraft(requestContent)).thenReturn(createDraft);
        when(modelFactory.updateDraft(requestContent)).thenReturn(updateDraft);

        when(keyFactory.createEncryptionKey(JWT)).thenReturn(SECRET);


        when(draft.getId()).thenReturn(DRAFT_ID);
        when(draft.getDocument()).thenReturn(requestContent);
    }

    @Test
    public void saveDraftShouldCreateANewDraftIfTheDraftDoesNotExist() {
        when(draftList.getData()).thenReturn(Collections.emptyList());

        underTest.saveDraft(JWT, requestContent);

        verify(client)
            .createDraft(JWT, SECRET, createDraft);
        verify(client, times(0))
            .updateDraft(any(), any(), any(), any());
    }

    @Test
    public void saveDraftShouldOverrideTheExistingDraftIfADivorceDraftExists() {
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
    public void getDraftShouldReturnTheDraftContentWhenTheDraftExists() throws IOException {
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
