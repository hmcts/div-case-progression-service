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
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubmitCcdClientTest {

    @Mock
    private CcdClientConfiguration ccdClientConfiguration;

    @Mock
    private TransformationHttpEntityFactory httpEntityFactory;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SubmitCcdClient ccdClient;

    @Test
    @SuppressWarnings("unchecked")
    public void createCaseReturnsCreateEvent() {
        String encodedJwt = "_jwt";
        String id = "60";
        UserDetails userDetails = UserDetails.builder().id(id).build();
        HttpEntity<String> httpEntity = mock(HttpEntity.class);
        ResponseEntity<CreateEvent> responseEntity = mock(ResponseEntity.class);

        CreateEvent createEvent = new CreateEvent();

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/event-triggers/create/"
                + "token?ignore-warning=true")
            .buildAndExpand("CCD_BASE_URL", id, "JID", "CTID");

        String urlString = uri.toUriString();

        when(httpEntityFactory.createRequestEntityForCcdGet(encodedJwt)).thenReturn(httpEntity);
        when(ccdClientConfiguration.getCreateCaseUrl(eq(id))).thenReturn(urlString);

        when(restTemplate.exchange(eq(urlString), eq(HttpMethod.GET), eq(httpEntity), eq(CreateEvent.class)))
            .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(createEvent);

        assertEquals(createEvent, ccdClient.createCase(userDetails, encodedJwt));

        verify(httpEntityFactory).createRequestEntityForCcdGet(encodedJwt);
        verify(ccdClientConfiguration).getCreateCaseUrl(eq(id));
        verify(restTemplate).exchange(eq(urlString), eq(HttpMethod.GET), eq(httpEntity), eq(CreateEvent.class));
        verify(responseEntity).getBody();

        verifyNoMoreInteractions(httpEntityFactory, restTemplate, responseEntity);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void submitCaseReturnsSubmitEvent()  {
        String userId= "60";
        String encodedJwt = "_jwt";
        CaseDataContent coreCaseData = mock(CaseDataContent.class);
        HttpEntity<CaseDataContent> httpEntity = mock(HttpEntity.class);
        UserDetails userDetails = UserDetails.builder().id(userId).build();
        ResponseEntity<SubmitEvent> responseEntity = mock(ResponseEntity.class);
        SubmitEvent submitEvent = new SubmitEvent();

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases?"
                + "ignore-warning=true")
            .buildAndExpand("CCD_BASE_URL", userId, "JID", "CTID");

        String urlString = uri.toUriString();

        when(httpEntityFactory.createRequestEntityForSubmitCase(encodedJwt, coreCaseData)).thenReturn(httpEntity);
        when(ccdClientConfiguration.getSubmitCaseUrl(eq(userId))).thenReturn(urlString);

        when(restTemplate.exchange(eq(urlString), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitEvent.class)))
            .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(submitEvent);

        assertEquals(submitEvent, ccdClient.submitCase(userDetails, encodedJwt, coreCaseData));

        verify(httpEntityFactory).createRequestEntityForSubmitCase(encodedJwt, coreCaseData);
        verify(ccdClientConfiguration).getSubmitCaseUrl(eq(userId));
        verify(restTemplate).exchange(eq(urlString), eq(HttpMethod.POST), eq(httpEntity), eq(SubmitEvent.class));
        verify(responseEntity).getBody();

        verifyNoMoreInteractions(httpEntityFactory, restTemplate, responseEntity);
    }
}
