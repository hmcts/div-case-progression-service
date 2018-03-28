package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;
import uk.gov.hmcts.reform.divorce.transformservice.client.CcdClient;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

@Component
public class SubmissionService {
    private static final String EVENT_SUMMARY = "Create case";

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);

    @Autowired
    private CcdClient ccdClient;

    @Autowired
    private DraftsService draftsService;

    @Autowired
    private TransformationService transformationService;

    public long submit(final DivorceSession divorceSessionData, final String jwt) {
        CreateEvent createEvent = ccdClient.createCase(jwt);

        SubmitEvent submitEvent = ccdClient.submitCase(jwt,
            transformationService.transform(divorceSessionData, createEvent, EVENT_SUMMARY));

        try {
            draftsService.deleteDraft(jwt);
        } catch (Exception e) {
            // we do not want to send an error response to the front end if deleting the draft fails
            log.warn("Could not delete the draft for case id {}", submitEvent.getCaseId());
        }

        log.info("Case Id: {} ", submitEvent.getCaseId());
        return submitEvent.getCaseId();
    }
}
