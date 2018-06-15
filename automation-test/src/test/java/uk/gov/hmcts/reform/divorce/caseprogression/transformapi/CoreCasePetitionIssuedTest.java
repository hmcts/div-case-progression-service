package uk.gov.hmcts.reform.divorce.caseprogression.transformapi;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.divorce.caseprogression.BaseIntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SerenityRunner.class)
public class CoreCasePetitionIssuedTest extends BaseIntegrationTest {

    private String petitionIssuedApiUrl;

    @Before
    public void setUp() {
        petitionIssuedApiUrl = getTransformationApiUrl().concat(getTransformationApiGeneratePdfEndpoint());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnSuccessWhenApdfIsCreated() throws Exception {

        Response caseProgressionResponse = postToRestService(loadJSON("ccd-callback-petition-issued.json"), petitionIssuedApiUrl);

        assertThat(caseProgressionResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertNotNull(caseProgressionResponse.getBody().path("data.D8DivorceWho"));
        assertNotNull(caseProgressionResponse.getBody().path("data.D8DocumentsGenerated"));
        assertThat((List<String>) caseProgressionResponse.getBody().path("errors")).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnResponseWithErrorsWhenThereInvalidD8MarriageDate() throws Exception {

        Response caseProgressionResponse = postToRestService(loadJSON("ccd-callback-invalid-marriage-date.json"), petitionIssuedApiUrl);

        assertThat(caseProgressionResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertNotNull(caseProgressionResponse.getBody().path("data.D8DivorceWho"));
        assertThat((List<String>) caseProgressionResponse.getBody().path("errors")).isNotEmpty();
    }
}
