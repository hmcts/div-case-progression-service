package uk.gov.hmcts.reform.divorce.draftservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.divorce.draftservice.domain.CreateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.DraftList;
import uk.gov.hmcts.reform.divorce.draftservice.domain.UpdateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.exception.DraftStoreUnavailableException;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DraftStoreClientTest {

    private static final String JWT = "Bearer hgsdja87wegqeuf...";
    private static final String SERVICE_JWT = "Bearer drhodsrotnsr7...";
    private static final String DRAFT_ID = "123";

    @Value("${draft.store.api.baseUrl}")
    private String draftsApiBaseUrl;

    @Value("${draft.store.api.document.type}")
    private String draftType;

    @Value("${draft.store.api.encryption.key}")
    private String secret;

    @Value("${draft.store.api.max.age}")
    private int maxAge;

    @Autowired
    private DraftStoreClient underTest;

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private DraftStoreHttpEntityFactory entityFactory;

    private MockRestServiceServer mockServer;

    private HttpHeaders headers;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

        objectMapper = new ObjectMapper();

        headers = new HttpHeaders();
        headers.add("Authorization", JWT);
        headers.add("ServiceAuthorization", SERVICE_JWT);
        headers.add("Secret", secret);
    }

    @Test
    public void getAllShouldReturnTheDraftListFromTheResponse() throws Exception {
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        given(entityFactory.createRequestEntityFroDraft(JWT, secret))
            .willReturn(httpEntity);

        DraftList expectedDraftList =
            new DraftList(Collections.emptyList(), null);

        mockServer
            .expect(requestTo(draftsApiBaseUrl + "/drafts"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", JWT))
            .andExpect(header("ServiceAuthorization", SERVICE_JWT))
            .andExpect(header("Secret", secret))
            .andRespond(withSuccess(objectMapper.writeValueAsBytes(expectedDraftList),
                MediaType.APPLICATION_JSON_UTF8));

        DraftList actualDraftList = underTest.getAll(JWT, secret);

        mockServer.verify();
        assertEquals(expectedDraftList, actualDraftList);

    }

    @Test(expected = DraftStoreUnavailableException.class)
    public void getAllShouldThrowDraftStoreUnavailableExceptionWhenTheDraftStoreIsNotAvailable() {
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        given(entityFactory.createRequestEntityFroDraft(JWT, secret))
            .willReturn(httpEntity);

        mockServer
            .expect(requestTo(draftsApiBaseUrl + "/drafts"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        underTest.getAll(JWT, secret);

    }

    @Test
    public void getAllShouldReturnTheSecondPageOfDrafts() throws Exception {
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        given(entityFactory.createRequestEntityFroDraft(JWT, secret))
            .willReturn(httpEntity);

        DraftList expectedDraftList =
            new DraftList(Collections.emptyList(), null);

        mockServer
            .expect(requestTo(draftsApiBaseUrl + "/drafts/?after=10"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", JWT))
            .andExpect(header("ServiceAuthorization", SERVICE_JWT))
            .andExpect(header("Secret", secret))
            .andRespond(withSuccess(objectMapper.writeValueAsBytes(expectedDraftList),
                MediaType.APPLICATION_JSON_UTF8));

        DraftList actualDraftList = underTest.getAll(JWT, secret, "10");

        mockServer.verify();
        assertEquals(expectedDraftList, actualDraftList);
    }

    @Test
    public void createDraftShouldCallTheRestTemplateToCreateADraft() throws Exception {
        CreateDraft createDraft = new CreateDraft(objectMapper.readTree("{}"), draftType, maxAge);
        HttpEntity<CreateDraft> httpEntity = new HttpEntity<>(createDraft, headers);
        given(entityFactory.createRequestEntityForDraft(JWT, secret, createDraft))
            .willReturn(httpEntity);

        mockServer
            .expect(requestTo(draftsApiBaseUrl + "/drafts"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("Authorization", JWT))
            .andExpect(header("ServiceAuthorization", SERVICE_JWT))
            .andExpect(header("Secret", secret))
            .andRespond(withSuccess());

        underTest.createDraft(JWT, secret, createDraft);

        mockServer.verify();
    }

    @Test(expected = DraftStoreUnavailableException.class)
    public void createDraftShouldThrowDraftStoreUnavailableExceptionWhenDraftStoreIsNotAvailable() throws IOException {
        CreateDraft createDraft = new CreateDraft(objectMapper.readTree("{}"), draftType, maxAge);
        HttpEntity<CreateDraft> httpEntity = new HttpEntity<>(createDraft, headers);
        given(entityFactory.createRequestEntityForDraft(JWT, secret, createDraft))
            .willReturn(httpEntity);

        mockServer
            .expect(requestTo(draftsApiBaseUrl + "/drafts"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        underTest.createDraft(JWT, secret, createDraft);

    }

    @Test
    public void updateDraftShouldCallTheRestTemplateToUpdateADraft() throws Exception {
        UpdateDraft updateDraft = new UpdateDraft(objectMapper.readTree("{}"), draftType);
        HttpEntity<UpdateDraft> httpEntity = new HttpEntity<>(updateDraft, headers);
        given(entityFactory.createRequestEntityForDraft(JWT, secret, updateDraft))
            .willReturn(httpEntity);

        mockServer
            .expect(requestTo(draftsApiBaseUrl + "/drafts/" + DRAFT_ID))
            .andExpect(method(HttpMethod.PUT))
            .andExpect(header("Authorization", JWT))
            .andExpect(header("ServiceAuthorization", SERVICE_JWT))
            .andExpect(header("Secret", secret))
            .andRespond(withSuccess());

        underTest.updateDraft(JWT, DRAFT_ID, secret, updateDraft);

        mockServer.verify();
    }

    @Test(expected = DraftStoreUnavailableException.class)
    public void updateDraftShouldThrowDraftStoreUnavailableExceptionWhenDraftStoreIsNotAvailable() throws IOException {
        UpdateDraft updateDraft = new UpdateDraft(objectMapper.readTree("{}"), draftType);
        HttpEntity<UpdateDraft> httpEntity = new HttpEntity<>(updateDraft, headers);
        given(entityFactory.createRequestEntityForDraft(JWT, secret, updateDraft))
            .willReturn(httpEntity);

        mockServer
            .expect(requestTo(draftsApiBaseUrl + "/drafts/" + DRAFT_ID))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));


        underTest.updateDraft(JWT, DRAFT_ID, secret, updateDraft);

    }

    @Test
    public void deleteDraftShouldCallTheRestTemplateToDeleteADraft() {
        headers = new HttpHeaders();
        headers.add("Authorization", JWT);
        headers.add("ServiceAuthorization", SERVICE_JWT);

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        given(entityFactory.createRequestEntityFroDraft(JWT))
            .willReturn(httpEntity);

        mockServer
            .expect(requestTo(draftsApiBaseUrl + "/drafts/" + DRAFT_ID))
            .andExpect(method(HttpMethod.DELETE))
            .andExpect(header("Authorization", JWT))
            .andExpect(header("ServiceAuthorization", SERVICE_JWT))
            .andRespond(withSuccess());

        underTest.deleteDraft(JWT, DRAFT_ID);

        mockServer.verify();
    }

    @Test(expected = DraftStoreUnavailableException.class)
    public void deleteDraftShouldThrowDraftStoreUnavailableExceptionWhenDraftStoreIsNotAvailable() {
        headers = new HttpHeaders();
        headers.add("Authorization", JWT);
        headers.add("ServiceAuthorization", SERVICE_JWT);

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        given(entityFactory.createRequestEntityFroDraft(JWT))
            .willReturn(httpEntity);

        mockServer
            .expect(requestTo(draftsApiBaseUrl + "/drafts/" + DRAFT_ID))
            .andExpect(method(HttpMethod.DELETE))
            .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        underTest.deleteDraft(JWT, DRAFT_ID);
    }
}
