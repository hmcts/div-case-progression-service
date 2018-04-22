package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.junit.Test;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CollectionMember;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Document;

import java.util.ArrayList;
import java.util.List;

public class PetitionValidatorServiceTest {

    private PetitionValidatorService underTest = new PetitionValidatorService();

    @Test(expected = InvalidPetitionException.class)
    public void shouldThrowExceptionOnDocumentTypeNotSet() {
        CreateEvent createEvent = buildCreateEvent(null);
        underTest.validateFieldsForIssued(createEvent);
    }

    @Test(expected = InvalidPetitionException.class)
    public void shouldThrowExceptionOnDocumentTypeEmpty() {
        CreateEvent createEvent = buildCreateEvent("");
        underTest.validateFieldsForIssued(createEvent);
    }

    private CreateEvent buildCreateEvent(String documentTypeValue) {
        final CreateEvent createEvent = new CreateEvent();
        final CaseDetails caseDetails = new CaseDetails();
        final CoreCaseData caseData = new CoreCaseData();
        final List<CollectionMember<Document>> documentUploaded = new ArrayList<>();
        final CollectionMember<Document> collectionMember = new CollectionMember<>();
        final Document aDocument = new Document();
        aDocument.setDocumentType(documentTypeValue);
        aDocument.setDocumentFileName("aFile");
        collectionMember.setValue(aDocument);
        documentUploaded.add(collectionMember);
        caseData.setD8DocumentsUploaded(documentUploaded);
        caseDetails.setCaseData(caseData);
        createEvent.setCaseDetails(caseDetails);
        return createEvent;
    }
}
