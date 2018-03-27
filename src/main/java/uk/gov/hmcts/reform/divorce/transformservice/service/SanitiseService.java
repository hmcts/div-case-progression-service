package uk.gov.hmcts.reform.divorce.transformservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Address;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@Slf4j
public class SanitiseService {
    private static final String BLANK_SPACE = " ";
    private static final String LINE_SEPARATOR = "\n";

    public CoreCaseData sanitiseCase(final CaseDetails caseDetails) {
        CoreCaseData caseData = caseDetails.getCaseData();

        String petitionerFullName = StringUtils.join(
                caseData.getD8PetitionerFirstName(),
                BLANK_SPACE,
                caseData.getD8PetitionerLastName());
        caseData.setD8DerivedPetitionerCurrentFullName(isNotBlank(petitionerFullName) ? petitionerFullName : null);

        String petitionerCorrespondenceAddress = Objects.nonNull(caseData.getD8PetitionerCorrespondenceAddress())
                ? deriveAddress(caseData.getD8PetitionerCorrespondenceAddress()) : null;
        caseData.setD8DerivedPetitionerCorrespondenceAddress(petitionerCorrespondenceAddress);

        String respondentName = StringUtils.join(
                caseData.getD8RespondentFirstName(),
                BLANK_SPACE,
                caseData.getD8RespondentLastName());
        caseData.setD8DerivedRespondentCurrentName(isNotBlank(respondentName) ? respondentName : null);

        String respondentCorrespondenceAddress = Objects.nonNull(caseData.getD8RespondentCorrespondenceAddress())
                ? deriveAddress(caseData.getD8RespondentCorrespondenceAddress()) : null;
        caseData.setD8DerivedRespondentCorrespondenceAddr(respondentCorrespondenceAddress);

        String respondentSolicitorAddress = Objects.nonNull(caseData.getD8RespondentSolicitorAddress())
                ? deriveAddress(caseData.getD8RespondentSolicitorAddress()) : null;
        String respondentSolicitorDetails = StringUtils.join(
                caseData.getD8RespondentSolicitorName(),
                LINE_SEPARATOR,
                caseData.getD8RespondentSolicitorCompany(),
                LINE_SEPARATOR,
                respondentSolicitorAddress);
        caseData.setD8DerivedRespondentSolicitorDetails(respondentSolicitorDetails);

        String adultery3rdPartyName = StringUtils.join(
                caseData.getD8ReasonForDivorceAdultery3rdPartyFName(),
                BLANK_SPACE,
                caseData.getD8ReasonForDivorceAdultery3rdPartyLName());
        caseData.setD8DerivedReasonForDivorceAdultery3dPtyNm(isNotBlank(adultery3rdPartyName) ? adultery3rdPartyName : null);

        String adultery3rdPartyAddress = Objects.nonNull(caseData.getD8ReasonForDivorceAdultery3rdAddress())
                ? deriveAddress(caseData.getD8ReasonForDivorceAdultery3rdAddress()) : null;
        caseData.setD8DerivedReasonForDivorceAdultery3rdAddr(adultery3rdPartyAddress);

        String statementOfCase = StringUtils.join(
                caseData.getD8ReasonForDivorceAdulteryDetails(),
                LINE_SEPARATOR,
                caseData.getD8ReasonForDivorceBehaviourDetails(),
                LINE_SEPARATOR,
                caseData.getSolSOCBehaviourExample2(),
                LINE_SEPARATOR,
                caseData.getSolSOCBehaviourExample3(),
                LINE_SEPARATOR,
                caseData.getD8ReasonForDivorceDesertionDetails(),
                LINE_SEPARATOR,
                caseData.getD8ReasonForDivorceSeperation()
        );
        caseData.setD8DerivedStatementOfCase(statementOfCase);

        return caseData;
    }

    private String deriveAddress(Address address) {
        return Stream.of(
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getAddressLine3(),
                address.getPostTown(),
                address.getCounty(),
                address.getPostCode(),
                address.getCountry()
        ).filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(LINE_SEPARATOR));
    }
}
