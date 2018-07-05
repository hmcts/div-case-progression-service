package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class TimeOverride {


    public static LocalDate time() {
        return LocalDate.now().plus(Period.ofDays(1));
    }

}
