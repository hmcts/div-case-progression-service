package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Address;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SanitiseServiceTest {
    private static final String BLANK_SPACE = " ";
    private static final String LINE_SEPARATOR = "\n";
    private static final String ADDRESS_LINE_1 = "102 PF";
    private static final String ADDRESS_LINE_2 = "";
    private static final String ADDRESS_LINE_3 = "6th Floor";
    private static final String POST_TOWN = "Westminster";
    private static final String COUNTY = "London";
    private static final String POSTCODE = "SW1H 9AJ";
    private static final String COUNTRY = "UK";
    private static final String SOLICITOR_NAME = "Mr Des Truct";
    private static final String SOLICITOR_COMPANY = "Divorce 4 You";
    private static final String ADULTERY_DETAILS = "Extramarital relations";
    private static final String BEHAVIOUR_DETAILS = "Unreasonable behaviour";
    private static final String BEHAVIOUR_EXAMPLE_2 = "Controlling partner's own finances";
    private static final String BEHAVIOUR_EXAMPLE_3 = " ";
    private static final String DESERTION_DETAILS = "No contact for over six months";
    private static final String DIVORCE_SEPARATION = "I separated from my partner on 01/01/2018";

    @InjectMocks
    private SanitiseService sanitiseService;

    @Test
    public void shouldSetNonBlankDerivedPetitionerName() {
        String firstname = "AN";
        String lastname = "Other";

        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8PetitionerFirstName(firstname);
        caseData.setD8PetitionerLastName(lastname);
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertThat(caseData.getD8DerivedPetitionerCurrentFullName(), equalTo(firstname + BLANK_SPACE + lastname));
    }

    @Test
    public void shouldNotSetBlankDerivedPetitionerName() {
        String firstname = "";
        String lastname = " ";

        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8PetitionerFirstName(firstname);
        caseData.setD8PetitionerLastName(lastname);
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertNull(caseData.getD8DerivedPetitionerCurrentFullName());
    }

    @Test
    public void shouldSetDerivedPetitionerCorrespondenceAddress() {
        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8PetitionerCorrespondenceAddress(createAddress());
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertThat(caseData.getD8DerivedPetitionerCorrespondenceAddress(),
                equalTo(ADDRESS_LINE_1 + LINE_SEPARATOR
                        + ADDRESS_LINE_3 + LINE_SEPARATOR
                        + POST_TOWN + LINE_SEPARATOR
                        + COUNTY + LINE_SEPARATOR
                        + POSTCODE + LINE_SEPARATOR
                        + COUNTRY));
    }

    @Test
    public void shouldSetNonBlankDerivedRespondentName() {
        String firstname = "AN";
        String lastname = "Other";

        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8RespondentFirstName(firstname);
        caseData.setD8RespondentLastName(lastname);
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertThat(caseData.getD8DerivedRespondentCurrentName(), equalTo(firstname + BLANK_SPACE + lastname));
    }

    @Test
    public void shouldNotSetBlankDerivedRespondentName() {
        String firstname = "";
        String lastname = " ";

        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8RespondentFirstName(firstname);
        caseData.setD8RespondentLastName(lastname);
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertNull(caseData.getD8DerivedRespondentCurrentName());
    }

    @Test
    public void shouldSetDerivedRespondentCorrespondenceAddress() {
        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8RespondentCorrespondenceAddress(createAddress());
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertThat(caseData.getD8DerivedRespondentCorrespondenceAddr(),
                equalTo(ADDRESS_LINE_1 + LINE_SEPARATOR
                        + ADDRESS_LINE_3 + LINE_SEPARATOR
                        + POST_TOWN + LINE_SEPARATOR
                        + COUNTY + LINE_SEPARATOR
                        + POSTCODE + LINE_SEPARATOR
                        + COUNTRY));
    }

    @Test
    public void shouldSetDerivedRespondentSolicitorDetails() {
        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8RespondentSolicitorAddress(createAddress());
        caseData.setD8RespondentSolicitorName(SOLICITOR_NAME);
        caseData.setD8RespondentSolicitorCompany(SOLICITOR_COMPANY);
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertThat(caseData.getD8DerivedRespondentSolicitorDetails(),
                equalTo(SOLICITOR_NAME + LINE_SEPARATOR
                        + SOLICITOR_COMPANY + LINE_SEPARATOR
                        + ADDRESS_LINE_1 + LINE_SEPARATOR
                        + ADDRESS_LINE_3 + LINE_SEPARATOR
                        + POST_TOWN + LINE_SEPARATOR
                        + COUNTY + LINE_SEPARATOR
                        + POSTCODE + LINE_SEPARATOR
                        + COUNTRY));
    }

    @Test
    public void shouldSetNonBlankDerivedAdultery3rdPartyName() {
        String firstname = "AN";
        String lastname = "Other";

        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8ReasonForDivorceAdultery3rdPartyFName(firstname);
        caseData.setD8ReasonForDivorceAdultery3rdPartyLName(lastname);
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertThat(caseData.getD8DerivedReasonForDivorceAdultery3dPtyNm(), equalTo(firstname + BLANK_SPACE + lastname));
    }

    @Test
    public void shouldNotSetBlankDerivedAdultery3rdPartyName() {
        String firstname = "";
        String lastname = " ";

        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8ReasonForDivorceAdultery3rdPartyFName(firstname);
        caseData.setD8ReasonForDivorceAdultery3rdPartyLName(lastname);
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertNull(caseData.getD8DerivedReasonForDivorceAdultery3dPtyNm());
    }

    @Test
    public void shouldSetDerivedAdultery3rdPartyAddress() {
        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8ReasonForDivorceAdultery3rdAddress(createAddress());
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertThat(caseData.getD8DerivedReasonForDivorceAdultery3rdAddr(),
                equalTo(ADDRESS_LINE_1 + LINE_SEPARATOR
                        + ADDRESS_LINE_3 + LINE_SEPARATOR
                        + POST_TOWN + LINE_SEPARATOR
                        + COUNTY + LINE_SEPARATOR
                        + POSTCODE + LINE_SEPARATOR
                        + COUNTRY));
    }

    @Test
    public void shouldSetDerivedStatementOfCase() {
        CaseDetails caseDetails = new CaseDetails();
        CoreCaseData caseData = new CoreCaseData();
        caseData.setD8ReasonForDivorceAdulteryDetails(ADULTERY_DETAILS);
        caseData.setD8ReasonForDivorceBehaviourDetails(BEHAVIOUR_DETAILS);
        caseData.setSolSOCBehaviourExample2(BEHAVIOUR_EXAMPLE_2);
        caseData.setSolSOCBehaviourExample3(BEHAVIOUR_EXAMPLE_3);
        caseData.setD8ReasonForDivorceDesertionDetails(DESERTION_DETAILS);
        caseData.setD8ReasonForDivorceSeperation(DIVORCE_SEPARATION);
        caseDetails.setCaseData(caseData);
        caseData = sanitiseService.sanitiseCase(caseDetails);

        assertThat(caseData.getD8DerivedStatementOfCase(),
                equalTo(ADULTERY_DETAILS + LINE_SEPARATOR
                        + BEHAVIOUR_DETAILS + LINE_SEPARATOR
                        + BEHAVIOUR_EXAMPLE_2 + LINE_SEPARATOR
                        + BEHAVIOUR_EXAMPLE_3 + LINE_SEPARATOR
                        + DESERTION_DETAILS + LINE_SEPARATOR
                        + DIVORCE_SEPARATION));
    }

    private Address createAddress() {
        Address address = new Address();
        address.setAddressLine1(ADDRESS_LINE_1);
        address.setAddressLine2(ADDRESS_LINE_2);
        address.setAddressLine3(ADDRESS_LINE_3);
        address.setPostTown(POST_TOWN);
        address.setCounty(COUNTY);
        address.setPostCode(POSTCODE);
        address.setCountry(COUNTRY);
        return address;
    }
}
