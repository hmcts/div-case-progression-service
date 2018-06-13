package uk.gov.hmcts.reform.divorce.caseprogression.draftsapi;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.client.Draft;
import uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.support.caseprogression.draftsapi.DraftBaseIntegrationTest;
import uk.gov.hmcts.reform.divorce.support.caseprogression.transformapi.TestUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SerenityRunner.class)
public class DraftsAPIIntegrationTest extends DraftBaseIntegrationTest {

    @Autowired
    protected DraftStoreClient draftStoreClient;

    @Value("${env}")
    private String environment;

    @Test
    public void shouldSaveTheDraftAndReturnOKWhenThereIsNoDraftSaved() {
        String draft = "{\"message\": \"Hello World!\"}";

        Response response = saveDivorceDraft(draft);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode());
        assertDraftIsSaved(draft);
    }

    @Test
    public void shouldUpdateTheDraftAndReturnOKWhenThereIsSavedDraft() {
        String savedDraft = "{\"message\": \"Draft!\"}";
        Response draftStoreResponse = draftStoreClient.createDraft(getIdamTestUser(), savedDraft);

        assertEquals(HttpStatus.CREATED.value(), draftStoreResponse.getStatusCode());
        String draft = "{\"message\": \"Hello World!\"}";
        saveDivorceDraft(draft);

        Response response = getDivorceDraft();

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertDraftIsSaved(draft);
    }

    @Test
    public void shouldReturn404WhenDraftDoesNotExist() {

        regenerateIdamTestUser();

        Response response = getDivorceDraft();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    @Test
    public void shouldReturnOKWhenDeletingADraft() {
        String draft = "{\"message\": \"Hello World!\"}";
        saveDivorceDraft(draft);

        Response response = deleteDivorceDraft();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode());
        assertThereAreNoDrafts();
    }

    @Test
    public void shouldReturnCaseDataIfDraftDoesNotExistButCaseExistsInCcd() throws Exception {

        // only execute on preview as feature toggle is currently only enabled on preview and prod
        if ("preview".equalsIgnoreCase(environment)) {
            // given
            Response caseSubmissionResponse = submitCase("addresses.json");

            // when
            Response draftResponse = getDivorceDraft();

            // then
            assertEquals(HttpStatus.OK.value(), draftResponse.getStatusCode());

            Long caseId = TestUtil.extractCaseId(caseSubmissionResponse);
            Long draftResponseCaseId = new Long(draftResponse.getBody().path("caseId").toString());
            Boolean submissionStarted = new Boolean(draftResponse.getBody().path("submissionStarted").toString());
            String courts = draftResponse.getBody().path("courts").toString();

            assertEquals(caseId, draftResponseCaseId);
            assertEquals("eastMidlands", courts);
            assertEquals(true, submissionStarted);
        }
    }

    @After
    public void tearDown() {
        deleteDivorceDraft();
    }

    private void assertDraftIsSaved(String draft) {
        List<Draft> drafts = draftStoreClient.getDivorceDrafts(getIdamTestUser());
        assertEquals(1, drafts.size());
        JSONAssert.assertEquals(draft, drafts.get(0).getDocument().toString(), false);
    }

    private void assertThereAreNoDrafts() {
        assertEquals(0, draftStoreClient.getDivorceDrafts(getIdamTestUser()).size());
    }
}
