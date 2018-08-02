package uk.gov.hmcts.reform.divorce.caseprogression.petition;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.divorce.support.caseprogression.BaseIntegrationTest;
import uk.gov.hmcts.reform.divorce.support.caseprogression.transformapi.TestUtil;
import uk.gov.hmcts.reform.divorce.support.util.ResourceLoader;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SerenityRunner.class)
public class PetitionIntegrationTest extends BaseIntegrationTest {

    @Value("${petition.api.url}")
    private String petitionApiUrl;

    @Before
    public void setup() {
        regenerateIdamTestUser();
    }

    @Test
    public void getPetitionShouldReturnDivorceSessionFromCaseInAwaitingDecreeNisi() throws Exception {

        // given
        Long caseId = submitAndMakePayment();
        issueCaseAndUpdateWithAosReceived(caseId);

        // when
        Response petitionResponse = getFromRestService(petitionApiUrl, headers());

        // then
        assertEquals(200, petitionResponse.getStatusCode());
        ResponseBody petitionResponseBody = petitionResponse.getBody();
        assertEquals(String.valueOf(caseId), petitionResponseBody.path("caseId"));
        JSONAssert.assertEquals(loadJSON("divorce-session-converted-expected.json"),
            new ObjectMapper().writeValueAsString(petitionResponseBody.path("divorceCase")), false);
    }

    private void issueCaseAndUpdateWithAosReceived(Long caseId) throws Exception {
        Response issuedResponse = submitEvent(caseId, "issueFromSubmitted");
        assertNotNull(issuedResponse.getBody().path("id"));

        Response aosReceivedResponse = submitEvent(caseId, "aosReceived");
        assertNotNull(aosReceivedResponse.getBody().path("id"));
    }

    @Test
    public void getPetitionShouldReturnNotFoundWhenUserHasMultipleIncompleteCases() throws Exception {

        // given
        submitCase("divorce-session.json");
        submitCase("divorce-session.json");

        // when
        Response petitionResponse = getFromRestService(petitionApiUrl, headers());

        // then
        assertEquals(404, petitionResponse.getStatusCode());
    }

    @Test
    public void getPetitionShouldReturnNotFoundWhenNoCasesFound() throws Exception {

        // given

        // when
        Response petitionResponse = getFromRestService(petitionApiUrl, headers());

        // then
        assertEquals(404, petitionResponse.getStatusCode());
    }

    private Long submitAndMakePayment() throws Exception {
        Long caseId = TestUtil.extractCaseId(submitCase("divorce-session.json"));

        Response paymentMadeResponse = submitEvent(caseId, "paymentMade");
        assertNotNull(paymentMadeResponse.getBody().path("id"));
        return caseId;
    }
}
