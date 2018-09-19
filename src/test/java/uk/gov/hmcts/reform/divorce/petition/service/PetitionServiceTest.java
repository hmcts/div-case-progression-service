package uk.gov.hmcts.reform.divorce.petition.service;

import feign.FeignException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.petition.domain.Petition;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PetitionServiceTest {

    private static final String JWT_TOKEN = "Bearer Something";
    private static final String CASE_ID = "CaseId";

    @Mock
    private CmsApiClient mockCmsClient;
    @Mock
    private CfsApiClient mockCfsClient;

    @InjectMocks
    private PetitionService underTest;

    @Test
    public void retrievePetitionShouldReturnNullWhenNoCaseDetailsFound() {

        // given
        CaseDetails caseDetails = null;
        given(mockCmsClient
            .retrieveCaseDetails(JWT_TOKEN))
            .willReturn(caseDetails);

        // when
        Petition petition = underTest.retrievePetition(JWT_TOKEN);

        // then
        assertNull(petition);
        verifyZeroInteractions(mockCfsClient);
    }

    @Test
    public void retrievePetitionShouldReturnNullWhenCmsThrowsException() {

        // given
        FeignException exception = mock(FeignException.class);

        given(mockCmsClient
            .retrieveCaseDetails(JWT_TOKEN))
            .willThrow(exception);

        // when
        Petition petition = underTest.retrievePetition(JWT_TOKEN);

        // then
        assertNull(petition);
        verifyZeroInteractions(mockCfsClient);
    }

    @Test
    public void retrievePetitionShouldReturnNullWhenCaseIsNotInAwaitingDecreeNisiState() {

        // given
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId(CASE_ID);
        caseDetails.setState("AwaitingPayment");
        CoreCaseData caseData = new CoreCaseData();
        caseDetails.setCaseData(caseData);

        given(mockCmsClient
            .retrieveCaseDetails(JWT_TOKEN))
            .willReturn(caseDetails);

        DivorceSession expectedDivorceSession = mock(DivorceSession.class);
        given(mockCfsClient
            .transform(caseData))
            .willReturn(expectedDivorceSession);

        // when
        Petition petition = underTest.retrievePetition(JWT_TOKEN);

        // then
        assertNull(petition);
        verifyZeroInteractions(mockCfsClient);
    }

    @Test
    public void retrievePetitionShouldReturnPetition() {

        // given
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId(CASE_ID);
        caseDetails.setState("AwaitingDecreeNisi");
        CoreCaseData caseData = new CoreCaseData();
        caseDetails.setCaseData(caseData);

        given(mockCmsClient
            .retrieveCaseDetails(JWT_TOKEN))
            .willReturn(caseDetails);

        DivorceSession expectedDivorceSession = mock(DivorceSession.class);
        given(mockCfsClient
            .transform(caseData))
            .willReturn(expectedDivorceSession);

        // when
        Petition petition = underTest.retrievePetition(JWT_TOKEN);

        // then
        assertNotNull(petition);
        assertEquals(expectedDivorceSession, petition.getDivorceCase());
        assertEquals(CASE_ID, petition.getCaseId());
    }
}
