package uk.gov.hmcts.reform.divorce.transformservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.draftservice.service.DraftsService;
import uk.gov.hmcts.reform.divorce.transformservice.client.CcdEventClient;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CaseEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
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
    private TransformationService transformationService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private PdfToCoreCaseDataMapper pdfToCoreCaseDataMapper;

    @Autowired
    private DraftsService draftsService;

    public long update(final Long caseId, final DivorceEventSession divorceEventSessionData, final String jwt) {

        CreateEvent createEvent = updateCcdEventClient.startEvent(jwt, caseId,
                divorceEventSessionData.getEventId());

        CaseEvent caseEvent = updateCcdEventClient.createCaseEvent(jwt, caseId,
                transformationService.transform(divorceEventSessionData.getEventData(), createEvent, EVENT_SUMMARY));

        try {
            draftsService.deleteDraft(jwt);
        } catch (Exception e) {
            // we do not want to send an error response to the front end if deleting the draft fails
            log.warn("Could not delete the draft for case id {}", caseEvent.getCaseId());
        }

        log.info("Update case Id: {} ", caseEvent.getCaseId());
        return caseEvent.getCaseId();
    }

    public CoreCaseData addPdf(final CreateEvent caseDetailsRequest) {
        PdfFile pdfFile = pdfService.generatePdf(caseDetailsRequest);

        return pdfToCoreCaseDataMapper.toCoreCaseData(pdfFile, caseDetailsRequest.getCaseDetails().getCaseData());
    }
}