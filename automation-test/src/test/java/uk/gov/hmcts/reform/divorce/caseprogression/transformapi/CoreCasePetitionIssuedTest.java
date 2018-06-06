package uk.gov.hmcts.reform.divorce.caseprogression.transformapi;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.divorce.caseprogression.BaseIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SerenityRunner.class)
public class CoreCasePetitionIssuedTest extends BaseIntegrationTest {

    private String petitionIssuedApiUrl;

    @BeforeClass
    public void setUpClass() {
        super.setUpClass();
    }

    @AfterClass
    public void tearDownClass() {
        super.tearDownClass();
    }

    @Before
    public void setUp() {
        petitionIssuedApiUrl = getTransformationApiUrl().concat(getTransformationApiGeneratePdfEndpoint());
    }

    @Test
    public void shouldReturnSuccessWhenApdfIsCreated() throws Exception {

        Response caseProgressionResponse = postToRestService(loadJSON("ccd-callback-petition-issued.json"), petitionIssuedApiUrl);

        assertThat(caseProgressionResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertNotNull(caseProgressionResponse.getBody().path("data.D8DivorceWho"));
        assertNotNull(caseProgressionResponse.getBody().path("data.D8DocumentsGenerated"));
    }
}
