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
public class CoreCaseDataGetUserCaseIntegrationTest extends BaseIntegrationTest {


    /**
     * Verify case id with address json.
     *
     * @throws Exception Resource loading exception
     */
    @Test
    public void shouldReturnCaseIdForValidAddressesSessionData() throws Exception {

        Response ccdResponse = submitCase("addresses.json");
        assertOkResponseAndCaseIdIsNotZero(ccdResponse);
        String userWhoCreatedTheCase = idamUserSupport.getIdamUsername();


    }

}
