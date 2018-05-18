package uk.gov.hmcts.reform.divorce.validationservice.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidationClientImplTest {

    @MockBean
    private DefaultValidationHttpEntityFactory httpEntityFactory;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    ValidationClient validationClient;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnValidationResponseOnValidateCall() {
        HttpEntity<ValidationRequest> httpEntity = mock(HttpEntity.class);
        ResponseEntity<ValidationResponse> responseEntity = mock(ResponseEntity.class);

        ValidationRequest validationRequest = new ValidationRequest();

        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setValidationStatus("success");

        String validateUrl = "http://localhost:4008/version/1/validate";
        
        when(httpEntityFactory.createRequestEntityForValidation(eq(validationRequest)))
            .thenReturn(httpEntity);
        when(restTemplate.exchange(eq(validateUrl), eq(HttpMethod.POST), eq(httpEntity), eq(ValidationResponse.class)))
            .thenReturn(responseEntity);

        when(responseEntity.getBody()).thenReturn(validationResponse);

        assertEquals(validationResponse, validationClient.validate(validationRequest));

        verify(httpEntityFactory).createRequestEntityForValidation(eq(validationRequest));
        verify(restTemplate).exchange(
            eq(validateUrl), eq(HttpMethod.POST), eq(httpEntity), eq(ValidationResponse.class)
        );

        verifyNoMoreInteractions(httpEntityFactory, restTemplate);
    }
}