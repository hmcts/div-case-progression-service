package uk.gov.hmcts.reform.divorce.transformservice.functional;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.apache.commons.io.FileUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.testutils.ObjectMapperTestUtil;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDCallbackResponse;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = CaseProgressionApplication.class)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD, classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CaseAddPdfFunctionalTest {

    private static final String TEST_AUTH_VALUE = "test";
    private static final String PDF_GENERATOR_ENDPOINT = "/version/1/generatePDF";
    private static final String VALIDATION_SERVICE_ENDPOINT = "/version/1/validate";
    private static final String REQUEST_ID_HEADER_KEY = "requestId";
    private static final String REQUEST_ID_HEADER_VALUE = "1234567";
    private static final String AUTHORIZATION = "Authorization";

    @ClassRule
    public static WireMockClassRule pdfGeneratorServer = new WireMockClassRule(
        WireMockSpring.options().port(4007).bindAddress("localhost"));
    @ClassRule
    public static WireMockClassRule validationServer = new WireMockClassRule(
        WireMockSpring.options().port(4008).bindAddress("localhost"));
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnCaseDataWhenAddPdf() throws Exception {
        String requestBody = loadResourceAsString("/divorce-payload-json/add-pdf.json");
        validationServiceStub("validate-200-response.json", "validate-request.json");
        pdfGeneratorStub();

        CCDCallbackResponse expectedResponse =
            ObjectMapperTestUtil.convertJsonToObject(
                loadResourceAsByteArray("/divorce-payload-json/add-pdf-response.json"),
                CCDCallbackResponse.class);

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDCallbackResponse> response =
            restTemplate.postForEntity(
                "/caseprogression/petition-issued",
                entity,
                CCDCallbackResponse.class,
                new HashMap<>());

        assertEquals(expectedResponse, response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        validationServiceVerify("validate-request.json");
        pdfGeneratorVerify();
    }

    @Test
    public void shouldReturnErrorWhenUploadedDocumentTypeIsNotSet() throws Exception {
        String requestBody = loadResourceAsString("/divorce-payload-json/add-pdf-no-documenttype.json");
        validationServiceStub("validate-200-response.json", "validate-request-no-documenttype.json");

        String expectedErrorMessage = "The Document Type has not been set for one of the uploaded documents. "
            + "This must be set before a new PDF can be created";

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDCallbackResponse> response =
            restTemplate.postForEntity(
                "/caseprogression/petition-issued",
                entity,
                CCDCallbackResponse.class,
                new HashMap<>());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEquals(expectedErrorMessage, response.getBody().getErrors().get(0));

        validationServiceVerify("validate-request-no-documenttype.json");
    }

    @Test
    public void shouldReturnResponseWithErrorsWhenThereIsInvalidData() throws Exception {
        String requestBody = loadResourceAsString("/divorce-payload-json/add-pdf.json");
        validationServiceStub("validate-200-response-with-errors.json", "validate-request.json");

        CCDCallbackResponse expectedResponse =
            ObjectMapperTestUtil.convertJsonToObject(
                loadResourceAsByteArray("/divorce-payload-json/add-pdf-response-with-errors.json"),
                CCDCallbackResponse.class);

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDCallbackResponse> response =
            restTemplate.postForEntity(
                "/caseprogression/petition-issued",
                entity,
                CCDCallbackResponse.class,
                new HashMap<>());

        assertEquals(expectedResponse, response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        validationServiceVerify("validate-request.json");
    }

    @Test
    public void shouldReturnResponseWithWarningsWhenThereIsInvalidData() throws Exception {
        String requestBody = loadResourceAsString("/divorce-payload-json/add-pdf.json");
        validationServiceStub("validate-200-response-with-warnings.json", "validate-request.json");

        CCDCallbackResponse expectedResponse =
            ObjectMapperTestUtil.convertJsonToObject(
                loadResourceAsByteArray("/divorce-payload-json/add-pdf-response-with-warnings.json"),
                CCDCallbackResponse.class);

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDCallbackResponse> response =
            restTemplate.postForEntity(
                "/caseprogression/petition-issued",
                entity,
                CCDCallbackResponse.class,
                new HashMap<>());

        assertEquals(expectedResponse, response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        validationServiceVerify("validate-request.json");
    }

    private String loadResourceAsString(final String filePath) throws Exception {
        return FileUtils.readFileToString(new File(getClass().getResource(filePath).toURI()), Charset.defaultCharset());
    }

    private byte[] loadResourceAsByteArray(final String filePath) throws Exception {
        return FileUtils.readFileToByteArray(new File(getClass().getResource(filePath).toURI()));
    }

    private HttpHeaders setHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add(REQUEST_ID_HEADER_KEY, REQUEST_ID_HEADER_VALUE);
        headers.set(AUTHORIZATION, TEST_AUTH_VALUE);

        return headers;
    }

    private void pdfGeneratorStub() throws Exception {
        String pdfGeneratedResponseBody =
            loadResourceAsString("/fixtures/pdf-generator/generate-pdf-200-response.json");

        String generateTemplateRequestBody =
            loadResourceAsString("/fixtures/pdf-generator/generate-pdf-request.json");

        pdfGeneratorServer.stubFor(post(PDF_GENERATOR_ENDPOINT)
            .withRequestBody(equalToJson(generateTemplateRequestBody))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withBody(pdfGeneratedResponseBody)));
    }

    private void pdfGeneratorVerify() throws Exception {
        String generateTemplateRequestBody =
            loadResourceAsString("/fixtures/pdf-generator/generate-pdf-request.json");

        pdfGeneratorServer.verify(postRequestedFor(urlEqualTo(PDF_GENERATOR_ENDPOINT))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .withRequestBody(equalToJson(generateTemplateRequestBody)));
    }

    private void validationServiceStub(String validationResponseJson, String validationRequestJson) throws Exception {
        String validateResponseBody =
            loadResourceAsString("/fixtures/validation-service/" + validationResponseJson);

        String validateRequestBody =
            loadResourceAsString("/fixtures/validation-service/" + validationRequestJson);

        validationServer.stubFor(post(VALIDATION_SERVICE_ENDPOINT)
            .withRequestBody(equalToJson(validateRequestBody))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withBody(validateResponseBody)));
    }

    private void validationServiceVerify(String validationRequestJson) throws Exception {
        String validateRequestBody =
            loadResourceAsString("/fixtures/validation-service/" + validationRequestJson);

        validationServer.verify(postRequestedFor(urlEqualTo(VALIDATION_SERVICE_ENDPOINT))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .withRequestBody(equalToJson(validateRequestBody)));
    }
}
