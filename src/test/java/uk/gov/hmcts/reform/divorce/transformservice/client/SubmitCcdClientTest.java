package uk.gov.hmcts.reform.divorce.transformservice.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.gov.hmcts.reform.divorce.transformservice.domain.Jwt;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.common.JwtFactory;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCcdClientTest {

    @Mock
    private CcdClientConfiguration ccdClientConfiguration;

    @Mock
    private TransformationHttpEntityFactory httpEntityFactory;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JwtFactory jwtFactory;

    @InjectMocks
    private SubmitCcdClient ccdClient;

    @Test
    @SuppressWarnings("unchecked")
    public void createCaseReturnsCreateEvent() throws Exception {
        String encodedJwt = "_jwt";
        Jwt jwt = mock(Jwt.class);
        long id = 60;

        HttpEntity<String> httpEntity = mock(HttpEntity.class);
        ResponseEntity<CreateEvent> responseEntity = mock(ResponseEntity.class);

        CreateEvent createEvent = new CreateEvent();

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/event-triggers/create/token?ignore-warning=true")
            .buildAndExpand("CCD_BASE_URL", id, "JID", "CTID");

        String urlString = uri.toUriString();

        when(httpEntityFactory.createRequestEntityForCcdGet(encodedJwt)).thenReturn(httpEntity);
        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);
        when(jwt.getId()).thenReturn(id);
        when(ccdClientConfiguration.getCreateCaseUrl(eq(id))).thenReturn(urlString);

        when(restTemplate.exchange(eq(urlString), eq(HttpMethod.GET), eq(httpEntity), eq(CreateEvent.class)))
                .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(createEvent);

        assertEquals(createEvent, ccdClient.createCase(encodedJwt));

        verify(httpEntityFactory).createRequestEntityForCcdGet(encodedJwt);
        verify(jwtFactory).create(encodedJwt);
        verify(jwt).getId();
        verify(ccdClientConfiguration).getCreateCaseUrl(eq(id));
        verify(restTemplate).exchange(eq(urlString), eq(HttpMethod.GET), eq(httpEntity), eq(CreateEvent.class));
        verify(responseEntity).getBody();

        verifyNoMoreInteractions(httpEntityFactory, jwtFactory, jwt, restTemplate, responseEntity);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void submitCaseReturnsSubmitEvent() throws Exception {
        String encodedJwt = "_jwt";
        CaseDataContent coreCaseData = mock(CaseDataContent.class);
        HttpEntity<CaseDataContent> httpEntity = mock(HttpEntity.class);
        Jwt jwt = mock(Jwt.class);
        ResponseEntity<SubmitEvent> responseEntity = mock(ResponseEntity.class);
        SubmitEvent submitEvent = new SubmitEvent();

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases?ignore-warning=true")
            .buildAndExpand("CCD_BASE_URL", jwt.getId(), "JID", "CTID");

        String urlString = uri.toUriString();

        when(httpEntityFactory.createRequestEntityForSubmitCase(encodedJwt, coreCaseData)).thenReturn(httpEntity);
        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);
        when(ccdClientConfiguration.getSubmitCaseUrl(eq(jwt.getId()))).thenReturn(urlString);

        when(restTemplate.exchange(eq(urlString), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitEvent.class)))
                .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(submitEvent);

        assertEquals(submitEvent, ccdClient.submitCase(encodedJwt, coreCaseData));

        verify(httpEntityFactory).createRequestEntityForSubmitCase(encodedJwt, coreCaseData);
        verify(jwtFactory).create(encodedJwt);
        verify(ccdClientConfiguration).getSubmitCaseUrl(eq(jwt.getId()));
        verify(restTemplate).exchange(eq(urlString), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitEvent.class));
        verify(responseEntity).getBody();

        verifyNoMoreInteractions(httpEntityFactory, jwtFactory, restTemplate, responseEntity);
    }
}
