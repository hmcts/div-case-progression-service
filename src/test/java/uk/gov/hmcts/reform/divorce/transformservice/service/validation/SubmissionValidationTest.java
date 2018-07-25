package uk.gov.hmcts.reform.divorce.transformservice.service.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubmissionValidationTest {

    private SubmissionValidation underTest;

    @Mock
    private DivorceSession mockDivorceSession;

    @Mock
    private SubmissionValidator aMockValidator;

    @Mock
    private SubmissionValidator anotherMockValidator;

    @Before
    public void setUp() throws Exception {
        List<SubmissionValidator> validators = new ArrayList<>();
        validators.add(aMockValidator);
        validators.add(anotherMockValidator);

        underTest = new SubmissionValidation(validators);
    }

    @Test
    public void shouldReturnTrueIfNoValidatorsReturnFalse() {

        // given
        when(aMockValidator
            .isValid(mockDivorceSession))
            .thenReturn(true);

        when(anotherMockValidator
            .isValid(mockDivorceSession))
            .thenReturn(true);

        // when
        boolean isValid = underTest.validate(mockDivorceSession);

        // then
        assertTrue(isValid);
    }

    @Test
    public void shouldReturnTrueIfOneValidatorReturnsFalse() {

        // given
        when(aMockValidator
            .isValid(mockDivorceSession))
            .thenReturn(false);

        when(anotherMockValidator
            .isValid(mockDivorceSession))
            .thenReturn(true);

        // when
        boolean isValid = underTest.validate(mockDivorceSession);

        // then
        assertFalse(isValid);
    }
}
