package uk.gov.hmcts.reform.divorce.transformservice.service;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = CaseProgressionApplication.class)
public class DivorceToCcdTransformationServiceTest {
    @Autowired
    private DivorceToCcdTransformationService transformationService;

    @Test
    public void shouldTransformDivorceSessionToCaseDataContent() {
        String token = "_token";
        String eventId = "event-id";
        String eventSummary = "event-summary";

        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorceBehaviourDetails(ImmutableList.of("Hello"));
        CaseDetails caseDetails = new CaseDetails();

        CreateEvent createEvent = new CreateEvent(token, eventId, caseDetails);

        CaseDataContent caseDataContent = transformationService.transform(divorceSession, createEvent, eventSummary);

        assertThat(caseDataContent.getToken(), equalTo(token));
        assertThat(caseDataContent.getEvent().getEventId(), equalTo(eventId));
        assertThat(caseDataContent.getEvent().getSummary(), equalTo(eventSummary));

    }
}
