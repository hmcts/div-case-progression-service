package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ReasonForDivorceContext {

    private static final List<ReasonForDivorceStrategy> reasonForDivorceStrategies = asList(new AdulteryStrategy(),
        new DesertionStrategy(),
        new SeparationFiveYearsStrategy(),
        new SeparationTwoYearsStrategy(),
        new UnreasonableBehaviourStrategy());

    public String deriveStatementOfWork(DivorceSession divorceSession) {
        return reasonForDivorceStrategies.stream()
            .filter(strategy -> strategy.accepts(divorceSession.getReasonForDivorce()))
            .map(s -> s.deriveStatementOfCase(divorceSession))
            .collect(Collectors.joining());
    }
}
