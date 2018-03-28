package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;
import uk.gov.hmcts.reform.divorce.transformservice.client.CcdEventClient;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CaseEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceEventSession;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.PdfToCoreCaseDataMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateServiceTest {

    @Mock
    private CcdEventClient ccdEventClient;

    @Mock
    private TransformationService transformationService;

    @Mock
    private PdfService pdfService;

    @Mock
    private PdfToCoreCaseDataMapper pdfToCoreCaseDataMapper;

    @Mock
    private DraftsService draftsService;

    @Mock
    private PetitionValidatorService petitionValidatorService;

    @InjectMocks
    private UpdateService updateService;

    @Test
    public void updateReturnsCaseId() {
        final DivorceEventSession divorceEventSession = new DivorceEventSession();
        final CaseDataContent caseDataContent = mock(CaseDataContent.class);
        final String jwt = "_jwt";
        final String token = "_token";
        final String eventSummary = "Update case";
        final Long caseId = 2893L;
        final String eventId = "paymentMade";

        divorceEventSession.setEventId(eventId);
        divorceEventSession.setEventData(new DivorceSession());

        CreateEvent createEvent = new CreateEvent();
        createEvent.setToken(token);
        CaseEvent caseEvent = new CaseEvent();
        caseEvent.setCaseId(caseId);

        when(ccdEventClient.startEvent(jwt, caseId, eventId)).thenReturn(createEvent);
        when(transformationService.transform(divorceEventSession.getEventData(), createEvent, eventSummary))
            .thenReturn(caseDataContent);
        when(ccdEventClient.createCaseEvent(jwt, caseId, caseDataContent)).thenReturn(caseEvent);

        assertThat(updateService.update(caseId, divorceEventSession, jwt)).isEqualTo(caseId);

        verify(ccdEventClient).startEvent(jwt, caseId, eventId);
        verify(transformationService).transform(divorceEventSession.getEventData(), createEvent, eventSummary);
        verify(ccdEventClient).createCaseEvent(jwt, caseId, caseDataContent);
        verifyNoMoreInteractions(ccdEventClient, transformationService);
    }

    @Test
    public void addPdfAddPdfAndReturnCoreCaseData() {
        final CoreCaseData coreCaseData = new CoreCaseData();
        final CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(coreCaseData);

        final CreateEvent caseDetailsRequest = new CreateEvent();
        caseDetailsRequest.setCaseDetails(caseDetails);

        PdfFile pdfFile = mock(PdfFile.class);

        when(pdfService.generatePdf(caseDetailsRequest)).thenReturn(pdfFile);
        when(pdfToCoreCaseDataMapper.toCoreCaseData(pdfFile, coreCaseData)).thenReturn(coreCaseData);

        assertEquals(coreCaseData, updateService.addPdf(caseDetailsRequest));

        verify(petitionValidatorService).validateFieldsForIssued(caseDetailsRequest);
        verify(pdfService).generatePdf(caseDetailsRequest);
        verify(pdfToCoreCaseDataMapper).toCoreCaseData(pdfFile, coreCaseData);
    }

    @Test
    public void updateShouldDeleteTheDivorceDraftAfterSubmissionToCCD() {
        DivorceEventSession divorceEventSession = new DivorceEventSession();
        String jwt = "_jwt";

        CaseEvent caseEvent = new CaseEvent();
        caseEvent.setCaseId(1L);
        when(ccdEventClient.createCaseEvent(anyString(), anyLong(), any()))
            .thenReturn(caseEvent);

        updateService.update(1L, divorceEventSession, jwt);

        verify(draftsService).deleteDraft(jwt);
    }

    @Test
    public void updateShouldNotThrowExceptionWhenTheDraftCannotBeDeleted() {
        DivorceEventSession divorceEventSession = new DivorceEventSession();
        String jwt = "_jwt";

        CaseEvent caseEvent = new CaseEvent();
        caseEvent.setCaseId(1L);
        when(ccdEventClient.createCaseEvent(anyString(), anyLong(), any()))
            .thenReturn(caseEvent);
        doThrow(Exception.class).when(draftsService).deleteDraft(jwt);

        try {
            updateService.update(1L, divorceEventSession, jwt);
        } catch (Exception e) {
            fail("Submission to CCD should not fail if the draft cannot be deleted");
        }
    }

}
