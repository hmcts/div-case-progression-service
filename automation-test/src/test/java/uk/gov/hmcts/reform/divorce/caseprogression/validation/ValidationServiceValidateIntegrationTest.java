package uk.gov.hmcts.reform.divorce.caseprogression.validation;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.divorce.support.caseprogression.BaseIntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SerenityRunner.class)
public class ValidationServiceValidateIntegrationTest extends BaseIntegrationTest {

    private String validateUrl;

    @Before
    public void setUp() {
        validateUrl = getTransformationApiUrl().concat(getTransformationApiValidateEndpoint());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnSuccessOnValidationWithValidData() throws Exception {

        Response validationResponse = postToRestService(loadJson("validation-valid-request.json"), validateUrl);

        assertThat(validationResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertEquals("success", validationResponse.getBody().path("validationStatus"));
        assertThat((List<String>) validationResponse.getBody().path("errors")).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnFailedOnValidationWithInvalidData() throws Exception {

        Response validationResponse = postToRestService(loadJson("validation-invalid-request.json"), validateUrl);

        assertThat(validationResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertEquals("failed", validationResponse.getBody().path("validationStatus"));
        assertThat((List<String>) validationResponse.getBody().path("errors")).isNotEmpty();
    }
}
