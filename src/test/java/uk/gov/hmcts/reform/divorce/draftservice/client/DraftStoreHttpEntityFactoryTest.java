package uk.gov.hmcts.reform.divorce.draftservice.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import uk.gov.hmcts.reform.divorce.draftservice.domain.CreateDraft;
import uk.gov.hmcts.reform.divorce.draftservice.domain.UpdateDraft;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class DraftStoreHttpEntityFactoryTest {

    private static final String JWT = "Bearer hgsdja87wegqeuf...";
    private static final String SERVICE_JWT = "Bearer drhodsrotnsr7...";
    private static final String SECRET = "SecretValue";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String SERVICE_AUTHORIZATION_HEADER_NAME = "ServiceAuthorization";
    private static final String SECRET_HEADER_NAME = "Secret";

    @Mock
    private AuthTokenGenerator serviceTokenGenerator;

    @Mock
    private CreateDraft createDraft;

    @Mock
    private UpdateDraft updateDraft;

    @InjectMocks
    private DraftStoreHttpEntityFactory underTest;


    @Before
    public void setUp() {
        given(serviceTokenGenerator.generate()).willReturn(SERVICE_JWT);
    }

    @Test
    public void createRequestEntityForDraftShouldReturnAuthorizationServiceAuthorizationSecretAndTheCreateDraft() {
        HttpEntity<CreateDraft> createDraftHttpEntity =
                underTest.createRequestEntityForDraft(JWT, SECRET, createDraft);

        assertEquals(JWT, createDraftHttpEntity.getHeaders().get(AUTHORIZATION_HEADER_NAME).get(0));
        assertEquals(SERVICE_JWT, createDraftHttpEntity.getHeaders().get(SERVICE_AUTHORIZATION_HEADER_NAME).get(0));
        assertEquals(SECRET, createDraftHttpEntity.getHeaders().get(SECRET_HEADER_NAME).get(0));
        assertEquals(createDraft, createDraftHttpEntity.getBody());

    }

    @Test
    public void createRequestEntityForDraftShouldReturnAuthorizationServiceAuthorizationSecretAndTheUpdateDraft() {
        HttpEntity<UpdateDraft> updateDraftHttpEntity =
                underTest.createRequestEntityForDraft(JWT, SECRET, updateDraft);

        assertEquals(JWT, updateDraftHttpEntity.getHeaders().get(AUTHORIZATION_HEADER_NAME).get(0));
        assertEquals(SERVICE_JWT, updateDraftHttpEntity.getHeaders().get(SERVICE_AUTHORIZATION_HEADER_NAME).get(0));
        assertEquals(SECRET, updateDraftHttpEntity.getHeaders().get(SECRET_HEADER_NAME).get(0));
        assertEquals(updateDraft, updateDraftHttpEntity.getBody());
    }

    @Test
    public void createRequestEntityFroDraftShouldReturnAuthorizationServiceAuthorizationAndSecret() {
        HttpEntity<Void> httpEntity = underTest.createRequestEntityFroDraft(JWT, SECRET);

        assertEquals(JWT, httpEntity.getHeaders().get(AUTHORIZATION_HEADER_NAME).get(0));
        assertEquals(SERVICE_JWT, httpEntity.getHeaders().get(SERVICE_AUTHORIZATION_HEADER_NAME).get(0));
        assertEquals(SECRET, httpEntity.getHeaders().get(SECRET_HEADER_NAME).get(0));
    }

    @Test
    public void createRequestEntityFroDraftShouldReturnAuthorizationAndServiceAuthorization() {
        HttpEntity<Void> httpEntity = underTest.createRequestEntityFroDraft(JWT);

        assertEquals(JWT, httpEntity.getHeaders().get(AUTHORIZATION_HEADER_NAME).get(0));
        assertEquals(SERVICE_JWT, httpEntity.getHeaders().get(SERVICE_AUTHORIZATION_HEADER_NAME).get(0));

    }
}