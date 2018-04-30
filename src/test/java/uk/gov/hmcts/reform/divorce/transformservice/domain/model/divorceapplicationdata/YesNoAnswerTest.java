package uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class YesNoAnswerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void fromInput_converts_yes_successfully() {

        // given
        String yes = "yes";

        // when
        YesNoAnswer yesNoAnswer = objectMapper.convertValue(yes, YesNoAnswer.class);

        // then
        assertEquals(YesNoAnswer.YES, yesNoAnswer);
    }

    @Test
    public void fromInput_converts_no_successfully() {

        // given
        String no = "no";

        // when
        YesNoAnswer yesNoAnswer = objectMapper.convertValue(no, YesNoAnswer.class);

        // then
        assertEquals(YesNoAnswer.NO, yesNoAnswer);
    }
}