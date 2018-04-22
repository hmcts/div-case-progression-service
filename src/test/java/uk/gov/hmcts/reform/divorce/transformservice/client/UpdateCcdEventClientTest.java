package uk.gov.hmcts.reform.divorce.transformservice.client;

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
import uk.gov.hmcts.reform.divorce.common.JwtFactory;
import uk.gov.hmcts.reform.divorce.transformservice.domain.Jwt;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CaseEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateCcdEventClientTest {

    private Long caseId = 123456789L;
    private String eventId = "paymentMade";

    @Mock
    private CcdClientConfiguration ccdClientConfiguration;

    @Mock
    private TransformationHttpEntityFactory httpEntityFactory;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JwtFactory jwtFactory;

    @InjectMocks
    private UpdateCcdEventClient updateCcdEventClient;

    @Test
    @SuppressWarnings("unchecked")
    public void startEventReturnsCreateEvent() {
        String encodedJwt = "_jwt";
        Jwt jwt = mock(Jwt.class);
        long id = 60;

        HttpEntity<String> httpEntity = mock(HttpEntity.class);
        ResponseEntity<CreateEvent> responseEntity = mock(ResponseEntity.class);

        CreateEvent createEvent = new CreateEvent();

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                + "event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("CCD_BASE_URL", id, "JID", "CTID", caseId, eventId);

        String urlString = uri.toUriString();

        when(httpEntityFactory.createRequestEntityForCcdGet(encodedJwt)).thenReturn(httpEntity);
        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);
        when(jwt.getId()).thenReturn(id);

        when(ccdClientConfiguration.getStartEventUrl(eq(encodedJwt), eq(caseId), eq(eventId))).thenReturn(urlString);

        when(restTemplate.exchange(eq(urlString), eq(HttpMethod.GET), eq(httpEntity), eq(CreateEvent.class)))
            .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(createEvent);

        assertEquals(createEvent, updateCcdEventClient.startEvent(encodedJwt, caseId, eventId));

        verify(httpEntityFactory).createRequestEntityForCcdGet(encodedJwt);
        verify(ccdClientConfiguration).getStartEventUrl(eq(encodedJwt), eq(caseId), eq(eventId));
        verify(restTemplate).exchange(eq(urlString), eq(HttpMethod.GET), eq(httpEntity), eq(CreateEvent.class));
        verify(responseEntity).getBody();

        verifyNoMoreInteractions(httpEntityFactory, jwtFactory, jwt, restTemplate, responseEntity);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createCaseEventReturnsCaseEvent() {
        String encodedJwt = "_jwt";
        CaseDataContent coreCaseData = mock(CaseDataContent.class);
        HttpEntity<CaseDataContent> httpEntity = mock(HttpEntity.class);
        Jwt jwt = mock(Jwt.class);
        ResponseEntity<CaseEvent> responseEntity = mock(ResponseEntity.class);
        CaseEvent caseEvent = new CaseEvent();

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/events?"
                + "ignore-warning=true")
            .buildAndExpand("CCD_BASE_URL", jwt.getId(), "JID", "CTID", caseId);

        String urlString = uri.toUriString();

        when(httpEntityFactory.createRequestEntityForSubmitCase(encodedJwt, coreCaseData)).thenReturn(httpEntity);
        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        when(ccdClientConfiguration.getCreateCaseEventUrl(eq(encodedJwt), eq(caseId))).thenReturn(urlString);

        when(restTemplate.exchange(eq(urlString), eq(HttpMethod.POST), eq(httpEntity), eq(CaseEvent.class)))
            .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(caseEvent);

        assertEquals(caseEvent, updateCcdEventClient.createCaseEvent(encodedJwt, caseId, coreCaseData));

        verify(httpEntityFactory).createRequestEntityForSubmitCase(encodedJwt, coreCaseData);
        verify(ccdClientConfiguration).getCreateCaseEventUrl(eq(encodedJwt), eq(caseId));
        verify(restTemplate).exchange(eq(urlString), eq(HttpMethod.POST), eq(httpEntity), eq(CaseEvent.class));
        verify(responseEntity).getBody();

        verifyNoMoreInteractions(httpEntityFactory, jwtFactory, restTemplate, responseEntity);
    }
}
