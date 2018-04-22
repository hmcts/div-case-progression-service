package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.reform.divorce.common.JwtFactory;
import uk.gov.hmcts.reform.divorce.transformservice.domain.Jwt;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseCcdClientConfigurationTest {

    @InjectMocks
    private BaseCcdClientConfiguration ccdClientConfiguration;

    @Mock
    private JwtFactory jwtFactory;

    @Test
    public void startingEventAsCitizenReturnsCitizenEndpointUrl() {
        long userId = 99L;

        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
            .data("citizen, divorce")
            .id(userId)
            .build();

        long caseId = 1234567812345678L;
        String eventId = "paymentMade";

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                + "event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId, eventId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(encodedJwt, caseId, eventId));
    }

    @Test
    public void startingEventAsCaseworkerReturnsCaseworkerEndpointUrl() {
        long userId = 99L;

        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
            .data("caseworker-divorce")
            .id(userId)
            .build();

        long caseId = 1234567812345678L;
        String eventId = "paymentMade";

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/caseworkers/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                + "event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId, eventId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(encodedJwt, caseId, eventId));
    }

    @Test
    public void creatingEventAsCitizenReturnsCitizenEndpointUrl() {
        long userId = 99L;

        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
            .data("citizen, divorce")
            .id(userId)
            .build();

        long caseId = 1234567812345678L;

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/events"
                + "?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(encodedJwt, caseId));
    }

    @Test
    public void creatingEventAsCaseworkerReturnsCaseworkerEndpointUrl() {
        long userId = 99L;

        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
            .data("caseworker-divorce")
            .id(userId)
            .build();

        long caseId = 1234567812345678L;

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/caseworkers/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                + "events?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(encodedJwt, caseId));
    }

    @Test
    public void startingEventWithNullDataReturnsCitizenEndpointUrl() {
        long userId = 99L;

        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
            .data(null)
            .id(userId)
            .build();

        long caseId = 1234567812345678L;
        String eventId = "paymentMade";

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                + "event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId, eventId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(encodedJwt, caseId, eventId));
    }

    @Test
    public void creatingEventWithNullDataReturnsCitizenEndpointUrl() {
        long userId = 99L;

        String encodedJwt = "_jwt";
        Jwt jwt = Jwt.builder()
            .data(null)
            .id(userId)
            .build();

        long caseId = 1234567812345678L;

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/events?"
                + "ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId);

        when(jwtFactory.create(encodedJwt)).thenReturn(jwt);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(encodedJwt, caseId));
    }
}
