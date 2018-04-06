package uk.gov.hmcts.reform.divorce.transformservice.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Strings;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDResponse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {CaseProgressionApplication.class})
public class CaseUpdateFunctionalTest {

    private static final String USER_ID = "60";
    private static final String JURISDICTION = "divorce";
    private static final String CASE_TYPE_ID = "divorce";
    private static final String CCD_CITIZENS_ENDPOINT = "/citizens";
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String REQUEST_ID_HEADER_KEY = "requestId";
    private static final String REQUEST_ID_HEADER_VALUE = "1234567";
    private static final String CASE_ID = "987654321";
    private static final String SERVICE_AUTHORIZATION_HEADER_KEY = "ServiceAuthorization";
    private static final String JWT = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhOGdyMjR2NmtiYXRibXFlcWthM3VuamVicSIsInN1YiI6I"
        + "jYwIiwiaWF0IjoxNTA2NDE0OTI0LCJleHAiOjE1MDY0NDM3MjQsImRhdGEiOiJjaXRpemVuLGRpdm9yY2UtcHJpdmF0ZS1iZXRhLGNpdGl"
        + "6ZW4tbG9hMSxkaXZvcmNlLXByaXZhdGUtYmV0YS1sb2ExIiwidHlwZSI6IkFDQ0VTUyIsImlkIjoiNjAiLCJmb3JlbmFtZSI6ImpvaG4iL"
        + "CJzdXJuYW1lIjoic21pdGgiLCJkZWZhdWx0LXNlcnZpY2UiOiJEaXZvcmNlIiwibG9hIjoxLCJkZWZhdWx0LXVybCI6Imh0dHBzOi8vd3d"
        + "3LWxvY2FsLnJlZ2lzdHJhdGlvbi5yZWZvcm0uaG1jdHMubmV0OjkwMDAvcG9jL2Rpdm9yY2UiLCJncm91cCI6ImRpdm9yY2UtcHJpdmF0Z"
        + "S1iZXRhIn0.mkKaw1_CGwC7KuntMlp8SWsLLgrCFwKtr0oFmFq42AA";

    private static final String SERVICE_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkaXZvcmNlX2NjZF9zdWJtaXNzaW9uIiwiZXh"
        + "wIjoxNTA2NDUwNTUyfQ.IvB5-Rtywc9_pDlLkk3wMnWFT5ACu9FU2av4Z4xjCi7NRuDlvLy78TIDC2KzIVSqyJL4IklHOUPG7FCBT3SoIQ";

    @ClassRule
    public static WireMockClassRule authTokenServer = new WireMockClassRule(4502);
    @ClassRule
    public static WireMockClassRule ccdServer = new WireMockClassRule(4000);
    @ClassRule
    public static WireMockClassRule draftStoreServer = new WireMockClassRule(4601);
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${draft.store.api.document.type}")
    private String draftDocumentType;
    private String requestBody;

    @Before
    public void setUp() throws IOException {
        setUpDraftStore();
    }

