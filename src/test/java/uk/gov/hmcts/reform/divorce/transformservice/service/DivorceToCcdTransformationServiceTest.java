package uk.gov.hmcts.reform.divorce.transformservice.service;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = CaseProgressionApplication.class)
public class DivorceToCcdTransformationServiceTest {
    @Autowired
    private DivorceToCcdTransformationService transformationService;
    private static final String TOKEN = "_token";
    private static final String EVENT_ID = "event-id";
    private static final String EVENT_SUMMARY = "event-summary";

    @Test
    public void shouldTransformDivorceSessionToCaseDataContentWhenSubmitting() {

        DivorceSession divorceSession = mock(DivorceSession.class);
        CaseDetails caseDetails = new CaseDetails();

        CreateEvent createEvent = new CreateEvent(TOKEN, EVENT_ID, caseDetails);

        CaseDataContent caseDataContent = transformationService
            .transformSubmission(divorceSession, createEvent, EVENT_SUMMARY);

        assertThat(caseDataContent.getToken(), equalTo(TOKEN));
        assertThat(caseDataContent.getEvent().getEventId(), equalTo(EVENT_ID));
        assertThat(caseDataContent.getEvent().getSummary(), equalTo(EVENT_SUMMARY));
        assertThat(caseDataContent.getData().get("createdDate"),
            equalTo(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()));
    }

    @Test
    public void shouldTransformDivorceSessionDataWhenSubmitting() {

        // given
        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setPetitionerPhoneNumber("07724879304");

        CaseDetails caseDetails = new CaseDetails();

        CreateEvent createEvent = new CreateEvent(TOKEN, EVENT_ID, caseDetails);

        // when
        CaseDataContent caseDataContent = transformationService
            .transformSubmission(divorceSession, createEvent, EVENT_SUMMARY);

        // then
        assertNotNull(caseDataContent.getData().get("D8PetitionerPhoneNumber"));
    }

    @Test
    public void shouldTransformDivorceSessionToCaseDataContentWhenUpdating() {

        DivorceSession divorceSession = mock(DivorceSession.class);
        CaseDetails caseDetails = new CaseDetails();

        CreateEvent createEvent = new CreateEvent(TOKEN, EVENT_ID, caseDetails);

        CaseDataContent caseDataContent = transformationService
            .transformUpdate(divorceSession, createEvent, EVENT_SUMMARY);

        assertThat(caseDataContent.getToken(), equalTo(TOKEN));
        assertThat(caseDataContent.getEvent().getEventId(), equalTo(EVENT_ID));
        assertThat(caseDataContent.getEvent().getSummary(), equalTo(EVENT_SUMMARY));
        assertNull(caseDataContent.getData().get("createdDate"));
    }
}
