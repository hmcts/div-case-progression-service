package uk.gov.hmcts.reform.divorce.caseprogression.transformapi;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.divorce.caseprogression.BaseIntegrationTest;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.reform.divorce.caseprogression.transformapi.TestUtil.assertOkResponseAndCaseIdIsNotZero;
import static uk.gov.hmcts.reform.divorce.caseprogression.transformapi.TestUtil.assertResponseErrorsAreAsExpected;

@RunWith(SerenityRunner.class)
public class CoreCaseDataUpdateIntegrationTest extends BaseIntegrationTest {

    private static final String URL_SEPARATOR = "/";
    private static final String VALIDATION_EXCEPTION = "Request Id : null and Exception message : 422 , Exception response body: {\"exception\":\"uk.gov.hmcts.ccd.endpoint.exceptions.ValidationException\"";
    private static final String CASE_VALIDATION_EXCEPTION = "Request Id : null and Exception message : 422 , Exception response body: {\"exception\":\"uk.gov.hmcts.ccd.endpoint.exceptions.CaseValidationException\"";
    private static final String UNAUTHORISED_JWT_EXCEPTION = "Request Id : null and Exception message : 403";
    private static final String BAD_REQUEST_EXCEPTION = "Request Id : null and Exception message : 400";
    private static final String RESOURCE_NOT_FOUND_EXCEPTION = "Request Id : null and Exception message : 404 , Exception response body: {\"exception\":\"uk.gov.hmcts.ccd.endpoint.exceptions.ResourceNotFoundException\"";

    private String transformationApiSubmitUrl;
    private String transformationApiUpdateUrl;

    @Before
    public void setUp() {
        transformationApiSubmitUrl = getTransformationApiUrl().concat(getTransformationApiSubmitEndpoint());
        transformationApiUpdateUrl = getTransformationApiUrl().concat(getTransformationApiUpdateEndpoint());
    }

    @Test
    public void shouldReturnCaseIdWhenUpdatingDataAfterInitialSubmit() throws Exception {

        String caseId = getCaseIdFromSubmittingANewCase();

        Response ccdResponse = postToRestService(loadJSON("update-addresses.json"),
                String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    @Test
    public void shouldReturnCaseIdWhenUpdatingPaymentAfterUpdatingWithPaymentReference() throws Exception {

        String caseId = getCaseIdFromSubmittingANewCase();

        postToRestService(loadJSON("update-payment-reference.json"), String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));

        Response ccdResponse = postToRestService(loadJSON("payment-update.json"), String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    @Test
    public void shouldReturnErrorWhenUpdatingDataWithInvalidRequestBody() throws Exception {

        String caseId = getCaseIdFromSubmittingANewCase();

        Response ccdResponse = postToRestService(loadJSON("addresses.json"), String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));

        assertResponseErrorsAreAsExpected(ccdResponse, RESOURCE_NOT_FOUND_EXCEPTION, "\"message\":\"Cannot findCaseEvent event null for case type DIVORCE\"");
    }

    @Test
    public void shouldReturnNotFoundForNoCaseId() throws Exception {

        Response ccdResponse = postToRestService(loadJSON("payment-update.json"), transformationApiUpdateUrl);

        assertEquals(Integer.valueOf(HttpStatus.NOT_FOUND.toString()).intValue(), ccdResponse.getStatusCode());

        assertEquals("Not Found", ccdResponse.getBody().path("error").toString());
    }

    @Test
    public void shouldReturnBadRequestForNonExistingCaseReference() throws Exception {

        Response ccdResponse = postToRestService(loadJSON("payment-update.json"), String.join(URL_SEPARATOR, transformationApiUpdateUrl, "123"));

        assertResponseErrorsAreAsExpected(ccdResponse, BAD_REQUEST_EXCEPTION, "\"message\":\"Case reference is not valid\"");
    }

    @Test
    public void shouldReturnErrorForInvalidEventId() throws Exception {

        String caseId = getCaseIdFromSubmittingANewCase();

        Response ccdResponse = postToRestService(loadJSON("update-invalid-event-id.json"), String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));

        assertResponseErrorsAreAsExpected(ccdResponse, RESOURCE_NOT_FOUND_EXCEPTION, "\"message\":\"Cannot findCaseEvent event invalidId for case type DIVORCE\"");
    }

    @Test
    public void shouldReturnErrorUpdatingWithCaseSameEventId() throws Exception {

        String caseId = getCaseIdFromSubmittingANewCase();

        String updatePaymentJson = loadJSON("payment-update.json");
        postToRestService(updatePaymentJson, String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));
        Response ccdResponse = postToRestService(updatePaymentJson, String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));

        assertResponseErrorsAreAsExpected(ccdResponse, VALIDATION_EXCEPTION, "\"message\":\"The case status did not qualify for the event\"");
    }

    @Test
    public void shouldReturnErrorUpdatingWithCaseWrongFlowEventId() throws Exception {

        String caseId = getCaseIdFromSubmittingANewCase();

        Response ccdResponse = postToRestService(loadJSON("update-with-pending-rejection.json"), String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));

        assertResponseErrorsAreAsExpected(ccdResponse, VALIDATION_EXCEPTION, "\"message\":\"The case status did not qualify for the event\"");
    }

    @Test
    public void shouldReturnErrorForInvalidSessionData() throws Exception {

        String caseId = getCaseIdFromSubmittingANewCase();

        Response ccdResponse = postToRestService(loadJSON("invalid-update-session.json"), String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));

        assertResponseErrorsAreAsExpected(ccdResponse, CASE_VALIDATION_EXCEPTION, "\"details\":{\"field_errors\":[{\"id\":\"D8DivorceWho\",\"message\":\"notAValidValue is not a valid value\"}]}");
    }

    @Test
    public void shouldReturnErrorForInvalidUserJwtToken() throws Exception {

        String caseId = getCaseIdFromSubmittingANewCase();

        Response ccdResponse = given()
                .header("Authorization", getInvalidToken())
                .contentType("application/json")
                .body(loadJSON("update-addresses.json"))
                .when()
                .post(String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId))
                .andReturn();

        assertResponseErrorsAreAsExpected(ccdResponse, UNAUTHORISED_JWT_EXCEPTION, "");
    }

    @Test
    public void shouldReturnBadRequestForNoRequestBody() throws Exception {

        String caseId = getCaseIdFromSubmittingANewCase();

        Response ccdResponse = postToRestService("", String.join(URL_SEPARATOR, transformationApiUpdateUrl, caseId));

        assertEquals(Integer.valueOf(HttpStatus.BAD_REQUEST.toString()).intValue(), ccdResponse.getStatusCode());
    }

    private String getCaseIdFromSubmittingANewCase() throws Exception {
        return postToRestService(loadJSON("addresses.json"), transformationApiSubmitUrl)
                .getBody().path("caseId").toString();
    }
}
