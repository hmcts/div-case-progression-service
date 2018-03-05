package uk.gov.hmcts.reform.divorce.transformservice.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
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
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CaseEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.common.JwtFactory;

@RunWith(MockitoJUnitRunner.class)
public class BaseCcdClientConfigurationTest {

    @Mock
    private JwtFactory jwtFactory;

    @InjectMocks
    BaseCcdClientConfiguration ccdClientConfiguration;

    @Test
    public void startingEventAsCitizenReturnsCitizenEndpointUrl() {
        long userId = 99l;
        
        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
                        .data("citizen, divorce")
                        .id(userId)
                        .build();
        
        long caseId = 1234567812345678l;
        String eventId = "paymentMade";

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId, eventId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(encodedJwt, caseId, eventId));
    }

    @Test
    public void startingEventAsCaseworkerReturnsCaseworkerEndpointUrl() {
        long userId = 99l;
        
        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
                        .data("caseworker-divorce")
                        .id(userId)
                        .build();
        
        long caseId = 1234567812345678l;
        String eventId = "paymentMade";

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/caseworkers/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId, eventId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(encodedJwt, caseId, eventId));
    }

    @Test
    public void creatingEventAsCitizenReturnsCitizenEndpointUrl() {
        long userId = 99l;
        
        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
                        .data("citizen, divorce")
                        .id(userId)
                        .build();
        
        long caseId = 1234567812345678l;

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/events?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(encodedJwt, caseId));
    }

    @Test
    public void creatingEventAsCaseworkerReturnsCaseworkerEndpointUrl() {
        long userId = 99l;
        
        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
                        .data("caseworker-divorce")
                        .id(userId)
                        .build();
        
        long caseId = 1234567812345678l;

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/caseworkers/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/events?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(encodedJwt, caseId));
    }

    @Test
    public void startingEventWithNullDataReturnsCitizenEndpointUrl() {
        long userId = 99l;
        
        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
                        .data(null)
                        .id(userId)
                        .build();
        
        long caseId = 1234567812345678l;
        String eventId = "paymentMade";

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId, eventId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(encodedJwt, caseId, eventId));
    }

    @Test
    public void creatingEventWithNullDataReturnsCitizenEndpointUrl() {
        long userId = 99l;
        
        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
                        .data(null)
                        .id(userId)
                        .build();
        
        long caseId = 1234567812345678l;

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/events?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(encodedJwt, caseId));
    }
}
