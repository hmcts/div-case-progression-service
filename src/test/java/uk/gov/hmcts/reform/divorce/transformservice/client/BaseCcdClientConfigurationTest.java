package uk.gov.hmcts.reform.divorce.transformservice.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.YesNoAnswer;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BaseCcdClientConfigurationTest {

    private static final String CASEWORKER_DIVORCE = "caseworker-divorce";
    private static final String USER_ID = "99";
    private static final long CASE_ID = 1234567812345678L;
    private static final String EVENT_ID = "paymentMade";
    private static final String CCD_BASE_URL = "ccdBaseUrl";
    private static final String CASE_TYPE_ID = "caseTypeId";
    private static final String JURISDICTION_ID = "jurisdictionId";

    private BaseCcdClientConfiguration ccdClientConfiguration;

    @Before
    public void setUp() throws Exception {
        ccdClientConfiguration = new BaseCcdClientConfiguration(CASEWORKER_DIVORCE, JURISDICTION_ID, CASE_TYPE_ID,
                CCD_BASE_URL);
    }

    @Test
    public void getCreateCaseUrl_should_return_correct_url_when_help_with_fees_is_not_required() {

        // given
        YesNoAnswer helpWithFees = YesNoAnswer.NO;

        String expectedUrl = "ccdBaseUrl/citizens/99/jurisdictions/jurisdictionId/case-types/caseTypeId/"
                + "event-triggers/create/token?ignore-warning=true";

        // when
        String url = ccdClientConfiguration.getCreateCaseUrl(USER_ID, helpWithFees);

        // then
        assertEquals(expectedUrl, url);
    }

    @Test
    public void getCreateCaseUrl_should_return_correct_url_when_help_with_fees_is_required() {

        // given
        YesNoAnswer helpWithFees = YesNoAnswer.YES;

        String expectedUrl = "ccdBaseUrl/citizens/99/jurisdictions/jurisdictionId/case-types/caseTypeId/"
                + "event-triggers/hwfCreate/token?ignore-warning=true";

        // when
        String url = ccdClientConfiguration.getCreateCaseUrl(USER_ID, helpWithFees);

        // then
        assertEquals(expectedUrl, url);
    }

    @Test
    public void startingEventAsCitizenReturnsCitizenEndpointUrl() {

        UserDetails userDetails = UserDetails.builder().id(USER_ID).roles(Collections.emptyList()).build();

        UriComponents uri = UriComponentsBuilder.fromUriString(
                "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                        + "event-triggers/{eventId}/token?ignore-warning=true")
                .buildAndExpand(CCD_BASE_URL, USER_ID, JURISDICTION_ID, CASE_TYPE_ID, CASE_ID, EVENT_ID);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(userDetails, CASE_ID, EVENT_ID));
    }

    @Test
    public void startingEventAsCaseworkerReturnsCaseworkerEndpointUrl() {

        UserDetails userDetails = UserDetails.builder()
                .id(USER_ID).roles(Collections.singletonList(CASEWORKER_DIVORCE)).build();

        UriComponents uri = UriComponentsBuilder.fromUriString(
                "{ccdBaseUrl}/caseworkers/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                        + "event-triggers/{eventId}/token?ignore-warning=true")
                .buildAndExpand(CCD_BASE_URL, USER_ID, JURISDICTION_ID, CASE_TYPE_ID, CASE_ID, EVENT_ID);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(userDetails, CASE_ID, EVENT_ID));
    }

    @Test
    public void creatingEventAsCitizenReturnsCitizenEndpointUrl() {

        UserDetails userDetails = UserDetails.builder().id(USER_ID).roles(Collections.emptyList()).build();

        UriComponents uri = UriComponentsBuilder.fromUriString(
                "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                        + "events?ignore-warning=true")
                .buildAndExpand(CCD_BASE_URL, USER_ID, JURISDICTION_ID, CASE_TYPE_ID, CASE_ID);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(userDetails, CASE_ID));
    }

    @Test
    public void creatingEventAsCaseworkerReturnsCaseworkerEndpointUrl() {

        UserDetails userDetails = UserDetails.builder()
                .id(USER_ID).roles(Collections.singletonList(CASEWORKER_DIVORCE)).build();

        UriComponents uri = UriComponentsBuilder.fromUriString(
                "{ccdBaseUrl}/caseworkers/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/"
                        + "{caseId}/events?ignore-warning=true")
                .buildAndExpand(CCD_BASE_URL, USER_ID, JURISDICTION_ID, CASE_TYPE_ID, CASE_ID);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(userDetails, CASE_ID));
    }

    @Test
    public void startingEventWithNullDataReturnsCitizenEndpointUrl() {

        UserDetails userDetails = UserDetails.builder().id(USER_ID).roles(Collections.emptyList()).build();

        UriComponents uri = UriComponentsBuilder.fromUriString(
                "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                        + "event-triggers/{eventId}/token?ignore-warning=true")
                .buildAndExpand(CCD_BASE_URL, USER_ID, JURISDICTION_ID, CASE_TYPE_ID, CASE_ID, EVENT_ID);

        assertEquals(uri.toString(), ccdClientConfiguration.getStartEventUrl(userDetails, CASE_ID, EVENT_ID));
    }

    @Test
    public void creatingEventWithNullDataReturnsCitizenEndpointUrl() {

        UserDetails userDetails = UserDetails.builder().id(USER_ID).roles(Collections.emptyList()).build();

        UriComponents uri = UriComponentsBuilder.fromUriString(
                "{ccdBaseUrl}/citizens/{id}/jurisdictions/{jurisdictionId}/case-types/{caseTypeId}/cases/{caseId}/"
                        + "events?ignore-warning=true")
                .buildAndExpand(CCD_BASE_URL, USER_ID, JURISDICTION_ID, CASE_TYPE_ID, CASE_ID);

        assertEquals(uri.toString(), ccdClientConfiguration.getCreateCaseEventUrl(userDetails, CASE_ID));
    }
}