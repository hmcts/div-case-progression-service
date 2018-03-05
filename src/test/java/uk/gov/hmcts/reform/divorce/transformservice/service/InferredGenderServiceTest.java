package uk.gov.hmcts.reform.divorce.transformservice.service;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Gender;
import uk.gov.hmcts.reform.divorce.transformservice.service.InferredGenderService;

import static org.junit.Assert.*;

public class InferredGenderServiceTest {

    private InferredGenderService inferredGenderService = new InferredGenderService();

    @Test
    public void shouldReturnMalePetitionerWhenSameSexAndDivorcingHusband() throws Exception {
        String respondentRole = "husband";

        Assert.assertEquals(Gender.MALE, inferredGenderService.getPetitionerGender("Yes", respondentRole));
    }

    @Test
    public void shouldReturnFemalePetitionerWhenSameSexAndDivorcingWife() throws Exception {
        String respondentRole = "wife";

        assertEquals(Gender.FEMALE, inferredGenderService.getPetitionerGender("Yes", respondentRole));
    }

    @Test
    public void shouldReturnFemalePetitionerWhenNotSameSexAndDivorcingHusband() throws Exception {
        String respondentRole = "husband";

        assertEquals(Gender.FEMALE, inferredGenderService.getPetitionerGender("No", respondentRole));
    }


    @Test
    public void shouldReturnMalePetitionerWhenNotSameSexAndDivorcingWife() throws Exception {
        String respondentRole = "wife";

        assertEquals(Gender.MALE, inferredGenderService.getPetitionerGender("No", respondentRole));
    }

    @Test
    public void shouldReturnMaleRespondentWhenRespondentIsHusband() throws Exception {
        String respondentRole = "husband";
        assertEquals(Gender.MALE, inferredGenderService.getRespondentGender(respondentRole));
    }

    @Test
    public void shouldReturnFemaleRespondentWhenRespondentIsWife() throws Exception {
        String respondentRole = "wife";
        assertEquals(Gender.FEMALE, inferredGenderService.getRespondentGender(respondentRole));
    }

    @Test
    public void shouldReturnNullIfRespondentRoleNotMatched() throws Exception {
        assertNull(inferredGenderService.getPetitionerGender("Yes", "notValid"));
    }

    @Test
    public void shouldReturnNullIfIsSameSexNotMatched() throws Exception {
        assertNull(inferredGenderService.getPetitionerGender("notValid", "husband"));
    }

}