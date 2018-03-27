package uk.gov.hmcts.reform.divorce.errorhandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDCallbackResponse;
import uk.gov.hmcts.reform.divorce.transformservice.service.InvalidPetitionException;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CcdCallBackExceptionTest {

    private CcdCallbackExceptionHandler underTest = new CcdCallbackExceptionHandler();

    @Test
    public void InvalidPetitionExceptionShouldReturnCorrectErrorMessage() {
        InvalidPetitionException exception = new InvalidPetitionException("DocumentType Missing");
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        ResponseEntity<CCDCallbackResponse> ccdCallbackResponseResponseEntity = underTest.handleInvalidPetitionException(exception, httpServletRequest);

        assertThat(
                ccdCallbackResponseResponseEntity.getBody().getErrors().get(0),
                equalTo("The Document Type has not been set for one of the uploaded documents. This must be set before a new PDF can be created")
        );
    }

}