    @Test
    public void shouldReturnCaseIdWhenUpdatingCcdWithPayment() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/payment-update.json");
        serviceTokenStub();
        eventStartStub();
        caseUpdateStub("/fixtures/ccd/update-payment-case-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate
            .postForEntity("/transformationapi/version/1/updateCase/987654321", entity, CCDResponse.class,
                new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(987654321);

        verifyServiceTokenStub();
        verifyEventStartStub();
        verifyCaseUpdateStub("/fixtures/ccd/update-payment-case-request-body.json");
    }

    @Test
    public void shouldReturnCaseIdWhenUpdatingCcdWithAdditionalPayment() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/payment-additional.json");
        serviceTokenStub();
        eventStartStubWithFilePath("/fixtures/ccd/event-start-with-payment-200-response.json");
        caseUpdateStub("/fixtures/ccd/update-payment-case-additional-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate
            .postForEntity("/transformationapi/version/1/updateCase/987654321", entity, CCDResponse.class,
                new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(987654321);

        verifyServiceTokenStub();
        verifyEventStartStub();
        verifyCaseUpdateStub("/fixtures/ccd/update-payment-case-additional-request-body.json");
    }

    @Test
    public void shouldReturnCaseIdWhenUpdatingCcdWithOverwritingPayment() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/payment-overwrite.json");
        serviceTokenStub();
        eventStartStubWithFilePath("/fixtures/ccd/event-start-with-payment-200-response.json");
        caseUpdateStub("/fixtures/ccd/update-payment-case-overwrite-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate
            .postForEntity("/transformationapi/version/1/updateCase/987654321", entity, CCDResponse.class,
                new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(987654321);

        verifyServiceTokenStub();
        verifyEventStartStub();
        verifyCaseUpdateStub("/fixtures/ccd/update-payment-case-overwrite-request-body.json");
    }

    @Test
    public void shouldReturnNotFoundWhenCaseIdIsNotFound() throws Exception {
        loadDivorceSessionData("/fixtures/divorce/update-request-body.json");
        serviceTokenStub();
        startEventCaseNotFoundStub();

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate
            .postForEntity("/transformationapi/version/1/updateCase/987654321", entity, CCDResponse.class,
                new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getError())
            .isEqualTo("Request Id : 1234567 and Exception message : 404 Not Found, Exception response body: ");

        verifyServiceTokenStub();
        verifyStartEventCaseNotFoundStub();
    }

    @Test
    public void shouldHandleCcdUpdateProcessCouldNotBeStarted() throws Exception {
        loadDivorceSessionData("/fixtures/divorce/update-request-body.json");
        serviceTokenStub();
        startEventProcessCouldNotBeStartedStub();

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate
            .postForEntity("/transformationapi/version/1/updateCase/987654321", entity, CCDResponse.class,
                new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getError())
            .isEqualTo("Request Id : 1234567 and Exception message : 422 Unprocessable Entity, "
                + "Exception response body: ");

        verifyServiceTokenStub();
        verifyStartEventProcessCouldNotBeStartedStub();
    }

    @Test
    public void shouldHandleCcdCaseUpdateFailed() throws Exception {
        loadDivorceSessionData("/fixtures/divorce/update-request-body.json");
        serviceTokenStub();
        eventStartStub();
        caseUpdateFailedStub();

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate
            .postForEntity("/transformationapi/version/1/updateCase/987654321", entity, CCDResponse.class,
                new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getError())
            .isEqualTo("Request Id : 1234567 and Exception message : 422 Unprocessable Entity, "
                + "Exception response body: ");

        verifyServiceTokenStub();
        verifyEventStartStub();
        verifyCaseUpdateFailedStub();
    }

    @Test
    public void shouldHandleServiceAuthUnknownMicroService() throws Exception {
        serviceTokenUnknownMicroserviceStub();

        String requestBody = FileUtils.readFileToString(
            new File(
                getClass()
                    .getResource("/fixtures/divorce/update-request-body.json").toURI()), Charset.defaultCharset()
        );

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate
            .postForEntity("/transformationapi/version/1/updateCase/987654321", entity, CCDResponse.class,
                new HashMap<>());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verifyServiceTokenUnknownMicroserviceStub();
    }

    @Test
    public void shouldHandleServiceAuthInvalidOneTimePassword() throws Exception {
        serviceTokenInvalidOneTimePasswordStub();

        String requestBody = FileUtils.readFileToString(
            new File(
                getClass().getResource("/fixtures/divorce/update-request-body.json").toURI()),
            Charset.defaultCharset()
        );

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate
            .postForEntity("/transformationapi/version/1/updateCase/987654321", entity, CCDResponse.class,
                new HashMap<>());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verifyServiceTokenInvalidOneTimePasswordStub();
    }

    @Test
    public void updateShouldDeleteTheDraft() throws Exception {
        shouldReturnCaseIdWhenUpdatingCcdWithOverwritingPayment();

        draftStoreServer.verify(deleteRequestedFor(urlEqualTo("/drafts/1")));

    }

    private void setUpDraftStore() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        DraftList draftList = createDraftList();
        draftStoreServer.stubFor(
            get(anyUrl())
                .willReturn(ok()
                    .withHeader("Content-type", "application/json;charset=UTF-8")
                    .withBody(objectMapper.writeValueAsBytes(draftList))));
    }

    private DraftList createDraftList() throws IOException {
        return new DraftList(
            Collections.singletonList(createDivorceDraft("{}")),
            new DraftList.PagingCursors(null));
    }

    private Draft createDivorceDraft(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return new Draft(
            "1",
            objectMapper.readTree(content),
            draftDocumentType);
    }

    private void loadDivorceSessionData(final String filePath) throws Exception {
        requestBody = FileUtils.readFileToString(new File(getClass().getResource(filePath).toURI()),
            Charset.defaultCharset());
    }

    private HttpHeaders setHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add(AUTHORIZATION_HEADER_KEY, "Bearer " + JWT);
        headers.add(REQUEST_ID_HEADER_KEY, REQUEST_ID_HEADER_VALUE);

        return headers;
    }

    private void serviceTokenStub() {
        authTokenServer.stubFor(post(urlPathMatching("/lease.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-type", "text/plain")
                .withBody(SERVICE_TOKEN)
            ));
    }

    private void verifyServiceTokenStub() {
        authTokenServer.verify(postRequestedFor(urlPathMatching("/lease.*")));
    }


    private void serviceTokenUnknownMicroserviceStub() throws Exception {
        String responseBody = FileUtils.readFileToString(
            new File(
                getClass().getResource("/fixtures/service-auth/401-unknown-microservice-response.json").toURI()),
            Charset.defaultCharset()
        );

        authTokenServer.stubFor(post(urlPathMatching("/lease.*"))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withBody(responseBody)
            ));
    }

