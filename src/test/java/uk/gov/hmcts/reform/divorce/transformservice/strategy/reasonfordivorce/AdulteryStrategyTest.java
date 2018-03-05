package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

public class AdulteryStrategyTest {

    private static final String ADULTERY = "adultery";

    private AdulteryStrategy adulteryStrategy = new AdulteryStrategy();

    @Test
    public void testAdulteryWithoutKnowStatementOfCase() {
        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorce(ADULTERY);
        divorceSession.setReasonForDivorceAdulteryKnowWhere("No");
        divorceSession.setReasonForDivorceAdulteryKnowWhen("No");
        divorceSession.setReasonForDivorceAdulteryWhereDetails("On a washing machine.");
        divorceSession.setReasonForDivorceAdulteryWhenDetails("Some time ago.");
        divorceSession.setReasonForDivorceAdulteryDetails("It hurts inside.");

        String derivedStatementOfCase = adulteryStrategy.deriveStatementOfCase(divorceSession);

        assertThat(derivedStatementOfCase, equalTo("It hurts inside."));
    }

    @Test
    public void testAdulteryWithKnowWhereStatementOfCase() {
        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorce(ADULTERY);
        divorceSession.setReasonForDivorceAdulteryKnowWhere("Yes");
        divorceSession.setReasonForDivorceAdulteryKnowWhen("No");
        divorceSession.setReasonForDivorceAdulteryWhereDetails("On a washing machine.");
        divorceSession.setReasonForDivorceAdulteryWhenDetails("Some time ago.");
        divorceSession.setReasonForDivorceAdulteryDetails("It hurts inside.");

        String derivedStatementOfCase = adulteryStrategy.deriveStatementOfCase(divorceSession);

        assertThat(derivedStatementOfCase, equalTo("On a washing machine.\nIt hurts inside."));
    }

    @Test
    public void testAdulteryWithKnowWhenStatementOfCase() {
        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorce(ADULTERY);
        divorceSession.setReasonForDivorceAdulteryKnowWhere("No");
        divorceSession.setReasonForDivorceAdulteryKnowWhen("Yes");
        divorceSession.setReasonForDivorceAdulteryWhereDetails("On a washing machine.");
        divorceSession.setReasonForDivorceAdulteryWhenDetails("Some time ago.");
        divorceSession.setReasonForDivorceAdulteryDetails("It hurts inside.");

        String derivedStatementOfCase = adulteryStrategy.deriveStatementOfCase(divorceSession);

        assertThat(derivedStatementOfCase, equalTo("Some time ago.\nIt hurts inside."));
    }

    @Test
    public void testAdulteryWithKnowBothStatementOfCase() {
        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorce(ADULTERY);
        divorceSession.setReasonForDivorceAdulteryKnowWhere("Yes");
        divorceSession.setReasonForDivorceAdulteryKnowWhen("Yes");
        divorceSession.setReasonForDivorceAdulteryWhereDetails("On a washing machine.");
        divorceSession.setReasonForDivorceAdulteryWhenDetails("Some time ago.");
        divorceSession.setReasonForDivorceAdulteryDetails("It hurts inside.");

        String derivedStatementOfCase = adulteryStrategy.deriveStatementOfCase(divorceSession);

        assertThat(derivedStatementOfCase, equalTo("On a washing machine.\nSome time ago.\nIt hurts inside."));
    }

}