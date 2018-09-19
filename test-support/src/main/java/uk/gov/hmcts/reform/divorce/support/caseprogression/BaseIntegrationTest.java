package uk.gov.hmcts.reform.divorce.support.caseprogression;

import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import lombok.Getter;
import net.serenitybdd.rest.SerenityRest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.divorce.support.auth.BaseIntegrationTestWithIdamSupport;
import uk.gov.hmcts.reform.divorce.support.auth.model.ServiceAuthTokenFor;
import uk.gov.hmcts.reform.divorce.support.util.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class BaseIntegrationTest extends BaseIntegrationTestWithIdamSupport {

    @Value("${transformation.api.url}${transformation.api.endpoint.submit}")
    private String transformationApiSubmitUrl;

    @Value("${transformation.api.url}")
    private String transformationApiUrl;

    @Value("${transformation.api.endpoint.submit}")
    private String transformationApiSubmitEndpoint;

    @Value("${transformation.api.endpoint.generatePetition}")
    private String transformationApiGeneratePdfEndpoint;

    @Value("${transformation.api.endpoint.update}")
    private String transformationApiUpdateEndpoint;

    @Value("${transformation.api.endpoint.validate}")
    private String transformationApiValidateEndpoint;

    @Value("${ccd.submit.event}")
    private String submitEventUrl;

    @Value("${ccd.create.event}")
    private String createEventUrl;

    private JSONArray fileUploadResponse = null;

    public String loadJson(final String fileName) throws Exception {
        String jsonPayload = ResourceLoader.loadAsText("divorce-payload-json/" + fileName);
        return replaceMockFileMetadataWithActualMetadata(jsonPayload);
    }

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
        return getFromRestService(url, headers(getIdamTestCaseWorkerUser(), true));
    }

    public Response getFromRestService(String url, Map<String, Object> headers) {
        return SerenityRest.given()
            .config(RestAssuredConfig.config()
                .sslConfig(new SSLConfig().allowAllHostnames()))
            .headers(headers).get(url).andReturn();
    }


    public Map<String, Object> headers() {
        return headers(getIdamTestUser());
    }

    private Map<String, Object> headers(String token) {
        return headers(token, false);
    }

    private Map<String, Object> headers(String token, boolean serviceToken) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.put("Authorization", token);
        headers.put("ServiceAuthorization", getServiceToken());
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
        return postToRestService(loadJson(fileName), transformationApiSubmitUrl);
    }

    @Override
    protected ServiceAuthTokenFor getServiceAuthTokenFor() {
        return ServiceAuthTokenFor.CASE_PROGRESSION;
    }

    public Response submitEvent(long caseId, String eventId) throws Exception {
        String eventToken = createEventAsCaseWorker(caseId, eventId);

        JSONObject jsonObject = new JSONObject(loadJson("submit-event.json"));
        JSONObject eventObject = jsonObject.getJSONObject("event").put("id", eventId);
        jsonObject.put("event", eventObject);
        jsonObject.put("event_token", eventToken);

        String submitEventUrl = String.format(this.submitEventUrl, Long.parseLong(getUserId(getIdamTestCaseWorkerUser())),
            caseId);

        return postToRestService(jsonObject.toString(), submitEventUrl, getIdamTestCaseWorkerUser());
    }

    public String createEventAsCaseWorker(long caseId, String event) {
        String createEventUrl = String.format(this.createEventUrl, Long.parseLong(getUserId(getIdamTestCaseWorkerUser())),
            caseId, event);
        Response fromRestService = getFromRestService(createEventUrl);

        return fromRestService.getBody().path("token");
    }
}
