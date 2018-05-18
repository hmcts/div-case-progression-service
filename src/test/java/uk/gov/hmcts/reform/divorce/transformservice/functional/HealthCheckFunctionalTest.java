package uk.gov.hmcts.reform.divorce.transformservice.functional;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.jayway.jsonpath.JsonPath;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;

import java.io.File;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.netflix.feign.encoding.HttpEncoding.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
    classes = {
        CaseProgressionApplication.class,
        HealthCheckFunctionalTest.LocalRibbonClientConfiguration.class
    })
@PropertySource(value = "classpath:application.properties")
@TestPropertySource(
    properties = {
        "endpoints.health.time-to-live=0",
        "feign.hystrix.enabled=true",
        "eureka.client.enabled=false"
    })
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD, classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class HealthCheckFunctionalTest {

    private static final String HEALTH_UP_RESPONSE = "{ \"status\": \"UP\"}";

    @ClassRule
    public static WireMockClassRule ccdServer = new WireMockClassRule(WireMockSpring.options().port(4000)
        .bindAddress("localhost"));

    @ClassRule
    public static WireMockClassRule authServer = new WireMockClassRule(WireMockSpring.options().port(4502)
        .bindAddress("localhost"));

    @ClassRule
    public static WireMockClassRule draftStoreServer = new WireMockClassRule(WireMockSpring.options().port(4601)
        .bindAddress("localhost"));

    @ClassRule
    public static WireMockClassRule pdfGeneratorServer = new WireMockClassRule(WireMockSpring.options().port(4007)
        .bindAddress("localhost"));

    @ClassRule
    public static WireMockClassRule validationServer = new WireMockClassRule(WireMockSpring.options().port(4008)
        .bindAddress("localhost"));

    @Value("${ccd.caseDataStore.health.path}")
    private String ccdHealthPath;
    @Value("${auth.provider.health.path}")
    private String authHealthPath;
    @Value("${draft.store.api.health.path}")
    private String draftStoreApiHealthPath;
    @Value("${pdf.generator.healthPath}")
    private String pdfGeneratorHealthPath;
    @Value("${div.validation.service.health.path}")
    private String validationServiceHealthPath;
    @Value("${service.service-auth-provider.health.context-path}")
    private String serviceAuthHealthContextPath;
    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
        mockServiceAuthFeignHealthCheck();
    }

    @After
    public void tearDown() {
        ccdServer.verify(getRequestedFor(urlPathEqualTo("/status/health")));
        authServer.verify(getRequestedFor(urlPathEqualTo("/health")));
        draftStoreServer.verify(getRequestedFor(urlPathEqualTo("/health")));
        pdfGeneratorServer.verify(getRequestedFor(urlPathEqualTo(pdfGeneratorHealthPath)));
        validationServer.verify(getRequestedFor(urlPathEqualTo(validationServiceHealthPath)));
    }

    @Test
    public void shouldReturnStatusUpWhenAllDependenciesAreUp() throws Exception {
        stubAuthHealthUp();
        stubCcdHealthUp();
        stubDraftStoreApiHealthUp();
        stubPDFGeneratorHealthUp();
        stubValidationServiceHealthUp();
        

        String body = this.restTemplate.getForObject("/status/health", String.class);

        assertThat(JsonPath.read(body, "$.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.caseDataStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.serviceAuthProviderApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.draftStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divDocumentGenerator.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divValidationService.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.diskSpace.status").toString()).isEqualTo("UP");
    }

    @Test
    public void shouldReturnStatusDownWhenAllDependenciesAreDown() throws Exception {
        stubAuthHealthDown();
        stubCcdHealthDown();
        stubDraftStoreApiHealthDown();
        stubPDFGeneratorHealthDown();
        stubValidationServiceHealthDown();

        String body = this.restTemplate.getForObject("/status/health", String.class);

        assertThat(JsonPath.read(body, "$.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.caseDataStoreApi.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.serviceAuthProviderApi.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.draftStoreApi.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.divDocumentGenerator.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.divValidationService.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.diskSpace.status").toString()).isEqualTo("UP");
    }

    @Test
    public void shouldReturnStatusDownWhenServiceAuthIsDown() throws Exception {
        stubAuthHealthDown();
        stubCcdHealthUp();
        stubDraftStoreApiHealthUp();
        stubPDFGeneratorHealthUp();
        stubValidationServiceHealthUp();

        String body = this.restTemplate.getForObject("/status/health", String.class);

        assertThat(JsonPath.read(body, "$.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.caseDataStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.serviceAuthProviderApi.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.draftStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divDocumentGenerator.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divValidationService.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.diskSpace.status").toString()).isEqualTo("UP");
    }


    @Test
    public void shouldReturnStatusDownWhenCcdIsDown() throws Exception {
        stubAuthHealthUp();
        stubCcdHealthDown();
        stubDraftStoreApiHealthUp();
        stubPDFGeneratorHealthUp();
        stubValidationServiceHealthUp();

        String body = this.restTemplate.getForObject("/status/health", String.class);

        assertThat(JsonPath.read(body, "$.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.caseDataStoreApi.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.serviceAuthProviderApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.draftStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divDocumentGenerator.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divValidationService.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.diskSpace.status").toString()).isEqualTo("UP");
    }

    @Test
    public void shouldReturnStatusUpWhenDraftStoreApiIsDown() throws Exception {
        stubAuthHealthUp();
        stubCcdHealthUp();
        stubDraftStoreApiHealthDown();
        stubPDFGeneratorHealthUp();
        stubValidationServiceHealthUp();

        String body = this.restTemplate.getForObject("/status/health", String.class);

        assertThat(JsonPath.read(body, "$.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.caseDataStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.serviceAuthProviderApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.draftStoreApi.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.divDocumentGenerator.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divValidationService.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.diskSpace.status").toString()).isEqualTo("UP");
    }

    @Test
    public void shouldReturnStatusDownWhenPdfGeneratorIsDown() throws Exception {
        stubAuthHealthUp();
        stubCcdHealthUp();
        stubDraftStoreApiHealthUp();
        stubPDFGeneratorHealthDown();
        stubValidationServiceHealthUp();

        String body = this.restTemplate.getForObject("/status/health", String.class);

        assertThat(JsonPath.read(body, "$.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.caseDataStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.serviceAuthProviderApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.draftStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divDocumentGenerator.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.divValidationService.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.diskSpace.status").toString()).isEqualTo("UP");
    }

    @Test
    public void shouldReturnStatusDownWhenValidationServiceIsDown() throws Exception {
        stubAuthHealthUp();
        stubCcdHealthUp();
        stubDraftStoreApiHealthUp();
        stubPDFGeneratorHealthUp();
        stubValidationServiceHealthDown();

        String body = this.restTemplate.getForObject("/status/health", String.class);

        assertThat(JsonPath.read(body, "$.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.caseDataStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.serviceAuthProviderApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.draftStoreApi.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divDocumentGenerator.status").toString()).isEqualTo("UP");
        assertThat(JsonPath.read(body, "$.divValidationService.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body, "$.diskSpace.status").toString()).isEqualTo("UP");
    }

    private void stubCcdHealthUp() throws Exception {
        stubServiceResponse(ccdServer, ccdHealthPath, 200, 
            "/fixtures/ccd/healthcheck-up.json");
    }

    private void stubCcdHealthDown() throws Exception {
        stubServiceResponse(ccdServer, ccdHealthPath, 503, 
            "/fixtures/ccd/healthcheck-down.json");
    }

    private void stubAuthHealthUp() throws Exception {
        stubServiceResponse(authServer, authHealthPath, 200,
            "/fixtures/service-auth/healthcheck-up.json");
    }

    private void stubAuthHealthDown() throws Exception {
        stubServiceResponse(authServer, authHealthPath, 503,
            "/fixtures/service-auth/healthcheck-down.json");
    }

    private void stubDraftStoreApiHealthUp() throws Exception {
        stubServiceResponse(draftStoreServer, draftStoreApiHealthPath, 200,
            "/fixtures/draft-store/healthcheck-up.json");
    }

    private void stubDraftStoreApiHealthDown() throws Exception {
        stubServiceResponse(draftStoreServer, draftStoreApiHealthPath, 503,
            "/fixtures/draft-store/healthcheck-down.json");
    }

    private void stubPDFGeneratorHealthUp() throws Exception {
        stubServiceResponse(pdfGeneratorServer, pdfGeneratorHealthPath, 200,
            "/fixtures/pdf-generator/healthcheck-up.json");
    }

    private void stubPDFGeneratorHealthDown() throws Exception {
        stubServiceResponse(pdfGeneratorServer, pdfGeneratorHealthPath, 503,
            "/fixtures/pdf-generator/healthcheck-down.json");
    }

    private void stubValidationServiceHealthUp() throws Exception {
        stubServiceResponse(validationServer, validationServiceHealthPath, 200,
            "/fixtures/validation-service/healthcheck-up.json");
    }

    private void stubValidationServiceHealthDown() throws Exception {
        stubServiceResponse(validationServer, validationServiceHealthPath, 503,
            "/fixtures/validation-service/healthcheck-down.json");
    }

    private void stubServiceResponse(WireMockClassRule server, String healthPath, int statusCode, String fixturePath)
        throws Exception {
        String responseBody = FileUtils.readFileToString(
            new File(getClass().getResource(fixturePath).toURI()),
            Charset.defaultCharset());

        server.stubFor(get(healthPath)
            .withHeader("Accept", matching("application/json;charset=UTF-8"))
            .willReturn(aResponse()
                .withStatus(statusCode)
                .withHeader("Content-type", "application/json;charset=UTF-8")
                .withBody(responseBody))
        );
    }

    private void mockServiceAuthFeignHealthCheck() {
        authServer.stubFor(get(serviceAuthHealthContextPath)
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .withBody(HEALTH_UP_RESPONSE)
            )
        );
    }

    @TestConfiguration
    public static class LocalRibbonClientConfiguration {
        @Bean
        public ServerList<Server> ribbonServerList(@Value("${auth.provider.service.client.port}") int serverPort) {
            return new StaticServerList<>(new Server("localhost", serverPort));
        }
    }
}
