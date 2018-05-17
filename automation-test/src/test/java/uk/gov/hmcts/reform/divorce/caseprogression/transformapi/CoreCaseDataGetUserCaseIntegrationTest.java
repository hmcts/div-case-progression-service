package uk.gov.hmcts.reform.divorce.caseprogression.transformapi;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.reform.divorce.caseprogression.BaseIntegrationTest;

import static uk.gov.hmcts.reform.divorce.caseprogression.transformapi.TestUtil.assertOkResponseAndCaseIdIsNotZero;

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

        /*
        get jwt token for user just created
        then use this JWT to search for case
         */
    }

}
