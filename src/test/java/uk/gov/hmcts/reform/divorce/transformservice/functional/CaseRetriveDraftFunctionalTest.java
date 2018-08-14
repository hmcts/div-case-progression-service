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
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.draftservice.domain.Draft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDResponse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {CaseProgressionApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CaseRetriveDraftFunctionalTest {

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String REQUEST_ID_HEADER_KEY = "requestId";
    private static final String REQUEST_ID_HEADER_VALUE = "1234567";
    private static final String JWT = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhOGdyMjR2NmtiYXRibXFlcWthM3VuamVicSIsInN1YiI6I"
        + "jYwIiwiaWF0IjoxNTA2NDE0OTI0LCJleHAiOjE1MDY0NDM3MjQsImRhdGEiOiJjaXRpemVuLGRpdm9yY2UtcHJpdmF0ZS1iZXRhLGNpdGl"
        + "6ZW4tbG9hMSxkaXZvcmNlLXByaXZhdGUtYmV0YS1sb2ExIiwidHlwZSI6IkFDQ0VTUyIsImlkIjoiNjAiLCJmb3JlbmFtZSI6ImpvaG4iL"
        + "CJzdXJuYW1lIjoic21pdGgiLCJkZWZhdWx0LXNlcnZpY2UiOiJEaXZvcmNlIiwibG9hIjoxLCJkZWZhdWx0LXVybCI6Imh0dHBzOi8vd3d"
        + "3LWxvY2FsLnJlZ2lzdHJhdGlvbi5yZWZvcm0uaG1jdHMubmV0OjkwMDAvcG9jL2Rpdm9yY2UiLCJncm91cCI6ImRpdm9yY2UtcHJpdmF0Z"
        + "S1iZXRhIn0.mkKaw1_CGwC7KuntMlp8SWsLLgrCFwKtr0oFmFq42AA";

    private static final String SERVICE_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkaXZvcmNlX2NjZF9zdWJtaXNzaW9uIiwiZXh"
        + "wIjoxNTA2NDUwNTUyfQ.IvB5-Rtywc9_pDlLkk3wMnWFT5ACu9FU2av4Z4xjCi7NRuDlvLy78TIDC2KzIVSqyJL4IklHOUPG7FCBT3SoIQ";

    @ClassRule
    public static WireMockClassRule authTokenServer = new WireMockClassRule(WireMockSpring.options().port(4502)
        .bindAddress("localhost"));
    @ClassRule
    public static WireMockClassRule draftStoreServer = new WireMockClassRule(WireMockSpring.options().port(4601)
        .bindAddress("localhost"));
    @ClassRule
    public static WireMockClassRule idamServer = new WireMockClassRule(
        new WireMockConfiguration().port(4503).bindAddress("localhost")
    );

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${draft.store.api.document.type}")
    private String draftDocumentType;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        serviceTokenStub();
        idamUserDetailsStub();
    }

    @Test
    public void shouldReturnErrorOnDraftStoreError() {
        draftStoreServer.stubFor(
            get(anyUrl())
                .willReturn(serverError()));

        HttpHeaders headers = setHttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate
            .exchange(
                "/draftsapi/version/1",
                HttpMethod.GET,
                entity,
                String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        draftStoreServer.verify(getRequestedFor(urlEqualTo("/drafts")));
        verifyServiceTokenStub();
    }

    @Test
    public void shouldReturnDraft() throws IOException {
        setUpDraftStore();

        HttpHeaders headers = setHttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate
            .exchange(
                "/draftsapi/version/1",
                HttpMethod.GET,
                entity,
                String.class
            );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        draftStoreServer.verify(getRequestedFor(urlEqualTo("/drafts")));
        verifyServiceTokenStub();
    }

    private Draft createDivorceDraft(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return new Draft(
            "1",
            objectMapper.readTree(content),
            draftDocumentType);
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

    private void idamUserDetailsStub() throws URISyntaxException, IOException {
        String idamResponseBody = FileUtils.readFileToString(
            new File(getClass().getResource("/fixtures/idam/user-details-200-response.json").toURI()),
            Charset.defaultCharset()
        );

        idamServer.stubFor(get("/details")
            .withHeader(AUTHORIZATION_HEADER_KEY, equalTo("Bearer " + JWT))
            .willReturn(aResponse()
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withStatus(200)
                .withBody(idamResponseBody)));
    }
}
