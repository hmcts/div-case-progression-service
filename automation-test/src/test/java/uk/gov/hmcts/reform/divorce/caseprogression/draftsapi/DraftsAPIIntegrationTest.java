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

    @Value("${draft_check_ccd_enabled}")
    private String draftCheckCCdFeatureEnabled;

    @Test
    public void shouldSaveTheDraftAndReturnOKWhenThereIsNoDraftSaved() {
        String draft = "{\"message\": \"Hello World!\"}";
        String token = generateNewUserAndReturnToken();
        Response response = saveDivorceDraft(token, draft);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode());
        assertDraftIsSaved(token, draft);
    }

    @Test
    public void shouldUpdateTheDraftAndReturnOKWhenThereIsSavedDraft() {
        String savedDraft = "{\"message\": \"Draft!\"}";
        String token = generateNewUserAndReturnToken();
        Response draftStoreResponse = draftStoreClient.createDraft(token, savedDraft);

        assertEquals(HttpStatus.CREATED.value(), draftStoreResponse.getStatusCode());
        String draft = "{\"message\": \"Hello World!\"}";
        saveDivorceDraft(token, draft);

        Response response = getDivorceDraft(token);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertDraftIsSaved(token, draft);
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
    public void shouldReturnCaseStateAsSubmittedOncePaymentHasBeenMade() throws Exception {

        System.out.println("Testing testing" + draftCheckCCdFeatureEnabled);

        // only execute on preview as feature toggle is currently only enabled on preview and prod
        if ("true".equalsIgnoreCase(draftCheckCCdFeatureEnabled)) {
            System.out.println("Executing shouldReturnCaseStateAsSubmittedOncePaymentHasBeenMade");

            // given
            regenerateIdamTestUser();
            Long caseId = TestUtil.extractCaseId(submitCase("addresses.json"));
            String updateUrl = getTransformationApiUrl() + getTransformationApiUpdateEndpoint() + "/" + caseId;
            postToRestService(loadJSON("payment-made.json"), updateUrl);

            // when
            Response draftResponse = getDivorceDraft();

            // then
            assertEquals(HttpStatus.OK.value(), draftResponse.getStatusCode());
            Long draftResponseCaseId = new Long(draftResponse.getBody().path("caseId").toString());
            String caseState = draftResponse.getBody().path("state").toString();

            assertEquals(caseId, draftResponseCaseId);
            assertEquals("Submitted", caseState);
        }
    }

    @Test
    public void shouldReturnCaseDataIfDraftDoesNotExistButCaseExistsInCcd() throws Exception {

        // only execute on preview as feature toggle is currently only enabled on preview and prod
        if ("true".equalsIgnoreCase(draftCheckCCdFeatureEnabled)) {
            // given
            regenerateIdamTestUser(); // generate a new idam user so previous test cases don't affect this one
            Response caseSubmissionResponse = submitCase("addresses.json");

            // when
            Response draftResponse = getDivorceDraft();

            // then
            assertEquals(HttpStatus.OK.value(), draftResponse.getStatusCode());

            Long caseId = TestUtil.extractCaseId(caseSubmissionResponse);
            Long draftResponseCaseId = new Long(draftResponse.getBody().path("caseId").toString());
            Boolean submissionStarted = Boolean.valueOf(draftResponse.getBody().path("submissionStarted").toString());
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

    private void assertDraftIsSaved(String token, String draft) {
        List<Draft> drafts = draftStoreClient.getDivorceDrafts(token);
        assertEquals(1, drafts.size());
        JSONAssert.assertEquals(draft, drafts.get(0).getDocument().toString(), false);
    }

    private void assertThereAreNoDrafts() {
        assertEquals(0, draftStoreClient.getDivorceDrafts(getIdamTestUser()).size());
    }
}
