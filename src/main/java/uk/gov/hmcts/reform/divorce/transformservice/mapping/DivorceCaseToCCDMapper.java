package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;
import uk.gov.hmcts.reform.divorce.transformservice.strategy.payments.PaymentContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

//@Mapper(componentModel = "spring", uses = DocumentCollectionMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DivorceCaseToCCDMapper {

    private final PaymentContext paymentContext = new PaymentContext();

    @Value("${cohort}")
    private String cohort;

    public abstract CoreCaseData divorceCaseDataToCourtCaseData(DivorceSession divorceSession);

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

}

