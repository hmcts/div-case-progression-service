package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

public class DesertionStrategyTest {

    private static final String DESERTION = "desertion";

    private DesertionStrategy desertionStrategy = new DesertionStrategy();

    @Test
    public void testDesertionStatementOfCase() throws ParseException {
        DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorce(DESERTION);
        divorceSession.setDivorceWho("husband");
        divorceSession
                .setReasonForDivorceDesertionDate(new SimpleDateFormat("yyyy-MM-dd").parse("2015-02-01T00:00:00.000Z"));
        divorceSession.setReasonForDivorceDesertionDetails("He told me that he is going to his mother.");

        String derivedStatementOfCase = desertionStrategy.deriveStatementOfCase(divorceSession);

        assertThat(derivedStatementOfCase, equalTo(
                "I have been deserted by my husband on the 01 February 2015.\nHe told me that he is going to his mother."));
    }

}