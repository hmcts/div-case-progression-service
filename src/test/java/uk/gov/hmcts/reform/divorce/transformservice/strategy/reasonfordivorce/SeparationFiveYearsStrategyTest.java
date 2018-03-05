package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

public class SeparationFiveYearsStrategyTest {

    private static final String SEPARATION_FIVE_YEARS = "separation-5-years";

    private SeparationFiveYearsStrategy separationFiveYearsStrategy = new SeparationFiveYearsStrategy();

    @Test
    public void testSeparationFiveYearsStatementOfCase() throws ParseException {
        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorce(SEPARATION_FIVE_YEARS);
        divorceSession.setDivorceWho("wife");
        divorceSession.setReasonForDivorceSeperationDate(
                new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01T00:00:00.000Z"));

        String derivedStatementOfCase = separationFiveYearsStrategy.deriveStatementOfCase(divorceSession);

        assertThat(derivedStatementOfCase,
                equalTo("I have been separated from my wife for 5 years or more from the 01 January 2015."));
    }

}