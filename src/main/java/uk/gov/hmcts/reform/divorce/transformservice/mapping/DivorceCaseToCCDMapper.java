package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;
import uk.gov.hmcts.reform.divorce.transformservice.service.InferredGenderService;
import uk.gov.hmcts.reform.divorce.transformservice.strategy.payments.PaymentContext;
import uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce.ReasonForDivorceContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.String.join;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Mapper(componentModel = "spring", uses = DocumentCollectionMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DivorceCaseToCCDMapper {

    private static final String BLANK_SPACE = " ";
    private static final String LINE_SEPARATOR = "\n";

    private ReasonForDivorceContext reasonForDivorceContext = new ReasonForDivorceContext();
    private PaymentContext paymentContext = new PaymentContext();

    @Value("${cohort}")
    private String cohort;

    @Autowired
    private InferredGenderService inferredGenderService;

    @Mapping(source = "helpWithFeesReferenceNumber", target = "d8HelpWithFeesReferenceNumber")
    @Mapping(source = "divorceWho", target = "d8DivorceWho")
    @Mapping(source = "marriageDate", dateFormat = "yyyy-MM-dd", target = "d8MarriageDate")
    @Mapping(source = "reasonForDivorceDesertionDay", target = "d8ReasonForDivorceDesertionDay")
    @Mapping(source = "reasonForDivorceDesertionMonth", target = "d8ReasonForDivorceDesertionMonth")
    @Mapping(source = "reasonForDivorceDesertionYear", target = "d8ReasonForDivorceDesertionYear")
    @Mapping(source = "reasonForDivorceDesertionDate", dateFormat = "yyyy-MM-dd",
        target = "d8ReasonForDivorceDesertionDate")
    @Mapping(source = "countryName", target = "d8CountryName")
    @Mapping(source = "placeOfMarriage", target = "d8MarriagePlaceOfMarriage")
    @Mapping(source = "petitionerContactDetailsConfidential", target = "d8PetitionerContactDetailsConfidential")
    @Mapping(source = "petitionerHomeAddress.postcode", target = "d8PetitionerHomeAddress.postCode")
    @Mapping(source = "petitionerCorrespondenceAddress.postcode", target = "d8PetitionerCorrespondenceAddress.postCode")
    @Mapping(source = "respondentHomeAddress.postcode", target = "d8RespondentHomeAddress.postCode")
    @Mapping(source = "respondentCorrespondenceAddress.postcode", target = "d8RespondentCorrespondenceAddress.postCode")
    @Mapping(source = "petitionerFirstName", target = "d8PetitionerFirstName")
    @Mapping(source = "petitionerLastName", target = "d8PetitionerLastName")
    @Mapping(source = "respondentFirstName", target = "d8RespondentFirstName")
    @Mapping(source = "respondentLastName", target = "d8RespondentLastName")
    @Mapping(source = "petitionerNameChangedHowOtherDetails", target = "d8PetitionerNameChangedHowOtherDetails")
    @Mapping(source = "petitionerEmail", target = "d8PetitionerEmail")
    @Mapping(source = "petitionerPhoneNumber", target = "d8PetitionerPhoneNumber")
    @Mapping(source = "livingArrangementsLiveTogether", target = "d8LivingArrangementsLiveTogether")
    @Mapping(source = "reasonForDivorce", target = "d8ReasonForDivorce")
    @Mapping(source = "reasonForDivorceAdultery3rdPartyFirstName", target = "d8ReasonForDivorceAdultery3rdPartyFName")
    @Mapping(source = "reasonForDivorceAdultery3rdPartyLastName", target = "d8ReasonForDivorceAdultery3rdPartyLName")
    @Mapping(source = "reasonForDivorceAdulteryDetails", target = "d8ReasonForDivorceAdulteryDetails")
    @Mapping(source = "reasonForDivorceAdulteryWhenDetails", target = "d8ReasonForDivorceAdulteryWhenDetails")
    @Mapping(source = "reasonForDivorceAdulteryWhereDetails", target = "d8ReasonForDivorceAdulteryWhereDetails")
    @Mapping(source = "reasonForDivorceAdultery3rdAddress", target = "d8ReasonForDivorceAdultery3rdAddress")
    @Mapping(source = "legalProceedingsDetails", target = "d8LegalProceedingsDetails")
    @Mapping(source = "residualJurisdictionEligible", target = "d8ResidualJurisdictionEligible")
    @Mapping(source = "reasonForDivorceDesertionDetails", target = "d8ReasonForDivorceDesertionDetails")
    @Mapping(source = "jurisdictionConnection", target = "d8JurisdictionConnection")
    @Mapping(source = "financialOrderFor", target = "d8FinancialOrderFor")
    @Mapping(source = "petitionerNameChangedHow", target = "d8PetitionerNameChangedHow")
    @Mapping(source = "legalProceedingsRelated", target = "d8LegalProceedingsRelated")
    @Mapping(source = "claimsCostsFrom", target = "d8DivorceClaimFrom")
    @Mapping(source = "marriagePetitionerName", target = "d8MarriagePetitionerName")
    @Mapping(source = "marriageRespondentName", target = "d8MarriageRespondentName")
    @Mapping(source = "reasonForDivorceSeperationDay", target = "d8ReasonForDivorceSeperationDay")
    @Mapping(source = "reasonForDivorceSeperationMonth", target = "d8ReasonForDivorceSeperationMonth")
    @Mapping(source = "reasonForDivorceSeperationYear", target = "d8ReasonForDivorceSeperationYear")
    @Mapping(source = "reasonForDivorceSeperationDate", dateFormat = "yyyy-MM-dd",
        target = "d8ReasonForDivorceSeperationDate")
    @Mapping(source = "respondentCorrespondenceUseHomeAddress", target = "d8RespondentCorrespondenceUseHomeAddress")
    @Mapping(source = "connections", target = "d8Connections")
    @Mapping(source = "connectionSummary", target = "d8ConnectionSummary")
    @Mapping(source = "courts", target = "d8DivorceUnit")
    @Mapping(source = "marriageCertificateFiles", target = "d8DocumentsUploaded")
    @Mapping(target = "createdDate",
        expression =
            "java(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyy-MM-dd\")))")
    @Mapping(source = "d8Documents", target = "d8Documents")
    public abstract CoreCaseData divorceCaseDataToCourtCaseData(DivorceSession divorceSession);

    private String translateToStringYesNo(final String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        return BooleanUtils.toStringYesNo(BooleanUtils.toBoolean(value)).toUpperCase();
    }

    @AfterMapping
    protected void mapReasonForDivorceBehaviourDetails(DivorceSession divorceSession,
                                                       @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceBehaviourDetails(
            emptyIfNull(divorceSession.getReasonForDivorceBehaviourDetails()).stream().findFirst().orElse(null));
    }

    @AfterMapping
    protected void mapScreenHasMarriageBroken(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ScreenHasMarriageBroken(translateToStringYesNo(divorceSession.getScreenHasMarriageBroken()));
    }

    @AfterMapping
    protected void mapScreenHasRespondentAddress(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ScreenHasRespondentAddress(translateToStringYesNo(divorceSession.getScreenHasRespondentAddress()));
    }

    @AfterMapping
    protected void mapScreenHasMarriageCert(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ScreenHasMarriageCert(translateToStringYesNo(divorceSession.getScreenHasMarriageCert()));
    }

    @AfterMapping
    protected void mapScreenHasPrinter(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ScreenHasPrinter(translateToStringYesNo(divorceSession.getScreenHasPrinter()));
    }

    @AfterMapping
    protected void mapMarriageIsSameSexCouple(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8MarriageIsSameSexCouple(translateToStringYesNo(divorceSession.getMarriageIsSameSexCouple()));
    }

    @AfterMapping
    protected void mapMarriedInUk(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8MarriedInUk(translateToStringYesNo(divorceSession.getMarriedInUk()));
    }

    @AfterMapping
    protected void mapCertificateInEnglish(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8CertificateInEnglish(translateToStringYesNo(divorceSession.getCertificateInEnglish()));
    }

    @AfterMapping
    protected void mapCertifiedTranslation(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8CertifiedTranslation(translateToStringYesNo(divorceSession.getCertifiedTranslation()));
    }

    @AfterMapping
    protected void mapPetitionerNameDifferentToMarriageCert(DivorceSession divorceSession,
                                                            @MappingTarget CoreCaseData result) {
        result.setD8PetitionerNameDifferentToMarriageCert(
            translateToStringYesNo(divorceSession.getPetitionerNameDifferentToMarriageCertificate()));
    }

    @AfterMapping
    protected void mapPetitionerCorrespondenceUseHomeAddress(DivorceSession divorceSession,
                                                             @MappingTarget CoreCaseData result) {
        result.setD8PetitionerCorrespondenceUseHomeAddress(
            translateToStringYesNo(divorceSession.getPetitionerCorrespondenceUseHomeAddress()));
    }

    @AfterMapping
    protected void mapRespondentNameAsOnMarriageCertificate(DivorceSession divorceSession,
                                                            @MappingTarget CoreCaseData result) {
        result.setD8RespondentNameAsOnMarriageCertificate(
            translateToStringYesNo(divorceSession.getRespondentNameAsOnMarriageCertificate()));
    }

    @AfterMapping
    protected void mapRespondentCorrespondenceSendToSol(DivorceSession divorceSession,
                                                        @MappingTarget CoreCaseData result) {
        result.setD8RespondentCorrespondenceSendToSol(
            translateToStringYesNo(divorceSession.getRespondentCorrespondenceSendToSolicitor()));
    }

    @AfterMapping
    protected void mapRespondentKnowsHomeAddress(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8RespondentKnowsHomeAddress(translateToStringYesNo(divorceSession.getRespondentKnowsHomeAddress()));
    }

    @AfterMapping
    protected void mapRespondentLivesAtLastAddress(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8RespondentLivesAtLastAddress(
            translateToStringYesNo(divorceSession.getRespondentLivesAtLastAddress()));
    }

    @AfterMapping
    protected void mapLivingArrangementsLastLivedTogether(DivorceSession divorceSession,
                                                          @MappingTarget CoreCaseData result) {
        result.setD8LivingArrangementsLastLivedTogether(
            translateToStringYesNo(divorceSession.getLivingArrangementsLastLivedTogether()));
    }

    @AfterMapping
    protected void mapLivingArrangementsLiveTogether(DivorceSession divorceSession,
                                                     @MappingTarget CoreCaseData result) {
        result.setD8LivingArrangementsLiveTogether(
            translateToStringYesNo(divorceSession.getLivingArrangementsLiveTogether()));
    }

    @AfterMapping
    protected void mapLegalProceedings(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8LegalProceedings(translateToStringYesNo(divorceSession.getLegalProceedings()));
    }

    @AfterMapping
    protected void mapReasonForDivorceDesertionAgreed(DivorceSession divorceSession,
                                                      @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceDesertionAgreed(
            translateToStringYesNo(divorceSession.getReasonForDivorceDesertionAgreed()));
    }

    @AfterMapping
    protected void mapReasonForDivorceAdulteryKnowWhen(DivorceSession divorceSession,
                                                       @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceAdulteryKnowWhen(
            translateToStringYesNo(divorceSession.getReasonForDivorceAdulteryKnowWhen()));
    }

    @AfterMapping
    protected void mapReasonForDivorceAdulteryWishToName(DivorceSession divorceSession,
                                                         @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceAdulteryWishToName(
            translateToStringYesNo(divorceSession.getReasonForDivorceAdulteryWishToName()));
    }

    @AfterMapping
    protected void mapReasonForDivorceAdulteryKnowWhere(DivorceSession divorceSession,
                                                        @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceAdulteryKnowWhere(
            translateToStringYesNo(divorceSession.getReasonForDivorceAdulteryKnowWhere()));
    }

    @AfterMapping
    protected void mapReasonForDivorceAdulteryIsNamed(DivorceSession divorceSession,
                                                      @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceAdulteryIsNamed(
            translateToStringYesNo(divorceSession.getReasonForDivorceAdulteryIsNamed()));
    }

    @AfterMapping
    protected void mapFinancialOrder(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8FinancialOrder(translateToStringYesNo(divorceSession.getFinancialOrder()));
    }

    @AfterMapping
    protected void mapHelpWithFeesNeedHelp(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8HelpWithFeesNeedHelp(translateToStringYesNo(divorceSession.getHelpWithFeesNeedHelp()));
    }

    @AfterMapping
    protected void mapHelpWithFeesAppliedForFees(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8HelpWithFeesAppliedForFees(translateToStringYesNo(divorceSession.getHelpWithFeesAppliedForFees()));
    }

    @AfterMapping
    protected void mapDivorceCostsClaim(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8DivorceCostsClaim(translateToStringYesNo(divorceSession.getClaimsCosts()));
    }

    @AfterMapping
    protected void mapDivorceIsNamed(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceAdulteryIsNamed(
            translateToStringYesNo(divorceSession.getReasonForDivorceAdulteryIsNamed()));
    }

    @AfterMapping
    protected void mapJurisdictionConfidentLegal(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8JurisdictionConfidentLegal(translateToStringYesNo(divorceSession.getJurisdictionConfidentLegal()));
    }

    @AfterMapping
    protected void mapJurisdictionLastTwelveMonths(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8JurisdictionLastTwelveMonths(
            translateToStringYesNo(divorceSession.getJurisdictionLastTwelveMonths()));
    }

    @AfterMapping
    protected void mapJurisdictionPetitionerDomicile(DivorceSession divorceSession,
                                                     @MappingTarget CoreCaseData result) {
        result.setD8JurisdictionPetitionerDomicile(
            translateToStringYesNo(divorceSession.getJurisdictionPetitionerDomicile()));
    }

    @AfterMapping
    protected void mapJurisdictionPetitionerResidence(DivorceSession divorceSession,
                                                      @MappingTarget CoreCaseData result) {
        result.setD8JurisdictionPetitionerResidence(
            translateToStringYesNo(divorceSession.getJurisdictionPetitionerResidence()));
    }

    @AfterMapping
    protected void mapJurisdictionRespondentDomicile(DivorceSession divorceSession,
                                                     @MappingTarget CoreCaseData result) {
        result.setD8JurisdictionRespondentDomicile(
            translateToStringYesNo(divorceSession.getJurisdictionRespondentDomicile()));
    }

    @AfterMapping
    protected void mapJurisdictionRespondentResidence(DivorceSession divorceSession,
                                                      @MappingTarget CoreCaseData result) {
        result.setD8JurisdictionRespondentResidence(
            translateToStringYesNo(divorceSession.getJurisdictionRespondentResidence()));
    }

    @AfterMapping
    protected void mapJurisdictionHabituallyResLast6Months(DivorceSession divorceSession,
                                                           @MappingTarget CoreCaseData result) {
        result.setD8JurisdictionHabituallyResLast6Months(
            translateToStringYesNo(divorceSession.getJurisdictionLastHabitualResident()));
    }

    @AfterMapping
    protected void mapResidualJurisdictionEligible(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ResidualJurisdictionEligible(
            translateToStringYesNo(divorceSession.getResidualJurisdictionEligible()));
    }

    @AfterMapping
    protected void mapReasonForDivorceShowAdultery(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceShowAdultery(
            translateToStringYesNo(divorceSession.getReasonForDivorceShowAdultery()));
    }

    @AfterMapping
    protected void mapReasonForDivorceShowUnreasonableBehavior(DivorceSession divorceSession,
                                                               @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceShowUnreasonableBehaviour(
            translateToStringYesNo(divorceSession.getReasonForDivorceShowUnreasonableBehaviour()));
    }

    @AfterMapping
    protected void mapReasonForDivorceShowTwoYearsSeparation(DivorceSession divorceSession,
                                                             @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceShowTwoYearsSeparation(
            translateToStringYesNo(divorceSession.getReasonForDivorceShowTwoYearsSeparation()));
    }

    @AfterMapping
    protected void mapReasonForDivorceShowDesertion(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceShowDesertion(
            translateToStringYesNo(divorceSession.getReasonForDivorceShowDesertion()));
    }

    @AfterMapping
    protected void mapReasonForDivorceLimitReasons(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceLimitReasons(
            translateToStringYesNo(divorceSession.getReasonForDivorceLimitReasons()));
    }

    @AfterMapping
    protected void mapReasonForDivorceEnableAdultery(DivorceSession divorceSession,
                                                     @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceEnableAdultery(
            translateToStringYesNo(divorceSession.getReasonForDivorceEnableAdultery()));
    }

    @AfterMapping
    protected void mapReasonForDivorceDesertionAlright(DivorceSession divorceSession,
                                                       @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceDesertionAlright(
            translateToStringYesNo(divorceSession.getReasonForDivorceDesertionAlright()));
    }

    @AfterMapping
    protected void mapClaimsCostsAppliedForFees(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ClaimsCostsAppliedForFees(translateToStringYesNo(divorceSession.getClaimsCostsAppliedForFees()));
    }

    @AfterMapping
    protected void mapReasonForDivorceClaimingAdultery(DivorceSession divorceSession,
                                                       @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceClaimingAdultery(
            translateToStringYesNo(divorceSession.getReasonForDivorceClaimingAdultery()));
    }

    @AfterMapping
    protected void mapReasonForDivorceSeperationIsSameOrAftr(DivorceSession divorceSession,
                                                             @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceSeperationIsSameOrAftr(
            translateToStringYesNo(divorceSession.getReasonForDivorceSeperationDateIsSameOrAfterLimitDate()));
    }

    @AfterMapping
    protected void mapReasonForDivorceSeperationInFuture(DivorceSession divorceSession,
                                                         @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceSeperationInFuture(
            translateToStringYesNo(divorceSession.getReasonForDivorceSeperationInFuture()));
    }

    @AfterMapping
    protected void mapReasonForDivorceDesertionBeforeMarriage(DivorceSession divorceSession,
                                                              @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceDesertionBeforeMarriage(
            translateToStringYesNo(divorceSession.getReasonForDivorceDesertionBeforeMarriage()));
    }

    @AfterMapping
    protected void mapReasonForDivorceDesertionInFuture(DivorceSession divorceSession,
                                                        @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceDesertionInFuture(
            translateToStringYesNo(divorceSession.getReasonForDivorceDesertionInFuture()));
    }

    @AfterMapping
    protected void mapMarriageCanDivorce(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8MarriageCanDivorce(translateToStringYesNo(divorceSession.getMarriageCanDivorce()));
    }

    @AfterMapping
    protected void mapMarriageIsFuture(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8MarriageIsFuture(translateToStringYesNo(divorceSession.getMarriageIsFuture()));
    }

    @AfterMapping
    protected void mapMarriageMoreThan100(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8MarriageMoreThan100(translateToStringYesNo(divorceSession.getMarriageMoreThan100()));
    }

    @AfterMapping
    protected void mapPetitionerHomeAddress(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getPetitionerHomeAddress())) {
            result.setD8DerivedPetitionerHomeAddress(
                join(LINE_SEPARATOR, divorceSession.getPetitionerHomeAddress().getAddressField()));
        }
    }

    @AfterMapping
    protected void mapPetitionerCorrespondenceAddress(DivorceSession divorceSession,
                                                      @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getPetitionerCorrespondenceAddress())) {
            result.setD8DerivedPetitionerCorrespondenceAddress(
                join(LINE_SEPARATOR, divorceSession.getPetitionerCorrespondenceAddress().getAddressField()));
        }
    }

    @AfterMapping
    protected void mapRespondentHomeAddress(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getRespondentHomeAddress())) {
            result.setD8DerivedRespondentHomeAddress(
                join(LINE_SEPARATOR, divorceSession.getRespondentHomeAddress().getAddressField()));
        }
    }

    @AfterMapping
    protected void mapRespondentCorrespondenceAddress(DivorceSession divorceSession,
                                                      @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getRespondentCorrespondenceAddress())) {
            result.setD8DerivedRespondentCorrespondenceAddr(
                join(LINE_SEPARATOR, divorceSession.getRespondentCorrespondenceAddress().getAddressField()));
        }
    }

    @AfterMapping
    protected void mapDerivedReasonForDivorceAdulteryThirdPartyName(DivorceSession divorceSession,
                                                                    @MappingTarget CoreCaseData result) {
        String adulteryThirdPartyName = StringUtils.join(divorceSession.getReasonForDivorceAdultery3rdPartyFirstName(),
            BLANK_SPACE, divorceSession.getReasonForDivorceAdultery3rdPartyLastName());
        result.setD8DerivedReasonForDivorceAdultery3dPtyNm(
            isNotBlank(adulteryThirdPartyName) ? adulteryThirdPartyName : null);
    }

    @AfterMapping
    protected void mapReasonForDivorceHasMarriage(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceHasMarriage(
            translateToStringYesNo(divorceSession.getReasonForDivorceHasMarriageDate()));
    }

    @AfterMapping
    protected void mapReasonForDivorceShowFiveYearsSeparation(DivorceSession divorceSession,
                                                              @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceShowFiveYearsSeparation(
            translateToStringYesNo(divorceSession.getReasonForDivorceShowFiveYearsSeparation()));
    }

    @AfterMapping
    protected void mapReasonForDivorceClaiming5YearSeparation(DivorceSession divorceSession,
                                                              @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceClaiming5YearSeparation(
            translateToStringYesNo(divorceSession.getReasonForDivorceClaiming5YearSeparation()));
    }

    @AfterMapping
    protected void mapReasonForDivorceSeperationBeforeMarriage(DivorceSession divorceSession,
                                                               @MappingTarget CoreCaseData result) {
        result.setD8ReasonForDivorceSeperationBeforeMarriage(
            translateToStringYesNo(divorceSession.getReasonForDivorceSeperationBeforeMarriage()));
    }

    @AfterMapping
    protected void mapDerivedPetitionerCurrentFullName(DivorceSession divorceSession,
                                                       @MappingTarget CoreCaseData result) {
        String petitionerFullName = StringUtils.join(divorceSession.getPetitionerFirstName(), BLANK_SPACE,
            divorceSession.getPetitionerLastName());
        result.setD8DerivedPetitionerCurrentFullName(isNotBlank(petitionerFullName) ? petitionerFullName : null);
    }

    @AfterMapping
    protected void mapRespondentSolicitorAddress(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getRespondentSolicitorAddress())) {
            result.setD8DerivedRespondentSolicitorAddr(
                join(LINE_SEPARATOR, divorceSession.getRespondentSolicitorAddress().getAddressField()));
        }
    }

    @AfterMapping
    protected void mapDerivedRespondentSolicitorDetails(DivorceSession divorceSession,
                                                        @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getRespondentSolicitorName())) {
            String solicitorAddress = join(LINE_SEPARATOR,
                divorceSession.getRespondentSolicitorAddress().getAddressField());

            String solictorDetails = join(LINE_SEPARATOR, Arrays.asList(divorceSession.getRespondentSolicitorName(),
                divorceSession.getRespondentSolicitorCompany()));

            result.setD8DerivedRespondentSolicitorDetails(join(LINE_SEPARATOR, solictorDetails, solicitorAddress));
        }
    }

    @AfterMapping
    protected void mapDerivedRespondentCurrentName(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        String respondentFullName = StringUtils.join(divorceSession.getRespondentFirstName(), BLANK_SPACE,
            divorceSession.getRespondentLastName());
        result.setD8DerivedRespondentCurrentName(isNotBlank(respondentFullName) ? respondentFullName : null);
    }

    @AfterMapping
    protected void mapDerivedLivingArrangementsLastLivedAddr(DivorceSession divorceSession,
                                                             @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getLivingArrangementsLastLivedTogetherAddress())) {
            result.setD8DerivedLivingArrangementsLastLivedAddr(join(LINE_SEPARATOR,
                divorceSession.getLivingArrangementsLastLivedTogetherAddress().getAddressField()));
        }
    }

    @AfterMapping
    protected void mapStatementOfTruth(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8StatementOfTruth(translateToStringYesNo(divorceSession.getConfirmPrayer()));
    }

    @AfterMapping
    protected void mapDerivedStatementOfCase(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getReasonForDivorce())) {
            result.setD8DerivedStatementOfCase(reasonForDivorceContext.deriveStatementOfWork(divorceSession));
        }
    }

    @AfterMapping
    protected void mapDerivedReasonForDivorceAdultery3rdAddr(DivorceSession divorceSession,
                                                             @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getReasonForDivorceAdultery3rdAddress())) {
            result.setD8DerivedReasonForDivorceAdultery3rdAddr(
                join(LINE_SEPARATOR, divorceSession.getReasonForDivorceAdultery3rdAddress().getAddressField()));
        }
    }

    @AfterMapping
    protected void mapPayments(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getPayment())) {

            if (Objects.nonNull(divorceSession.getPayment().getPaymentDate())) {
                divorceSession.getPayment().setPaymentDate(LocalDate.parse(
                    divorceSession.getPayment().getPaymentDate(), DateTimeFormatter.ofPattern("ddMMyyyy"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }

            result.setPayments(paymentContext.getListOfPayments(divorceSession));
        }
    }

    @AfterMapping
    protected void mapCohort(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        result.setD8Cohort(cohort);
    }

    @AfterMapping
    protected void mapInferredPetitionerGender(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getMarriageIsSameSexCouple())
            && Objects.nonNull(divorceSession.getDivorceWho())) {
            result.setD8InferredPetitionerGender(
                inferredGenderService.getPetitionerGender(divorceSession.getMarriageIsSameSexCouple(),
                    divorceSession.getDivorceWho()));
        }
    }

    @AfterMapping
    protected void mapInferredRespondentGender(DivorceSession divorceSession, @MappingTarget CoreCaseData result) {
        if (Objects.nonNull(divorceSession.getDivorceWho())) {
            result.setD8InferredRespondentGender(
                inferredGenderService.getRespondentGender(divorceSession.getDivorceWho()));
        }
    }


}
