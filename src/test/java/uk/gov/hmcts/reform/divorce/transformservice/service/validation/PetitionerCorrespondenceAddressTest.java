package uk.gov.hmcts.reform.divorce.transformservice.service.validation;

import org.junit.Test;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Address;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PetitionerCorrespondenceAddressTest {

    private PetitionerCorrespondenceAddress underTest = new PetitionerCorrespondenceAddress();

    @Test
    public void isValidShouldReturnFalseWhenAddressNull() {

        // given
        DivorceSession mockDivorceSession = mock(DivorceSession.class);

        when(mockDivorceSession
            .getPetitionerCorrespondenceAddress())
            .thenReturn(null);

        // when
        boolean isValid = underTest.isValid(mockDivorceSession);

        // then
        assertFalse(isValid);
    }

    @Test
    public void isValidShouldReturnTrueWhenAddressIsNotNull() {

        // given
        DivorceSession mockDivorceSession = mock(DivorceSession.class);

        when(mockDivorceSession
            .getPetitionerCorrespondenceAddress())
            .thenReturn(new Address());

        // when
        boolean isValid = underTest.isValid(mockDivorceSession);

        // then
        assertTrue(isValid);
    }
}
