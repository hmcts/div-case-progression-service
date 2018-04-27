package uk.gov.hmcts.reform.divorce.transformservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;
import uk.gov.hmcts.reform.divorce.transformservice.client.CcdClient;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

@Component
@Slf4j
public class SubmissionService {
    private static final String EVENT_SUMMARY = "Create case";

    @Autowired
    private CcdClient ccdClient;

    @Autowired
    private DraftsService draftsService;

    @Autowired
    private TransformationService transformationService;

    @Autowired
    private UserService userService;

    public long submit(final DivorceSession divorceSessionData, final String jwt) {
        UserDetails userDetails = userService.getUserDetails(jwt);
        CreateEvent createEvent = ccdClient.createCase(userDetails, jwt);

        SubmitEvent submitEvent = ccdClient.submitCase(userDetails, jwt,
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
