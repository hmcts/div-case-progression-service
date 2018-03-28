package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import org.junit.Test;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class SeparationTwoYearsStrategyTest {

    private static final String SEPARATION_TWO_YEARS = "separation-2-years";

    private SeparationTwoYearsStrategy separationTwoYearsStrategy = new SeparationTwoYearsStrategy();

    @Test
    public void testSeparationTwoYearsStatementOfCase() throws ParseException {
        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorce(SEPARATION_TWO_YEARS);
        divorceSession.setDivorceWho("husband");
        divorceSession.setReasonForDivorceSeperationDate(
            new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01T00:00:00.000Z"));

        String derivedStatementOfCase = separationTwoYearsStrategy.deriveStatementOfCase(divorceSession);

        assertThat(derivedStatementOfCase,
            equalTo("I have been separated from my husband for 2 years or more from the 01 January 2015."));
    }

}
