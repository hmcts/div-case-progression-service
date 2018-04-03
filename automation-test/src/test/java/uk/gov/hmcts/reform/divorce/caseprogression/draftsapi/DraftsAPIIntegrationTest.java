package uk.gov.hmcts.reform.divorce.caseprogression.draftsapi;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.divorce.auth.BaseIntegrationTestWithIdamSupport;
import uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.client.Draft;
import uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.client.DraftStoreClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SerenityRunner.class)
public class DraftsAPIIntegrationTest extends BaseIntegrationTestWithIdamSupport {

    /**
     * The endpoint route for submitting a case
     */
    @Value("${drafts.api.url}")
    private String draftsApiUrl;

    @Autowired
    private DraftStoreClient draftStoreClient;

    @Test
    @WithTag("test-type:smoke")
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

    /**
     * Setup headers required for POST to CCD
     *
     * @return map of objects
     */
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
