package uk.gov.hmcts.reform.divorce.transformservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;
import uk.gov.hmcts.reform.divorce.transformservice.client.SubmitCcdClient;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.SubmitEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;
import uk.gov.hmcts.reform.divorce.transformservice.exception.DuplicateCaseException;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SubmissionService {
    private static final String EVENT_SUMMARY = "Create case";

    @Autowired
    private SubmitCcdClient ccdClient;

    @Autowired
    private DraftsService draftsService;

    @Autowired
    private TransformationService transformationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RetrieveCcdClient retrieveCcdClient;

    public long submit(final DivorceSession divorceSessionData, final String jwt) {
        UserDetails userDetails = userService.getUserDetails(jwt);

        final List<Map<String, Object>> ccdCases = retrieveCcdClient.getNonRejectedCases(userDetails.getId(), jwt);

        if (!CollectionUtils.isEmpty(ccdCases)) {
            log.error("Attempted to submit a duplicate case for userId {}", userDetails.getId());
            throw new DuplicateCaseException("User already has a non-rejected case");
        }

        CreateEvent createEvent = ccdClient.createCase(userDetails, jwt, divorceSessionData);

        SubmitEvent submitEvent = ccdClient.submitCase(userDetails, jwt,
            transformationService.transformSubmission(divorceSessionData, createEvent, EVENT_SUMMARY));

        try {
            draftsService.deleteDraft(jwt);
        } catch (Exception e) {
            // we do not want to send an error response to the front end if deleting the draft fails
            log.warn("Could not delete the draft for userId {} with case id {}",
                    userDetails.getId(), submitEvent.getCaseId());
        }

        log.info("Case Id: {} ", submitEvent.getCaseId());
        return submitEvent.getCaseId();
    }
}
