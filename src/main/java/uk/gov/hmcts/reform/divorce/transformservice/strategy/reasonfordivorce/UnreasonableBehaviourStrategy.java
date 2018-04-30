package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import static org.apache.commons.lang3.StringUtils.join;

@Component
public class UnreasonableBehaviourStrategy implements ReasonForDivorceStrategy {

    private static final String UNREASONABLE_BEHAVIOUR = "unreasonable-behaviour";
    private static final String LINE_SEPARATOR = "\n";

    @Override
    public String deriveStatementOfCase(DivorceSession divorceSession) {
        return join(divorceSession.getReasonForDivorceBehaviourDetails(), LINE_SEPARATOR);
    }

    @Override
    public boolean accepts(String reasonForDivorce) {
        return UNREASONABLE_BEHAVIOUR.equalsIgnoreCase(reasonForDivorce);
    }

}
