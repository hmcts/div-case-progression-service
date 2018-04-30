package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import static org.apache.commons.lang3.StringUtils.join;

@Component
public class AdulteryStrategy implements ReasonForDivorceStrategy {

    private static final String ADULTERY = "adultery";
    private static final String LINE_SEPARATOR = "\n";
    private static final String YES = "Yes";

    @Override
    public String deriveStatementOfCase(DivorceSession divorceSession) {
        String derivedStatementOfCase = "";

        if (divorceSession.getReasonForDivorceAdulteryKnowWhere().equals(YES)) {
            derivedStatementOfCase = join(divorceSession.getReasonForDivorceAdulteryWhereDetails(), LINE_SEPARATOR);
        }

        if (divorceSession.getReasonForDivorceAdulteryKnowWhen().equals(YES)) {
            derivedStatementOfCase = join(derivedStatementOfCase,
                divorceSession.getReasonForDivorceAdulteryWhenDetails(), LINE_SEPARATOR);
        }

        return join(derivedStatementOfCase, divorceSession.getReasonForDivorceAdulteryDetails());
    }

    @Override
    public boolean accepts(String reasonForDivorce) {
        return ADULTERY.equalsIgnoreCase(reasonForDivorce);
    }

}
