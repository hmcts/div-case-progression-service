package uk.gov.hmcts.reform.divorce.transformservice.service;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Gender;

import java.util.Map;

@Component
public class InferredGenderService {
    private final Map<String, Gender> genderMap = ImmutableMap.of("husband", Gender.FEMALE, "wife", Gender.MALE);
    private final Map<String, Gender> roleGender = ImmutableMap.of("husband", Gender.MALE, "wife", Gender.FEMALE);

    public Gender getRespondentGender(String respondentRole) {
        return roleGender.get(respondentRole.toLowerCase());
    }

    public Gender getPetitionerGender(String isSameSexMarriage, String respondentRole) {
        if ("yes".equalsIgnoreCase(isSameSexMarriage)) {
            return roleGender.get(respondentRole.toLowerCase());
        } else if ("no".equalsIgnoreCase(isSameSexMarriage)) {
            return genderMap.get(respondentRole.toLowerCase());
        }
        return null;
    }
}
