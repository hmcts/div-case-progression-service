package uk.gov.hmcts.reform.divorce.transformservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.idam.services.UserService;
import uk.gov.hmcts.reform.divorce.transformservice.client.CcdEventClient;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CaseEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceEventSession;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;
import uk.gov.hmcts.reform.divorce.transformservice.mapping.PdfToCoreCaseDataMapper;

@Component
@Slf4j
public class UpdateService {
    private static final String EVENT_SUMMARY = "Update case";

    @Autowired
    private CcdEventClient updateCcdEventClient;

    @Autowired
    private DivorceToCcdTransformationServiceTemp transformationService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private PdfToCoreCaseDataMapper pdfToCoreCaseDataMapper;

    @Autowired
    private DraftsService draftsService;

    @Autowired
    private PetitionValidatorService petitionValidatorService;

    @Autowired
    private UserService userService;

    public long update(final Long caseId, final DivorceEventSession divorceEventSessionData, final String jwt) {

        UserDetails userDetails = userService.getUserDetails(jwt);

        CreateEvent createEvent = updateCcdEventClient.startEvent(userDetails, jwt, caseId,
            divorceEventSessionData.getEventId());

        System.out.println("eventData " + divorceEventSessionData.getEventData());
        System.out.println("createEvent " + createEvent);

        CaseDataContent transformed = transformationService
                .transform(divorceEventSessionData.getEventData(), createEvent, EVENT_SUMMARY);

        System.out.println("CaseDataContent" + transformed);

        CaseEvent caseEvent = updateCcdEventClient.createCaseEvent(userDetails, jwt, caseId,
                transformed);

        try {
            draftsService.deleteDraft(jwt);
        } catch (Exception e) {
            // we do not want to send an error response to the front end if deleting the draft fails
            log.warn("Could not delete the draft for case id {}", caseEvent.getCaseId());
        }

        log.info("Update case Id: {} ", caseEvent.getCaseId());
        return caseEvent.getCaseId();
    }

    public CoreCaseData addPdf(final CreateEvent caseDetailsRequest, String authorization) {

        petitionValidatorService.validateFieldsForIssued(caseDetailsRequest);

        PdfFile pdfFile = pdfService.generatePdf(caseDetailsRequest, authorization);

        return pdfToCoreCaseDataMapper.toCoreCaseData(pdfFile, caseDetailsRequest.getCaseDetails().getCaseData());
    }
}
