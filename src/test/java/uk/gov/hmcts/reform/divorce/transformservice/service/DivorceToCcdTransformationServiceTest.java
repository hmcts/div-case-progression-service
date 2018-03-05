package uk.gov.hmcts.reform.divorce.transformservice.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.DivorceCaseToCCDMapper;

@RunWith(MockitoJUnitRunner.class)
public class DivorceToCcdTransformationServiceTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DivorceCaseToCCDMapper divorceCaseToCCDMapper;

    @InjectMocks
    private DivorceToCcdTransformationService transformationService;

    @Test
    public void shouldTransformDivorceSessionToCaseDataContent() throws Exception {
        String token = "_token";
        String eventId = "event-id";
        String eventSummary = "event-summary";

        Map<String, String> caseData = new HashMap<>();
        caseData.put("test", "data");

        DivorceSession divorceSession = mock(DivorceSession.class);
        CaseDetails caseDetails = mock(CaseDetails.class);
        CoreCaseData coreCaseData = mock(CoreCaseData.class);

        CreateEvent createEvent = new CreateEvent(token, eventId, caseDetails);

        when(divorceCaseToCCDMapper.divorceCaseDataToCourtCaseData(divorceSession)).thenReturn(coreCaseData);
        when(objectMapper.setSerializationInclusion(eq(Include.NON_NULL))).thenReturn(objectMapper);
        when(objectMapper.convertValue(eq(coreCaseData), eq(Map.class))).thenReturn(caseData);

        CaseDataContent caseDataContent = transformationService.transform(divorceSession, createEvent, eventSummary);

        assertThat(caseDataContent.getToken(), equalTo(token));
        assertThat(caseDataContent.getEvent().getEventId(), equalTo(eventId));
        assertThat(caseDataContent.getEvent().getSummary(), equalTo(eventSummary));
        assertThat(caseDataContent.getData(), equalTo(caseData));

        verify(divorceCaseToCCDMapper).divorceCaseDataToCourtCaseData(divorceSession);
        verify(objectMapper).setSerializationInclusion(eq(Include.NON_NULL));
        verify(objectMapper).convertValue(eq(coreCaseData), eq(Map.class));
        verifyNoMoreInteractions(divorceCaseToCCDMapper, objectMapper);
    }
}