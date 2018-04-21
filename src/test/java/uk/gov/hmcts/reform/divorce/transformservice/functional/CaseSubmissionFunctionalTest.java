package uk.gov.hmcts.reform.divorce.transformservice.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
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
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD, classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CaseSubmissionFunctionalTest {

    private static final String USER_ID = "60";
    private static final String JURISDICTION = "divorce";
    private static final String CASE_TYPE_ID = "divorce";
    private static final String CCD_CITIZENS_ENDPOINT = "/citizens";
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String REQUEST_ID_HEADER_KEY = "requestId";
    private static final String REQUEST_ID_HEADER_VALUE = "1234567";
    private static final String SERVICE_AUTHORIZATION_HEADER_KEY = "ServiceAuthorization";
    private static final String JWT = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhOGdyMjR2NmtiYXRibXFlcWthM3VuamVicSIsInN1YiI6"
        + "IjYwIiwiaWF0IjoxNTA2NDE0OTI0LCJleHAiOjE1MDY0NDM3MjQsImRhdGEiOiJjaXRpemVuLGRpdm9yY2UtcHJpdmF0ZS1iZXRhLGNpd"
        + "Gl6ZW4tbG9hMSxkaXZvcmNlLXByaXZhdGUtYmV0YS1sb2ExIiwidHlwZSI6IkFDQ0VTUyIsImlkIjoiNjAiLCJmb3JlbmFtZSI6ImpvaG"
        + "4iLCJzdXJuYW1lIjoic21pdGgiLCJkZWZhdWx0LXNlcnZpY2UiOiJEaXZvcmNlIiwibG9hIjoxLCJkZWZhdWx0LXVybCI6Imh0dHBzOi8"
        + "vd3d3LWxvY2FsLnJlZ2lzdHJhdGlvbi5yZWZvcm0uaG1jdHMubmV0OjkwMDAvcG9jL2Rpdm9yY2UiLCJncm91cCI6ImRpdm9yY2UtcHJp"
        + "dmF0ZS1iZXRhIn0.mkKaw1_CGwC7KuntMlp8SWsLLgrCFwKtr0oFmFq42AA";

    private static final String SERVICE_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkaXZvcmNlX2NjZF9zdWJtaXNzaW9uIiwiZX"
        + "hwIjoxNTA2NDUwNTUyfQ.IvB5-Rtywc9_pDlLkk3wMnWFT5ACu9FU2av4Z4xjCi7NRuDlvLy78TIDC2KzIVSqyJL4IklHOUPG7FCBT3SoIQ";

    @ClassRule
    public static WireMockClassRule authTokenServer = new WireMockClassRule(new WireMockConfiguration().port(4502)
        .bindAddress("localhost"));
    @ClassRule
    public static WireMockClassRule ccdServer = new WireMockClassRule(new WireMockConfiguration().port(4000)
        .bindAddress("localhost"));
    @ClassRule
    public static WireMockClassRule draftStoreServer = new WireMockClassRule(new WireMockConfiguration().port(4601)
        .bindAddress("localhost"));
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
    public void submitHowNameChangedCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/how-name-changed.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/how-name-changed-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/how-name-changed-case-submission-request-body.json");
    }

    @Test
    public void submitJurisdictionSixTwelveCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/jurisdiction-6-12.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/jurisdiction-6-12-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/jurisdiction-6-12-case-submission-request-body.json");
    }

    @Test
    public void submitReasonAdulteryCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/reason-adultery.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/reason-adultery-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/reason-adultery-case-submission-request-body.json");
    }

    @Test
    public void submitReasonDesertionCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/reason-desertion.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/reason-desertion-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/reason-desertion-case-submission-request-body.json");
    }

    @Test
    public void submitReasonSeparationCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/reason-separation.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/reason-separation-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/reason-separation-case-submission-request-body.json");
    }

    @Test
    public void submitReasonUnresaonableBehaviourCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/reason-unreasonable-behaviour.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/reason-unreasonable-behaviour-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/reason-unreasonable-behaviour-case-submission-request-body.json");
    }

    @Test
    public void submitSameSexCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/same-sex.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/same-sex-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/same-sex-case-submission-request-body.json");
    }

    @Test
    public void submitJurisdictionAllCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/jurisdiction-all.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/jurisdiction-all-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/jurisdiction-all-case-submission-request-body.json");
    }

    @Test
    public void submitAddressesReturnsCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/addresses.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/address-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/address-case-submission-request-body.json");
    }

    @Test
    public void submitPaymentReturnsCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/payment.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/payment-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/payment-case-submission-request-body.json");
    }

    @Test
    public void submitD8DocumentReturnsCaseId() throws Exception {
        loadDivorceSessionData("/divorce-payload-json/d8-document.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionStub("/fixtures/ccd/d8-document-case-submission-request-body.json");

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getCaseId()).isEqualTo(83287);

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionStub("/fixtures/ccd/d8-document-case-submission-request-body.json");
    }

    @Test
    public void submitHandlesCcdProcessCouldNotBeStarted() throws Exception {
        loadDivorceSessionData("/fixtures/divorce/submit-request-body.json");
        serviceTokenStub();
        caseCreationProcessCouldNotBeStartedStub();

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getError()).isEqualTo("Request Id : 1234567 and Exception message : 422 Unprocessable Entity, "
            + "Exception response body: ");

        verifyServiceTokenStub();
        verifyCaseCreationProcessCouldNotBeStartedStub();
    }

    @Test
    public void submitHandlesCcdCaseSubmissionFailed() throws Exception {
        loadDivorceSessionData("/fixtures/divorce/submit-request-body.json");
        serviceTokenStub();
        caseCreationStub();
        caseSubmissionFailedStub();

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());
        CCDResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body.getError()).isEqualTo("Request Id : 1234567 and Exception message : 422 Unprocessable Entity, "
            + "Exception response body: ");

        verifyServiceTokenStub();
        verifyCaseCreationStub();
        verifyCaseSubmissionFailedStub();
    }

    @Test
    public void submitHandlesServiceAuthUnknownMicroService() throws Exception {
        serviceTokenUnknownMicroserviceStub();

        String requestBody = FileUtils.readFileToString(
            new File(getClass().getResource("/fixtures/divorce/submit-request-body.json").toURI()),
            Charset.defaultCharset()
        );

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response = restTemplate.postForEntity("/transformationapi/version/1/submit",
            entity, CCDResponse.class, new HashMap<>());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verifyServiceTokenUnknownMicroserviceStub();
    }

    @Test
    public void submitHandlesServiceAuthInvalidOneTimePassword() throws Exception {
        serviceTokenInvalidOneTimePasswordStub();

        String requestBody = FileUtils.readFileToString(
            new File(getClass().getResource("/fixtures/divorce/submit-request-body.json").toURI()),
            Charset.defaultCharset()
        );

        HttpHeaders headers = setHttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CCDResponse> response =
            restTemplate.postForEntity("/transformationapi/version/1/submit", entity, CCDResponse.class,
                new HashMap<>());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verifyServiceTokenInvalidOneTimePasswordStub();
    }

    @Test
    public void submitDeletesTheDraft() throws Exception {

        submitAddressesReturnsCaseId();

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
        requestBody = FileUtils.readFileToString(
            new File(getClass().getResource(filePath).toURI()), Charset.defaultCharset());
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
            new File(getClass().getResource("/fixtures/service-auth/401-unknown-microservice-response.json")
                .toURI()),
            Charset.defaultCharset());

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
            new File(getClass().getResource("/fixtures/service-auth/401-invalid-one-time-password-response.json")
                .toURI()),
            Charset.defaultCharset());

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


    private void caseCreationStub() throws Exception {
        String caseCreationResponseBody = FileUtils.readFileToString(new File(getClass()
            .getResource("/fixtures/ccd/case-creation-200-response.json").toURI()), Charset.defaultCharset());

        String eventTypeId = "create";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.stubFor(get(Strings.concat("/").concat(url).concat("?ignore-warning=true"))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withStatus(200)
                .withBody(caseCreationResponseBody)));
    }

    private void verifyCaseCreationStub() {
        String eventTypeId = "create";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.verify(getRequestedFor(urlEqualTo(Strings.concat("/")
            .concat(url)
            .concat("?ignore-warning=true")))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8")));
    }

    private void caseSubmissionStub(final String filePath) throws Exception {
        String requestBody = FileUtils.readFileToString(
            new File(getClass().getResource(filePath).toURI()), Charset.defaultCharset());

        JSONObject requestBodyWithCreatedDate = populateCreatedDate(requestBody);

        String responseBody = FileUtils.readFileToString(new File(getClass()
            .getResource("/fixtures/ccd/case-submission-201-response.json").toURI()), Charset.defaultCharset());

        String url = String.join("/", CCD_CITIZENS_ENDPOINT, USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases?ignore-warning=true");

        ccdServer.stubFor(post(url).withRequestBody(equalToJson(requestBodyWithCreatedDate.toString()))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withBody(responseBody)));
    }

    private void verifyCaseSubmissionStub(final String filePath) throws Exception {
        String requestBody = FileUtils.readFileToString(new File(getClass().getResource(filePath).toURI()),
            Charset.defaultCharset());
        JSONObject requestBodyWithCreatedDate = populateCreatedDate(requestBody);

        String url = String.join("/", CCD_CITIZENS_ENDPOINT, USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases?ignore-warning=true");

        ccdServer.verify(postRequestedFor(urlEqualTo(url)).withRequestBody(
            equalToJson(requestBodyWithCreatedDate.toString()))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8")));
    }

    private void caseCreationProcessCouldNotBeStartedStub() {
        String eventTypeId = "create";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.stubFor(get(Strings.concat("/").concat(url).concat("?ignore-warning=true"))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withStatus(422)));
    }

    private void verifyCaseCreationProcessCouldNotBeStartedStub() {
        String eventTypeId = "create";

        String url = String.join("/", "citizens", USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "event-triggers", eventTypeId, "token");

        ccdServer.verify(getRequestedFor(urlEqualTo(Strings.concat("/")
            .concat(url)
            .concat("?ignore-warning=true")))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8")));
    }

    private void caseSubmissionFailedStub() throws Exception {
        String requestBody = FileUtils.readFileToString(new File(getClass()
            .getResource("/fixtures/ccd/case-submission-request-body.json").toURI()), Charset.defaultCharset());

        JSONObject requestBodyWithCreatedDate = populateCreatedDate(requestBody);

        String url = String.join("/", CCD_CITIZENS_ENDPOINT, USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases?ignore-warning=true");

        ccdServer.stubFor(post(url).withRequestBody(equalToJson(requestBodyWithCreatedDate.toString()))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withStatus(422)));
    }

    private JSONObject populateCreatedDate(String requestBody) throws JSONException {
        JSONObject requestBodyJson = new JSONObject(requestBody);
        JSONObject data = (JSONObject) requestBodyJson.get("data");
        data.put("createdDate", LocalDate.now().format(ofPattern("yyyy-MM-dd")));
        return requestBodyJson;
    }

    private void verifyCaseSubmissionFailedStub() throws Exception {
        String requestBody = FileUtils.readFileToString(new File(getClass()
            .getResource("/fixtures/ccd/case-submission-request-body.json").toURI()), Charset.defaultCharset());
        JSONObject requestBodyWithCreatedDate = populateCreatedDate(requestBody);
        String url = String.join("/", CCD_CITIZENS_ENDPOINT, USER_ID, "jurisdictions", JURISDICTION,
            "case-types", CASE_TYPE_ID, "cases?ignore-warning=true");

        ccdServer.verify(postRequestedFor(urlEqualTo(url)).withRequestBody(
            equalToJson(requestBodyWithCreatedDate.toString()))
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .withHeader(SERVICE_AUTHORIZATION_HEADER_KEY, equalTo(SERVICE_TOKEN))
            .withHeader("Content-type", equalTo("application/json;charset=UTF-8")));
    }
}
