package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;
import uk.gov.hmcts.reform.divorce.transformservice.client.SubmitCcdClient;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubmissionServiceTest {

    @Mock
    private SubmitCcdClient ccdClient;

    @Mock
    private TransformationService transformationService;

    @Mock
    private DraftsService draftsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SubmissionService submissionService;

    @Test
    public void submitReturnsCaseId() throws Exception {
        final DivorceSession divorceSession = new DivorceSession();
        final CaseDataContent caseDataContent = mock(CaseDataContent.class);
        String jwt = "_jwt";
        String token = "_token";
        final String eventSummary = "Create case";
        int caseId = 2893;
        String userId = "60";
        UserDetails userDetails = UserDetails.builder().id(userId).build();
        CreateEvent createEvent = new CreateEvent();
        createEvent.setToken(token);
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseId(caseId);

        when(userService.getUserDetails(jwt)).thenReturn(userDetails);
        when(ccdClient.createCase(userDetails, jwt, divorceSession)).thenReturn(createEvent);
        when(transformationService
            .transformSubmission(divorceSession, createEvent, eventSummary)).thenReturn(caseDataContent);
        when(ccdClient.submitCase(userDetails, jwt, caseDataContent)).thenReturn(submitEvent);

        assertThat(submissionService.submit(divorceSession, jwt)).isEqualTo(caseId);

        verify(ccdClient).createCase(userDetails, jwt, divorceSession);
        verify(transformationService).transformSubmission(divorceSession, createEvent, eventSummary);
        verify(ccdClient).submitCase(userDetails, jwt, caseDataContent);
        verifyNoMoreInteractions(ccdClient, transformationService);
    }

    @Test
    public void submitShouldDeleteTheDivorceDraftAfterSubmissionToCCD() {
        DivorceSession divorceSession = new DivorceSession();
        String jwt = "_jwt";

        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseId(1);

        when(ccdClient.submitCase(any(), anyString(), any())).thenReturn(submitEvent);

        submissionService.submit(divorceSession, jwt);

        verify(draftsService).deleteDraft(jwt);
    }

    @Test
    public void submitShouldNotFailWhenDeletingTheDraftFails() {
        DivorceSession divorceSession = new DivorceSession();
        String jwt = "_jwt";

        UserDetails userDetails = UserDetails.builder().id("1").build();
        when(userService.getUserDetails(jwt)).thenReturn(userDetails);

        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseId(1);

        when(ccdClient.submitCase(any(), anyString(), any())).thenReturn(submitEvent);
        doThrow(Exception.class).when(draftsService).deleteDraft(jwt);

        try {
            submissionService.submit(divorceSession, jwt);
        } catch (Exception e) {
            fail("Submitting to CCD should not fail if the draft cannot be deleted");
        }
    }
}
