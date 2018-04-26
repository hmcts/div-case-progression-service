package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BaseCcdClientConfigurationTest {

    private static final String CASEWORKER_DIVORCE = "caseworker-divorce";

    @InjectMocks
    private BaseCcdClientConfiguration ccdClientConfiguration;

    @Test
    public void startingEventAsCitizenReturnsCitizenEndpointUrl() {
        String userId = "99";
        UserDetails userDetails = UserDetails.builder().id(userId).roles(Collections.emptyList()).build();

        long caseId = 1234567812345678L;
        String eventId = "paymentMade";

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                + "event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId, eventId);


        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(userDetails, caseId, eventId));
    }

    @Test
    public void startingEventAsCaseworkerReturnsCaseworkerEndpointUrl() {
        String userId = "99";
        UserDetails userDetails = UserDetails.builder().id(userId).roles(Collections.singletonList(CASEWORKER_DIVORCE)).build();


        String encodedJwt = "_jwt";

        long caseId = 1234567812345678L;
        String eventId = "paymentMade";

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/caseworkers/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                + "event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId, eventId);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(userDetails, caseId, eventId));
    }

    @Test
    public void creatingEventAsCitizenReturnsCitizenEndpointUrl() {
        String userId = "99";
        UserDetails userDetails = UserDetails.builder().id(userId).roles(Collections.emptyList()).build();

        long caseId = 1234567812345678L;

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/events"
                + "?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(userDetails, caseId));
    }

    @Test
    public void creatingEventAsCaseworkerReturnsCaseworkerEndpointUrl() {
        String userId = "99";
        UserDetails userDetails = UserDetails.builder().id(userId).roles(Collections.singletonList(CASEWORKER_DIVORCE)).build();


        String encodedJwt = "_jwt";

        long caseId = 1234567812345678L;

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/caseworkers/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                + "events?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(userDetails, caseId));
    }

    @Test
    public void startingEventWithNullDataReturnsCitizenEndpointUrl() {
        String userId = "99";
        UserDetails userDetails = UserDetails.builder().id(userId).roles(Collections.emptyList()).build();

        long caseId = 1234567812345678L;
        String eventId = "paymentMade";

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                + "event-triggers/{eventId}/token?ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId, eventId);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(userDetails, caseId, eventId));
    }

    @Test
    public void creatingEventWithNullDataReturnsCitizenEndpointUrl() {
        String userId = "99";
        UserDetails userDetails = UserDetails.builder().id(userId).roles(Collections.emptyList()).build();

        long caseId = 1234567812345678L;

        UriComponents uri = UriComponentsBuilder.fromUriString(
            "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/events?"
                + "ignore-warning=true")
            .buildAndExpand("null", userId, "null", "null", caseId);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(userDetails, caseId));
    }
}
