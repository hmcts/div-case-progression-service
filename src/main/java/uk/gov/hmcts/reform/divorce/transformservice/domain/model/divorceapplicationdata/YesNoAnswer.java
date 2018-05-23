package uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum YesNoAnswer {

    YES("Yes"),
    NO("No");

    private final String answer;

    YesNoAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return this.answer;
    }

    @JsonCreator
    public static YesNoAnswer fromInput(String input) {
        if (input.equalsIgnoreCase(YES.getAnswer())) {
            return YES;
        } else if (input.equalsIgnoreCase(NO.getAnswer())) {
            return NO;
        }
        throw new IllegalArgumentException(
                String.format("Could not find match for input '%s' in %s",
                        input,
                        Arrays.asList(YesNoAnswer.values())));
    }
}