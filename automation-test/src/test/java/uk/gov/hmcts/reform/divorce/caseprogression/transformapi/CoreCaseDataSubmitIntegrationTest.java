package uk.gov.hmcts.reform.divorce.caseprogression.transformapi;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.divorce.caseprogression.BaseIntegrationTest;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.reform.divorce.caseprogression.transformapi.TestUtil.assertOkResponseAndCaseIdIsNotZero;
import static uk.gov.hmcts.reform.divorce.caseprogression.transformapi.TestUtil.assertResponseErrorsAreAsExpected;

/**
 * The Class CoreCaseDataSubmitIntegration.
 */
@RunWith(SerenityRunner.class)
public class CoreCaseDataSubmitIntegrationTest extends BaseIntegrationTest {

    private static final String CASE_VALIDATION_EXCEPTION = "Request Id : null and Exception message : 422 , Exception response body: {\"exception\":\"uk.gov.hmcts.ccd.endpoint.exceptions.CaseValidationException\"";
    private static final String UNAUTHORISED_JWT_EXCEPTION = "Request Id : null and Exception message : 403  reading IdamApiClient#retrieveUserDetails(String)";

    /**
     * Verify case id with address json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidAddressesSessionData() throws Exception {

        Response ccdResponse = submitCase("addresses.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Verify case id with how name changed json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidHowNameChangedSessionData() throws Exception {

        Response ccdResponse = submitCase("how-name-changed.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Verify case id with jurisdiction 6 to 12 json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidJurisdiction6To12SessionData() throws Exception {

        Response ccdResponse = submitCase("jurisdiction-6-12.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Verify case id with jurisdiction all json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidJurisdictionAllSessionData() throws Exception {

        Response ccdResponse = submitCase("jurisdiction-all.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Verify case id with reason adultery json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidAdulterySessionData() throws Exception {

        Response ccdResponse = submitCase("reason-adultery.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Verify case id with reason desertion json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidDesertionSessionData() throws Exception {

        Response ccdResponse = submitCase("reason-desertion.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Verify case id with reason separation json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidSeparationSessionData() throws Exception {

        Response ccdResponse = submitCase("reason-separation.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Verify case id with reason unreasonable behaviour json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidUnreasonableBehaviourSessionData() throws Exception {

        Response ccdResponse = submitCase("reason-unreasonable-behaviour.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Verify case id with reason same sex json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidSameSexSessionData() throws Exception {

        Response ccdResponse = submitCase("same-sex.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Verify case id with reason d8 pdf document json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidD8DocumentSessionData() throws Exception {

        Response ccdResponse = submitCase("d8-document.json");

        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
    }

    /**
     * Incorrect data as per spreadsheet.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnErrorForInvalidSessionData() throws Exception {

        Response ccdResponse = submitCase("invalid-session.json");

        assertResponseErrorsAreAsExpected(ccdResponse, CASE_VALIDATION_EXCEPTION, "\"details\":{\"field_errors\":[{\"id\":\"D8DivorceWho\",\"message\":\"notAValidValue is not a valid value\"}]}");
    }

    /**
     * Invalid idam token.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnErrorForInvalidUserJwtToken() throws Exception {

        Response ccdResponse = given()
                .header("Authorization", getInvalidToken())
                .contentType("application/json")
                .body(loadJSON("reason-adultery.json"))
                .when()
                .post(getTransformationApiSubmitUrl())
                .andReturn();

        assertResponseErrorsAreAsExpected(ccdResponse, UNAUTHORISED_JWT_EXCEPTION, "");
    }

    /**
     * Without request body.
     */
    @Test
    public void shouldReturnBadRequestForNoRequestBody() {
        Response ccdResponse = postToRestService("", getTransformationApiSubmitUrl());

        assertEquals(Integer.valueOf(HttpStatus.BAD_REQUEST.toString()).intValue(), ccdResponse.getStatusCode());
    }
}
