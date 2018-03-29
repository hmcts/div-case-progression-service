package uk.gov.hmcts.reform.divorce.caseprogression;

import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import lombok.Getter;
import net.serenitybdd.rest.SerenityRest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.divorce.auth.BaseIntegrationTestWithIdamSupport;
import uk.gov.hmcts.reform.divorce.auth.model.ServiceAuthTokenFor;
import uk.gov.hmcts.reform.divorce.util.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class BaseIntegrationTest extends BaseIntegrationTestWithIdamSupport {

    /**
     * The ccd base url.
     */
    @Value("${ccd.caseDataStore.baseUrl}")
    private String ccdBaseUrl;

    /**
     * The endpoint url for transformation
     */
    @Value("${transformation.api.url}")
    private String transformationApiUrl;

    @Value("${ccd.retrieve.case.url}")
    private String cadRetrieveCaseAdiUrl;

    /**
     * The endpoint route for submitting a case
     */
    @Value("${transformation.api.endpoint.submit}")
    private String transformationApiSubmitEndpoint;

    /**
     * The endpoint route for submitting a case
     */
    @Value("${transformation.api.endpoint.generatePetition}")
    private String transformationApiGeneratePdfEndpoint;

    @Value("${transformation.api.url}${transformation.api.endpoint.submit}")
    private String transformationApiSubmitUrl;

    /**
     * The endpoint route for submitting a case
     */
    @Value("${transformation.api.endpoint.update}")
    private String transformationApiUpdateEndpoint;

    private JSONArray fileUploadResponse = null;

    @Autowired
    @Qualifier("caseProgressionAuthTokenGenerator")
    private AuthTokenGenerator authTokenGenerator;

    /**
     * Load JSON.
     *
     * @param fileName the file name
     * @return the string
     * @throws Exception Resource loading exception
     */
    public String loadJSON(final String fileName) throws Exception {
        String jsonPayload = ResourceLoader.loadJSON("transformservice/divorce-payload-json/" + fileName);
        return replaceMockFileMetadataWithActualMetadata(jsonPayload);
    }

    /**
     * Make REST call to an API
     *
     * @param requestBody the request body to be sent in JSON string format
     * @param url         the endpoint to make a call as a string url
     * @return the rest assured response
     */
    public Response postToRestService(String requestBody, String url) {
        //default to case worker
        return postToRestService(requestBody, url, null);
    }

    public Response postToRestService(String requestBody, String url, String userToken) {

        Map<String, Object> header = headers();
        if (userToken != null) {
            header = headers(userToken);
        }

        return SerenityRest.given()
                .headers(header)
                .body(requestBody)
                .when()
                .post(url)
                .andReturn();
    }

    public Response getFromRestService(String url) {
        return SerenityRest.given()
                .config(RestAssuredConfig.config()
                        .sslConfig(new SSLConfig().allowAllHostnames()))
                .headers(headers(getIdamTestCaseWorkerUser(), true)).get(url).andReturn();
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
        return headers(token, false);
    }

    /**
     * Setup headers required for POST to CCD
     *
     * @return map of objects
     */
    private Map<String, Object> headers(String token, boolean serviceToken) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.put("Authorization", token);
        if (serviceToken) {
            headers.put("ServiceAuthorization", getServiceToken());
        }
        return headers;
    }

    public String replaceMockFileMetadataWithActualMetadata(String resource) {
        JSONObject jsonObject = new JSONObject(resource);

        if (jsonObject.has("eventData")) {
            jsonObject.getJSONObject("eventData").put("marriageCertificateFiles", fileUploadResponse);
            jsonObject.getJSONObject("eventData").put("d8", fileUploadResponse);
        } else {
            jsonObject.put("marriageCertificateFiles", fileUploadResponse);
            jsonObject.put("d8", fileUploadResponse);
        }

        return jsonObject.toString();
    }

    protected Response submitCase(String fileName) throws Exception {
        return postToRestService(loadJSON(fileName), transformationApiSubmitUrl);
    }

    @Override
    protected ServiceAuthTokenFor getServiceAuthTokenFor() {
        return ServiceAuthTokenFor.CASE_PROGRESSION;
    }
}