    private void verifyServiceTokenUnknownMicroserviceStub() {
        authTokenServer.verify(postRequestedFor(urlPathMatching("/lease.*")));
    }


    private void serviceTokenInvalidOneTimePasswordStub() throws Exception {
        String responseBody = FileUtils.readFileToString(
            new File(
                getClass().getResource("/fixtures/service-auth/401-invalid-one-time-password-response.json")
                    .toURI()),
            Charset.defaultCharset()
        );

        authTokenServer.stubFor(post(urlPathMatching("/lease.*"))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withBody(responseBody)
            ));
    }

    private void verifyServiceTokenInvalidOneTimePasswordStub() {
        authTokenServer.verify(postRequestedFor(urlPathMatching("/lease.*")));
    }


    private void eventStartStub() throws Exception {
        String eventStartResponseBody = FileUtils.readFileToString(
            new File(getClass().getResource("/fixtures/ccd/event-start-200-response.json")
                .toURI()), Charset.defaultCharset());

        String eventTypeId = "paymentMade";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.stubFor(get(Strings.concat("/").concat(url).concat("?ignore-warning=true"))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withStatus(200)
                .withBody(eventStartResponseBody)));
    }

    private void eventStartStubWithFilePath(final String filePath) throws Exception {
        String eventStartResponseBody = FileUtils.readFileToString(new File(getClass().getResource(filePath).toURI()),
            Charset.defaultCharset());

        String eventTypeId = "paymentMade";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.stubFor(get(Strings.concat("/").concat(url).concat("?ignore-warning=true"))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withStatus(200)
                .withBody(eventStartResponseBody)));
    }

    private void verifyEventStartStub() {
        String eventTypeId = "paymentMade";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.verify(getRequestedFor(urlEqualTo(Strings.concat("/")
            .concat(url)
            .concat("?ignore-warning=true")))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8")));
    }

    private void caseUpdateStub(final String filePath) throws Exception {
        String requestBody = FileUtils.readFileToString(new File(getClass().getResource(filePath).toURI()),
            Charset.defaultCharset());
        JSONObject requestBodyWithCreatedDate = populateCreatedDate(requestBody);

        String responseBody = FileUtils.readFileToString(
            new File(getClass().getResource("/fixtures/ccd/case-event-creation-201-response.json").toURI()),
            Charset.defaultCharset()
        );

        String url = String.join("/", CCD_CITIZENS_ENDPOINT, USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "events?ignore-warning=true");

        ccdServer.stubFor(post(url).withRequestBody(equalToJson(requestBodyWithCreatedDate.toString()))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withBody(responseBody)));
    }

    private void verifyCaseUpdateStub(final String filePath) throws Exception {
        String requestBody = FileUtils.readFileToString(
            new File(getClass().getResource(filePath).toURI()), Charset.defaultCharset());

        JSONObject requestBodyWithCreatedDate = populateCreatedDate(requestBody);

        String url = String.join("/", CCD_CITIZENS_ENDPOINT, USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "events?ignore-warning=true");

        ccdServer.verify(postRequestedFor(urlEqualTo(url))
            .withRequestBody(equalToJson(requestBodyWithCreatedDate.toString()))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8")));
    }

    private void startEventCaseNotFoundStub() {
        String eventTypeId = "paymentMade";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.stubFor(get(Strings.concat("/").concat(url).concat("?ignore-warning=true"))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withStatus(404)));
    }

    private void verifyStartEventCaseNotFoundStub() {
        String eventTypeId = "paymentMade";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.verify(getRequestedFor(urlEqualTo(Strings.concat("/")
            .concat(url)
            .concat("?ignore-warning=true")))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8")));
    }

    private void startEventProcessCouldNotBeStartedStub() {
        String eventTypeId = "paymentMade";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.stubFor(get(Strings.concat("/").concat(url).concat("?ignore-warning=true"))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withStatus(422)));
    }

    private void verifyStartEventProcessCouldNotBeStartedStub() {
        String eventTypeId = "paymentMade";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.verify(getRequestedFor(urlEqualTo(Strings.concat("/")
            .concat(url)
            .concat("?ignore-warning=true")))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8")));
    }

    private void caseUpdateFailedStub() throws Exception {
        String requestBody = FileUtils.readFileToString(
            new File(getClass().getResource("/fixtures/ccd/case-event-creation-request-body.json").toURI()),
            Charset.defaultCharset()
        );

        JSONObject requestBodyWithCreatedDate = populateCreatedDate(requestBody);

        String url = String.join("/", CCD_CITIZENS_ENDPOINT, USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "events?ignore-warning=true");

        ccdServer.stubFor(post(url).withRequestBody(equalToJson(requestBodyWithCreatedDate.toString()))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withStatus(422)));
    }

    private void verifyCaseUpdateFailedStub() throws Exception {
        String requestBody = FileUtils.readFileToString(
            new File(getClass().getResource("/fixtures/ccd/case-event-creation-request-body.json").toURI()),
            Charset.defaultCharset()
        );

        JSONObject requestBodyWithCreatedDate = populateCreatedDate(requestBody);

        String url = String.join("/", CCD_CITIZENS_ENDPOINT, USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases", CASE_ID, "events?ignore-warning=true");

        ccdServer.verify(postRequestedFor(urlEqualTo(url))
            .withRequestBody(equalToJson(requestBodyWithCreatedDate.toString()))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8")));
    }

    private JSONObject populateCreatedDate(String requestBody) throws JSONException {
        JSONObject requestBodyJson = new JSONObject(requestBody);
        JSONObject data = (JSONObject) requestBodyJson.get("data");
        data.put("createdDate", LocalDate.now().format(ofPattern("yyyy-MM-dd")));
        return requestBodyJson;
    }
}
