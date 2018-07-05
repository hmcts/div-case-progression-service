package uk.gov.hmcts.reform.divorce.transformservice.service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Event;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.DivorceCaseToCCDMapper;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.DivorceCaseToCCDSubmissionMapper;

import java.util.Map;
import java.util.Objects;

@Component
public class DivorceToCcdTransformationService implements TransformationService {

    private final ObjectMapper objectMapper;

    private final DivorceCaseToCCDMapper divorceCaseToCCDMapper;
    private final DivorceCaseToCCDSubmissionMapper divorceCaseToCCDSubmissionMapper;

    @Autowired
    public DivorceToCcdTransformationService(@Qualifier("divorceCaseToCCDMapperImpl") DivorceCaseToCCDMapper divorceCaseToCCDMapper,
                                             @Qualifier("divorceCaseToCCDSubmissionMapperImpl")DivorceCaseToCCDSubmissionMapper divorceCaseToCCDSubmissionMapper) {
        this.divorceCaseToCCDMapper = divorceCaseToCCDMapper;
        this.divorceCaseToCCDSubmissionMapper = divorceCaseToCCDSubmissionMapper;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CaseDataContent transformSubmission(DivorceSession divorceSession, CreateEvent createEvent, String eventSummary) {

        if (Objects.nonNull(createEvent.getCaseDetails().getCaseData())) {
            divorceSession.setExistingPayments(createEvent.getCaseDetails().getCaseData().getPayments());
        }

        CoreCaseData coreCaseData = divorceCaseToCCDSubmissionMapper.divorceCaseDataToCourtCaseData(divorceSession);

        return CaseDataContent.builder()
            .data(objectMapper.convertValue(coreCaseData, Map.class))
            .token(createEvent.getToken())
            .event(Event.builder().eventId(createEvent.getEventId()).summary(eventSummary).build())
            .build();

    }

    @Override
    @SuppressWarnings("unchecked")
    public CaseDataContent transformUpdate(DivorceSession divorceSession, CreateEvent createEvent, String eventSummary) {

        if (Objects.nonNull(createEvent.getCaseDetails().getCaseData())) {
            divorceSession.setExistingPayments(createEvent.getCaseDetails().getCaseData().getPayments());
        }

        CoreCaseData coreCaseData = divorceCaseToCCDMapper.divorceCaseDataToCourtCaseData(divorceSession);

        return CaseDataContent.builder()
                .data(objectMapper.convertValue(coreCaseData, Map.class))
                .token(createEvent.getToken())
                .event(Event.builder().eventId(createEvent.getEventId()).summary(eventSummary).build())
                .build();

    }
}
