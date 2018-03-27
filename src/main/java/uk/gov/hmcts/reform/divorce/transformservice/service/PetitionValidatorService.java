package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CollectionMember;

@Component
public class PetitionValidatorService {

    /**
     * Check if DocumentType is correctly set
     */
    public void validateFieldsForIssued(CreateEvent caseDetailsRequest) {
        if (caseDetailsRequest.getCaseDetails().getCaseData().getD8DocumentsUploaded() != null
                && caseDetailsRequest.getCaseDetails().getCaseData().getD8DocumentsUploaded()
                .stream()
                .map(CollectionMember::getValue)
                .anyMatch(d -> (d.getDocumentType() == null || d.getDocumentType().isEmpty()))
                ) throw new InvalidPetitionException("DocumentType is missing");
    }
}