package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfGenerateDocumentRequest;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseProgressionApplication.class)
public class PdfGenerateDocumentRequestMapperTest {

    @Autowired
    private PdfGenerateDocumentRequestMapper mapper;

    @Test
    public void mapperIsMapping() {

        final CreateEvent caseDetailsWrap = new CreateEvent();
        final CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8PetitionerFirstName("PFname");
        caseData.setD8PetitionerLastName("PLname");
        caseData.setD8RespondentFirstName("RFname");
        caseData.setD8RespondentLastName("RLname");
        caseData.setD8MarriagePlaceOfMarriage("placeOfMarriage");
        caseData.setD8MarriageDate("01/07/2000");
        caseData.setD8ResidualJurisdictionEligible("London");
        caseData.setD8ReasonForDivorce("It's complicated");
        caseData.setD8StatementOfTruth("The truth is that is complicated");
        caseDetails.setCaseData(caseData);
        caseDetailsWrap.setCaseDetails(caseDetails);

        String templates = "divorceminipetition";
        Map<String, Object> values = new HashMap<>();
        values.put("caseDetails", caseDetails);
        PdfGenerateDocumentRequest expectedResult = new PdfGenerateDocumentRequest(templates, values);

        PdfGenerateDocumentRequest mapped = mapper.toPdfGenerateDocumentRequest(caseDetailsWrap);

        assertThat(mapped, samePropertyValuesAs(expectedResult));
    }
}
