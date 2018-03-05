package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import java.util.List;
import java.util.Arrays;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

public class UnreasonableBehaviourStrategyTest {

    private static final String UNREASONABLE_BEHAVIOUR = "unreasonable-behaviour";

    private UnreasonableBehaviourStrategy unreasonableBehaviourStrategy = new UnreasonableBehaviourStrategy();

    @Test
    public void testUnreasonableBehaviourStatementOfCase() {
        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorce(UNREASONABLE_BEHAVIOUR);

        List<String> behaviourDetails = Arrays.asList("My wife is having an affair this week.",
                "This is totally not cool.");

        divorceSession.setReasonForDivorceBehaviourDetails(behaviourDetails);

        String derivedStatementOfCase = unreasonableBehaviourStrategy.deriveStatementOfCase(divorceSession);

        assertThat(derivedStatementOfCase,
                equalTo("My wife is having an affair this week.\nThis is totally not cool."));
    }

}