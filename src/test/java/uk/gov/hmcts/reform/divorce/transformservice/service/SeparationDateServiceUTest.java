package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SeparationDateServiceUTest {
    private static final SeparationDateService classUnderTest = new SeparationDateService();

    private final Date separationDate;
    private final Date decisionDate;
    private final Date livingApartDate;
    private final Date expected;

    public SeparationDateServiceUTest(Date separationDate, Date decisionDate, Date livingApartDate, Date expected) {
        this.separationDate = separationDate;
        this.decisionDate = decisionDate;
        this.livingApartDate = livingApartDate;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Date[]> data() {
        return Arrays.asList(new Date[][] {
            { null, null, null, null },
            { getDate("2015-1-1"), null, null, getDate("2015-1-1") },
            { null, getDate("2015-1-1"), null, getDate("2015-1-1") },
            { null, null, getDate("2015-1-1"), getDate("2015-1-1") },
            { getDate("2015-1-1"), getDate("2015-1-2"), null, getDate("2015-1-2") },
            { getDate("2015-1-2"), getDate("2015-1-1"), null, getDate("2015-1-1") },
            { getDate("2015-1-1"), null , getDate("2015-1-2"), getDate("2015-1-2") },
            { getDate("2015-1-2"), null , getDate("2015-1-1"), getDate("2015-1-1") },
            { null, getDate("2015-1-1") , getDate("2015-1-2"), getDate("2015-1-2") },
            { null, getDate("2015-1-2") , getDate("2015-1-1"), getDate("2015-1-2") },
            { getDate("2015-1-1"), getDate("2015-1-1") , getDate("2015-1-1"), getDate("2015-1-1") },
            { getDate("2015-1-1"), getDate("2015-1-2") , getDate("2015-1-3"), getDate("2015-1-3") },
            { getDate("2015-1-3"), getDate("2015-1-2") , getDate("2015-1-1"), getDate("2015-1-2") },
            { getDate("2015-1-3"), getDate("2015-1-1") , getDate("2015-1-2"), getDate("2015-1-2") },
        });
    }

    @Test
    public void givenSeparationDates_whenUpdateSeparationDate_thenReturnSeparationDate() {
        final DivorceSession divorceSession = new DivorceSession();
        divorceSession.setReasonForDivorceSeperationDate(separationDate);
        divorceSession.setReasonForDivorceDecisionDate(decisionDate);
        divorceSession.setReasonForDivorceLivingApartDate(livingApartDate);

        classUnderTest.updateSeparationDate(divorceSession);

        assertEquals(expected, divorceSession.getReasonForDivorceSeperationDate());
    }

    private static Date getDate(String date) {
        return new LocalDate(date).toDate();
    }
}
