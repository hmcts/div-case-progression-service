package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

public interface ReasonForDivorceStrategy {

    boolean accepts(String reasonForDivorce);

    String deriveStatementOfCase(DivorceSession divorceSession);
}
