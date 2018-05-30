package uk.gov.hmcts.reform.divorce.caseprogression.draftsapi;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.divorce.auth.BaseIntegrationTestWithIdamSupport;
import uk.gov.hmcts.reform.divorce.caseprogression.BaseIntegrationTest;
import uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.client.Draft;
import uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.client.DraftStoreClient;
import uk.gov.hmcts.reform.divorce.caseprogression.transformapi.TestUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SerenityRunner.class)
public class DraftsAPIIntegrationTest extends BaseIntegrationTest {

    @Value("${drafts.api.url}")
    private String draftsApiUrl;

    @Autowired
    private DraftStoreClient draftStoreClient;

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
        draftStoreClient.createDraft(getIdamTestUser(), savedDraft);

        String draft = "{\"message\": \"Hello World!\"}";
        saveDivorceDraft(draft);

        Response response = getDivorceDraft();

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertDraftIsSaved(draft);
    }

    @Test
    public void shouldReturn404WhenDraftDoesNotExist() {
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
    public void should_return_case_data_if_draft_does_not_exist_but_case_exists_in_ccd() throws Exception {

        // given
        Response caseSubmissionResponse = submitCase("addresses.json");

        // when
        Response draftResponse = getDivorceDraft();

        // then
        Long caseId = TestUtil.extractCaseId(caseSubmissionResponse);
        Long draftResponseCaseId = new Long(draftResponse.getBody().path("case_id").toString());
        Boolean submissionStarted = new Boolean(draftResponse.getBody().path("submissionStarted").toString());
        String courts = draftResponse.getBody().path("courts").toString();

        assertEquals(caseId, draftResponseCaseId);
        assertEquals("eastMidlands", courts);
        assertEquals(true, submissionStarted);

    }

    @After
    public void tearDown() {
        deleteDivorceDraft();
    }

    private Response deleteDivorceDraft() {
        return SerenityRest.given()
                .headers(headers())
                .when()
                .delete(draftsApiUrl)
                .andReturn();
    }

    private Response saveDivorceDraft(String draft) {
        return SerenityRest.given()
                .headers(headers())
                .body(draft)
                .when()
                .put(draftsApiUrl)
                .andReturn();
    }

    private Response getDivorceDraft() {
        return SerenityRest.given()
                .headers(headers())
                .when()
                .get(draftsApiUrl)
                .andReturn();
    }

    private Map<String, Object> headers() {
        return headers(getIdamTestUser());
    }

    private Map<String, Object> headers(String token) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.put("Authorization", token);
        return headers;
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
