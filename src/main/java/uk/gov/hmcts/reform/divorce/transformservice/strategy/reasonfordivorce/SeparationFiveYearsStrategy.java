package uk.gov.hmcts.reform.divorce.transformservice.strategy.reasonfordivorce;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

@Component
public class SeparationFiveYearsStrategy implements ReasonForDivorceStrategy {

    private static final String SEPARATION_5_YEARS = "separation-5-years";

    private static final String SEPARATION_STRING = "I have been separated from my %s for 5 years or more from the %s.";

    @Override
    public String deriveStatementOfCase(DivorceSession divorceSession) {
        String prettySeparationDate = DateFormatUtils.format(divorceSession.getReasonForDivorceSeperationDate(),
            "dd MMMM yyyy");

        return String.format(SEPARATION_STRING, divorceSession.getDivorceWho(), prettySeparationDate);
    }

    @Override
    public boolean accepts(String reasonForDivorce) {
        return SEPARATION_5_YEARS.equalsIgnoreCase(reasonForDivorce);
    }

}
